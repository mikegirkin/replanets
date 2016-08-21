package replanets.common

object Constants {
  val HullsInHullspec: Int = 105
  val EnginesInSpec: Int = 9
  val BeamsInBeamspec: Int = 10
  val TorpspecRecordsNumber = 10

  val NumberOfRaces = 11
  val NumberOfPlanets: Int = 500

  val MapHeight = 4000
  val MapWidth = 4000

  val xyplanFilename = "xyplan.dat"
  val racenmFilename = "race.nm"
  val planetnmFilename = "planet.nm"
  val torpspecFilename = "torpspec.dat"
  val engspecFilename = "engspec.dat"
  val beamspecFilename = "beamspec.dat"
  val hullspecFileName = "hullspec.dat"
  val hullsAssignmentFilename = "truehull.dat"

  val techLevelsCost = IndexedSeq(0, 100, 200, 300, 400, 500, 600, 700, 800, 900)

  val natives = Map[Int, String](
    0 -> "none",
    1 -> "Humanoid",
    2 -> "Bovinoid",
    3 -> "Reptilian",
    4 -> "Avian",
    5 -> "Amorphous",
    6 -> "Insectoid",
    7 -> "Amphibian",
    8 -> "Ghipsoldal",
    9 -> "Siliconoid"
  )

  val nativeGovernments = Map[Int, String](
    0 -> "none",
    1 -> "Anarchy",
    2 -> "Pre-Tribal",
    3 -> "Early-Tribal",
    4 -> "Tribal",
    5 -> "Feudal",
    6 -> "Monarchy",
    7 -> "Representative",
    8 -> "Participatory",
    9 -> "Unity"
  )

  val StormCategoryText = Map[Int, String](
    0 -> "Inexistent",
    1 -> "Harmless",
    2 -> "Moderate",
    3 -> "Strong",
    4 -> "Dangerous",
    5 -> "Very dangerous"
  )

  val baseMissions = Map[Int, String](
    0 -> "none",
    1 -> "Refuel",
    2 -> "Max defence",
    3 -> "Load torps or fighters",
    4 -> "Unload freighters",
    5 -> "Repair base",
    6 -> "Force a surrender"
  )

  val thostHullFunctions: Map[HullId, Set[HullFunc]] = Map(
    HullId(3) -> Set(HeatsTo50, ScansAllWormholes),
    HullId(7) -> Set(AntiCloak),
    HullId(8) -> Set(CoolsTo50),
    HullId(9) -> Set(Bioscan),
    HullId(21) -> Set(Cloak),
    HullId(22) -> Set(Cloak),
    HullId(25) -> Set(Cloak),
    HullId(26) -> Set(Cloak),
    HullId(27) -> Set(Cloak),
    HullId(28) -> Set(Cloak),
    HullId(29) -> Set(AdvancedCloak),
    HullId(31) -> Set(AdvancedCloak),
    HullId(32) -> Set(Cloak),
    HullId(33) -> Set(Cloak),
    HullId(36) -> Set(Cloak),
    HullId(38) -> Set(Cloak),
    HullId(39) -> Set(GloryDeviceHighDamage),
    HullId(41) -> Set(GloryDeviceLowDamage),
    HullId(42) -> Set(Gambling),
    HullId(43) -> Set(Cloak),
    HullId(44) -> Set(Gravitonic, Cloak),
    HullId(45) -> Set(Gravitonic, Cloak),
    HullId(46) -> Set(Gravitonic, Cloak),
    HullId(47) -> Set(Cloak),
    HullId(51) -> Set(Hyperdrive),
    HullId(56) -> Set(Chunneling),
    HullId(64) -> Set(HeatsTo100),
    HullId(69) -> Set(ImperialAssault),
    HullId(77) -> Set(Hyperdrive),
    HullId(84) -> Set(FullBioscan),
    HullId(87) -> Set(Hyperdrive),
    HullId(96) -> Set(Ramscoop, Bioscan),
    HullId(43) -> Set(Cloak),
    HullId(97) -> Set(AdvancedRefinery),
    HullId(104) -> Set(Refinery),
    HullId(105) -> Set(Alchemy)
  )
}


