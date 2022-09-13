package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class DetectorPipe : Geometry() {
    companion object Parameters {
        const val TotalLength: Double = 491.0

        // Outside
        const val ChamberFlangeThickness: Double = 14.0
        const val ChamberFlangeRadius: Double = Chamber.SquareSide / 2
        const val TelescopeFlangeThickness: Double = 18.0
        const val TelescopeFlangeRadius: Double = 150.0 / 2

        const val Section2of2Length: Double = 150.0 - TelescopeFlangeThickness
        const val Section1of2Length: Double =
            TotalLength - TelescopeFlangeThickness - ChamberFlangeThickness - Section2of2Length
        const val OuterRadius1: Double = 92.0 / 2
        const val OuterRadius2: Double = 108.0 / 2
        const val Union1Z: Double = ChamberFlangeThickness / 2 + Section1of2Length / 2
        const val Union2Z: Double = Union1Z + Section1of2Length / 2 + Section2of2Length / 2
        const val Union3Z: Double = Union2Z + Section2of2Length / 2 + TelescopeFlangeThickness / 2

        // Inside

        const val InsideSection1of3Radius: Double = 43.0 / 2
        const val InsideSection2of3Radius: Double = 68.0 / 2
        const val InsideSection3of3Radius: Double = 85.0 / 2
        const val InsideSectionTelescopeRadius: Double = 108.0 / 2
        const val InsideCone1of3Length: Double = 21.65
        const val InsideCone2of3Length: Double = 14.72
        const val InsideCone3of3Length: Double = 9.0
        const val InsideSection3of3Length: Double = 115.0 - InsideCone3of3Length
        const val InsideSection2of3Length: Double =
            290.0 - InsideSection3of3Length - InsideCone3of3Length - InsideCone2of3Length
        const val InsideSection1of3Length: Double = 201.0 - InsideCone1of3Length
        const val InsideUnion1Z: Double = InsideSection1of3Length / 2 + InsideCone1of3Length / 2
        const val InsideUnion2Z: Double = InsideUnion1Z + InsideCone1of3Length / 2 + InsideSection2of3Length / 2
        const val InsideUnion3Z: Double = InsideUnion1Z + InsideCone1of3Length / 2 + InsideSection2of3Length / 2
        const val InsideUnion4Z: Double = InsideUnion3Z + InsideCone2of3Length / 2 + InsideSection3of3Length / 2
        const val InsideUnion5Z: Double = InsideUnion4Z + InsideSection3of3Length / 2 + InsideCone3of3Length / 2
        const val FillingOffsetWithPipe: Double = InsideSection1of3Length / 2 - ChamberFlangeThickness / 2

        // World
        const val ZinWorld: Double =
            ChamberFlangeThickness / 2 + Chamber.Height / 2 + Chamber.CathodeTeflonDiskThickness
    }

    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
        val detectorPipeVolume: GdmlRef<GdmlAssembly> by lazy {

            val detectorPipeChamberFlangeSolid = gdml.solids.tube(
                ChamberFlangeRadius,
                ChamberFlangeThickness,
                "detectorPipeChamberFlangeSolid"
            )
            val detectorPipeTelescopeFlangeSolid = gdml.solids.tube(
                TelescopeFlangeRadius,
                TelescopeFlangeThickness,
                "detectorPipeTelescopeFlangeSolid"
            )
            val detectorPipeSection1of2Solid =
                gdml.solids.tube(
                    OuterRadius1,
                    Section1of2Length,
                    "detectorPipeSection1of2Solid"
                )
            val detectorPipeSection2of2Solid =
                gdml.solids.tube(
                    OuterRadius2,
                    Section2of2Length,
                    "detectorPipeSection2of2Solid"
                )
            val detectorPipeAux1 =
                gdml.solids.union(detectorPipeChamberFlangeSolid, detectorPipeSection1of2Solid, "detectorPipeAux1")
                {
                    position(z = Union1Z) { unit = LUnit.MM }
                }
            val detectorPipeAux2 =
                gdml.solids.union(detectorPipeAux1, detectorPipeSection2of2Solid, "detectorPipeAux2")
                {
                    position(z = Union2Z) { unit = LUnit.MM }
                }
            val detectorPipeNotEmpty =
                gdml.solids.union(detectorPipeAux2, detectorPipeTelescopeFlangeSolid, "detectorPipeNotEmpty")
                {
                    position(z = Union3Z) { unit = LUnit.MM }
                }
            val detectorPipeInside1of3Solid = gdml.solids.tube(
                InsideSection1of3Radius,
                InsideSection1of3Length,
                "detectorPipeInside1of3Solid"
            )
            val detectorPipeInside2of3Solid = gdml.solids.tube(
                InsideSection2of3Radius,
                InsideSection2of3Length,
                "detectorPipeInside2of3Solid"
            )
            val detectorPipeInside3of3Solid = gdml.solids.tube(
                InsideSection3of3Radius,
                InsideSection3of3Length,
                "detectorPipeInside3of3Solid"
            )
            val detectorPipeInsideCone1of3Solid = gdml.solids.cone(
                InsideCone1of3Length,
                InsideSection1of3Radius, InsideSection2of3Radius,
                "detectorPipeInsideCone1of3Solid"
            )
            val detectorPipeInsideCone2of3Solid = gdml.solids.cone(
                InsideCone2of3Length,
                InsideSection2of3Radius, InsideSection3of3Radius,
                "detectorPipeInsideCone2of3Solid"
            )
            val detectorPipeInsideCone3of3Solid = gdml.solids.cone(
                InsideCone3of3Length,
                InsideSection3of3Radius, InsideSectionTelescopeRadius,
                "detectorPipeInsideCone3of3Solid"
            )
            val detectorPipeInsideAux1 =
                gdml.solids.union(
                    detectorPipeInside1of3Solid,
                    detectorPipeInsideCone1of3Solid,
                    "detectorPipeInsideAux1"
                ) {
                    position(z = InsideUnion1Z) { unit = LUnit.MM }
                }
            val detectorPipeInsideAux2 =
                gdml.solids.union(detectorPipeInsideAux1, detectorPipeInside2of3Solid, "detectorPipeInsideAux2") {
                    position(z = InsideUnion2Z) { unit = LUnit.MM }
                }
            val detectorPipeInsideAux3 =
                gdml.solids.union(
                    detectorPipeInsideAux2,
                    detectorPipeInsideCone2of3Solid,
                    "detectorPipeInsideAux3"
                ) {
                    position(z = InsideUnion3Z) { unit = LUnit.MM }
                }
            val detectorPipeInsideAux4 =
                gdml.solids.union(detectorPipeInsideAux3, detectorPipeInside3of3Solid, "detectorPipeInsideAux4") {
                    position(z = InsideUnion4Z) { unit = LUnit.MM }
                }
            val detectorPipeInside =
                gdml.solids.union(detectorPipeInsideAux4, detectorPipeInsideCone3of3Solid, "detectorPipeInside") {
                    position(z = InsideUnion5Z) { unit = LUnit.MM }
                }
            val detectorPipeSolid =
                gdml.solids.subtraction(detectorPipeNotEmpty, detectorPipeInside, "detectorPipeSolid") {
                    position(z = InsideSection1of3Length / 2 - ChamberFlangeThickness / 2) { unit = LUnit.MM }
                }
            val detectorPipeVolume =
                gdml.structure.volume(Materials.Copper.ref, detectorPipeSolid, "detectorPipeVolume")
            val detectorPipeFillingVolume =
                gdml.structure.volume(Materials.Vacuum.ref, detectorPipeInside, "detectorPipeFillingVolume")

            return@lazy gdml.structure.assembly {
                name = "DetectorPipe"
                physVolume(detectorPipeVolume) {
                    name = "detectorPipe"
                }
                physVolume(detectorPipeFillingVolume) {
                    name = "detectorPipeFilling"
                    position(z = FillingOffsetWithPipe) { unit = LUnit.MM }
                }
            }
        }

        return detectorPipeVolume
    }
}