package BabyIAXO

import materialsUrl

import space.kscience.gdml.*

private const val worldSize = 2000

val geometries = mapOf(
    "Default" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val shieldingVolume = Shielding().generate(this)
        val vetoSystemVolume = VetoSystem().generate(this)

        structure {
            val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
                physVolume(shieldingVolume, name = "Shielding")
                physVolume(vetoSystemVolume, name = "VetoSystem")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
    "NoVetos" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val shieldingVolume = Shielding().generate(this)

        structure {
            val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
                physVolume(shieldingVolume, name = "Shielding")
            }
        }
    }.withUnits(LUnit.CM, AUnit.RAD),
    "ChamberAndPipe" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)

        structure {
            val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
            }
        }
    }.withUnits(LUnit.CM, AUnit.RAD),
    "VetoSystem" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val vetoSystemVolume = VetoSystem().generate(this)

        structure {
            val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(vetoSystemVolume, name = "VetoSystem") {
                    position(
                        x = 0,
                        y = 40,
                        z = 400,
                    ) { unit = LUnit.MM }
                }
            }
        }
    }.withUnits(LUnit.CM, AUnit.RAD),
    "Veto" to Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val vetoVolume = Veto().generate(this)

        structure {
            val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(vetoVolume, name = "Veto")
            }
        }
    }.withUnits(LUnit.CM, AUnit.RAD),
    "Chamber" to Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)

        structure {
            val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
            }
        }
    }.withUnits(LUnit.CM, AUnit.RAD),
    "Shielding" to Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val shieldingVolume = Shielding().generate(this)

        structure {
            val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(shieldingVolume, name = "Shielding")
            }
        }
    }.withUnits(LUnit.CM, AUnit.RAD),
    "DetectorPipe" to Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val detectorPipeVolume = DetectorPipe().generate(this)

        structure {
            val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(detectorPipeVolume, name = "DetectorPipe")
            }
        }
    }.withUnits(LUnit.CM, AUnit.RAD),
)

