package BabyIAXO

import Geometry

import space.kscience.gdml.*

open class TelescopePipe : Geometry() {
    companion object Parameters {
        const val TelescopePipeThickness: Double = 20.0
        const val TelescopePipeLength: Double = 5000.0

        const val TelescopeFlangeHeight = 10.0
        const val TelescopeFlangeInRadius = 75.0
        const val TelescopeFlangeOutRadius = 350.0

        const val TelescopePipeZinWorld = 512.0

        const val TelescopePipeGeneratorThickness = 0.1
        const val TelescopePipeGeneratorRadius = 275.0
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

            // aux volume for simulations
            val telescopePipeGeneratorSolid = gdml.solids.tube(
                TelescopePipeGeneratorRadius,
                TelescopePipeGeneratorThickness,
                "telescopePipeGeneratorSolid"
            )

            val telescopePipeGeneratorVolume =
                gdml.structure.volume(Materials.Vacuum.ref, telescopePipeGeneratorSolid, "telescopePipeGeneratorVolume")


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

                physVolume(telescopePipeGeneratorVolume) {
                    name = "telescopePipeGeneratorVolume"
                    position(z = TelescopePipeLength + TelescopePipeGeneratorThickness / 2.0 + 0.1) { unit = LUnit.MM }
                }

            }
        }

        return telescopePipe
    }
}
