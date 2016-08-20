package replanets.ui.actions

/**
  * Created by mgirkin on 06/08/2016.
  */
class Actions(
  val selectStarbase: SelectBase,
  val selectPlanet: SelectPlanet,
  val setFcode: SetFcode,
  val showBuildShipView: () => Unit,
  val showMapView: () => Unit
)
