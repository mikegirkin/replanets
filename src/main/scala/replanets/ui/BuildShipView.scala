package replanets.ui

import javafx.scene.{paint => jfxsp}

import replanets.common.Minerals
import replanets.model.{BaseId, Game}
import replanets.ui.viewmodels.ViewModel

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.Label
import scalafx.scene.layout.{GridPane, HBox, VBox}


case class StarbaseState(
  hullsTech: Int,
  engineTech: Int,
  beamsTech: Int,
  torpTech: Int,
  minerals: Minerals,
  money: Int,
  supplies: Int
)

object StarbaseState {
  def default = StarbaseState(0, 0, 0, 0, Minerals(0, 0, 0, 0), 0, 0)
}

class BuildShipView(
  val game: Game,
  val viewModel: ViewModel
) extends VBox {

  val data = new ObjectProperty[StarbaseState](this, "starbase", StarbaseState.default)

  styleClass = Seq("buildShipView")

  val techLevels = new VBox(
    new Label("Tech levels"),
    new GridPane {
      add(new Label("Hulls:"), 0, 0)
      add(bindedLabel(_.hullsTech.toString), 1, 0)
      add(new Label("Engines:"), 0, 1)
      add(bindedLabel(_.engineTech.toString), 1, 1)
      add(new Label("Beams:"), 0, 2)
      add(bindedLabel(_.beamsTech.toString), 1, 2)
      add(new Label("Torps:"), 0, 3)
      add(bindedLabel(_.torpTech.toString), 1, 3)
    }
  )

  val resources = new VBox(
    new Label("Resources"),
    new GridPane {
      add(new Label("Tri:"), 0, 0)
      add(bindedLabel(_.minerals.tritanium.toString), 1, 0)
      add(new Label("Dur:"), 0, 1)
      add(bindedLabel(_.minerals.duranium.toString), 1, 1)
      add(new Label("Mol:"), 0, 2)
      add(bindedLabel(_.minerals.molybdenium.toString), 1, 2)
      add(new Label("Money:"), 0, 3)
      add(bindedLabel(_.money.toString), 1, 3)
      add(new Label("Supp:"), 0, 4)
      add(bindedLabel(_.supplies.toString), 1, 4)
    }
  )

  val hullsList = new VBox {
    children = Seq(
      new Label("Available hulls:")
    ) ++
    game.specs.getRaceHulls(game.playingRace).sortBy(_.techLevel)
      .map { hull =>
        new Label {
          text = hull.name
          textFill <== createObjectBinding[jfxsp.Color](() => if(hull.techLevel > data.value.hullsTech) jfxsp.Color.DARKGRAY else jfxsp.Color.WHITE, data)
        }
      }
  }
  val enginesList = new VBox {
    children = Seq(
      new Label("Engines:")
    ) ++
    game.specs.engineSpecs.sortBy(_.techLevel)
      .map { engine =>
        new Label {
          text = engine.name
          textFill <== createObjectBinding[jfxsp.Color](() => if(engine.techLevel > data.value.engineTech) jfxsp.Color.DARKGRAY else jfxsp.Color.WHITE, data)
        }
      }
  }
  val beamsList = new VBox {
    children = Seq(
      new Label("Beams:")
    ) ++
    game.specs.beamSpecs.sortBy(_.techLevel)
      .map { beam =>
        new Label {
          text = beam.name
          textFill <== createObjectBinding[jfxsp.Color](() => if(beam.techLevel > data.value.beamsTech) jfxsp.Color.DARKGRAY else jfxsp.Color.WHITE, data)
        }
      }
  }
  val launchersList = new VBox {
    children = Seq(
      new Label("Launchers:")
    ) ++
      game.specs.torpSpecs.sortBy(_.techLevel)
        .map { launcher =>
          new Label {
            text = launcher.name
            textFill <== createObjectBinding[jfxsp.Color](() => if(launcher.techLevel > data.value.torpTech) jfxsp.Color.DARKGRAY else jfxsp.Color.WHITE, data)
          }
        }
  }
  val calculations = Label("Calculations")


  children = Seq(
    new HBox(
      new VBox(Label("ShipInfoView")),
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
    )
  )

  private def bindedLabel(extractor: (StarbaseState) => String) = new Label("???") {
    text <== createStringBinding(() => extractor(data.value), data)
  }

  def setData(starbaseId: BaseId): Unit = {
    for (
      base <- game.turnSeverData(viewModel.turnShown).bases.find(b => b.baseId == starbaseId.value);
      planet <- game.turnSeverData(viewModel.turnShown).planets.find(p => p.planetId == starbaseId.value)
    ) {
      data.value = StarbaseState(
        base.hullsTech, base.engineTech, base.beamTech, base.torpedoTech,
        planet.surfaceMinerals, planet.money, planet.supplies
      )
    }
  }
}

