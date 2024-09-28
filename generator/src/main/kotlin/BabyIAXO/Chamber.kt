package BabyIAXO

import Geometry
import Materials

import space.kscience.gdml.*

open class Chamber(
    val useXenon: Boolean = false,
    val splitGas: Boolean = false,
) : Geometry() {
    companion object Parameters {
        // Body
        const val Height: Double = 30.0
        const val Diameter: Double = 102.0
        const val BackplateThickness: Double = 15.0
        const val SquareSide: Double = 134.0
        const val TeflonWallThickness: Double = 1.0

        // Readout
        const val ReadoutKaptonThickness: Double = 0.5
        const val ReadoutCopperThickness: Double = 0.2
        const val ReadoutPlaneSide: Double = 60.0

        // Cathode
        const val CathodeTeflonDiskHoleRadius: Double = 15.0
        const val CathodeTeflonDiskThickness: Double = 5.0
        const val CathodeCopperSupportOuterRadius: Double = 45.0
        const val CathodeCopperSupportInnerRadius: Double = 8.5
        const val CathodeCopperSupportThickness: Double = 1.0
        const val CathodeWindowThickness: Double = 0.004
        const val CathodeWindowAliminiumThickness: Double = 0.00004
        const val CathodeWindowMylarThickness: Double = CathodeWindowThickness - CathodeWindowAliminiumThickness
        const val CathodePatternDiskRadius: Double = 4.25
        const val CathodePatternLineWidth: Double = 0.3
    }

    override fun generate(gdml: Gdml): GdmlRef<GdmlAssembly> {
        val chamberVolume: GdmlRef<GdmlAssembly> by lazy {
            val chamberBodySolid = gdml.solids.subtraction(
                gdml.solids.box(
                    SquareSide,
                    SquareSide,
                    Height,
                    "chamberBodyBaseSolid"
                ), gdml.solids.tube(Diameter / 2, Height, "chamberBodyHoleSolid"),
                "chamberBodySolid"
            )
            val chamberBodyVolume = gdml.structure.volume(Materials.Copper.ref, chamberBodySolid, "chamberBodyVolume")

            val chamberBackplateSolid = gdml.solids.box(
                SquareSide,
                SquareSide,
                BackplateThickness,
                "chamberBackplateSolid"
            )
            val chamberBackplateVolume =
                gdml.structure.volume(Materials.Copper.ref, chamberBackplateSolid, "chamberBackplateVolume")

            val chamberTeflonWallSolid =
                gdml.solids.tube(Diameter / 2, Height, "chamberTeflonWallSolid") {
                    rmin = Diameter / 2 - TeflonWallThickness
                }
            val chamberTeflonWallVolume =
                gdml.structure.volume(Materials.Teflon.ref, chamberTeflonWallSolid, "chamberTeflonWallVolume")
            // readout
            val kaptonReadoutSolid = gdml.solids.box(
                SquareSide,
                SquareSide,
                ReadoutKaptonThickness,
                "kaptonReadoutSolid"
            )
            val kaptonReadoutVolume =
                gdml.structure.volume(Materials.Kapton.ref, kaptonReadoutSolid, "kaptonReadoutVolume")

            val copperReadoutSolid = gdml.solids.box(
                ReadoutPlaneSide,
                ReadoutPlaneSide,
                ReadoutCopperThickness,
                "copperReadoutSolid"
            )

            val copperReadoutVolume =
                gdml.structure.volume(Materials.Copper.ref, copperReadoutSolid, "copperReadoutVolume")

            // cathode
            val cathodeTeflonDiskBaseSolid = gdml.solids.tube(
                SquareSide / 2,
                CathodeTeflonDiskThickness,
                "cathodeTeflonDiskBaseSolid"
            ) {
                rmin = CathodeTeflonDiskHoleRadius
            }

            val cathodeCopperDiskSolid = gdml.solids.tube(
                CathodeCopperSupportOuterRadius,
                CathodeCopperSupportThickness,
                "cathodeCopperDiskSolid"
            ) {
                rmin = CathodeCopperSupportInnerRadius
            }

            val cathodeTeflonDiskSolid =
                gdml.solids.subtraction(
                    cathodeTeflonDiskBaseSolid,
                    cathodeCopperDiskSolid,
                    "cathodeTeflonDiskSolid"
                ) {
                    position(z = -CathodeTeflonDiskThickness / 2 + CathodeCopperSupportThickness / 2) {
                        unit = LUnit.MM
                    }
                }

            val cathodeTeflonDiskVolume =
                gdml.structure.volume(Materials.Teflon.ref, cathodeTeflonDiskSolid, "cathodeTeflonDiskVolume") {}

            val cathodeWindowMylarSolid =
                gdml.solids.tube(
                    CathodeTeflonDiskHoleRadius,
                    CathodeWindowMylarThickness,
                    "cathodeWindowMylarSolid"
                )
            val cathodeWindowMylarVolume =
                gdml.structure.volume(Materials.Mylar.ref, cathodeWindowMylarSolid, "cathodeWindowMylarVolume")

            val cathodeWindowAluminiumSolid =
                gdml.solids.tube(
                    CathodeTeflonDiskHoleRadius,
                    CathodeWindowAliminiumThickness,
                    "cathodeWindowAluminiumSolid"
                )
            val cathodeWindowAluminiumVolume =
                gdml.structure.volume(
                    Materials.Aluminium.ref,
                    cathodeWindowAluminiumSolid,
                    "cathodeWindowAluminiumVolume"
                )

            // cathode copper disk pattern
            val cathodePatternLineAux = gdml.solids.box(
                CathodePatternLineWidth,
                CathodeCopperSupportInnerRadius * 2,
                CathodeCopperSupportThickness,
                "cathodePatternLineAux"
            )
            val cathodePatternCentralHole = gdml.solids.tube(
                CathodePatternDiskRadius,
                CathodeCopperSupportThickness * 1.1, "cathodePatternCentralHole"
            )
            val cathodePatternLine =
                gdml.solids.subtraction(cathodePatternLineAux, cathodePatternCentralHole, "cathodePatternLine")

            val cathodePatternDisk = gdml.solids.tube(
                CathodePatternDiskRadius,
                CathodeCopperSupportThickness, "cathodePatternDisk"
            ) { rmin = CathodePatternDiskRadius - CathodePatternLineWidth }


            var cathodeCopperDiskSolidAux: GdmlRef<GdmlUnion> = GdmlRef("")

            for (i in 0..3) {
                cathodeCopperDiskSolidAux =
                    gdml.solids.union(
                        if (i > 0) cathodeCopperDiskSolidAux else cathodeCopperDiskSolid,
                        cathodePatternLine, "cathodeCopperDiskSolidAux$i"
                    ) {
                        rotation(x = 0, y = 0, z = 45 * i) {
                            unit = AUnit.DEG
                        }
                    }
            }

            val cathodeCopperDiskFinal =
                gdml.solids.union(cathodeCopperDiskSolidAux, cathodePatternDisk, "cathodeCopperDiskFinal.solid")

            val cathodeCopperDiskVolume =
                gdml.structure.volume(Materials.Copper.ref, cathodeCopperDiskFinal, "cathodeCopperDiskFinal")

            val cathodeFillingBaseSolid = gdml.solids.tube(
                CathodeTeflonDiskHoleRadius,
                CathodeTeflonDiskThickness,
                "cathodeFillingBaseSolid"
            )
            val cathodeFillingSolid =
                gdml.solids.subtraction(cathodeFillingBaseSolid, cathodeCopperDiskFinal, "cathodeFillingSolid") {
                    position(z = -CathodeTeflonDiskThickness / 2 + CathodeCopperSupportThickness / 2) {
                        unit = LUnit.MM
                    }
                }
            val cathodeFillingVolume =
                gdml.structure.volume(Materials.Vacuum.ref, cathodeFillingSolid, "cathodeFillingVolume") {}

            // gas
            val gasSolidOriginal = gdml.solids.tube(
                Diameter / 2 - TeflonWallThickness,
                Height, "gasSolidOriginal"
            )
            val gasSolidAux1 = gdml.solids.subtraction(gasSolidOriginal, copperReadoutSolid, "gasSolidAux1") {
                position(z = -Height / 2 + ReadoutCopperThickness / 2) { unit = LUnit.MM }
                rotation(z = 45) { unit = AUnit.DEG }
            }

            val gasSolidAux2 =
                gdml.solids.subtraction(gasSolidAux1, cathodeWindowAluminiumSolid, "gasSolidAux2") {
                    position(z = Height / 2 - CathodeWindowMylarThickness - CathodeWindowAliminiumThickness / 2) {
                        unit = LUnit.MM
                    }
                }
            val gasSolid = gdml.solids.subtraction(gasSolidAux2, cathodeWindowMylarSolid, "gasSolid") {
                position(z = Height / 2 - CathodeWindowMylarThickness / 2) {
                    unit = LUnit.MM
                }
            }

            val gasSolidAboveReadoutOriginal = gdml.solids.box(
                ReadoutPlaneSide,
                ReadoutPlaneSide,
                Height,
                "gasSolidAboveReadoutOriginal"
            )
            val gasSolidAboveReadoutAux = gdml.solids.subtraction(
                gasSolidAboveReadoutOriginal,
                copperReadoutSolid,
                "gasSolidAboveReadoutAux"
            ) {
                position(z = -Height / 2 + ReadoutCopperThickness / 2) { unit = LUnit.MM }
                rotation(z = 45) { unit = AUnit.DEG }
            }

            val gasSolidAboveReadoutAux2 =
                gdml.solids.subtraction(gasSolidAboveReadoutAux, cathodeWindowAluminiumSolid, "gasSolidAboveReadoutAux2") {
                    position(z = Height / 2 - CathodeWindowMylarThickness - CathodeWindowAliminiumThickness / 2) {
                        unit = LUnit.MM
                    }
                }

            val gasSolidAboveReadoutSolid =
                gdml.solids.subtraction(gasSolidAboveReadoutAux2, cathodeWindowMylarSolid, "gasSolidAboveReadoutSolid") {
                    position(z = Height / 2 - CathodeWindowMylarThickness / 2) {
                        unit = LUnit.MM
                    }
                }

            val gasSolidWithHole =
                gdml.solids.subtraction(gasSolid, gasSolidAboveReadoutSolid, "gasSolidWithHole") {
                    rotation(z = 45) { unit = AUnit.DEG }
                }

            val gasMaterialRef = if (useXenon) Materials.GasXenon.ref else Materials.GasArgon.ref

            val gasVolume = gdml.structure.volume(gasMaterialRef, gasSolid, "gasVolume")
            val gasVolumeNotAboveReadout =
                gdml.structure.volume(gasMaterialRef, gasSolidWithHole, "gasVolumeNotAboveReadout")
            val gasVolumeAboveReadout =
                gdml.structure.volume(gasMaterialRef, gasSolidAboveReadoutSolid, "gasVolumeAboveReadout")

            return@lazy gdml.structure.assembly {
                name = "Chamber"
                if (splitGas) {
                    physVolume(gasVolumeNotAboveReadout, name = "gasNotAboveReadout")
                    physVolume(gasVolumeAboveReadout, name = "gasAboveReadout").rotation(z = 45) { unit = AUnit.DEG }
                } else {
                    physVolume(gasVolume, name = "gas")
                }
                physVolume(chamberBackplateVolume, name = "chamberBackplate") {
                    position(
                        z = -Height / 2 - ReadoutKaptonThickness - BackplateThickness / 2
                    ) { unit = LUnit.MM }
                }
                physVolume(chamberBodyVolume, name = "chamberBody")
                physVolume(chamberTeflonWallVolume, name = "chamberTeflonWall")
                physVolume(kaptonReadoutVolume, name = "kaptonReadout") {
                    position(z = -Height / 2 - ReadoutKaptonThickness / 2) { unit = LUnit.MM }
                }
                physVolume(copperReadoutVolume, name = "copperReadout") {
                    position(z = -Height / 2 + ReadoutCopperThickness / 2) { unit = LUnit.MM }
                    rotation(z = 45) { unit = AUnit.DEG }
                }
                physVolume(cathodeWindowMylarVolume, name = "cathodeWindowMylar") {
                    position(z = Height / 2 - CathodeWindowMylarThickness / 2) {
                        unit = LUnit.MM
                    }
                }
                physVolume(cathodeWindowAluminiumVolume, name = "cathodeWindowAluminium") {
                    position(z = Height / 2 - CathodeWindowMylarThickness - CathodeWindowAliminiumThickness / 2) {
                        unit = LUnit.MM
                    }
                }
                physVolume(cathodeTeflonDiskVolume, name = "cathodeTeflonDisk") {
                    position(z = Height / 2 + CathodeTeflonDiskThickness / 2) { unit = LUnit.MM }
                }
                physVolume(cathodeFillingVolume, name = "cathodeFilling") {
                    position(z = Height / 2 + CathodeTeflonDiskThickness / 2) { unit = LUnit.MM }
                }
                physVolume(cathodeCopperDiskVolume, name = "cathodeCopperDiskPattern") {
                    position(z = Height / 2 + CathodeCopperSupportThickness / 2) { unit = LUnit.MM }
                }
            }
        }

        return chamberVolume
    }

}