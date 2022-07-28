# Detector Geometry

This repository serves as a database of all serialized geometry files used in IAXO simulations (written in [gdml](https://indico.cern.ch/event/408139/contributions/979922/attachments/815913/1118019/GDML_CHEP06.pdf) format).

It also hosts software required to produce these files and some tools to validate or visualize them.

All gdml files are stored in the `gdml` directory and are all self-contained meaning they do not reference any other files and can be used directly in ROOT or Geant4.

### Visualization

In order to visualize a gdml file with root one can do the following (for a file named `Setup.gdml`):

```
TGeoManager::Import("Setup.gdml");
gGeoManager->SetVisLevel(10);
gGeoManager->GetTopVolume()->Draw("ogl");
```

The `SetVisLevel` command is required to view some geometries that have nested assemblies, such as the veto system (veto < veto layer < veto group < veto system). ROOT's default value is too low.

Additionally, the macro file `ViewGdml.C` from the `root` directory can be used to visualize the gdml. To use this macro to visualize some file named `Setup.gdml` one could do the following from a shell that has access to the `root` command (not from the root prompt itself!):

```
root 'ViewGdml.C("Setup.gdml")'
```

### Generation

The `generator` section of this repository is used to produce the gdml files. The files present in the repository should already be updated to the latest version of the generator, but you can still tweak the generator code to produce variations in the geometry more easily than to modify the GDML.

Running the generator from the command line is really easy, just go to the `generator` directory and type:

```
./gradlew run
```

You may need to give the script permission to run via `chmod +x gradlew`.

### Showcase

![image](https://user-images.githubusercontent.com/35803280/158363274-5b60f45a-3c72-430a-bec7-19b0b18e42a9.png)

Since the geometry is divided into groups, we can use the tree view to easily navigate the geometry.

![image](https://user-images.githubusercontent.com/35803280/158363294-0a850d7a-1d6c-4949-a195-0d91de79bc8e.png)
