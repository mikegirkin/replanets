package replanets.ui

import replanets.common._
import replanets.model.{Game, PlanetId}
import replanets.ui.actions.Actions
import replanets.ui.viewmodels.ViewModel

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{GridPane, HBox, VBox}

class BuildShipView(
  val game: Game,
  val viewModel: ViewModel,
  val actions: Actions
) extends VBox {

  val data = new ObjectProperty[Option[Starbase]](this, "starbase", None)

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
    game.specs.getRaceHulls(game.playingRace).sortBy(_.techLevel),
    _.techLevel,
    _.name,
    _.hullsTech,
    (b, hull, _) => b.storedHulls.getOrElse(hull.id.toShort, 0.toShort).toInt
  ) {
    styleClass.append("hullList")
  }

  val enginesList =  new ShipItemsView[EngspecItem](
    "Engines:",
    data,
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
    game.specs.torpSpecs,
    _.techLevel,
    _.name,
    _.torpedoTech,
    (b, _, idx) => b.storedLaunchers(idx)
  ) {
    styleClass.append("launcherList")
  }

  val info = new CurrentHullInfoView(hullsList.selectedItem)

  val calculations = new CalculationsView(
    data,
    hullsList.selectedItem,
    enginesList.selectedItem,
    beamsList.selectedItem,
    launchersList.selectedItem
  )

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
        new Button {
          text = "Start construction"
        }
      )
    }
  )

  private def bindedLabel(extractor: (Starbase) => String) = new Label("???") {
    text <== createStringBinding(() => {
      data.value.map(extractor).getOrElse("")
    }, data)
  }

  private def handleExitButton() = actions.showMapView()

  def setData(starbaseId: PlanetId): Unit = {
    val base = game.turnSeverData(viewModel.turnShown).bases(starbaseId)
    data.value = Some(base)
  }
}