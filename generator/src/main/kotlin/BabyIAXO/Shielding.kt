package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class Shielding(
    open val layers: Boolean = false,
    open val copperBox: Boolean = true,
    open val hdpeNeutronShieldingDiameter: Double = 20.0,
) : Geometry() {
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
                val copperBoxOuterSolid =
                    gdml.solids.box(
                        ShaftShortSideX,
                        ShaftShortSideY,
                        ShaftLongSide,
                        "copperBoxOuterSolid"
                    )
                val copperBoxInnerSolid =
                    gdml.solids.box(
                        ShaftShortSideX - 2 * copperBoxThickness,
                        ShaftShortSideY - 5 * copperBoxThickness,
                        ShaftLongSide,
                        "copperBoxInnerSolid"
                    )
                val copperBoxSolid =
                    gdml.solids.subtraction(copperBoxOuterSolid, copperBoxInnerSolid, "copperBoxSolid") {
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

                var neutronShieldingRodSolidShielding: GdmlRef<GdmlSolid>? = null

                val counts = 14
                val spacing = 600.0 / counts
                val hdpeNeutronShielding = hdpeNeutronShieldingDiameter > 0.0
                val leadShieldingVolume = if (hdpeNeutronShielding) {

                    val neutronShieldingRodSolid =
                        gdml.solids.tube(
                            hdpeNeutronShieldingDiameter * 0.5,
                            SizeZ,
                            "neutronShieldingRodSolid"
                        )

                    neutronShieldingRodSolidShielding =
                        gdml.solids.union(
                            neutronShieldingRodSolid,
                            neutronShieldingRodSolid,
                            "shieldingNeutronRodX0Y0"
                        )

                    // do a grid over 600x600 in 50x50 squares
                    var leadBoxWithNeutronShieldingSolid = leadBoxWithShaftSolid

                    for (i in 1 until counts) {
                        for (j in 1 until counts) {
                            // if x or y is in the range of -100 to 100, skip
                            val x = -300.0 + i * spacing
                            val y = -300.0 + j * spacing

                            if (x > -100 && x < 100 && y > -100 && y < 100) {
                                continue
                            }

                            neutronShieldingRodSolidShielding =
                                neutronShieldingRodSolidShielding?.let {
                                    gdml.solids.union(
                                        it,
                                        neutronShieldingRodSolid,
                                        "shieldingNeutronRodX${i}Y${j}"
                                    ) {
                                        position(x = (i - 1) * spacing, y = (j - 1) * spacing) { unit = LUnit.MM }
                                    }
                                }


                            leadBoxWithNeutronShieldingSolid =
                                gdml.solids.subtraction(
                                    leadBoxWithNeutronShieldingSolid,
                                    neutronShieldingRodSolid,
                                    "leadBoxWithNeutronShieldingSolidX${i}Y${j}"
                                ) {
                                    position(x = x, y = y) { unit = LUnit.MM }
                                }
                        }
                    }

                    gdml.structure.volume(Materials.Lead.ref, leadBoxWithNeutronShieldingSolid, "shieldingVolume")
                } else {
                    gdml.structure.volume(Materials.Lead.ref, leadBoxWithShaftSolid, "shieldingVolume")
                }

                return@lazy gdml.structure.assembly {
                    name = "shielding"
                    physVolume(leadShieldingVolume, name = "shielding20cm") {
                        position(z = -OffsetZ) { unit = LUnit.MM }
                    }
                    physVolume(copperBoxVolume, name = "copperBox") {
                        position(z = -OffsetZ + SizeZ / 2 - ShaftLongSide / 2) { unit = LUnit.MM }
                    }
                    if (hdpeNeutronShielding) {
                        neutronShieldingRodSolidShielding?.let {
                            physVolume(
                                gdml.structure.volume(
                                    Materials.BoratedHDPE5pct.ref,
                                    neutronShieldingRodSolidShielding,
                                    "neutronShieldingRods"
                                ), name = "neutronShieldingRods"
                            ) {
                                position(x = -300 + spacing, y = -300 + spacing, z = -OffsetZ) { unit = LUnit.MM }
                            }
                        }
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

                val copperBoxOuterSolid =
                    gdml.solids.box(
                        ShaftShortSideX,
                        ShaftShortSideY,
                        ShaftLongSide,
                        "copperBoxOuterSolid"
                    )
                val copperBoxInnerSolid =
                    gdml.solids.box(
                        ShaftShortSideX - 2 * copperBoxThickness,
                        ShaftShortSideY - 5 * copperBoxThickness,
                        ShaftLongSide,
                        "copperBoxInnerSolid"
                    )
                val copperBoxSolid =
                    gdml.solids.subtraction(copperBoxOuterSolid, copperBoxInnerSolid, "copperBoxSolid") {
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