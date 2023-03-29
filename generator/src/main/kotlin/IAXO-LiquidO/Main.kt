package `IAXO-LiquidO`

import materialsUrl

import space.kscience.gdml.*

private const val worldSizeX = 1500.0
private const val worldSizeY = 1500.0
private const val worldSizeZ = 1500.0

val geometries = mapOf(
    "Default" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val shielding = Shielding().generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
                physVolume(shielding, name = "Shielding")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
)

