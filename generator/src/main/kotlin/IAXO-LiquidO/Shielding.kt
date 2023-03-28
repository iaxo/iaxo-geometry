package `IAXO-LiquidO`

import Geometry

import space.kscience.gdml.*

open class Shielding : Geometry() {
    companion object Parameters {
        const val FirstLayerLeadThickness: Double = 5.0
        const val HoleSideXY: Double = 170.0
        const val HoleSideZ: Double = 300.0
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
            val leadShieldingVolume =
                gdml.structure.volume(Materials.Lead.ref, leadBoxWithHoleSolid, "shieldingVolume")

            return@lazy gdml.structure.assembly {
                name = "shielding"
                physVolume(leadShieldingVolume, name = "shielding20cm") {
                    position(z = 0.0) { unit = LUnit.MM }
                }
            }
        }
        return shieldingVolume
    }
}
