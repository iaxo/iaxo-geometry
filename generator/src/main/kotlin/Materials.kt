import space.kscience.gdml.*

const val materialsUrl = "https://raw.githubusercontent.com/lobis/geant4-materials-xml/main/materials.xml"

private val materialsGdml = Gdml {
    loadMaterialsFromUrl(materialsUrl)
}

private val materials = materialsGdml.materials

private fun resolve(tag: String): GdmlRef<GdmlMaterial> {
    return materials.get<GdmlMaterial>(tag)?.ref()
        ?: throw Exception("Material '$tag' does not exist! It should be present in $materialsUrl")
}

fun resolveMaterialByRef(material: Materials): GdmlMaterial {
    return material.ref.resolve(materialsGdml)!!
}

enum class Materials(val ref: GdmlRef<GdmlMaterial>) {
    Air(resolve("G4_AIR")),
    Gas(resolve("GasMixture-Argon2%Isobutane1.4Bar")),
    Vacuum(resolve("G4_Galactic")),
    Copper(resolve("G4_Cu")),
    Teflon(resolve("G4_TEFLON")),
    Kapton(resolve("G4_KAPTON")),
    Mylar(resolve("G4_MYLAR")),
    Lead(resolve("G4_Pb")),
    BC408(resolve("BC408")),
    Cadmium(resolve("G4_Cd")),
    Lucite(resolve("G4_LUCITE")),
    Neoprene(resolve("G4_RUBBER_NEOPRENE")),
    Steel(resolve("G4_STAINLESS-STEEL")),
}

