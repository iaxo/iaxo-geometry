
//*******************************************************************************************************
//*** Description:  A ROOT macro to obtain volume information from a geometry in aGDML file
//*** --------------
//*** Usage: in ROOT/restRoot terminal: 
//*** 1. Compile in root terminal:
//*** root [0] .L PrintGeometryInfo.C
//*** 2. Call with gdml file as argument:
//*** root [1] PrintGeometryInfo("mygdml.gdml");
//*** --------------
//*** Output: 
//*** The macro prints the properties (volume in cm^3, density in g/cm^3, material and some details about the structure of the geometry) 
//*** of each volume in the geometry specified by the GDML file
//*** --------------
//*** Author: María Jiménez (maria.jimenez@unizar.es)
//*******************************************************************************************************

struct VolumeInfo {
    std::string name;
    std::string parentName;
    std::string material;
    double density;
    double volume;
    int level;
    int ndaughters;
    int ID;
};

std::vector<VolumeInfo> volume_info;

std::set<std::string> printed_volumes;
std::map<std::string, int> volume_counts;


void iterateOverVolumes(TGeoVolume *vol, const std::string& parentName, int level, int ID){
    if(!vol) return;

    std::string name = vol->GetName();
    std::string matName;
    double density = 0.0;
    double volume = vol->Capacity();

    TObjArray *daughters = vol->GetNodes();
    int ndaughters;
    if(daughters){
        ndaughters = daughters->GetEntries();
    } else{
        ndaughters = 0;
    }

    TGeoMaterial *material = vol->GetMaterial();
    if(material){
        matName = material->GetName();
        density = material->GetDensity();
    }

    if (printed_volumes.find(name) == printed_volumes.end()) {
        volume_info.push_back({name, parentName, matName, density, volume, level, ndaughters, ID});
        printed_volumes.insert(name);

        if(daughters){
            for(int i = 0; i < daughters->GetEntries(); i++){
                TGeoNode *node = (TGeoNode*)daughters->At(i);
                TGeoVolume *daughtervol = node->GetVolume();
                iterateOverVolumes(daughtervol, name, level+1, i);
            }
        }

    } else {
        volume_info.push_back({name, parentName, matName, density, volume, level, ndaughters, ID});
    }

    volume_counts[name]++;
}

void GeometryInfo(const std::string &gdmlfile){
    TGeoManager::Import(gdmlfile.c_str());
    TGeoVolume *topVolume = gGeoManager->GetTopVolume();
    cout << "\n" << endl;
    cout << "The list of volumes and properties in this geometry : " << endl;
    cout << "\n" << endl;
    cout << "Top volume is called " << topVolume->GetName() << endl;
    iterateOverVolumes(topVolume,"world",0,0);    
}

void PrintGeometryInfo(const std::string &gdmlfile){

    GeometryInfo(gdmlfile);

    for(const auto& info : volume_info){
        for (int i = 0; i < info.level; ++i) {
            std::cout << "\t";
        }
        std::cout << "Volume: " << info.name << ", Parent: " << info.parentName << ", Level: " << info.level << ", ID: " << info.ID;
        if (volume_counts.find(info.name) != volume_counts.end()) {
            std::cout << ", Volume Counts in mother volume: " << volume_counts[info.name];
        }
        std::cout << std::endl;
        for (int i = 0; i < info.level; ++i) {
            std::cout << "\t";
        }
        std::cout << "Material: " << info.material << ", Density: " << info.density << " g/cm^3" << ", Volume: " << info.volume << " cm^3" << ", Daughters: " << info.ndaughters << std::endl;
        for (int i = 0; i < info.level; ++i) {
            std::cout << "\t";
        }
        std::cout << "------------------------------------------" << std::endl;
    }

    volume_counts.clear();
    printed_volumes.clear();
    volume_info.clear();

}
