package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class Shielding(open val layers: Boolean = false, open val copperBox: Boolean = true) : Geometry() {
    companion object Parameters {
        const val SizeXY: Double = 600.0
        const val SizeZ: Double = 550.0

        const val ShaftShortSideX: Double = 200.0
        const val ShaftShortSideY: Double = 200.0
        const val ShaftLongSide: Double = 350.0

        const val copperBoxThickness: Double = 10.0

        const val MultiLayerThickness: Double = 10.0

        private const val DetectorToShieldingSeparation: Double = -60.0 + copperBoxThickness
        const val EnvelopeThickness: Double = 10.0
        const val OffsetZ: Double =
            DetectorToShieldingSeparation + Chamber.Height / 2 + Chamber.ReadoutKaptonThickness + Chamber.BackplateThickness
    }

    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
        if (!layers) {
            val shieldingVolume: GdmlRef<GdmlAssembly> by lazy {
                val copperBoxOutterSolid =
                    gdml.solids.box(
                        ShaftShortSideX,
                        ShaftShortSideY,
                        ShaftLongSide,
                        "copperBoxOutterSolid"
                    )
                val copperBoxInnerSolid =
                    gdml.solids.box(
                        ShaftShortSideX - 2 * copperBoxThickness,
                        ShaftShortSideY - 5 * copperBoxThickness,
                        ShaftLongSide,
                        "copperBoxInnerSolid"
                    )
                val copperBoxSolid =
                    gdml.solids.subtraction(copperBoxOutterSolid, copperBoxInnerSolid, "copperBoxSolid") {
                        position(z = copperBoxThickness) { unit = LUnit.MM }
                    }
                val copperBoxVolume =
                    gdml.structure.volume(Materials.Copper.ref, copperBoxSolid, "copperBoxVolume")

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
                    physVolume(copperBoxVolume, name = "copperBox") {
                        position(z = -OffsetZ + SizeZ / 2 - ShaftLongSide / 2) { unit = LUnit.MM }
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

                var shieldingLayerLastNullable: GdmlRef<GdmlVolume>? = null;
                // last layer
                if (ShaftShortSideX + 2 * MultiLayerThickness * numberOfLayers < SizeXY || ShaftShortSideX + 2 * MultiLayerThickness * numberOfLayers < SizeXY || ShaftLongSide + MultiLayerThickness * numberOfLayers < SizeZ) {
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
                    shieldingLayerLastNullable =
                        gdml.structure.volume(Materials.Lead.ref, leadBoxWithShaftSolid, "shieldingVolumeLayerLast")
                }

                val copperBoxOutterSolid =
                    gdml.solids.box(
                        ShaftShortSideX,
                        ShaftShortSideY,
                        ShaftLongSide,
                        "copperBoxOutterSolid"
                    )
                val copperBoxInnerSolid =
                    gdml.solids.box(
                        ShaftShortSideX - 2 * copperBoxThickness,
                        ShaftShortSideY - 5 * copperBoxThickness,
                        ShaftLongSide,
                        "copperBoxInnerSolid"
                    )
                val copperBoxSolid =
                    gdml.solids.subtraction(copperBoxOutterSolid, copperBoxInnerSolid, "copperBoxSolid") {
                        position(z = copperBoxThickness) { unit = LUnit.MM }
                    }
                val copperBoxVolume =
                    gdml.structure.volume(Materials.Copper.ref, copperBoxSolid, "copperBoxVolume")

                return@lazy gdml.structure.assembly {
                    name = "shielding"

                    if (copperBox) {
                        physVolume(copperBoxVolume, name = "copperBox") {
                            position(z = -OffsetZ + SizeZ / 2 - ShaftLongSide / 2) { unit = LUnit.MM }
                        }
                    }

                    if (shieldingLayerLastNullable != null) {
                        physVolume(shieldingLayerLastNullable, name = "shieldingLayerLast") {
                            position(z = -OffsetZ, x = 0) { unit = LUnit.MM }
                        }
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