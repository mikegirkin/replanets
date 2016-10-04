package replanets.common

trait MissionArgumentRequirement
case class TowArgument(
  text: String
) extends MissionArgumentRequirement

case class InterceptArgument(
  text: String
) extends MissionArgumentRequirement

class MissionRequirement(
  tow: Option[TowArgument] = None,
  itercept: Option[InterceptArgument] = None
)

object MissionRequirement {
  def apply(tow: TowArgument) = new MissionRequirement(Some(tow), None)
  def apply(intercept: InterceptArgument) = new MissionRequirement(None, Some(intercept))
  def apply(intercept: InterceptArgument, tow: TowArgument) = new MissionRequirement(Some(tow), Some(intercept))
}

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
    20 -> "PH - Make torpedoes",
    21 -> "PH - Lay minefield",
    23 -> "PH - Scoop torpedoes",
    24 -> "PH - Load and make torpedoes",
    25 -> "PH - Beam down money",
    26 -> "PH - Transfer torpedoes to ship",
    27 -> "PH - Transfer fighters to ship",
    28 -> "PH - Transfer money to ship",
    29 -> "PH - Standard super spy",
    30 -> "PH - Cloak",
    31 -> "PH - Extended mission",
    33 -> "PH - Beam up money",
    34 -> "PH - Beam up clans",
    35 -> "PH - Beam up multiple",
    36 -> "PH - Add mines to field"
  ) ++ (
    if(race.value == 7) Seq(
      22 -> "Lay web minefield",
      37 -> "PH - Add web mines to field"
    ) else Seq()
  ) ++ (
    if(race.value == 9 || race.value == 10 || race.value == 11) Seq(32 -> "PH - Gather-build fighters") else Seq()
  )

  private val phost4Missions = Map(
    38 -> "PH4 - Training",
    39 -> "PH4 - Exchange crew",
    40 -> "PH4 - Repair ship"
  )

  val argementRequiremets = Map(
    21 -> MissionRequirement(InterceptArgument("Torpedo count"), TowArgument("Owner race")),
    22 -> MissionRequirement(InterceptArgument("Torpedo count"), TowArgument("Owner race")),
    23 -> MissionRequirement(InterceptArgument("Max torpedoes"), TowArgument("Minefield ID")),
    24 -> MissionRequirement(InterceptArgument("Max torpedoes")),
    25 -> MissionRequirement(InterceptArgument("Max credits")),
    26 -> MissionRequirement(InterceptArgument("Ship id"), TowArgument("Max torpedoes")),
    27 -> MissionRequirement(InterceptArgument("Ship id"), TowArgument("Max fighters")),
    28 -> MissionRequirement(InterceptArgument("Ship id"), TowArgument("Max credits")),
    32 -> MissionRequirement(InterceptArgument("Max fighters")),
    33 -> MissionRequirement(InterceptArgument("Max credits")),
    34 -> MissionRequirement(InterceptArgument("Max clans")),
    36 -> MissionRequirement(InterceptArgument("Torpedo count"), TowArgument("Minefield id")),
    37 -> MissionRequirement(InterceptArgument("Torpedo count"), TowArgument("Minefield id")),
    38 -> MissionRequirement(InterceptArgument("Spend supplies")),
    29 -> MissionRequirement(InterceptArgument("Ship id"), TowArgument("Crew")),
    30 -> MissionRequirement(InterceptArgument("Ship id"))
  )

  val all = classicMissions ++
    (if(hostType == PHost3 || hostType == PHost4) phostMissions else Map()) ++
    (if(hostType == PHost4) phost4Missions else Map())
  
  def get(mission: Int) = all(mission)
}
