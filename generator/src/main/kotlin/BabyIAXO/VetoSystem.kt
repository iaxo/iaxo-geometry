package BabyIAXO

import Geometry

import space.kscience.gdml.*

enum class VetoSize {
    DEFAULT, LARGE, SMALL
}

class Veto(size: VetoSize = VetoSize.DEFAULT) : Geometry() {
    companion object Parameters {

        val LongSideLengthMap = mapOf(
            VetoSize.DEFAULT to 800.0,
            VetoSize.LARGE to 1500.0,
            VetoSize.SMALL to 300.0
        )

        const val Width: Double = 200.0
        const val Thickness: Double = 50.0

        const val CaptureLayerThickness: Double = 1.0
        const val WrappingThickness: Double = 1.0
        const val SeparationAdjacent: Double = 5.0
        const val FullThickness: Double = Thickness + 2 * WrappingThickness + 2 * CaptureLayerThickness

        const val LightGuide1Length: Double = 130.0
        const val LightGuide2Length: Double = 80.0
    }

    private val vetoSize: VetoSize = size

    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
        val vetoVolume: GdmlRef<GdmlAssembly> by lazy {
            val length = LongSideLengthMap[vetoSize]!!
            val label = "-${length}mm"
            val scintillatorSolid = gdml.solids.box(
                Width,
                Thickness,
                length,
                "scintillatorSolid$label",
            )

            val scintillatorVolume =
                gdml.structure.volume(Materials.BC408.ref, scintillatorSolid, "scintillatorVolume$label")

            val captureLayerSolid = gdml.solids.box(
                Width,
                CaptureLayerThickness,
                length,
                "captureLayerSolid$label"
            )
            val captureLayerVolume = gdml.structure.volume(
                Materials.Cadmium.ref,
                captureLayerSolid,
                "captureLayerVolume$label"
            )

            val scintillatorWrappingBaseSolid = gdml.solids.box(
                Width + 2 * WrappingThickness,
                Thickness + 2 * WrappingThickness,
                length + 2 * WrappingThickness,
                "scintillatorWrappingBaseSolid$label"
            )
            val scintillatorWrappingHoleSolid = gdml.solids.box(
                Width,
                Thickness,
                length,
                "scintillatorWrappingHoleSolid$label",
            )
            val scintillatorWrappingFullSolid = gdml.solids.subtraction(
                scintillatorWrappingBaseSolid,
                scintillatorWrappingHoleSolid,
                "scintillatorWrappingFullSolid$label"
            )
            val scintillatorWrappingSolid = gdml.solids.subtraction(
                scintillatorWrappingFullSolid,
                gdml.solids.box(
                    Width + 2 * WrappingThickness,
                    Thickness + 2 * WrappingThickness,
                    WrappingThickness * 1.2, // we remove a bit more, it shouldn't matter since its a sub
                    "scintillatorWrappingRemoveSideSolid"
                ), "scintillatorWrappingSolid$label.solid"
            ) {
                position(z = -length / 2 - WrappingThickness / 2) { unit = LUnit.MM }
            }

            val scintillatorWrappingVolume = gdml.structure.volume(
                Materials.Neoprene.ref,
                scintillatorWrappingSolid,
                name = "scintillatorWrappingSolid$label"
            )

            val scintillatorLightGuide1Solid = gdml.solids.trd(
                Width,
                Width,
                Thickness + 10,
                Thickness,
                LightGuide1Length,
                "scintillatorLightGuide1Solid$label"
            )
            val scintillatorLightGuide2Solid = gdml.solids.trd(
                70,
                Width,
                70,
                Thickness + 10,
                LightGuide2Length,
                "scintillatorLightGuide2Solid$label"
            )
            val scintillatorLightGuideSolid = gdml.solids.union(
                scintillatorLightGuide1Solid,
                scintillatorLightGuide2Solid,
                "scintillatorLightGuideSolid$label"
            ) {
                position(z = -LightGuide2Length / 2 - LightGuide1Length / 2) { unit = LUnit.MM }
            }

            val scintillatorLightGuideVolume =
                gdml.structure.volume(
                    Materials.Lucite.ref,
                    scintillatorLightGuideSolid,
                    name = "scintillatorLightGuideVolume$label"
                )

            return@lazy gdml.structure.assembly {
                physVolume(scintillatorVolume)
                physVolume(scintillatorWrappingVolume)
                val captureLayerYOffset =
                    Thickness / 2 + WrappingThickness + CaptureLayerThickness / 2
                physVolume(captureLayerVolume) {
                    position(y = captureLayerYOffset) { unit = LUnit.MM }
                }
                physVolume(captureLayerVolume) {
                    position(y = -captureLayerYOffset) { unit = LUnit.MM }
                }
                physVolume(scintillatorLightGuideVolume) {
                    position(z = -length / 2 - LightGuide1Length / 2) { unit = LUnit.MM }
                }
            }
        }

        return vetoVolume
    }
}

