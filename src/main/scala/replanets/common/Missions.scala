package replanets.common

class Missions(race: RaceId, hostType: HostType) {
  private val raceSpecificMissions = Map(
    1 -> "Super refit",
    2 -> "Hisssss!",
    3 -> "Super Spy",
    4 -> "Pillage planet",
    5 -> "Rob ship",
    6 -> "Self repair",
    8 -> "Lay web mines",
    9 -> "Dark sense",
    10 -> "Rebel ground attack",
    11 -> "Build fighters"
  )

  private val classicMissions = Map(
    0 -> "none",
    1 -> "Exploration",
    2 -> "Mine sweep",
    3 -> "Lay mines",
    4 -> "KILL",
    5 -> "Sensor sweep",
    6 -> "Colonize & disassemble",
    7 -> "Tow",
    8 -> "Intercept",
    9 -> raceSpecificMissions(race.value),
    10 -> "Cloak",
    11 -> "Beam up fuel",
    12 -> "Beam up Duranium",
    13 -> "Beam up Tritanium",
    14 -> "Beam up Molybdenium",
    15 -> "Beam up supplies"
  )

  private val phostMissions = Map(
    20 -> "Make torpedoes",
    21 -> "Lay minefield",
    23 -> "Scoop torpedoes from minefield",
    24 -> "Load minerals and make torpedoes",
    25 -> "Beam down money",
    26 -> "Transfer torpedoes to ship",
    27 -> "Transfer fighters to ship",
    28 -> "Transfer money to ship",
    29 -> "Standard super spy",
    30 -> "Cloak",
    31 -> "Extended mission",
    32 -> "Gather-build fighters",
    33 -> "Beam up money",
    34 -> "Beam up clans",
    35 -> "Beam up multiple",
    36 -> "Add mines to field",
    37 -> "Add web mines to field"
  ) ++ (
    if(race.value == 7) Seq(22 -> "Lay web minefield") else Seq()
    )

  private val phost4Missions = Map(
    38 -> "Training",
    39 -> "Exchange crew",
    40 -> "Repair ship"
  )

  val all = classicMissions ++
    (if(hostType == PHost3 || hostType == PHost4) phostMissions else Map()) ++
    (if(hostType == PHost4) phost4Missions else Map())
  
  def get(mission: Short) = all(mission)
}
