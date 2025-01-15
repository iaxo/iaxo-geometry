package `IAXO-D1`

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

        //Card Dimensions
        const val cardLongSizeZ: Double = 100.0
        const val cardShortSideX: Double = 2.5
        const val cardShortSideY: Double = 70.0

        //flat cable Dimensions
        const val flatCableLongSideZ: Double = 150.0
        const val flatCableShortSideX: Double = 1.0
        const val flatCableShortSideY: Double = 70.0

        //chip Dimensions
        const val chipSideYZ: Double = 25.0
        const val chipShortSideX: Double = 1.0

        //components Dimensions
        const val componentsLongSideZ: Double = 5.0
        const val componentsShortSideX: Double = 1.0
        const val componentsShortSideY: Double = 40.0

        // Distance 
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
            val electronicsCardSolid = gdml.solids.box(
                cardShortSideX,
                cardShortSideY,
                cardLongSizeZ,
                "electronicsCardSolid")

            //Flat cable
            val flatCableSolid = gdml.solids.box(
                flatCableShortSideX,
                flatCableShortSideY,
                flatCableLongSideZ,
                "flatCableSolid")

            val electronicsBoxVolume = 
            gdml.structure.volume(Materials.Copper.ref,electronicsBoxSolid,"electronicsBoxVolume")

            val electronicsBoxFillingVolume =
            gdml.structure.volume(Materials.Vacuum.ref,electronicsBoxInSolid,"electronicsBoxFillingVolume")

            val electronicsCardVolume = 
            gdml.structure.volume(Materials.Copper.ref,electronicsCardSolid,"electronicsCardVolume")

            val flatCableVolume =
            gdml.structure.volume(Materials.Copper.ref,flatCableSolid,"flatCableVolume")

            return@lazy gdml.structure.assembly {
                name = "electronicsBox"
                //physVolume(electronicsBoxVolume, name = "electronicsBox")
                //physVolume(electronicsBoxFillingVolume, name = "electronicsBoxFilling")

                physVolume(electronicsCardVolume, name = "electronicsCard1"){
                    position(x = - 15.0 ) { unit = LUnit.MM }
                }
                physVolume(electronicsCardVolume, name = "electronicsCard2"){
                    position(x = - 5.0 ) { unit = LUnit.MM }
                }
                physVolume(electronicsCardVolume, name = "electronicsCard3"){
                    position(x = 5.0 ) { unit = LUnit.MM }
                }
                physVolume(electronicsCardVolume, name = "electronicsCard4"){
                    position(x = 15.0 ) { unit = LUnit.MM }
                }

                physVolume(flatCableVolume, name = "flatCable1"){
                    position(x = - 15.0, z = - cardLongSizeZ / 2 - flatCableLongSideZ / 2) { unit = LUnit.MM }
                }
                physVolume(flatCableVolume, name = "flatCable2"){
                    position(x = - 5.0, z = - cardLongSizeZ / 2 - flatCableLongSideZ / 2) { unit = LUnit.MM }
                }
                physVolume(flatCableVolume, name = "flatCable3"){
                    position(x = 5.0, z = - cardLongSizeZ / 2 - flatCableLongSideZ / 2) { unit = LUnit.MM }
                }
                physVolume(flatCableVolume, name = "flatCable4"){
                    position(x = 15.0, z = - cardLongSizeZ / 2 - flatCableLongSideZ / 2) { unit = LUnit.MM }
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
            val electronicsCardSolid = gdml.solids.box(
                cardShortSideX,
                cardShortSideY,
                cardLongSizeZ,
                "electronicsCardSolid")

            //Flat cable
            val flatCableSolid = gdml.solids.box(
                flatCableShortSideX,
                flatCableShortSideY,
                flatCableLongSideZ,
                "flatCableSolid")

            //chip
            val chipSolid = gdml.solids.box(
                chipShortSideX,
                chipSideYZ,
                chipSideYZ,
                "chipSolid")

            //components
            val componentsSolid = gdml.solids.box(
                componentsShortSideX,
                componentsShortSideY,
                componentsLongSideZ,
                "componentsSolid")

            val electronicsBoxVolume = 
            gdml.structure.volume(Materials.Copper.ref,electronicsBoxSolid,"electronicsBoxVolume")

            val electronicsBoxFillingVolume =
            gdml.structure.volume(Materials.Vacuum.ref,electronicsBoxInSolid,"electronicsBoxFillingVolume")

            val electronicsCardVolume = 
            gdml.structure.volume(Materials.Copper.ref,electronicsCardSolid,"electronicsCardVolume")

            val flatCableVolume =
            gdml.structure.volume(Materials.Copper.ref,flatCableSolid,"flatCableVolume")

            val chipVolume =
            gdml.structure.volume(Materials.Silicon.ref,chipSolid,"chipVolume")

            val componentsVolume =
            gdml.structure.volume(Materials.Silicon.ref,componentsSolid,"componentsVolume")

            return@lazy gdml.structure.assembly {
                name = "electronicsBox"
                //physVolume(electronicsBoxVolume, name = "electronicsBox")
                //physVolume(electronicsBoxFillingVolume, name = "electronicsBoxFilling")

                physVolume(electronicsCardVolume, name = "electronicsCard1"){
                    position(x = - 15.0 ) { unit = LUnit.MM }
                }
                physVolume(electronicsCardVolume, name = "electronicsCard2"){
                    position(x = - 5.0 ) { unit = LUnit.MM }
                }
                physVolume(electronicsCardVolume, name = "electronicsCard3"){
                    position(x = 5.0 ) { unit = LUnit.MM }
                }
                physVolume(electronicsCardVolume, name = "electronicsCard4"){
                    position(x = 15.0 ) { unit = LUnit.MM }
                }

                physVolume(chipVolume, name = "chip1"){
                    position(x = - 15.0 + cardShortSideX/2 + chipShortSideX/2) { unit = LUnit.MM }
                }
                physVolume(chipVolume, name = "chip2"){
                    position(x = - 5.0 + cardShortSideX/2 + chipShortSideX/2) { unit = LUnit.MM }
                }
                physVolume(chipVolume, name = "chip3"){
                    position(x = 5.0 + cardShortSideX/2 + chipShortSideX/2) { unit = LUnit.MM }
                }
                physVolume(chipVolume, name = "chip4"){
                    position(x = 15.0 + cardShortSideX/2 + chipShortSideX/2) { unit = LUnit.MM }
                }

                physVolume(componentsVolume, name = "components1"){
                    position(x = - 15.0 + cardShortSideX/2 + componentsShortSideX/2, z = -30.0 ) { unit = LUnit.MM }
                }
                physVolume(componentsVolume, name = "components2"){
                    position(x = - 5.0 + cardShortSideX/2 + componentsShortSideX/2, z = -30.0 ) { unit = LUnit.MM }
                }
                physVolume(componentsVolume, name = "components3"){
                    position(x = 5.0 + cardShortSideX/2 + componentsShortSideX/2, z = -30.0 ) { unit = LUnit.MM }
                }
                physVolume(componentsVolume, name = "components4"){
                    position(x = 15.0 + cardShortSideX/2 + componentsShortSideX/2, z = -30.0 ) { unit = LUnit.MM }
                }

                physVolume(flatCableVolume, name = "flatCable1"){
                    position(x = - 15.0, z = - cardLongSizeZ / 2 - flatCableLongSideZ / 2) { unit = LUnit.MM }
                }
                physVolume(flatCableVolume, name = "flatCable2"){
                    position(x = - 5.0, z = - cardLongSizeZ / 2 - flatCableLongSideZ / 2) { unit = LUnit.MM }
                }
                physVolume(flatCableVolume, name = "flatCable3"){
                    position(x = 5.0, z = - cardLongSizeZ / 2 - flatCableLongSideZ / 2) { unit = LUnit.MM }
                }
                physVolume(flatCableVolume, name = "flatCable4"){
                    position(x = 15.0, z = - cardLongSizeZ / 2 - flatCableLongSideZ / 2) { unit = LUnit.MM }
                }
                }
            }
        
            return electronicsBoxVolume

        }

    }
}