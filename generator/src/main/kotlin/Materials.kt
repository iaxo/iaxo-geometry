import space.kscience.gdml.*

const val materialsUrl = "https://raw.githubusercontent.com/rest-for-physics/materials/main/output/materials.xml"

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
    GasArgon(resolve("Argon1%Isobutane1.4bar")),
    GasXenon(resolve("XenonNeon2.3%Isobutane1.05bar")),
    Vacuum(resolve("G4_Galactic")),
    Copper(resolve("G4_Cu")),
    Teflon(resolve("G4_TEFLON")),
    Kapton(resolve("G4_KAPTON")),
    Mylar(resolve("G4_MYLAR")),
    Aluminium(resolve("G4_Al")),
    Lead(resolve("G4_Pb")),
    BC408(resolve("BC408")),
    Cadmium(resolve("G4_Cd")),
    Lucite(resolve("G4_LUCITE")),
    Neoprene(resolve("G4_RUBBER_NEOPRENE")),
    Steel(resolve("G4_STAINLESS-STEEL")),
    BoratedHDPE5pct(resolve("BoratedHDPE5pct")),
    EJ254_5pct(resolve("EJ254-5pct")),
    EJ254_1pct(resolve("EJ254-1pct")),
    Silicon(resolve("G4_Si"))
}