class VetoLayer(
    private val n: Int = 4,
    private val separation: Double = Veto.SeparationAdjacent,
    val vetoSize: VetoSize = VetoSize.DEFAULT,
    val isFront: Boolean = false,
    val reverseIndex: Boolean = false
) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
        val step = Veto.Width + 2 * Veto.WrappingThickness + separation

        val veto = Veto(vetoSize).generate(gdml)

        if (!isFront) {
            val offset = step * (n + 1) / 2.0
            val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
                return@lazy gdml.structure.assembly {
                    for (i in 1..n) {
                        physVolume(veto, name = "$name.veto$i") {
                            position(x = step * i - offset) { unit = LUnit.MM }
                        }
                    }
                }
            }

            return vetoLayerVolume
        } else {
            val offset = step * (n - 1) / 2.0
            if (n != 3) {
                throw Exception("Front veto layer can only have 3 vetoes (n=3)")
            }
            val vetoSmall = Veto(VetoSize.SMALL).generate(gdml)
            val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
                return@lazy gdml.structure.assembly {
                    repeat(n) { j ->
                        val index = if (reverseIndex) n - j else j
                        if (j == 1) {
                            physVolume(vetoSmall, name = "vetoSmall$index") {
                                position(
                                    x = step * j - offset,
                                    z = (Veto.LongSideLengthMap[VetoSize.SMALL]!! - Veto.LongSideLengthMap[VetoSize.DEFAULT]!!) / 2
                                ) { unit = LUnit.MM }
                            }
                            // rotated
                            physVolume(vetoSmall, name = "vetoSmallRotated$index") {

                                position(
                                    x = step * j - offset,
                                    z = -(Veto.LongSideLengthMap[VetoSize.SMALL]!! - Veto.LongSideLengthMap[VetoSize.DEFAULT]!!) / 2
                                ) { unit = LUnit.MM }
                                rotation(x = 180) { unit = AUnit.DEG }
                            }
                        } else {
                            physVolume(veto, name = "veto$index") {
                                position(x = step * j - offset) { unit = LUnit.MM }
                            }
                        }
                    }
                }
            }

            return vetoLayerVolume
        }
    }
}

// BabyIAXO veto system

private const val xyShieldingDistance =
    Shielding.SizeXY / 2 + Veto.FullThickness / 2 + Shielding.EnvelopeThickness

private const val zShieldingDistance =
    Shielding.SizeZ / 2 + Veto.FullThickness / 2 + Shielding.EnvelopeThickness

class VetoLayerTop(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(4).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerTop$i") {
                        position(
                            y = (Veto.FullThickness + 20) * (i - 1)
                        ) { unit = LUnit.MM }
                        rotation { unit = AUnit.DEG; y = 180 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerBottom(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(4).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerBottom$i") {
                        position(
                            y = -(Veto.FullThickness + 0) * (i - 1)
                        ) { unit = LUnit.MM }
                        rotation { unit = AUnit.DEG; y = 180 * (i + 1) }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerRight(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(4).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerRight$i") {
                        position(
                            x = -(Veto.FullThickness + 20) * (i - 1)
                        ) { unit = LUnit.MM }
                        rotation { unit = AUnit.DEG; x = -90; z = 90 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerLeft(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(4).generate(gdml)

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

        val vetoLayer = VetoLayer(4).generate(gdml)

        val vetoLayerVolume: GdmlRef<GdmlAssembly> by lazy {
            return@lazy gdml.structure.assembly {
                for (i in 1..numberOfLayers) {
                    physVolume(vetoLayer, name = "vetoLayerBack$i") {
                        position(
                            z = -(Veto.FullThickness + 20) * (i - 1),
                            y = 80
                        ) { unit = LUnit.MM }
                        rotation { unit = AUnit.DEG; x = -90 }
                    }
                }
            }
        }

        return vetoLayerVolume
    }
}

class VetoLayerFront(private val numberOfLayers: Int = 3) : Geometry() {
    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {

        val vetoLayer = VetoLayer(3, isFront = true).generate(gdml)

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
                        y = xyShieldingDistance + 20,
                        z = -Shielding.OffsetZ
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerBottom(numberOfLayers).generate(gdml), name = "vetoSystemBottom") {
                    position(
                        y = -xyShieldingDistance - 20,
                        z = -Shielding.OffsetZ
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerRight(numberOfLayers).generate(gdml), name = "vetoSystemRight") {
                    position(
                        x = -xyShieldingDistance - 130,
                        z = -Shielding.OffsetZ - 30
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerLeft(numberOfLayers).generate(gdml), name = "vetoSystemLeft") {
                    position(
                        x = xyShieldingDistance + 130,
                        z = -Shielding.OffsetZ
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerBack(numberOfLayers).generate(gdml), name = "vetoSystemBack") {
                    position(
                        z = -zShieldingDistance - Shielding.OffsetZ - 130,
                        y = 80
                    ) { unit = LUnit.MM }
                }
                physVolume(VetoLayerFront(numberOfLayers).generate(gdml), name = "vetoSystemFront") {
                    position(
                        z = -Shielding.OffsetZ + zShieldingDistance + 130
                    ) { unit = LUnit.MM }
                }
            }
        }

        return vetoSystemVolume
    }
}