vector<string> filesContainer = {};

void FindFiles(const char *dirname)
{
    const char *ext = ".gdml";
    TSystemDirectory dir(dirname, dirname);
    TList *files = dir.GetListOfFiles();
    if (files)
    {
        TSystemFile *file;
        TString fname;
        TIter next(files);
        while ((file = (TSystemFile *)next()))
        {
            fname = file->GetName();
            if (!file->IsDirectory() && fname.EndsWith(ext))
            {
                filesContainer.push_back(string(dirname) + "/" + string(fname.Data()));
            }
            else if (file->IsDirectory() && !(fname == "." || fname == ".."))
            {
                string dirnameNext = string(dirname) + "/" + string(fname.Data());
                FindFiles(dirnameNext.c_str());
            }
        }
    }
}

void CheckOverlaps(const char *dirname = "../gdml")
{
    FindFiles(dirname);
    map<string, Int_t> overlaps;

    for (const auto &filename : filesContainer)
    {
        cout << filename << endl;
        TGeoManager::Import(filename.c_str());
        gGeoManager->SetVisLevel(5);
        gGeoManager->GetTopVolume()->Draw("ogl");
        TGeoNode *node = gGeoManager->GetTopNode();
        node->CheckOverlaps(0.0001);
        auto nOverlaps = gGeoManager->GetListOfOverlaps()->GetEntries();
        overlaps[filename] = nOverlaps;
    }
    for (const auto &filename : filesContainer)
    {
        cout << "'" << filename << "' overlaps: " << overlaps[filename] << endl;
    }
}
