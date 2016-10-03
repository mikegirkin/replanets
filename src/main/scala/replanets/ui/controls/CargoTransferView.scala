package replanets.ui.controls

import replanets.common._
import replanets.model.{Cargo, CargoHold, Game}
import replanets.ui.MapObject
import replanets.ui.actions.Actions
import replanets.ui.viewmodels.ViewModel

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.{HPos, Pos}
import scalafx.scene.control.{Label, ListView}
import scalafx.scene.layout.{ColumnConstraints, GridPane, VBox}

class CargoTransferView(
  game: Game,
  viewModel: ViewModel,
  actions: Actions
) extends VBox {

  private val sourceCargo = ObjectProperty[CargoHold](CargoHold.zero)
  private val destinationCargo = ObjectProperty[CargoHold](CargoHold.zero)
  private var isSource: MapObject => Boolean = (_) => false
  private var isDestination: MapObject => Boolean = (_) => false
  private var onSourceUpdated: () => Unit = () => {}
  private var onDestinationUpdated: () => Unit = () => {}

  private var onChange: Cargo => Unit = (_) => Unit

  viewModel.objectChanged += onObjectChanged

  styleClass = Seq("popupOuter")

  val lblTorpsHeader = new Label("Torps:")
  val lblFightersHeader = new Label("Fighters:")
  val lblMoneyHeader = new Label("Money:")

  val lblNeuSource = new Label("???")
  val lblTriSource = new Label("???")
  val lblDurSource = new Label("???")
  val lblMolSource = new Label("???")
  val lblSuppliesSource = new Label("???")
  val lblColonistsSource = new Label("???")
  val lblMoneySource = new Label("???")
  val lblTorpsSource = new Label("???")
  val lblFightersSource = new Label("???")

  val lblNeuTarget = new Label("???")
  val lblTriTarget = new Label("???")
  val lblDurTarget = new Label("???")
  val lblMolTarget = new Label("???")
  val lblSuppliesTarget = new Label("???")
  val lblColonistsTarget = new Label("???")
  val lblMoneyTarget = new Label("???")
  val lblTorpsTarget = new Label("???")
  val lblFightersTarget = new Label("???")

  private def boundedDeltaForCargo(valueExtracor: Cargo => Int)(delta: Int): Int = {
    if(delta < 0) {
      -Seq(sourceCargo.value.cargoCapacityLeft, valueExtracor(destinationCargo.value.cargo), -delta, sourceCargo.value.maxIndividual - valueExtracor(sourceCargo.value.cargo)).min
    } else {
      Seq(destinationCargo.value.cargoCapacityLeft, valueExtracor(sourceCargo.value.cargo), delta, destinationCargo.value.maxIndividual - valueExtracor(destinationCargo.value.cargo)).min
    }
  }

  val spinnerNeu = transferSpinnerButtons(delta => {
    val boundedDelta = if (delta < 0) {
      -Seq(sourceCargo.value.neuCapacityLeft, destinationCargo.value.cargo.neu, -delta, sourceCargo.value.maxIndividual - sourceCargo.value.cargo.neu).min
    } else {
      Seq(destinationCargo.value.neuCapacityLeft, sourceCargo.value.cargo.neu, delta, destinationCargo.value.maxIndividual - destinationCargo.value.cargo.neu).min
    }
    Cargo(neu = boundedDelta)
  })

  val spinnerTri = transferSpinnerButtons(delta => Cargo(tri = boundedDeltaForCargo(_.tri)(delta)))
  val spinnerDur = transferSpinnerButtons(delta => Cargo(dur = boundedDeltaForCargo(_.dur)(delta)))
  val spinnerMol = transferSpinnerButtons(delta => Cargo(mol = boundedDeltaForCargo(_.mol)(delta)))
  val spinnerSupplies = transferSpinnerButtons(delta => Cargo(supplies = boundedDeltaForCargo(_.supplies)(delta)))
  val spinnerColonists = transferSpinnerButtons(delta => Cargo(colonists = boundedDeltaForCargo(_.colonists)(delta)))
  val spinnerTorps = transferSpinnerButtons(delta => Cargo(torps = boundedDeltaForCargo(_.torps)(delta)))
  val spinnerFighters = transferSpinnerButtons(delta => Cargo(fighters = boundedDeltaForCargo(_.fighters)(delta)))

  val spinnerMoney = transferSpinnerButtons(delta => {
    val boundedDelta = if(delta < 0) {
      -Seq(destinationCargo.value.cargo.money, -delta, sourceCargo.value.maxIndividual - sourceCargo.value.cargo.money).min
    } else {
      Seq(sourceCargo.value.cargo.money, delta, destinationCargo.value.maxIndividual - destinationCargo.value.cargo.money).min
    }
    Cargo(money = boundedDelta)
  })

  val grid = new GridPane {
    columnConstraints = Seq(
      new ColumnConstraints {
        percentWidth = 32
        halignment = HPos.Right
      },
      new ColumnConstraints {
        percentWidth = 18
      },
      new ColumnConstraints {
        percentWidth = 15
        halignment = HPos.Center
      },
      new ColumnConstraints {
        percentWidth = 35
        halignment = HPos.Left
      }
    )

    add(new Label("Neu:") {
      alignment = Pos.CenterRight
    }, 0, 0)
    add(new Label("Tri:"), 0, 1)
    add(new Label("Dur:"), 0, 2)
    add(new Label("Mol:"), 0, 3)
    add(new Label("Supplies:"), 0, 4)
    add(new Label("Colonists:"), 0, 5)
    add(lblMoneyHeader, 0, 6)
    add(lblTorpsHeader, 0, 7)
    add(lblFightersHeader, 0, 7)
    add(lblNeuSource, 1, 0)
    add(lblTriSource, 1, 1)
    add(lblDurSource, 1, 2)
    add(lblMolSource, 1, 3)
    add(lblSuppliesSource, 1, 4)
    add(lblColonistsSource, 1, 5)
    add(lblMoneySource, 1, 6)
    add(lblTorpsSource, 1, 7)
    add(lblFightersSource, 1, 7)
    add(spinnerNeu, 2, 0)
    add(spinnerTri, 2, 1)
    add(spinnerDur, 2, 2)
    add(spinnerMol, 2, 3)
    add(spinnerSupplies, 2, 4)
    add(spinnerColonists, 2, 5)
    add(spinnerMoney, 2, 6)
    add(spinnerTorps, 2, 7)
    add(spinnerFighters, 2, 7)
    add(lblNeuTarget, 3, 0)
    add(lblTriTarget, 3, 1)
    add(lblDurTarget, 3, 2)
    add(lblMolTarget, 3, 3)
    add(lblSuppliesTarget, 3, 4)
    add(lblColonistsTarget, 3, 5)
    add(lblMoneyTarget, 3, 6)
    add(lblTorpsTarget, 3, 7)
    add(lblFightersTarget, 3, 7)
  }

  val lblTotalCargoLoaded = new Label {
    text <== createStringBinding(() => s"${sourceCargo.value.cargo.weight}", sourceCargo)
  }

  val lblTotalCargoPossible = new Label {
    text <== createStringBinding(() => sourceCargo.value.cargoCapacityTotal.toString, sourceCargo)
  }

  children = Seq(
    grid
  )

  lblNeuSource.text <== bindToSource((c) => c.neu)
  lblTriSource.text <== bindToSource(_.tri)
  lblDurSource.text <== bindToSource(_.dur)
  lblMolSource.text <== bindToSource(_.mol)
  lblSuppliesSource.text <== bindToSource(_.supplies)
  lblColonistsSource.text <== bindToSource(_.colonists)
  lblMoneySource.text <== bindToSource(_.money)
  lblTorpsSource.text <== bindToSource(_.torps)

  lblNeuTarget.text <== bindToTarget(_.neu)
  lblTriTarget.text <== bindToTarget(_.tri)
  lblDurTarget.text <== bindToTarget(_.dur)
  lblMolTarget.text <== bindToTarget(_.mol)
  lblSuppliesTarget.text <== bindToTarget(_.supplies)
  lblColonistsTarget.text <== bindToTarget(_.colonists)
  lblMoneyTarget.text <== bindToTarget(_.money)
  lblTorpsTarget.text <== bindToTarget(_.torps)

  private def isOwnShip(source: OwnShip)(mapObject: MapObject): Boolean = {
    mapObject match {
      case MapObject.OwnShip(id, _, _, _) => source.id.value == id
      case _ => false
    }
  }

  private def isPlanet(planet: Planet)(mapObject: MapObject): Boolean = {
    hasPlanetId(planet.id)(mapObject)
  }

  private def hasPlanetId(planetId: PlanetId)(mapObject: MapObject): Boolean = {
    mapObject match {
      case MapObject.Planet(id, _, _) => planetId.value == id
      case _ => false
    }
  }

  private def isOtherShip(target: ShipId)(mapObject: MapObject): Boolean = {
    mapObject match {
      case MapObject.Target(id, _, _, _) => target.value == id
      case _ => false
    }
  }

  private def setData(source: OwnShip)(setDestination: => Unit) = {
    this.isSource = isOwnShip(source) _
    onSourceUpdated = () => {
      val sourceAfterCommands = game.turnInfo(viewModel.turnShown).stateAfterCommands.ownShips(source.id)
      sourceCargo.value = sourceAfterCommands.cargoHold
    }
    setDestination
    onSourceUpdated()
    onDestinationUpdated()
  }

  def setData(source: OwnShip, target: Planet): Unit = {
    setData(source) {
      this.isDestination = isPlanet(target) _
      if(target.owner == source.owner) {
        //From own ship to own planet
        val base = game.turnInfo(viewModel.turnShown).stateAfterCommands.bases.get(target.id)
        val torpsTransferAvailable = (
          for (
            tt <- source.torpsType;
            b <- base
          ) yield tt.techLevel <= b.torpedoTech
        ).getOrElse(false)
        val fightersTransferAvailable = base.isDefined && source.isCarrier
        setTransferControlsVisibility(moneyTransferAvailable = true, torpsTransferAvailable, fightersTransferAvailable)
        onDestinationUpdated = () => {
          val planetAfterCommands = game.turnInfo(viewModel.turnShown).stateAfterCommands.planets(target.id)
          destinationCargo.value = planetAfterCommands.cargoHold
        }
        onChange = transfer => actions.shipToOwnPlanetTransfer(source, target, transfer)
      }
    }
  }

  //Unowned or enemy planet
  def setData(source: OwnShip, target: PlanetId): Unit =
    setData(source) {
      this.isDestination = hasPlanetId(target) _
      onDestinationUpdated = () => {
        val sourceAfterCommands = game.turnInfo(viewModel.turnShown).stateAfterCommands.ownShips(source.id)
        destinationCargo.value = sourceAfterCommands.transferToPlanet.map { _.asCargoHold }.getOrElse(CargoHold(Int.MaxValue, Int.MaxValue, Int.MaxValue, Cargo.zero))
      }
      setTransferControlsVisibility(false, false, false)
      onChange = transfer => actions.shipToOtherPlanetTransfer(source, target, transfer)
    }

  def setData(source: OwnShip, target: OwnShip): Unit = {
    setData(source) {
      this.isDestination = isOwnShip(target) _
      onDestinationUpdated = () => {
        val targetAfterCommands = game.turnInfo(viewModel.turnShown).stateAfterCommands.ownShips(target.id)
        this.destinationCargo.value = targetAfterCommands.cargoHold
      }
      val torpsTransferAvailable = source.torpsType == target.torpsType
      val fightersTransferAvailable = source.isCarrier && target.isCarrier
      setTransferControlsVisibility(moneyTransferAvailable = true, torpsTransferAvailable, fightersTransferAvailable)
      onChange = transfer => actions.shipToOwnShipTransfer(source, target, transfer)
    }
  }

  //Enemy ship
  def setData(source: OwnShip, target: ShipId): Unit =
    setData(source) {
      isDestination = isOtherShip(target) _
      onDestinationUpdated = () => {
        val sourceAfterCommands = game.turnInfo(viewModel.turnShown).stateAfterCommands.ownShips(source.id)
        destinationCargo.value = sourceAfterCommands.transferToEnemyShip.map { _.asCargoHold }.getOrElse(CargoHold(Int.MaxValue, Int.MaxValue, 10000, Cargo.zero))
      }
      setTransferControlsVisibility(false, false, false)
      onChange = transfer => actions.shipToOtherShipTransfer(source, target, transfer)
    }

  private def setTransferControlsVisibility(
    moneyTransferAvailable: Boolean,
    torpsTransferAvailable: Boolean,
    fightersTransferAvailable: Boolean
  ) = {
    lblTorpsHeader.visible = torpsTransferAvailable
    lblTorpsSource.visible = torpsTransferAvailable
    spinnerTorps.visible = torpsTransferAvailable
    lblTorpsTarget.visible = torpsTransferAvailable

    lblMoneyHeader.visible = moneyTransferAvailable
    lblMoneySource.visible = moneyTransferAvailable
    lblMoneyTarget.visible = moneyTransferAvailable
    spinnerMoney.visible = moneyTransferAvailable

    lblFightersHeader.visible = fightersTransferAvailable
    lblFightersSource.visible = fightersTransferAvailable
    lblFightersTarget.visible = fightersTransferAvailable
    spinnerFighters.visible = fightersTransferAvailable
  }

  private def bindToSource(extractor: Cargo => Int) = {
    createStringBinding(() => {
      val value = extractor(sourceCargo.value.cargo)
      if(value == 0) "" else value.toString
    }, sourceCargo)
  }

  private def bindToTarget(extractor: Cargo => Int) = {
    createStringBinding(() => {
      val value = extractor(destinationCargo.value.cargo)
      if(value == 0) "" else value.toString
    }, destinationCargo)
  }

  private def transferSpinnerButtons(cargoCreator: Int => Cargo) =
    new SpinnerButtons(
      (delta) => {
        val transferringCargo = cargoCreator(delta)
        onChange(transferringCargo)
      },
      leftText = "<",
      rightText = ">"
    )

  private def onObjectChanged(mapObject: MapObject): Unit = {
    if(isSource(mapObject)) onSourceUpdated()
    if(isDestination(mapObject)) onDestinationUpdated()
  }
}