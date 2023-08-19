import space.kscience.gdml.*

import java.io.File

import BabyIAXO.geometries as BabyIAXO
import `IAXO-D1`.geometries as IAXOD1
import `IAXO-LiquidO`.geometries as IAXOLiquidO

fun main() {

    val geometriesTotal = mapOf(
        "BabyIAXO" to BabyIAXO,
        "IAXO-D1" to IAXOD1,
        "IAXO-LiquidO" to IAXOLiquidO,
    )

    // Save all gdml files into "gdml" directory
    val outputDirectoryName = "gdml"
    val parentDirectory = File(".", outputDirectoryName)
    parentDirectory.deleteRecursively()
    parentDirectory.mkdir()

    for ((geometryName, geometries) in geometriesTotal) {
        val directory = File(outputDirectoryName, geometryName)
        directory.deleteRecursively()
        directory.mkdir()

        for ((name, gdml) in geometries) {
            File("$outputDirectoryName/$geometryName/$name.gdml")
                .writeText(gdml.encodeToString()) // do not use removeUnusedMaterials() here
        }
    }
}