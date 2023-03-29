package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class TelescopePipe : Geometry() {
    companion object Parameters {
        const val TelescopePipeThickness: Double = 20.0
        const val TelescopePipeLength: Double = 5000.0

        const val TelescopeFlangeHeight = 10.0
        const val TelescopeFlangeInRadius = 75.0
        const val TelescopeFlangeOutRadius = 300.0

        const val TelescopePipeZinWorld = 512.0
    }

    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
        val telescopePipe: GdmlRef<GdmlAssembly> by lazy {

            val telescopePipeSolidFilled = gdml.solids.cone(
                TelescopePipeLength,
                TelescopeFlangeInRadius,
                TelescopeFlangeOutRadius,
                "steelPipeCone"
            )

            val telescopePipeSolidFilling = gdml.solids.cone(
                TelescopePipeLength + 0.1,
                TelescopeFlangeInRadius - TelescopePipeThickness,
                TelescopeFlangeOutRadius - TelescopePipeThickness,
                "steelPipeFlange"
            )

            // subtract the flange from the pipe
            val telescopePipeSolid = gdml.solids.subtraction(
                telescopePipeSolidFilled,
                telescopePipeSolidFilling,
                "telescopePipeSolid"
            ) {
                position(z = 0) { unit = LUnit.MM }
            }

            val telescopePipeVolume =
                gdml.structure.volume(Materials.Steel.ref, telescopePipeSolid, "telescopePipeVolume")

            val telescopePipeFillingVolume =
                gdml.structure.volume(Materials.Vacuum.ref, telescopePipeSolidFilling, "telescopePipeFillingVolume")

            return@lazy gdml.structure.assembly {
                name = "telescopePipe"
                physVolume(telescopePipeVolume) {
                    name = "telescopePipeVolume"
                    position(z = TelescopePipeLength / 2) { unit = LUnit.MM }
                }

                physVolume(telescopePipeFillingVolume) {
                    name = "telescopePipeFillingVolume"
                    position(z = TelescopePipeLength / 2) { unit = LUnit.MM }
                }


            }
        }

        return telescopePipe
    }
}
