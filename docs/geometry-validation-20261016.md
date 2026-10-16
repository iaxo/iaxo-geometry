# Geometry validation, 2026-10-16

The readout fix aligns the copper subtraction with the local square gas solid.
The square gas volume and copper readout both receive the same 45-degree chamber
placement; rotating the local subtraction by another 45 degrees left gas inside
copper. The cylindrical, unsplit gas subtraction still needs its 45-degree
rotation and is unchanged.

## Other corrections

- `BabyIAXO/VetoSystem` uses its centered placement, as in the complete BabyIAXO
  geometry. The former IAXO-D1 offset placed 235 components outside the world.
- `IAXO-D1/DefaultXenon` now uses the IAXO-D1 veto layout and world, with xenon gas.
  Previously it called the complete BabyIAXO generator.
- `IAXO-D1/CompleteVeto4Layers` keeps the 64 mm upper-layer pitch. The former
  fourth-layer placement reduced the pitch to 54 mm and intersected the third
  layer's thicker light guide. Layers one through three retain their positions.
- The IAXO-D1 complete-geometry helper forwards `withCap`; `DefaultClosedPipe`
  uses this helper rather than duplicating it.
- The three published BabyIAXO chamber/pipe gas variants are generated explicitly.
- The six custom chamber geometries not emitted by the generator receive only
  the same local readout-subtraction correction. This includes the four
  parameterized shielding templates. Their other dimensions and materials are
  preserved.
- `other/HERASouthHallSimple6ScintillatorPairs` embeds the material dependencies
  it references. Its unused external-entity declaration did not define those
  materials; ROOT previously reported undefined concrete and air.

## Checks

Generation used Temurin JDK 11, the repository's Gradle 7.1 wrapper, gdml.kt dev
commit `bfd113f01147c499457c084898618315a2dd4b7f`, and the shared REST material
catalog. All 41 generated files match the checked-in output byte for byte.
The existing `gdml/old` archive is excluded.

`tests/check_gdml.py` checks XML references in all 48 current GDML files, the
readout frame, gas selection, detector identity for xenon variants, and pipe caps.
The CI workflow runs these checks after generation with an explicit JDK 11.

`macros/CheckOverlaps.C` runs headlessly with ROOT 6.40.04. It combines the surface
check with one million sampled points per checked volume and a fixed seed. An
explicit grid across the readout and cathode slices checks that the two placed
gas volumes partition the unsplit gas and do not intersect the copper readout.
The pre-fix chamber fails with 3468 grid mismatches and a sampled gas/copper
intersection; the corrected chamber has zero mismatches.

The four `${...}` shielding templates must be instantiated before import.
The layered templates were checked with each supplied layer thickness set to
20, 50, and 90 mm; their remaining lead thickness follows the template formula.
The simple lead templates were checked at 200 mm. These examples do not validate
all possible parameter combinations.

After the final correction, all 44 standalone current geometries pass the mesh,
sampling, and applicable gas-partition checks with no detected overlaps or
extrusions. The eight distinct template examples described above also pass.

ROOT mesh and sampling checks are finite numerical checks, not a proof of absence
of arbitrarily small intersections. No Geant4 transport or new simulation campaign
was run as part of this validation.
