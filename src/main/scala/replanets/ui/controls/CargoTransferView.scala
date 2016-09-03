package replanets.ui.controls

import replanets.model.{Cargo, CargoHold}

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.{HPos, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.{ColumnConstraints, GridPane, VBox}

abstract class CargoTransferView(
  source: ObjectProperty[CargoHold],
  destination: ObjectProperty[CargoHold],
  onChange: Cargo => Unit
) extends VBox {

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
      -Seq(source.value.cargoCapacityLeft, valueExtracor(destination.value.cargo), -delta, source.value.maxIndividual - valueExtracor(source.value.cargo)).min
    } else {
      Seq(destination.value.cargoCapacityLeft, valueExtracor(source.value.cargo), delta, destination.value.maxIndividual - valueExtracor(destination.value.cargo)).min
    }
  }

  val spinnerNeu = transferSpinnerButtons(delta => {
    val boundedDelta = if (delta < 0) {
      -Seq(source.value.neuCapacityLeft, destination.value.cargo.neu, -delta, source.value.maxIndividual - source.value.cargo.neu).min
    } else {
      Seq(destination.value.neuCapacityLeft, source.value.cargo.neu, delta, destination.value.maxIndividual - destination.value.cargo.neu).min
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
      -Seq(destination.value.cargo.money, -delta, source.value.maxIndividual - source.value.cargo.money).min
    } else {
      Seq(source.value.cargo.money, delta, destination.value.maxIndividual - destination.value.cargo.money).min
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
    text <== createStringBinding(() => s"${source.value.cargo.weight}", source)
  }

  val lblTotalCargoPossible = new Label {
    text <== createStringBinding(() => source.value.cargoCapacityTotal.toString, source)
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

  def setData(
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
      val value = extractor(source.value.cargo)
      if(value == 0) "" else value.toString
    }, source)
  }

  private def bindToTarget(extractor: Cargo => Int) = {
    createStringBinding(() => {
      val value = extractor(destination.value.cargo)
      if(value == 0) "" else value.toString
    }, destination)
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
}
