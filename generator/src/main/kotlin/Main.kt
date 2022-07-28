import space.kscience.gdml.*

import java.io.File

import BabyIAXO.geometries as BabyIAXO
import `IAXO-D1`.geometries as IAXOD1

fun main() {

    val geometriesTotal = mapOf(
        "BabyIAXO" to BabyIAXO,
        "IAXO-D1" to IAXOD1,
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
                .writeText(gdml.removeUnusedMaterials().encodeToString())
        }
    }

}