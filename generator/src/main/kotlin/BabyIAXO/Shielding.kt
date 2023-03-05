package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class Shielding(open val layers: Boolean = false) : Geometry() {
    companion object Parameters {
        const val SizeXY: Double = 590.0
        const val SizeZ: Double = 540.0

        const val ShaftShortSideX: Double = 194.0
        const val ShaftShortSideY: Double = 170.0
        const val ShaftLongSide: Double = 340.0

        const val MultiLayerThickness: Double = 40.0

        private const val DetectorToShieldingSeparation: Double = -60.0
        const val EnvelopeThickness: Double = 10.0
        const val OffsetZ: Double =
            DetectorToShieldingSeparation + Chamber.Height / 2 + Chamber.ReadoutKaptonThickness + Chamber.BackplateThickness
    }

    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
        if (!layers) {
            val shieldingVolume: GdmlRef<GdmlAssembly> by lazy {
                val leadBoxSolid =
                    gdml.solids.box(SizeXY, SizeXY, SizeZ, "leadBoxSolid")
                val leadBoxShaftSolid =
                    gdml.solids.box(
                        ShaftShortSideX,
                        ShaftShortSideY,
                        ShaftLongSide,
                        "leadBoxShaftSolid"
                    )
                val leadBoxWithShaftSolid =
                    gdml.solids.subtraction(leadBoxSolid, leadBoxShaftSolid, "leadBoxWithShaftSolid") {
                        position(z = SizeZ / 2 - ShaftLongSide / 2) { unit = LUnit.MM }
                    }
                val leadShieldingVolume =
                    gdml.structure.volume(Materials.Lead.ref, leadBoxWithShaftSolid, "shieldingVolume")

                return@lazy gdml.structure.assembly {
                    name = "shielding"
                    physVolume(leadShieldingVolume, name = "shielding20cm") {
                        position(z = -OffsetZ) { unit = LUnit.MM }
                    }
                }
            }

            return shieldingVolume
        } else {
            val shieldingVolume: GdmlRef<GdmlAssembly> by lazy {

                val minShieldingThickness =
                    minOf((SizeXY - ShaftShortSideX) / 2, (SizeXY - ShaftShortSideY) / 2, SizeZ - ShaftLongSide)
                // print minShieldingThickness
                val numberOfLayers = (minShieldingThickness / MultiLayerThickness).toInt()
                // print numberOfLayers

                // empty container of volumes
                val shieldingLayerList = mutableListOf<GdmlRef<GdmlVolume>>()
                for (i in 1..numberOfLayers) {
                    val shieldingLayerBox =
                        gdml.solids.box(
                            ShaftShortSideX + 2 * MultiLayerThickness * i,
                            ShaftShortSideY + 2 * MultiLayerThickness * i,
                            ShaftLongSide + MultiLayerThickness * i,
                            "shieldingLayerBox$i"
                        )

                    val shieldingLayerHole =
                        gdml.solids.box(
                            ShaftShortSideX + 2 * MultiLayerThickness * (i - 1),
                            ShaftShortSideY + 2 * MultiLayerThickness * (i - 1),
                            ShaftLongSide + MultiLayerThickness * (i - 1),
                            "shieldingLayerHole$i"
                        )

                    val shieldingLayerBoxWithHole =
                        gdml.solids.subtraction(shieldingLayerBox, shieldingLayerHole, "shieldingLayerBoxWithHole$i") {
                            position(z = MultiLayerThickness * 0.5) { unit = LUnit.MM }
                        }
                    val shieldingLayerBoxWithHoleVolume =
                        gdml.structure.volume(Materials.Lead.ref, shieldingLayerBoxWithHole, "shieldingVolumeLayer$i")

                    shieldingLayerList.add(shieldingLayerBoxWithHoleVolume)
                }

                // last layer
                val leadBoxLastLayerSolid =
                    gdml.solids.box(SizeXY, SizeXY, SizeZ, "leadBoxSolid")
                val leadBoxLastLayerShaftSolid =
                    gdml.solids.box(
                        ShaftShortSideX + 2 * MultiLayerThickness * numberOfLayers,
                        ShaftShortSideY + 2 * MultiLayerThickness * numberOfLayers,
                        ShaftLongSide + MultiLayerThickness * numberOfLayers,
                        "leadBoxShaftSolid"
                    )
                val leadBoxWithShaftSolid =
                    gdml.solids.subtraction(
                        leadBoxLastLayerSolid,
                        leadBoxLastLayerShaftSolid,
                        "leadBoxWithShaftSolid"
                    ) {
                        position(z = SizeZ / 2 - (ShaftLongSide + MultiLayerThickness * numberOfLayers) / 2) {
                            unit = LUnit.MM
                        }
                    }
                val shieldingLayerLast =
                    gdml.structure.volume(Materials.Lead.ref, leadBoxWithShaftSolid, "shieldingVolumeLayerLast")


                return@lazy gdml.structure.assembly {
                    name = "shielding"
                    physVolume(shieldingLayerLast, name = "shieldingLayerLast") {
                        position(z = -OffsetZ, x = 0) { unit = LUnit.MM }
                    }

                    for ((index, value) in shieldingLayerList.withIndex()) {
                        physVolume(value, name = "shieldingLayer${index + 1}") {
                            position(
                                z = -OffsetZ - MultiLayerThickness * index / 2 + (SizeZ - ShaftLongSide - MultiLayerThickness) / 2,//-OffsetZ+ 2 * MultiLayerThickness  ,
                            ) {
                                unit = LUnit.MM
                            }
                        }
                    }
                }
            }

            return shieldingVolume
        }
    }
}