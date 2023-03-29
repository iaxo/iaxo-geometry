package `IAXO-LiquidO`

import Geometry

import space.kscience.gdml.*

open class Shielding : Geometry() {
    companion object Parameters {
        const val FirstLayerLeadThickness: Double = 5.0
        const val HoleSideXY: Double = 150.0
        const val HoleSideZ: Double = 150.0
        const val EntryHoleSideXY: Double = 100.0

        const val LiquidVetoClearance: Double = 10.0
        const val LiquidVetoThickness: Double = 600.0
    }

    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
        val shieldingVolume: GdmlRef<GdmlAssembly> by lazy {
            val leadBoxSolid =
                gdml.solids.box(
                    HoleSideXY + 2.0 * FirstLayerLeadThickness,
                    HoleSideXY + 2.0 * FirstLayerLeadThickness,
                    HoleSideZ + 2 * FirstLayerLeadThickness,
                    "leadBoxSolid"
                )
            val leadBoxHoleSolid =
                gdml.solids.box(
                    HoleSideXY,
                    HoleSideXY,
                    HoleSideZ,
                    "leadBoxHoleSolid"
                )
            val leadBoxWithHoleSolid =
                gdml.solids.subtraction(leadBoxSolid, leadBoxHoleSolid, "leadBoxWithHoleSolid")

            val leadBoxEntryHole =
                gdml.solids.box(
                    EntryHoleSideXY,
                    EntryHoleSideXY,
                    FirstLayerLeadThickness * 2,
                    "leadBoxEntryHole"
                )

            val leadBoxWithHoleAndEntrySolid =
                gdml.solids.subtraction(leadBoxWithHoleSolid, leadBoxEntryHole, "leadBoxWithHoleAndEntrySolid") {
                    position(z = HoleSideZ / 2 + FirstLayerLeadThickness / 2) { unit = LUnit.MM }
                }

            val leadShieldingVolume =
                gdml.structure.volume(Materials.Lead.ref, leadBoxWithHoleAndEntrySolid, "shieldingVolume")

            // veto

            val liquidVetoBox =
                gdml.solids.box(
                    HoleSideXY + 2.0 * FirstLayerLeadThickness + LiquidVetoClearance * 2 + LiquidVetoThickness * 2,
                    HoleSideXY + 2.0 * FirstLayerLeadThickness + LiquidVetoClearance * 2 + LiquidVetoThickness * 2,
                    HoleSideZ + 2.0 * FirstLayerLeadThickness + LiquidVetoClearance * 2 + LiquidVetoThickness * 2,
                    "liquidVetoBox"
                )
            val liquidVetoHoleBox =
                gdml.solids.box(
                    HoleSideXY + 2.0 * FirstLayerLeadThickness,
                    HoleSideXY + 2.0 * FirstLayerLeadThickness,
                    HoleSideZ + 2.0 * FirstLayerLeadThickness,
                    "liquidVetoHoleBox"
                )
            val liquidVetoHoleSolid =
                gdml.solids.subtraction(liquidVetoBox, liquidVetoHoleBox, "liquidVetoHoleSolid")

            val liquidVetoEntryHole =
                gdml.solids.box(
                    HoleSideXY + LiquidVetoClearance * 2 + FirstLayerLeadThickness * 2,
                    HoleSideXY + LiquidVetoClearance * 2 + FirstLayerLeadThickness * 2,
                    LiquidVetoThickness + HoleSideZ + FirstLayerLeadThickness * 6,
                    "liquidVetoEntryHole"
                )

            val liquidVetoHoleAndEntry =
                gdml.solids.subtraction(liquidVetoHoleSolid, liquidVetoEntryHole, "liquidVetoHoleAndEntry") {
                    position(z = +LiquidVetoThickness / 2 + FirstLayerLeadThickness / 2) {
                        unit = LUnit.MM
                    }
                }

            // add a new material


            val liquidVetoVolume =
                gdml.structure.volume(Materials.BC408.ref, liquidVetoHoleAndEntry, "liquidVetoVolume")

            return@lazy gdml.structure.assembly {
                name = "vetoSystem"
                physVolume(leadShieldingVolume, name = "innerShielding") {
                    position(z = 0.0) { unit = LUnit.MM }
                }
                physVolume(liquidVetoVolume, name = "liquidVeto") {
                    position(z = 0.0) { unit = LUnit.MM }
                }
            }
        }

        return shieldingVolume
    }
}
