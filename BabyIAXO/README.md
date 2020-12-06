The structure of the geometry is divided in a set of mostly independent `.GDML` files. This is done in order to avoid working with an extremely large file. However one has to be careful as there can be dependances between files (e.g. steel pipe file can look for a value defined in copper pipe file) so the order in which they are imported in the main `Setup` file critical and should not be altered.

The goal of this structure is to have one main file which we called `Setup.gdml` in which only the most important parameters are defined and where only the physical volumes are defined so that if for example we would like to work without the steel pipe we would just comment a single, small block of code to remove them with all references to the steel pipe contained in this compact block.

TODO. This comment can be removed once the full project follows this approach. It is ok to have commented geometries into the main file. However, in order to be deterministic. I.e. have full associatin of GDML files used for simulation and restG4 ROOT generated files we need to create a GDML setup with a tagged geometry version, as it is described for example at [basic-geometries](https://lfna.unizar.es/gifna/basic-geometries), using the `<!-- ##VERSION ..... >` header at the beginning of the file. If we generate the variation of a given geometry, such as removing/commenting the steel pipe, we should produce a new directory and define inside setup.gdml a new header. This README.md should provide a list with a brief description of those geometry variations.

As an example to remove the steel pipe we would just comment the following lines in `Setup.gdml`:

```
...
<!-- BEGIN STEEL PIPE -->
<physvol name="steelPipe">
    <volumeref ref="steelPipe"/>
    <position name="steelPipePosition" unit="mm" x="0" y="0" z="copperPipeZInWorld + steelPipeWithRespectToCopperPipeOffset"/>
</physvol>
<physvol name="steelPipeVacuum">
    <volumeref ref="steelPipeVacuum"/>
    <position name="steelPipePosition" unit="mm" x="0" y="0" z="copperPipeZInWorld + steelPipeWithRespectToCopperPipeOffset"/>
</physvol>
<!-- END STEEL PIPE -->
...
```

The different components of the geometry should be clearly labeled with a BEGIN and END comment in `Setup.gdml` and in a single block so that they can be turned on and off easily.

![alt text](/miscellaneous/pictures/reference.JPG "BabyIAXO/reference@688356a3")
![alt text](/miscellaneous/pictures/steel_pipe.JPG "BabyIAXO/with_steel_pipe@bf2eab49")
