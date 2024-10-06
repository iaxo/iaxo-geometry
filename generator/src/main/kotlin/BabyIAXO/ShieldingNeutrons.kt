package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class ShieldingNeutrons(
    open val innerVetoes: Boolean = false,
) : Geometry() {
    companion object Parameters {

        const val vetoOuterThickness: Double = 100.0
        const val vetoInnerThickness: Double = 50.0
        const val vetoInnerSpacing = 50.0
        const val shieldingThicknessBase: Double = 200.0

        const val shieldingThickness: Double = shieldingThicknessBase + vetoInnerThickness

        const val chamberHoleXY: Double = 200.0
        const val chamberHoleZ: Double = 150.0

        const val SizeXY: Double = chamberHoleXY + 2 * shieldingThickness
        const val SizeZ: Double = chamberHoleZ + 2 * shieldingThickness

        const val ShaftShortSideX: Double = 200.0
        const val ShaftShortSideY: Double = 200.0
        const val ShaftLongSide: Double = shieldingThickness + chamberHoleZ

        const val copperBoxThickness: Double = 10.0

        private const val DetectorToShieldingSeparation: Double = -60.0 + copperBoxThickness
        const val OffsetZ: Double =
            DetectorToShieldingSeparation + Chamber.Height / 2 + Chamber.ReadoutKaptonThickness + Chamber.BackplateThickness

        const val vetoInnerLength = shieldingThickness + chamberHoleZ + vetoInnerSpacing
    }

    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
        val shieldingVolume: GdmlRef<GdmlAssembly> by lazy {
            val vetoOuterBoxLateralSolid = gdml.solids.box(
                SizeXY + vetoOuterThickness,
                vetoOuterThickness,
                SizeZ + vetoOuterThickness,
                "vetoOuterBoxLateralSolid"
            )

            val vetoOuterBackBoxSolid = gdml.solids.box(
                SizeXY,
                SizeXY,
                vetoOuterThickness,
                "vetoOuterBoxSolid"
            )

            val vetoInnerBoxLateralSolid = gdml.solids.box(
                chamberHoleXY + 2 * vetoInnerSpacing + vetoInnerThickness,
                vetoInnerThickness,
                vetoInnerLength,
                "vetoInnerBoxLateralSolid"
            )

            val vetoOuterBoxLateralVolume =
                gdml.structure.volume(
                    Materials.EJ254_5pct.ref,
                    vetoOuterBoxLateralSolid,
                    "scintillatorVolumeOuterLateral"
                )

            val vetoOuterBoxBackVolume =
                gdml.structure.volume(Materials.EJ254_5pct.ref, vetoOuterBackBoxSolid, "scintillatorVolumeOuterBack")

            val vetoInnerBoxLateralVolume =
                gdml.structure.volume(
                    Materials.EJ254_5pct.ref,
                    vetoInnerBoxLateralSolid,
                    "scintillatorVolumeInnerLateral"
                )

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


            val leadShieldingVolume = if (innerVetoes) {
                val leadShieldingVolumeHoleInnerVetoes1 =
                    gdml.solids.subtraction(
                        leadBoxWithShaftSolid,
                        vetoInnerBoxLateralSolid,
                        "leadShieldingVolumeHoleInnerVetoes1"
                    ) {
                        position(
                            y = vetoInnerThickness / 2 + chamberHoleXY / 2 + vetoInnerSpacing,
                            x = vetoInnerThickness / 2,
                            z = -vetoInnerLength / 2 + SizeZ / 2
                        ) { unit = LUnit.MM }
                    }
                val leadShieldingVolumeHoleInnerVetoes2 =
                    gdml.solids.subtraction(
                        leadShieldingVolumeHoleInnerVetoes1,
                        vetoInnerBoxLateralSolid,
                        "leadShieldingVolumeHoleInnerVetoes2"
                    ) {
                        position(
                            y = -vetoInnerThickness / 2 - chamberHoleXY / 2 - vetoInnerSpacing,
                            x = -vetoInnerThickness / 2,
                            z = -vetoInnerLength / 2 + SizeZ / 2
                        ) { unit = LUnit.MM }
                    }
                val leadShieldingVolumeHoleInnerVetoes3 =
                    gdml.solids.subtraction(
                        leadShieldingVolumeHoleInnerVetoes2,
                        vetoInnerBoxLateralSolid,
                        "leadShieldingVolumeHoleInnerVetoes3"
                    ) {
                        rotation(z = 90.0) { unit = AUnit.DEGREE }
                        position(
                            x = -vetoInnerThickness / 2 - chamberHoleXY / 2 - vetoInnerSpacing,
                            y = vetoInnerThickness / 2,
                            z = -vetoInnerLength / 2 + SizeZ / 2
                        ) { unit = LUnit.MM }
                    }
                val leadShieldingVolumeHoleInnerVetoes4 =
                    gdml.solids.subtraction(
                        leadShieldingVolumeHoleInnerVetoes3,
                        vetoInnerBoxLateralSolid,
                        "leadShieldingVolumeHoleInnerVetoes4"
                    ) {
                        rotation(z = 90.0) { unit = AUnit.DEGREE }
                        position(
                            x = vetoInnerThickness / 2 + chamberHoleXY / 2 + vetoInnerSpacing,
                            y = -vetoInnerThickness / 2,
                            z = -vetoInnerLength / 2 + SizeZ / 2
                        ) { unit = LUnit.MM }
                    }

                gdml.structure.volume(Materials.Lead.ref, leadShieldingVolumeHoleInnerVetoes4, "shieldingVolume")
            } else {
                gdml.structure.volume(Materials.Lead.ref, leadBoxWithShaftSolid, "shieldingVolume")
            }

            return@lazy gdml.structure.assembly {
                name = "shielding"
                physVolume(leadShieldingVolume, name = "shieldingLead") {
                    position(z = -OffsetZ) { unit = LUnit.MM }
                }
                physVolume(copperBoxVolume, name = "copperBox") {
                    position(z = -OffsetZ + SizeZ / 2 - ShaftLongSide / 2) { unit = LUnit.MM }
                }

                physVolume(vetoOuterBoxBackVolume, name = "vetoOuterBack") {
                    position(z = -OffsetZ - SizeZ / 2 - vetoOuterThickness / 2) { unit = LUnit.MM }
                }
                physVolume(vetoOuterBoxLateralVolume, name = "scintillatorOuterTop") {
                    position(
                        y = SizeXY / 2 + vetoOuterThickness / 2,
                        x = vetoOuterThickness / 2,
                        z = -OffsetZ - vetoOuterThickness / 2
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(vetoOuterBoxLateralVolume, name = "scintillatorOuterBottom") {
                    position(
                        y = -SizeXY / 2 - vetoOuterThickness / 2,
                        x = -vetoOuterThickness / 2,
                        z = -OffsetZ - vetoOuterThickness / 2
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(vetoOuterBoxLateralVolume, name = "scintillatorOuterRight") {
                    rotation(z = 90.0) { unit = AUnit.DEGREE }
                    position(
                        x = -SizeXY / 2 - vetoOuterThickness / 2,
                        y = vetoOuterThickness / 2,
                        z = -OffsetZ - vetoOuterThickness / 2
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(vetoOuterBoxLateralVolume, name = "scintillatorOuterLeft") {
                    rotation(z = 90.0) { unit = AUnit.DEGREE }
                    position(
                        x = SizeXY / 2 + vetoOuterThickness / 2,
                        y = -vetoOuterThickness / 2,
                        z = -OffsetZ - vetoOuterThickness / 2
                    ) {
                        unit = LUnit.MM
                    }
                }

                if (innerVetoes) {
                    physVolume(vetoInnerBoxLateralVolume, name = "vetoInnerTop") {
                        position(
                            y = vetoInnerThickness / 2 + chamberHoleXY / 2 + vetoInnerSpacing,
                            x = vetoInnerThickness / 2,
                            z = -OffsetZ - vetoInnerLength / 2 + SizeZ / 2
                        ) {
                            unit = LUnit.MM
                        }
                    }
                    physVolume(vetoInnerBoxLateralVolume, name = "vetoInnerBottom") {
                        position(
                            y = -vetoInnerThickness / 2 - chamberHoleXY / 2 - vetoInnerSpacing,
                            x = -vetoInnerThickness / 2,
                            z = -OffsetZ - vetoInnerLength / 2 + SizeZ / 2
                        ) {
                            unit = LUnit.MM
                        }
                    }
                    physVolume(vetoInnerBoxLateralVolume, name = "vetoInnerRight") {
                        rotation(z = 90.0) { unit = AUnit.DEGREE }
                        position(
                            x = -vetoInnerThickness / 2 - chamberHoleXY / 2 - vetoInnerSpacing,
                            y = vetoInnerThickness / 2,
                            z = -OffsetZ - vetoInnerLength / 2 + SizeZ / 2
                        ) {
                            unit = LUnit.MM
                        }
                    }
                    physVolume(vetoInnerBoxLateralVolume, name = "vetoInnerLeft") {
                        rotation(z = 90.0) { unit = AUnit.DEGREE }
                        position(
                            x = vetoInnerThickness / 2 + chamberHoleXY / 2 + vetoInnerSpacing,
                            y = -vetoInnerThickness / 2,
                            z = -OffsetZ - vetoInnerLength / 2 + SizeZ / 2
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
