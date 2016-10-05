package replanets.ui

import replanets.common.{Constants, PlanetId}
import replanets.model.{Game, Starbase}
import replanets.ui.actions.Actions
import replanets.ui.controls.Spinner
import replanets.ui.viewmodels.ViewModel

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control._
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, Pane, VBox}
import scalafx.stage.Popup
import scalafxml.core.macros.sfxml

import replanets.model.StarbaseExtensions._
import replanets.common.NumberExtensions._

trait IBaseInfoView {
  def rootPane: Pane
  def setData(baseId: PlanetId): Unit
}

@sfxml
class BaseInfoView(
  val rootPane: VBox,
  val lblStarbaseId: Label,
  val phDefences: Pane,
  val lblDamage: Label,
  val phFighters: Pane,
  val lblPrimaryOrder: Label,
  val lblEngines: Label,
  val lblHulls: Label,
  val lblBeams: Label,
  val lblTorpedoes: Label,

  val lvStorage: ListView[(Int, String)],

  val actions: Actions,
  val game: Game,
  val viewModel: ViewModel
) extends IBaseInfoView {

  viewModel.objectChanged += handleObjectChanged

  var starbase: ObjectProperty[Option[Starbase]] = ObjectProperty(None)

  val defenceSpinner = new Spinner(
    createStringBinding(() => starbase.value.map(_.defences).getOrElse(0).toString, starbase),
    (delta) => starbase.value.foreach { s =>
      val initiallyHad = game.turnInfo(viewModel.turnShown).initialState.bases(s.id).defences
      val boundedDelta = delta.bounded(initiallyHad - s.defences, s.maxPossibleDefencesBuild())
      actions.baseBuildDefences(s, boundedDelta)
    },
    minLabelWidth = 30
  )

  phDefences.children = Seq(defenceSpinner)

  val fightersSpinner = new Spinner(
    createStringBinding(() => starbase.value.map(_.fightersNumber).getOrElse(0).toString, starbase),
    (delta) => starbase.value.foreach { s =>
      val initiallyHad = game.turnInfo(viewModel.turnShown).initialState.bases(s.id).fightersNumber
      val boundedDelta = delta.bounded(initiallyHad - s.fightersNumber, s.maxPossibleFightersBuild())
      actions.baseBuildFighters(s, boundedDelta)
    },
    minLabelWidth = 30
  )

  phFighters.children = Seq(fightersSpinner)

  val primaryOrderSelector =
    new ListView[(Int, String)] {
      styleClass = Seq("popup", "primaryOrderSelection")
      maxWidth <== rootPane.width - 10
      minWidth <== rootPane.width - 10
      items = ObservableBuffer(Constants.baseMissions.toSeq)
      cellFactory = { _: ListView[(Int, String)] =>
        new ListCell[(Int, String)] {
          item.onChange { (_, _, item) =>
            if(item == null) text = null
            else text = item._2
          }
          onMouseClicked = (e: MouseEvent) => handlePrimaryOrderClicked(item.value)(e)
        }
      }
    }

  val popup = new Popup {
    autoHide = true
    content.add(primaryOrderSelector)
  }

  private def lvItem(amount: Int, name: String) = new HBox {
    children = Seq(
      new Label {
        text = amount.toString
        styleClass = Seq("storageListCount")
      },
      new Label {
        text = name
      })
  }

  private def lvCellFactory = { _: ListView[(Int, String)] =>
    new ListCell[(Int, String)] {
      item.onChange { (_, _, item) =>
        if(item == null) graphic = null
        else graphic = lvItem(item._1, item._2)
      }
    }
  }

  lvStorage.cellFactory = lvCellFactory

  override def setData(baseId: PlanetId): Unit = {
    val turn = viewModel.turnShown
    val base = game.turnInfo(turn).stateAfterCommands.bases(baseId)
    starbase.value = Some(base)

    lblStarbaseId.text = base.id.value.toString
    lblDamage.text = base.damage.toString
    val primaryOrderText = Constants.baseMissions(base.primaryOrder)
    lblPrimaryOrder.text = primaryOrderText
    primaryOrderSelector.selectionModel.delegate.get.select((base.primaryOrder, primaryOrderText))
    lblEngines.text = base.engineTech.toString
    lblHulls.text = base.hullsTech.toString
    lblBeams.text = base.beamTech.toString
    lblTorpedoes.text = base.torpedoTech.toString

    val hulls = base.storedHulls
      .toSeq
      .map{ case (hullId, hullCount) => (hullCount, game.specs.hullSpecs(hullId.value - 1).name) } //TODO: Check if stored hulls are correctly calculated

    val beams = base.storedBeams.zipWithIndex
      .filter { case (beamCount, idx) => beamCount > 0 }
      .map { case (beamCount, idx) => (beamCount, game.specs.beamSpecs(idx).name) }

    val drives = base.storedEngines.zipWithIndex
      .filter { case (driveCount, idx) => driveCount > 0 }
      .map { case (driveCount, idx) => (driveCount, game.specs.engineSpecs(idx).name) }

    val launchers = base.storedLaunchers.zipWithIndex
      .filter { case (launcherCount, idx) => launcherCount > 0 }
      .map { case (launcherCount, idx) => (launcherCount, game.specs.torpSpecs(idx).name) }

    val torpedoes = base.storedTorpedoes.zipWithIndex
      .filter { case (torpedoCount, idx) => torpedoCount > 0 }
      .map { case (torpedoCount, idx) => (torpedoCount, s"${game.specs.torpSpecs(idx).name} torpedoes")}

    lvStorage.items = ObservableBuffer(hulls ++ beams ++ drives ++ launchers ++ torpedoes)
  }

  private def handleObjectChanged(mo: MapObject): Unit = {
    mo match {
      case MapObject.Starbase(id, _, _) if id == starbase.value.map(_.id.value).getOrElse(0) =>
        setData(PlanetId(id))
      case _ =>
    }
  }

  def handlePlanetButton(e: ActionEvent) = actions.selectPlanet.execute()

  def handleBuildShipButton(e: ActionEvent) = actions.showBuildShipView()

  def handleSelectMissionButton(e: ActionEvent) = {
    val pointY = lblPrimaryOrder.localToScreen(0, 0)
    val pointX = rootPane.localToScreen(0, 0)
    popup.show(lblPrimaryOrder.getScene.getWindow, pointX.getX + 2, pointY.getY + lblPrimaryOrder.height.get() + 2)
  }

  def handlePrimaryOrderClicked(item: (Int, String))(e: MouseEvent): Unit = {
    starbase.value.foreach( sb =>
      actions.setPrimaryOrder(sb, item._1)
    )
    popup.hide()
  }
}
