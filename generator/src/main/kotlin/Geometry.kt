import space.kscience.gdml.*

abstract class Geometry {
    companion object Parameters

    abstract fun generate(gdml: Gdml): GdmlRef<GdmlAssembly>
}
