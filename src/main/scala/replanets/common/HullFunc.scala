package replanets.common

class HullFunc protected (
  val id: Int,
  val displayName: String,
  val hullFuncTxtCode: String
)

//THost
case object Alchemy extends HullFunc(0, "Alchemy", "Alchemy")
case object Refinery extends HullFunc(1, "Neutronic refinery", "Refinery")
case object AdvancedRefinery extends HullFunc(2, "Advanced refinery", "AdvancedRefinery")
case object HeatsTo50 extends HullFunc(3, "Terraforming (heats to 50)", "HeatsTo50")
case object CoolsTo50 extends HullFunc(4, "Terraforming (cools to 50)", "CoolsTo50")
case object HeatsTo100 extends HullFunc(5, "Terraforming (heats to 100)", "HeatsTo100")
case object Hyperdrive extends HullFunc(6, "Hyperdrive", "Hyperdrive")
case object Gravitonic extends HullFunc(7, "Gravitonic accelerators", "Gravitonic")
case object ScansAllWormholes extends HullFunc(8, "Advanced wormhole scanner", "ScansAllWormholes")
case object Gambling extends HullFunc(9, "Gambling", "Gambling")
case object AntiCloak extends HullFunc(10, "Anti-cloak", "AntiCloak")
case object ImperialAssault extends HullFunc(11, "Imperial assault", "ImperialAssault")
case object Chunneling extends HullFunc(12, "Firecloud chunnel", "Chunneling")
case object Ramscoop extends HullFunc(13, "Ramscoop", "Ramscoop")
case object FullBioscan extends HullFunc(14, "Advanced bioscanner", "FullBioscan")
case object AdvancedCloak extends HullFunc(15, "Advanced cloak", "AdvancedCloak")
case object Cloak extends HullFunc(16, "Cloak", "Cloak")
case object Bioscan extends HullFunc(17, "Bioscanner", "Bioscan")
case object GloryDeviceLowDamage extends HullFunc(18, "Advanced glory device", "GloryDeviceLowDamage")
case object GloryDeviceHighDamage extends HullFunc(19, "Glory device", "GloryDeviceHighDamage")

//Phost 4.0+
case object Unclonable extends HullFunc(20, "Unclonable", "Unclonable")
case object CloneOnce extends HullFunc(21, "Clonable only once", "CloneOnce")
case object Ungiveable extends HullFunc(22, "Cannot be given away", "Ungiveable")
case object GiveOnce extends HullFunc(23, "Can be given away once", "GiveOnce")
case object Level2Tow extends HullFunc(24, "Stronger tractor beam", "Level2Tow")

//Phost 4.0i+
case object Tow extends HullFunc(25, "Tractor beam", "Tow")
case object ChunnelSelf extends HullFunc(26, "Chunnel itself", "ChunnelSelf")
case object ChunnelOthers extends HullFunc(27, "Chunnel others", "ChunnelOthers")
case object ChunnelTarget extends HullFunc(28, "Chunnel target", "ChunnelTarget")
case object PlanetImmunity extends HullFunc(29, "Immune to ATT/NUK", "PlanetImmunity")
case object OreCondenser extends HullFunc(30, "Ore condenser", "OreCondenser")
case object Boarding extends HullFunc(31, "Tow-capture", "Boarding")
case object AntiCloakImmunity extends HullFunc(32, "Immune to anti-cloak", "AntiCloakImmunity")
case object Academy extends HullFunc(33, "Academy", "Academy")
case object Repairs extends HullFunc(34, "Repairs", "Repairs")
case object FullWeaponry extends HullFunc(35, "Full weaponry", "FullWeaponry")
case object HardenedEngines extends HullFunc(36, "Hardened engines", "HardenedEngines")

//Phost 4.0j+
case object Commander extends HullFunc(37, "Commander", "Commander")
case object IonShield extends HullFunc(38, "Ion shield", "IonShield")
case object HardenedCloak extends HullFunc(39, "Hardened cloak", "HardenedCloak")

//Phost 4.0k+
case object AdvancedAntiCloak extends HullFunc(40, "Advanced anti-cloak", "AdvancedAntiCloak")