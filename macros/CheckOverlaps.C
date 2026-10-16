#include <TGeoBBox.h>
#include <TGeoManager.h>
#include <TGeoOverlap.h>
#include <TROOT.h>
#include <TRandom.h>
#include <TSystem.h>
#include <algorithm>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <iterator>
#include <string>
#include <vector>

namespace {
// Check the thin readout slice explicitly: surface meshes can miss it.
bool CheckGasPartition(TGeoManager *geometry) {
    auto chamber = geometry->FindVolumeFast("Chamber");
    if (!chamber) return true;
    auto above = chamber->FindNode("gasAboveReadout");
    auto outside = chamber->FindNode("gasNotAboveReadout");
    auto copper = chamber->FindNode("copperReadout");
    if (!above || !outside || !copper) return true; // Unsplit gas or another chamber design.
    auto fullGas = geometry->FindVolumeFast("gasVolume");
    if (!fullGas) return false;
    auto readoutBox = dynamic_cast<TGeoBBox *>(copper->GetVolume()->GetShape());
    if (!readoutBox) return false;
    const double mm = readoutBox->GetDX() / 30.; // GDML readout half-width is 30 mm.
    int errors = 0;
    for (double z : {-14.999, -14.9, -14.801, -14.799, 0., 14.995, 14.99602, 14.998}) {
        for (double x = -49.937; x < 50.; x += 0.731) {
            for (double y = -49.913; y < 50.; y += 0.733) {
                double point[3] = {x * mm, y * mm, z * mm}, local[3];
                above->GetMatrix()->MasterToLocal(point, local);
                const bool inAbove = above->GetVolume()->GetShape()->Contains(local);
                outside->GetMatrix()->MasterToLocal(point, local);
                const bool inOutside = outside->GetVolume()->GetShape()->Contains(local);
                copper->GetMatrix()->MasterToLocal(point, local);
                const bool inCopper = copper->GetVolume()->GetShape()->Contains(local);
                const bool inGas = fullGas->GetShape()->Contains(point);
                if (int(inAbove) + int(inOutside) != int(inGas) ||
                    (inCopper && (inAbove || inOutside))) ++errors;
            }
        }
    }
    std::cout << "Gas partition mismatches: " << errors << std::endl;
    return errors == 0;
}
}

// From the repository root:
// root -l -b -q 'macros/CheckOverlaps.C("gdml")'
// A single GDML file is also accepted. Tolerance is in ROOT geometry units
// (cm by default, so 0.0001 cm = 1 micrometer). Sampling supplements the mesh
// check; it does not prove the absence of arbitrarily small intersections.
void CheckOverlaps(const char *path = "gdml", double tolerance = 0.0001,
                   int samplePoints = 1000000) {
    namespace fs = std::filesystem;
    gROOT->SetBatch(kTRUE);
    std::vector<fs::path> files;
    const fs::path input(path);
    if (fs::is_regular_file(input)) files.push_back(input);
    else if (fs::is_directory(input)) {
        for (const auto &entry : fs::recursive_directory_iterator(input)) {
            if (entry.is_regular_file() && entry.path().extension() == ".gdml") {
                bool archived = false;
                for (const auto &part : entry.path()) if (part == "old") archived = true;
                if (!archived) files.push_back(entry.path());
            }
        }
    }
    std::sort(files.begin(), files.end());
    if (files.empty()) {
        std::cerr << "No GDML files found: " << path << std::endl;
        gSystem->Exit(2);
        return;
    }
    int failed = 0, checked = 0, templates = 0;
    for (const auto &file : files) {
        std::ifstream stream(file);
        const std::string text((std::istreambuf_iterator<char>(stream)), {});
        if (text.find("${") != std::string::npos) {
            std::cout << "TEMPLATE (requires parameter substitution): " << file << std::endl;
            ++templates;
            if (fs::is_regular_file(input)) ++failed;
            continue;
        }
        delete gGeoManager;
        gRandom->SetSeed(12345);
        auto geometry = TGeoManager::Import(file.c_str());
        if (!geometry) { ++failed; continue; }
        ++checked;
        geometry->CheckOverlaps(tolerance);
        const int meshOverlaps = geometry->GetListOfOverlaps()->GetEntries();
        geometry->PrintOverlaps();
        geometry->CheckOverlapsBySampling(tolerance, samplePoints);
        const int sampledOverlaps = geometry->GetListOfOverlaps()->GetEntries();
        geometry->PrintOverlaps();
        const bool partition = CheckGasPartition(geometry);
        const bool ok = meshOverlaps == 0 && sampledOverlaps == 0 && partition;
        std::cout << "RESULT " << file << " mesh=" << meshOverlaps
                  << " sampled=" << sampledOverlaps << " partition=" << partition
                  << " " << (ok ? "PASS" : "FAIL") << std::endl;
        if (!ok) ++failed;
    }
    std::cout << "SUMMARY checked=" << checked << " templates=" << templates
              << " failed=" << failed << std::endl;
    gSystem->Exit(failed ? 1 : 0);
}
