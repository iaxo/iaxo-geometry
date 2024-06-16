package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class Electronics : Geometry() {

    companion object Parameters {

        // Box : Dimensions 
        const val electronicsBoxLongSideZ: Double = 200.0
        const val electronicsBoxShortSideX: Double = 40.0
        const val electronicsBoxShortSideY: Double = 90.0
        const val electronicsBoxThickness: Double = 5.0

        // Distance 
        const val DetectorToElectronicsDistanceZ: Double = 245.0
        const val PipeToElectronicsOffSetX: Double = 5.0
        const val PipeToElectronicsDistanceX: Double = 82.0 - (electronicsBoxShortSideX / 2) + PipeToElectronicsOffSetS

    }

    override fun generate (gdml : Gdml): GdmlRef<GdmlAssembly> {
        val electronicsBoxVolume: GdmlRef<GdmlAssembly> by lazy {

            val electronicsBoxOutSolid = gdml.solids.box(
                electronicsBoxShortSideX,
                electronicsBoxShortSideY,
                electronicsBoxLongSideZ,
                "electronicsBoxOutSolid"
            )

            val electronicsBoxInSolid = gdml.solids.box(
                electronicsBoxShortSideX - electronicsBoxThickness*2,
                electronicsBoxShortSideY - electronicsBoxThickness*2,
                electronicsBoxLongSideZ - electronicsBoxThickness*2,
                "electronicsBoxInSolid"
            )

            val electronicsBoxSolid = gdml.solids.subtraction(
                electronicsBoxOutSolid, electronicsBoxInSolid,
                "electronicsBoxSolid")

            val electronicsBoxVolume = 
            gdml.structure.volume(Materials.Copper.ref,electronicsBoxSolid,"electronicsBoxVolume")

            val electronicsBoxFillingVolume =
            gdml.structure.volume(Materials.Vacuum.ref,electronicsBoxInSolid,"electronicsBoxFillingVolume")

            return@lazy gdml.structure.assembly {
                name = "electronicsBox"
                physVolume(electronicsBoxVolume){
                    name = "electronicsBox"
                }
                physVolume(electronicsBoxFillingVolume){
                    name = "electronicsBoxFilling"
                }
            }
        }
        
        return electronicsBoxVolume
    }
}