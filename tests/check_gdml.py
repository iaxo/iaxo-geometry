"""Check current GDML references, variant identity, and generator freshness."""
import argparse
from copy import deepcopy
from pathlib import Path
import xml.etree.ElementTree as ET

REPO = Path(__file__).resolve().parents[1]
GAS_VOLUMES = ("gasVolume", "gasVolumeNotAboveReadout", "gasVolumeAboveReadout")


def require(condition, message):
    if not condition:
        raise AssertionError(message)


def load(relative):
    return ET.parse(REPO / "gdml" / relative).getroot()


def material(root, volume):
    return root.find(f"structure/volume[@name='{volume}']/materialref").get("ref")


def check_current():
    files = sorted((REPO / "gdml").glob("*/*.gdml"))
    for path in files:
        root = ET.parse(path).getroot()
        names = {
            "solid": {node.get("name") for node in root.find("solids")},
            "volume": {node.get("name") for node in root.find("structure")},
            "material": {node.get("name") for node in root.find("materials")},
        }
        for tag, target in (("solidref", "solid"), ("first", "solid"),
                            ("second", "solid"), ("volumeref", "volume"),
                            ("materialref", "material"), ("fraction", "material"),
                            ("composite", "material"), ("world", "volume")):
            for node in root.iter(tag):
                require(node.get("ref") in names[target], f"{path}: unresolved {tag} {node.get('ref')}")
        cut = root.find("solids/subtraction[@name='gasSolidAboveReadoutAux']")
        if cut is not None:
            rotation = cut.find("rotation")
            require(rotation is not None and float(rotation.get("z", "0")) == 0,
                    f"{path}: readout subtraction must align with the local gas square")
    print(f"PASS: XML references and readout frame in {len(files)} current files")


def check_variants():
    for family in ("BabyIAXO", "IAXO-D1"):
        default = load(f"{family}/Default.gdml")
        xenon = load(f"{family}/DefaultXenon.gdml")
        normalized = deepcopy(xenon)
        for volume in GAS_VOLUMES:
            require(material(xenon, volume) == "XenonNeon2.3%Isobutane1.05bar", f"{family}: missing xenon")
            normalized.find(f"structure/volume[@name='{volume}']/materialref").set("ref", material(default, volume))
        for section in ("solids", "structure"):
            require(ET.tostring(default.find(section)) == ET.tostring(normalized.find(section)),
                    f"{family}: DefaultXenon changed detector geometry")
    for suffix, gas in (("Argon1pct", "Argon1%Isobutane1.4bar"),
                        ("Argon2pct", "Argon2%Isobutane1.4bar"),
                        ("Xenon", "XenonNeon2.3%Isobutane1.05bar")):
        root = load(f"BabyIAXO/ChamberAndPipe{suffix}.gdml")
        for volume in GAS_VOLUMES:
            require(material(root, volume) == gas, f"{suffix}: incorrect gas")
    for name in ("Default", "NoVetoes"):
        plain = load(f"IAXO-D1/{name}.gdml")
        closed = load(f"IAXO-D1/{name}ClosedPipe.gdml")
        require(plain.find("structure/assembly/physvol[@name='detectorPipeCap']") is None,
                f"{name}: open pipe has a cap")
        require(closed.find("structure/assembly/physvol[@name='detectorPipeCap']") is not None,
                f"{name}: closed pipe is missing its cap")
    four_layers = load("IAXO-D1/CompleteVeto4Layers.gdml")
    y3 = float(four_layers.find(".//physvol[@name='vetoLayerTop3']/position").get("y"))
    y4 = float(four_layers.find(".//physvol[@name='vetoLayerTop4']/position").get("y"))
    # 35 mm light-guide half-thickness plus 27 mm wrapped/cadmium bar half-thickness.
    require(y4 - y3 > 62, "Fourth top layer intersects the third layer's light guide")
    print("PASS: detector identity, gas variants, pipe caps, and top-layer clearance")


def check_generated(directory):
    files = sorted(directory.glob("*/*.gdml"))
    require(files, f"No generated GDML files in {directory}")
    for path in files:
        checked_in = REPO / "gdml" / path.relative_to(directory)
        require(checked_in.exists() and checked_in.read_bytes() == path.read_bytes(),
                f"Generated file differs: {checked_in}")
    for suffix in ("Argon1pct", "Argon2pct", "Xenon"):
        require((directory / f"BabyIAXO/ChamberAndPipe{suffix}.gdml").exists(),
                f"Generator omitted the {suffix} variant")
    print(f"PASS: {len(files)} generated files match the published GDML byte for byte")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--generated", type=Path, help="also compare fresh generator output")
    args = parser.parse_args()
    check_current()
    check_variants()
    if args.generated:
        check_generated(args.generated)
