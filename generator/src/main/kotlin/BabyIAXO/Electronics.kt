package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class Electronics (    
    open val detailedElectronics: Boolean = false,
) : Geometry() {
    companion object Parameters {

        // Box : Dimensions 
        const val electronicsBoxLongSideZ: Double = 200.0
        const val electronicsBoxShortSideX: Double = 41.0
        const val electronicsBoxShortSideY: Double = 90.0
        const val electronicsBoxThickness: Double = 5.0

        //Card (SAFEC) Dimensions
        const val SAFECLongSideZ: Double = 226.94
        const val SAFECShortSideX: Double = 0.62
        const val SAFECShortSideY: Double = 102.6

        //flat cable (SAFEC_MM) Dimensions
        const val SAFEC_MMLongSideZ: Double = 141.0 //had to reduce 20.0 mm to avoid overlap with pipe
        const val SAFEC_MMShortSideX: Double = 0.2
        const val SAFEC_MMShortSideY: Double = 102.25

        //chip Dimensions
        const val chipSideYZ: Double = 28.0
        const val chipShortSideX: Double = 1.0

        //components Dimensions
        const val componentsSideZ: Double = 9.3
        const val componentsSideX: Double = 1.0
        const val componentsSideY: Double = 76.2

        // Distance 
        const val SAFECLimandeContactDistance: Double = 55.2
        const val DetectorToElectronicsDistanceZ: Double = 235.0
        const val PipeToElectronicsOffSetX: Double = 5.0
        const val PipeToElectronicsDistanceX: Double = 82.0 - (electronicsBoxShortSideX / 2) + PipeToElectronicsOffSetX

    }

    override fun generate (gdml : Gdml): GdmlRef<GdmlAssembly> {

        if(!detailedElectronics){

            val electronicsBoxVolume: GdmlRef<GdmlAssembly> by lazy {

            //Box 
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

            //Card
            val SAFECSolid = gdml.solids.box(
                SAFECShortSideX/2,
                SAFECShortSideY,
                SAFECLongSideZ,
                "SAFECSolid")

            //Flat cable
            val SAFEC_MMSolid = gdml.solids.box(
                SAFEC_MMShortSideX/2,
                SAFEC_MMShortSideY,
                SAFEC_MMLongSideZ,
                "SAFEC_MMSolid")

            val electronicsBoxVolume = 
            gdml.structure.volume(Materials.Copper.ref,electronicsBoxSolid,"electronicsBoxVolume")

            val electronicsBoxFillingVolume =
            gdml.structure.volume(Materials.Vacuum.ref,electronicsBoxInSolid,"electronicsBoxFillingVolume")

            val kaptonSAFECVolume = 
            gdml.structure.volume(Materials.Kapton.ref,SAFECSolid,"kaptonSAFECVolume")
            val copperSAFECVolume = 
            gdml.structure.volume(Materials.Copper.ref,SAFECSolid,"copperSAFECVolume")

            val copperSAFEC_MMVolume =
            gdml.structure.volume(Materials.Copper.ref,SAFEC_MMSolid,"copperSAFEC_MMVolume")
            val kaptonSAFEC_MMVolume =
            gdml.structure.volume(Materials.Kapton.ref,SAFEC_MMSolid,"kaptonSAFEC_MMVolume")

            return@lazy gdml.structure.assembly {
                name = "electronicsBox"
                //physVolume(electronicsBoxVolume, name = "electronicsBox")
                //physVolume(electronicsBoxFillingVolume, name = "electronicsBoxFilling")

                val positionsX = listOf(-15.0, -5.0, 5.0, 15.0)

                //SAFEC: copperSAFEC{i} + kaptonSAFEC{i}
                //SAFEC_MM: copperSAFEC_MM{i} + kaptonSAFEC_MM{i}
                for (i in positionsX.indices) {
                    val posX = positionsX[i]
                    val copperName = "copperSAFEC${i + 1}"
                    val kaptonName = "kaptonSAFEC${i + 1}"
                    val coppersafecMMName = "copperSAFEC_MM${i + 1}"
                    val kaptonsafecMMName = "kaptonSAFEC_MM${i + 1}"

                    physVolume(copperSAFECVolume, name = copperName) {
                        position(x = posX - SAFECShortSideX / 4) { unit = LUnit.MM }
                    }

                    physVolume(kaptonSAFECVolume, name = kaptonName) {
                        position(x = posX + SAFECShortSideX / 4) { unit = LUnit.MM }
                    }

                    physVolume(copperSAFEC_MMVolume, name = coppersafecMMName) {
                        position(
                            x = posX + SAFECShortSideX / 2 + SAFEC_MMShortSideX / 4,
                            z = -SAFECLongSideZ / 2 - SAFEC_MMLongSideZ / 2 + SAFECLimandeContactDistance
                        ) { unit = LUnit.MM }
                    }

                    physVolume(kaptonSAFEC_MMVolume, name = kaptonsafecMMName) {
                        position(
                            x = posX + SAFECShortSideX / 2 + 3*SAFEC_MMShortSideX / 4,
                            z = -SAFECLongSideZ / 2 - SAFEC_MMLongSideZ / 2 + SAFECLimandeContactDistance
                        ) { unit = LUnit.MM }
                    }
                }
                }
            }
        
            return electronicsBoxVolume
            
        } else {
            val electronicsBoxVolume: GdmlRef<GdmlAssembly> by lazy {

            //Box 
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

            //Card
            val SAFECSolid = gdml.solids.box(
                SAFECShortSideX/2,
                SAFECShortSideY,
                SAFECLongSideZ,
                "SAFECSolid")

            //Flat cable
            val SAFEC_MMSolid = gdml.solids.box(
                SAFEC_MMShortSideX/2,
                SAFEC_MMShortSideY,
                SAFEC_MMLongSideZ,
                "SAFEC_MMSolid")

            //chip
            val chipSolid = gdml.solids.box(
                chipShortSideX,
                chipSideYZ,
                chipSideYZ,
                "chipSolid")

            //components
            val componentsSolid = gdml.solids.box(
                componentsSideX,
                componentsSideY,
                componentsSideZ,
                "componentsSolid")

            val electronicsBoxVolume = 
            gdml.structure.volume(Materials.Copper.ref,electronicsBoxSolid,"electronicsBoxVolume")

            val electronicsBoxFillingVolume =
            gdml.structure.volume(Materials.Vacuum.ref,electronicsBoxInSolid,"electronicsBoxFillingVolume")

            val kaptonSAFECVolume = 
            gdml.structure.volume(Materials.Kapton.ref,SAFECSolid,"kaptonSAFECVolume")
            val copperSAFECVolume = 
            gdml.structure.volume(Materials.Copper.ref,SAFECSolid,"copperSAFECVolume")

            val copperSAFEC_MMVolume =
            gdml.structure.volume(Materials.Copper.ref,SAFEC_MMSolid,"copperSAFEC_MMVolume")
            val kaptonSAFEC_MMVolume =
            gdml.structure.volume(Materials.Kapton.ref,SAFEC_MMSolid,"kaptonSAFEC_MMVolume")

            val chipVolume =
            gdml.structure.volume(Materials.Silicon.ref,chipSolid,"chipVolume")

            val componentsVolume =
            gdml.structure.volume(Materials.Silicon.ref,componentsSolid,"componentsVolume")

            return@lazy gdml.structure.assembly {
                name = "electronicsBox"
                //physVolume(electronicsBoxVolume, name = "electronicsBox")
                //physVolume(electronicsBoxFillingVolume, name = "electronicsBoxFilling")

                val positionsX = listOf(-15.0, -5.0, 5.0, 15.0)
                //SAFEC: copperSAFEC{i} + kaptonSAFEC{i}
                //SAFEC_MM: copperSAFEC_MM{i} + kaptonSAFEC_MM{i}
                for (i in positionsX.indices) {
                    val posX = positionsX[i]
                    val copperName = "copperSAFEC${i + 1}"
                    val kaptonName = "kaptonSAFEC${i + 1}"
                    val coppersafecMMName = "copperSAFEC_MM${i + 1}"
                    val kaptonsafecMMName = "kaptonSAFEC_MM${i + 1}"
                    val chipName = "chip${i + 1}"
                    val componentsName = "components${i + 1}"

                    physVolume(copperSAFECVolume, name = copperName) {
                        position(x = posX - SAFECShortSideX / 4) { unit = LUnit.MM }
                    }

                    physVolume(kaptonSAFECVolume, name = kaptonName) {
                        position(x = posX + SAFECShortSideX / 4) { unit = LUnit.MM }
                    }

                    physVolume(copperSAFEC_MMVolume, name = coppersafecMMName) {
                        position(
                            x = posX + SAFECShortSideX / 2 + SAFEC_MMShortSideX / 4,
                            z = -SAFECLongSideZ / 2 - SAFEC_MMLongSideZ / 2 + SAFECLimandeContactDistance
                        ) { unit = LUnit.MM }
                    }

                    physVolume(kaptonSAFEC_MMVolume, name = kaptonsafecMMName) {
                        position(
                            x = posX + SAFECShortSideX / 2 + 3*SAFEC_MMShortSideX / 4,
                            z = -SAFECLongSideZ / 2 - SAFEC_MMLongSideZ / 2 + SAFECLimandeContactDistance
                        ) { unit = LUnit.MM }
                    }

                    physVolume(chipVolume, name = chipName){
                    position(x = posX + SAFECShortSideX/2 + chipShortSideX/2, z = componentsSideY/2) { unit = LUnit.MM }
                    }
                    physVolume(componentsVolume, name = componentsName){
                    position(x = posX + SAFECShortSideX/2 + componentsSideX/2, z = -componentsSideY/2) { unit = LUnit.MM }
                    }
                }

                }
            }
        
            return electronicsBoxVolume

        }

    }
}