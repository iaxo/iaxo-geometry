package BabyIAXO

import materialsUrl

import space.kscience.gdml.*

private const val worldSizeX = 1450.0
private const val worldSizeY = 1600.0
private const val worldSizeZ = 1450.0

fun completeGeometry(
    vetoSystem: VetoSystem = VetoSystem(),
    useXenon: Boolean = false,
): Gdml {
    return Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber(useXenon = useXenon).generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val electronicsBoxVolume = Electronics().generate(this)
        val shieldingVolume = Shielding().generate(this)
        val vetoSystemVolume = vetoSystem.generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
                physVolume(electronicsBoxVolume, name = "ElectronicsBox") {
                    position(
                        x = Electronics.PipeToElectronicsDistanceX,
                        z = Electronics.DetectorToElectronicsDistanceZ
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(shieldingVolume, name = "Shielding")
                physVolume(vetoSystemVolume, name = "VetoSystem")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD)
}

val geometries = mapOf(
    "Default" to completeGeometry(),
    "DefaultXenon" to completeGeometry(useXenon = true),
    "CompleteVeto1Layers" to completeGeometry(vetoSystem = VetoSystem(numberOfLayers = 1)),
    "CompleteVeto2Layers" to completeGeometry(vetoSystem = VetoSystem(numberOfLayers = 2)),
    "CompleteVeto3Layers" to completeGeometry(vetoSystem = VetoSystem(numberOfLayers = 3)),
    "CompleteVeto4Layers" to completeGeometry(vetoSystem = VetoSystem(numberOfLayers = 4)),
    "NoVetos" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val electronicsBoxVolume = Electronics().generate(this)
        val shieldingVolume = Shielding().generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
                physVolume(electronicsBoxVolume, name = "ElectronicsBox") {
                    position(
                        x = Electronics.PipeToElectronicsDistanceX,
                        z = Electronics.DetectorToElectronicsDistanceZ
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(shieldingVolume, name = "Shielding")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
    "NeutronShieldingTubes" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val shieldingVolume = Shielding(hdpeNeutronShieldingDiameter = 20.0).generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

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
    }.withUnits(LUnit.MM, AUnit.RAD),

    "ShieldingLayers" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val electronicsBoxVolume = Electronics().generate(this)
        val shieldingVolume = Shielding(layers = true).generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
                physVolume(electronicsBoxVolume, name = "ElectronicsBox") {
                    position(
                        x = Electronics.PipeToElectronicsDistanceX,
                        z = Electronics.DetectorToElectronicsDistanceZ
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(shieldingVolume, name = "Shielding")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
    "ShieldingLayersNoCopperBox" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val electronicsBoxVolume = Electronics().generate(this)
        val shieldingVolume = Shielding(layers = true, copperBox = false).generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
                physVolume(electronicsBoxVolume, name = "ElectronicsBox") {
                    position(
                        x = Electronics.PipeToElectronicsDistanceX,
                        z = Electronics.DetectorToElectronicsDistanceZ
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(shieldingVolume, name = "Shielding")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
    "ChamberAndPipe" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val electronicsBoxVolume = Electronics().generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
                physVolume(electronicsBoxVolume, name = "ElectronicsBox") {
                    position(
                        x = Electronics.PipeToElectronicsDistanceX,
                        z = Electronics.DetectorToElectronicsDistanceZ
                    ) {
                        unit = LUnit.MM
                    }
                }
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
    "VetoSystem" to Gdml {

        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val vetoSystemVolume = VetoSystem().generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

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
    }.withUnits(LUnit.MM, AUnit.RAD),
    "Veto" to Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val vetoVolume = Veto().generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(vetoVolume, name = "Veto")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
    "Chamber" to Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
    "Shielding" to Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val shieldingVolume = Shielding().generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(shieldingVolume, name = "Shielding")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
    "DetectorPipe" to Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val detectorPipeVolume = DetectorPipe().generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(detectorPipeVolume, name = "DetectorPipe")
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
    "WithTelescopePipe" to Gdml {
        loadMaterialsFromUrl(materialsUrl) /* This adds all materials form the URL (we do not need them all) */

        val chamberVolume = Chamber().generate(this)
        val detectorPipeVolume = DetectorPipe().generate(this)
        val electronicsBoxVolume = Electronics().generate(this)
        val shieldingVolume = Shielding().generate(this)
        val vetoSystemVolume = VetoSystem().generate(this)
        val telescopePipe = TelescopePipe().generate(this)

        structure {
            val worldBox = solids.box(worldSizeX, worldSizeY, worldSizeZ + 10000.0, "worldBox")

            world = volume(Materials.Air.ref, worldBox, "world") {
                physVolume(chamberVolume, name = "Chamber")
                physVolume(detectorPipeVolume, name = "DetectorPipe") {
                    position(z = DetectorPipe.ZinWorld) {
                        unit = LUnit.MM
                    }
                }
                physVolume(electronicsBoxVolume, name = "ElectronicsBox") {
                    position(
                        x = Electronics.PipeToElectronicsDistanceX,
                        z = Electronics.DetectorToElectronicsDistanceZ
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(shieldingVolume, name = "Shielding")
                physVolume(vetoSystemVolume, name = "VetoSystem")
                physVolume(telescopePipe, name = "TelescopePipe") {
                    position(z = TelescopePipe.TelescopePipeZinWorld) {
                        unit = LUnit.MM
                    }
                }
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD),
)

