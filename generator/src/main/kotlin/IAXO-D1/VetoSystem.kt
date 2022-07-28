package `IAXO-D1`

import BabyIAXO.VetoSize
import BabyIAXO.VetoLayer
import BabyIAXO.Veto
import BabyIAXO.Shielding
import Geometry
import space.kscience.gdml.*

private const val xyShieldingDistance =
    Shielding.SizeXY / 2 + Veto.FullThickness / 2 + Shielding.EnvelopeThickness

private const val zShieldingDistance =
    Shielding.SizeZ / 2 + Veto.FullThickness / 2 + Shielding.EnvelopeThickness

class VetoLayerTop : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val numberOfLayers = 3
        val vetoLayer = VetoLayer(4, vetoSize = VetoSize.LARGE).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    if (i == 1) {
                        physVolume(VetoLayer(3, vetoSize = VetoSize.DEFAULT).generate(gdml), name = "vetoLayerTop$i") {
                            position {
                                y = (Veto.FullThickness + 5) * (i - 1) - 35
                                z = -240
                            }
                            rotation { unit = AUnit.DEG; y = 180 * i }
                        }
                    } else {
                        physVolume(vetoLayer, name = "vetoLayerTop$i") {
                            position {
                                y = (Veto.FullThickness + 5) * (i - 1)
                            }
                            rotation { unit = AUnit.DEG; y = 180 * i }
                        }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}


class VetoLayerBottom : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val numberOfLayers = 3
        val vetoLayer = VetoLayer(4, vetoSize = VetoSize.LARGE).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    if (i == 1) {
                        physVolume(vetoLayer, name = "vetoLayerBottom$i") {
                            position {
                                y = -(Veto.FullThickness + 5) * (i - 1) + 20
                            }
                            rotation { unit = AUnit.DEG; y = 180 * (i + 1) }
                        }
                    } else {
                        physVolume(vetoLayer, name = "vetoLayerBottom$i") {
                            position {
                                y = -(Veto.FullThickness + 5) * (i - 1)
                            }
                            rotation { unit = AUnit.DEG; y = 180 * (i + 1) }
                        }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerEast : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val numberOfLayers = 3
        val vetoLayer = VetoLayer(3, vetoSize = VetoSize.LARGE, separation = 10.0).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerEast$i") {
                        position {
                            x = -(Veto.FullThickness + 20) * (i - 1)
                        }
                        rotation { unit = AUnit.DEG; x = 180; z = -90 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerWest : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val numberOfLayers = 3
        val vetoLayer = VetoLayer(3, vetoSize = VetoSize.LARGE, separation = 10.0).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerWest$i") {
                        position {
                            x = (Veto.FullThickness + 20) * (i - 1)
                        }
                        rotation { unit = AUnit.DEG; x = 0; z = 90; y = 0 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}


class VetoLayerBack : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val numberOfLayers = 3
        val vetoLayer = VetoLayer(3, separation = 10.0).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerBack$i") {
                        position {
                            z = -(Veto.FullThickness + 20) * (i - 1)
                            y = 0
                        }
                        rotation { unit = AUnit.DEG; x = -90; y = -90 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerFront : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val numberOfLayers = 3
        val vetoLayer = VetoLayer(3, separation = 10.0).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerBack$i") {
                        position {
                            z = (Veto.FullThickness + 20) * (i - 1)
                        }
                        rotation { unit = AUnit.DEG; x = -90; y = 90 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoSystem : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoSystemVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                physVolume(VetoLayerTop().generate(gdml), name = "vetoSystemTop") {
                    position {
                        y = xyShieldingDistance - 10
                        z = -Shielding.OffsetZ
                    }
                }
                physVolume(VetoLayerBottom().generate(gdml), name = "vetoSystemBottom") {
                    position {
                        y = -xyShieldingDistance - 100
                        z = -Shielding.OffsetZ
                    }
                }
                physVolume(VetoLayerEast().generate(gdml), name = "vetoSystemEast") {
                    position {
                        x = -xyShieldingDistance - 130 + 80
                        z = -Shielding.OffsetZ + 100
                    }
                }
                physVolume(VetoLayerWest().generate(gdml), name = "vetoSystemWest") {
                    position {
                        x = xyShieldingDistance + 130 - 80
                        z = -Shielding.OffsetZ - 100
                    }
                }
                physVolume(VetoLayerBack().generate(gdml), name = "vetoSystemBack") {
                    position {
                        z = -zShieldingDistance - Shielding.OffsetZ - 520 + 120
                        x = -65
                    }
                }
                physVolume(VetoLayerFront().generate(gdml), name = "vetoSystemFront") {
                    position {
                        z = -Shielding.OffsetZ + zShieldingDistance + 500 - 120
                        x = 65
                    }
                }
            }
        }

        return vetoSystemVolume
    }
}