In order to visualize the geometry with `ROOT` one can use the following command, where `Setup.gdml` is the main `gdml` file.

TODO: This README should give a quick description of the contents.

TODO. This comment needs to be removed once the full project adopts this trend. Now we can use `TRestGDMLParser::GetGeoManager()` that accepts http remote ENTITYs and ${VARIABLES}`. This should be exploited in iaxo geometries. As it is done i.e. at trexdm-geometry. This could allow to create variations into a single file through variables that are specified at the <!-- ##VERSION header?

TODO: We should use the centralized https://sultan.unizar.es/materials/materials.xml file in our geometries.

```
TRestGDMLParser *gdml = new TRestGDMLParser();
gdml->GetGeoManager()->GetTopVolume()->Draw("ogl");
```

![alt text](/miscellaneous/pictures/reference.JPG "BabyIAXO/reference@688356a3")
![alt text](/miscellaneous/pictures/steel_pipe.JPG "BabyIAXO/with_steel_pipe@bf2eab49")
