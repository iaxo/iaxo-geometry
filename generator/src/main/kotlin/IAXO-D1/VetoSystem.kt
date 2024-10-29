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

class VetoLayerTop(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(4, vetoSize = VetoSize.LARGE).generate(gdml)
        val vetoLayerReversedIndex = VetoLayer(4, vetoSize = VetoSize.LARGE, reverseIndex = true).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    if (i == 1) {
                        physVolume(VetoLayer(3, vetoSize = VetoSize.DEFAULT).generate(gdml), name = "vetoLayerTop$i") {
                            position(
                                y = (Veto.FullThickness + 5) * (i - 1) - 35,
                                z = -240
                            ) { unit = LUnit.MM }
                            rotation { unit = AUnit.DEG; y = 180 * i }
                        }
                    } else {
                        if (i % 2 == 0) {
                            physVolume(vetoLayerReversedIndex, name = "vetoLayerTop$i") {
                                position(
                                    y = (Veto.FullThickness + 5) * (i - 1)
                                ) { unit = LUnit.MM }
                                rotation { unit = AUnit.DEG; y = 180 * i }
                            }
                        } else {
                            physVolume(vetoLayer, name = "vetoLayerTop$i") {
                                position(
                                    y = (Veto.FullThickness + 5) * (i - 1)
                                ) { unit = LUnit.MM }
                                rotation { unit = AUnit.DEG; y = 180 * i }
                            }
                        }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}


class VetoLayerBottom(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(4, vetoSize = VetoSize.LARGE).generate(gdml)
        val vetoLayerReversedIndex = VetoLayer(4, vetoSize = VetoSize.LARGE, reverseIndex = true).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    if (i == 1) {
                        physVolume(vetoLayer, name = "vetoLayerBottom$i") {
                            position(
                                y = -(Veto.FullThickness + 5) * (i - 1) + 20
                            ) { unit = LUnit.MM }
                            rotation { unit = AUnit.DEG; y = 180 * (i + 1) }
                        }
                    } else {
                        if (i % 2 == 0) {
                            // revert the indices so that geometry looks okay
                            physVolume(vetoLayerReversedIndex, name = "vetoLayerBottom$i") {
                                position(
                                    y = -(Veto.FullThickness + 5) * (i - 1)
                                ) { unit = LUnit.MM }
                                rotation { unit = AUnit.DEG; y = 180 * (i + 1) }
                            }
                        } else {
                            physVolume(vetoLayer, name = "vetoLayerBottom$i") {
                                position(
                                    y = -(Veto.FullThickness + 5) * (i - 1)
                                ) { unit = LUnit.MM }
                                rotation { unit = AUnit.DEG; y = 180 * (i + 1) }
                            }
                        }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerRight(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(3, vetoSize = VetoSize.LARGE, separation = 10.0).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerRight$i") {
                        position(
                            x = -(Veto.FullThickness + 20) * (i - 1)
                        ) { unit = LUnit.MM }
                        rotation { unit = AUnit.DEG; x = 180; z = -90 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerLeft(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(3, vetoSize = VetoSize.LARGE, separation = 10.0).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerLeft$i") {
                        position(
                            x = (Veto.FullThickness + 20) * (i - 1)
                        ) { unit = LUnit.MM }
                        rotation { unit = AUnit.DEG; x = 0; z = 90; y = 0 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}


class VetoLayerBack(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(3, separation = 10.0).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    // We revert back layer so the numbering looks uniform
                    val i_reverse = numberOfLayers - i + 1
                    physVolume(vetoLayer, name = "vetoLayerBack$i_reverse") {
                        position(
                            z = -(Veto.FullThickness + 20) * (i - 1),
                            y = 0
                        ) { unit = LUnit.MM }
                        rotation { unit = AUnit.DEG; x = -90; y = -90 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerFront(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(3, separation = 10.0).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerFront$i") {
                        position(
                            z = (Veto.FullThickness + 20) * (i - 1)
                        ) { unit = LUnit.MM }
                        rotation { unit = AUnit.DEG; x = -90; y = 90 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoSystem(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoSystemVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                physVolume(VetoLayerTop(numberOfLayers).generate(gdml), name = "vetoSystemTop") {
                    position(
                        y = xyShieldingDistance - 10,
                        z = -Shielding.OffsetZ
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerBottom(numberOfLayers).generate(gdml), name = "vetoSystemBottom") {
                    position(
                        y = -xyShieldingDistance - 100,
                        z = -Shielding.OffsetZ
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerRight(numberOfLayers).generate(gdml), name = "vetoSystemRight") {
                    position(
                        x = -xyShieldingDistance - 130 + 80,
                        z = -Shielding.OffsetZ + 100
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerLeft(numberOfLayers).generate(gdml), name = "vetoSystemLeft") {
                    position(
                        x = xyShieldingDistance + 130 - 80,
                        z = -Shielding.OffsetZ - 100
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerBack(numberOfLayers).generate(gdml), name = "vetoSystemBack") {
                    position(
                        z = -zShieldingDistance - Shielding.OffsetZ - 520 + 120,
                        x = -65
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerFront(numberOfLayers).generate(gdml), name = "vetoSystemFront") {
                    position(
                        z = -Shielding.OffsetZ + zShieldingDistance + 500 - 120,
                        x = 65
                    ) { unit = LUnit.MM }
                }
            }
        }

        return vetoSystemVolume
    }
}