package replanets.ui

import replanets.common._
import replanets.model.Game
import replanets.ui.actions.Actions
import replanets.ui.viewmodels.ViewModel

import scalafx.Includes._
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{GridPane, HBox, VBox}

class BuildShipView(
  val game: Game,
  val viewModel: ViewModel,
  val actions: Actions
) extends VBox {

  viewModel.selectedObjectChaged += onSelectedObjectChanged
  viewModel.objectChanged += onModelObjectChanged

  private val data = ObjectProperty[Option[Starbase]](None)
  private val selectedHull = ObjectProperty[HullspecItem](game.specs.getRaceHulls(game.playingRace).sortBy(_.techLevel).head)
  private val selectedEngine = ObjectProperty[EngspecItem](game.specs.engineSpecs.head)
  private val selectedBeam = ObjectProperty[BeamspecItem](game.specs.beamSpecs.head)
  private val selectedLauncher = ObjectProperty[TorpspecItem](game.specs.torpSpecs.head)
  private val beamsToBuild = IntegerProperty(selectedHull.value.maxBeamWeapons)
  private val launchersToBuild = IntegerProperty(selectedHull.value.maxTorpedoLaunchers)

  selectedHull.onChange({
    beamsToBuild.value = selectedHull.value.maxBeamWeapons
    launchersToBuild.value = selectedHull.value.maxTorpedoLaunchers
  })

  styleClass = Seq("buildShipView")

  val techLevels = new VBox(
    new Label("Tech levels"),
    new GridPane {
      add(new Label("Hulls:"), 0, 0)
      add(bindedLabel(_.hullsTech.toString), 1, 0)
      add(new Label("Engines:"), 0, 1)
      add(bindedLabel(_.engineTech.toString), 1, 1)
      add(new Label("Beams:"), 0, 2)
      add(bindedLabel(_.beamTech.toString), 1, 2)
      add(new Label("Torps:"), 0, 3)
      add(bindedLabel(_.torpedoTech.toString), 1, 3)
    }
  )

  val resources = new VBox(
    new Label("Resources"),
    new GridPane {
      add(new Label("Tri:"), 0, 0)
      add(bindedLabel(_.planet.surfaceMinerals.tritanium.toString), 1, 0)
      add(new Label("Dur:"), 0, 1)
      add(bindedLabel(_.planet.surfaceMinerals.duranium.toString), 1, 1)
      add(new Label("Mol:"), 0, 2)
      add(bindedLabel(_.planet.surfaceMinerals.molybdenium.toString), 1, 2)
      add(new Label("Money:"), 0, 3)
      add(bindedLabel(_.planet.money.toString), 1, 3)
      add(new Label("Supp:"), 0, 4)
      add(bindedLabel(_.planet.supplies.toString), 1, 4)
    }
  )

  val hullsList = new ShipItemsView[HullspecItem](
    "Available hulls:",
    data,
    selectedHull,
    _.hull,
    game.specs.getRaceHulls(game.playingRace).sortBy(_.techLevel),
    _.techLevel,
    _.name,
    _.hullsTech,
    (b, hull, _) => b.storedHulls.getOrElse(hull.id, 0)
  ) {
    styleClass.append("hullList")
  }

  val enginesList =  new ShipItemsView[EngspecItem](
    "Engines:",
    data,
    selectedEngine,
    _.engine,
    game.specs.engineSpecs,
    _.techLevel,
    _.name,
    _.engineTech,
    (b, _, idx) => b.storedEngines(idx)
  ) {
    styleClass.append("engineList")
  }

  val beamsList = new ShipItemsView[BeamspecItem](
    "Beams:",
    data,
    selectedBeam,
    _.beam,
    game.specs.beamSpecs,
    _.techLevel,
    _.name,
    _.beamTech,
    (b, _, idx) => b.storedBeams(idx)
  ) {
    styleClass.append("beamList")
  }

  val launchersList = new ShipItemsView[TorpspecItem](
    "Launchers:",
    data,
    selectedLauncher,
    _.launchers,
    game.specs.torpSpecs,
    _.techLevel,
    _.name,
    _.torpedoTech,
    (b, _, idx) => b.storedLaunchers(idx)
  ) {
    styleClass.append("launcherList")
  }

  val info = new CurrentHullInfoView(selectedHull, beamsToBuild, launchersToBuild)

  val calculations = new CalculationsView(data, selectedHull, selectedEngine, selectedBeam, beamsToBuild, selectedLauncher, launchersToBuild)

  val btnStartStopConstruction = new Button{
    text <== createStringBinding(() => if(data.value.map(_.shipBeingBuilt.isDefined).getOrElse(false)) "Stop construction" else "Start construction", data)
    onAction = (e: ActionEvent) => startStopConstruction()
  }

  children = Seq(
    new HBox(
      info,
      techLevels,
      resources
    ),
    new HBox(
      hullsList,
      new VBox(
        new HBox(
          enginesList,
          beamsList,
          launchersList
        ),
        calculations
      )
    ),
    new HBox {
      alignment = Pos.CenterRight
      children = Seq(
        new Button {
          text = "Exit"
          onAction = (e: ActionEvent) => handleExitButton()
        },
        btnStartStopConstruction
      )
    }
  )

  private def startStopConstruction() = {
    for(base <- data.value) {
      if(base.shipBeingBuilt.isDefined) {
        data.value.foreach( base =>
          actions.stopShipConstruction(base)
        )
      } else {
        data.value.foreach(base =>
          actions.buildShip(base, ShipBuildOrder(
            selectedHull.value, selectedEngine.value,
            selectedBeam.value, beamsToBuild.value,
            selectedLauncher.value, launchersToBuild.value))
        )
      }
    }
  }

  private def bindedLabel(extractor: (Starbase) => String) = new Label("???") {
    text <== createStringBinding(() => {
      data.value.map(extractor).getOrElse("")
    }, data)
  }

  private def handleExitButton() = actions.showMapView()

  private def onSelectedObjectChanged(x: Unit): Unit = {
    viewModel.selectedObject.foreach { so =>
      so match {
        case MapObject.Starbase(id, _, _) => rebindTo(PlanetId(id))
        case _ => data.value = None
      }
    }
  }

  private def onModelObjectChanged(mo: MapObject): Unit = {
    for(base <- data.value) {
      mo match {
        case MapObject.Starbase(id, _, _) if PlanetId(id) == base.id => rebindTo(PlanetId(id))
        case MapObject.Planet(id, _, _) if PlanetId(id) == base.id => rebindTo(PlanetId(id))
          data.value = Some(base)
      }
    }
  }

  private def rebindTo(id: PlanetId) = {
    val base = game.turnInfo(viewModel.turnShown).getStarbaseState(id)(game.specs)
    data.value = Some(base)
  }
}