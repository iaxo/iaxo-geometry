package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class Shielding : Geometry() {
    companion object Parameters {
        const val SizeXY: Double = 590.0
        const val SizeZ: Double = 540.0

        const val ShaftShortSideX: Double = 194.0
        const val ShaftShortSideY: Double = 170.0
        const val ShaftLongSide: Double = 340.0

        private const val DetectorToShieldingSeparation: Double = -60.0
        const val EnvelopeThickness: Double = 10.0
        const val OffsetZ: Double =
            DetectorToShieldingSeparation + Chamber.Height / 2 + Chamber.ReadoutKaptonThickness + Chamber.BackplateThickness
    }

    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
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
                gdml.structure.volume(Materials.Lead.ref, leadBoxWithShaftSolid, "ShieldingVolume")

            return@lazy gdml.structure.assembly {
                name = "Shielding"
                physVolume(leadShieldingVolume, name = "shielding20cm") {
                    position(z = -OffsetZ) { unit = LUnit.MM }
                }
            }
        }

        return shieldingVolume
    }
}