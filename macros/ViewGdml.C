void ViewGdml(string filename, float transparency = 50)
{
    // first import it and see if there is something wrong with the file
    TGeoManager::Import(filename.c_str());
    // check for overlaps
    TGeoNode *node = gGeoManager->GetTopNode();
    node->CheckOverlaps(0.0001);
    // print overlaps
    gGeoManager->PrintOverlaps();

    for (auto i = 0; i < gGeoManager->GetListOfVolumes()->GetEntries(); i++)
    {
        TGeoVolume *geoVolume = gGeoManager->GetVolume(i); // https://root.cern/doc/v606/classTGeoVolume.html
        if (!geoVolume)
        {
            continue;
        }
        TGeoMaterial *material = geoVolume->GetMaterial();
        double density = material->GetDensity();
        string materialName = material->GetName();

        geoVolume->SetTransparency(transparency);
        if (density < 0.10 /* g/cm3 */)
        {
            geoVolume->SetVisibility(kFALSE);
        }
    }

    TEveManager::Create();

    auto viewer = gEve->GetDefaultGLViewer();
    viewer->SetClearColor(0);
    viewer->SetCurrentCamera(TGLViewer::kCameraPerspXOZ);

    TEveGeoTopNode *top_node = new TEveGeoTopNode(gGeoManager, node);
    gEve->AddGlobalElement(top_node);

    gEve->FullRedraw3D(kTRUE);

    viewer->CurrentCamera().Reset();
}
