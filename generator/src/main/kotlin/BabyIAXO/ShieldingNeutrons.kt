package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class ShieldingNeutrons(
    open val copperBox: Boolean = true,
) : Geometry() {
    companion object Parameters {

        const val vetoOuterThickness: Double = 100.0
        const val vetoInnerThickness: Double = 50.0

        const val shieldingThickness: Double = 200.0 + vetoInnerThickness
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

        val vetoInnerSpacing = 50.0
        val vetoInnerLength = shieldingThickness + chamberHoleZ + vetoInnerSpacing
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
                gdml.structure.volume(Materials.EJ254_5pct.ref, vetoOuterBoxLateralSolid, "vetoOuterBoxLateralVolume")

            val vetoOuterBoxBackVolume =
                gdml.structure.volume(Materials.EJ254_5pct.ref, vetoOuterBackBoxSolid, "vetoOuterBoxBackVolume")

            val vetoInnerBoxLateralVolume =
                gdml.structure.volume(Materials.EJ254_5pct.ref, vetoInnerBoxLateralSolid, "vetoInnerBoxLateralVolume")

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

            val leadShieldingVolume =
                gdml.structure.volume(Materials.Lead.ref, leadShieldingVolumeHoleInnerVetoes4, "shieldingVolume")

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
                physVolume(vetoOuterBoxLateralVolume, name = "vetoOuterLateralTop") {
                    position(
                        y = SizeXY / 2 + vetoOuterThickness / 2,
                        x = vetoOuterThickness / 2,
                        z = -OffsetZ - vetoOuterThickness / 2
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(vetoOuterBoxLateralVolume, name = "vetoOuterLateralBottom") {
                    position(
                        y = -SizeXY / 2 - vetoOuterThickness / 2,
                        x = -vetoOuterThickness / 2,
                        z = -OffsetZ - vetoOuterThickness / 2
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(vetoOuterBoxLateralVolume, name = "vetoOuterLateralRight") {
                    rotation(z = 90.0) { unit = AUnit.DEGREE }
                    position(
                        x = -SizeXY / 2 - vetoOuterThickness / 2,
                        y = vetoOuterThickness / 2,
                        z = -OffsetZ - vetoOuterThickness / 2
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(vetoOuterBoxLateralVolume, name = "vetoOuterLateralLeft") {
                    rotation(z = 90.0) { unit = AUnit.DEGREE }
                    position(
                        x = SizeXY / 2 + vetoOuterThickness / 2,
                        y = -vetoOuterThickness / 2,
                        z = -OffsetZ - vetoOuterThickness / 2
                    ) {
                        unit = LUnit.MM
                    }
                }

                // inner veto
                physVolume(vetoInnerBoxLateralVolume, name = "vetoInnerLateralTop") {
                    position(
                        y = vetoInnerThickness / 2 + chamberHoleXY / 2 + vetoInnerSpacing,
                        x = vetoInnerThickness / 2,
                        z = -OffsetZ - vetoInnerLength / 2 + SizeZ / 2
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(vetoInnerBoxLateralVolume, name = "vetoInnerLateralBottom") {
                    position(
                        y = -vetoInnerThickness / 2 - chamberHoleXY / 2 - vetoInnerSpacing,
                        x = -vetoInnerThickness / 2,
                        z = -OffsetZ - vetoInnerLength / 2 + SizeZ / 2
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(vetoInnerBoxLateralVolume, name = "vetoInnerLateralRight") {
                    rotation(z = 90.0) { unit = AUnit.DEGREE }
                    position(
                        x = -vetoInnerThickness / 2 - chamberHoleXY / 2 - vetoInnerSpacing,
                        y = vetoInnerThickness / 2,
                        z = -OffsetZ - vetoInnerLength / 2 + SizeZ / 2
                    ) {
                        unit = LUnit.MM
                    }
                }
                physVolume(vetoInnerBoxLateralVolume, name = "vetoInnerLateralLeft") {
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

        return shieldingVolume
    }
}
