package replanets.common

class HullFunc protected (
  val id: Int,
  val displayName: String,
  val hullFuncTxtCode: String
)

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