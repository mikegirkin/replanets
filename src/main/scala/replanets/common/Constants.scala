package replanets.common

object Constants {
  val HullsInHullspec: Int = 105
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
}

