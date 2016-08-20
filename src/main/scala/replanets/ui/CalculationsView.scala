package replanets.ui

import replanets.common._

import scalafx.beans.property.ObjectProperty
import scalafx.geometry.HPos
import scalafx.scene.control.Label
import scalafx.scene.layout.{ColumnConstraints, GridPane}
import scalafx.scene.paint.Color

class NonNegativeIntLabel(initial: Int = 0) extends Label {
  def setValue(v: Int): Unit = {
    if(v >= 0) {
      text = v.toString
      textFill = Color.DarkGray
    } else {
      text = Math.abs(v).toString
      textFill = Color.OrangeRed
    }
  }
}

class CalculationsView(
  base: ObjectProperty[Option[Starbase]],
  selectedHull: ObjectProperty[HullspecItem],
  selectedEngines: ObjectProperty[EngspecItem],
  selectedBeams: ObjectProperty[BeamspecItem],
  selectedLaunchers: ObjectProperty[TorpspecItem]
) extends GridPane {

  columnConstraints = Seq(
    new ColumnConstraints {
      halignment = HPos.Right
    },
    new ColumnConstraints {
      halignment = HPos.Right
      prefWidth = 40
    },
    new ColumnConstraints {
      halignment = HPos.Right
      prefWidth = 60
    },
    new ColumnConstraints {
      halignment = HPos.Right
      prefWidth = 45
    },
    new ColumnConstraints {
      halignment = HPos.Right
      prefWidth = 45
    },
    new ColumnConstraints {
      halignment = HPos.Right
      prefWidth = 45
    },
    new ColumnConstraints {
      halignment = HPos.Right
      prefWidth = 50
    },
    new ColumnConstraints {
      halignment = HPos.Right
      prefWidth = 45
    },
    new ColumnConstraints {
      halignment = HPos.Right
      prefWidth = 65
    }
  )

  val lblHullMoneyCost = new Label("???")
  val lblHullTriCost = new Label("???")
  val lblHullDurCost = new Label("???")
  val lblHullMolCost = new Label("???")
  val lblEngineMoneyCost = new Label("???")
  val lblEngineTriCost = new Label("???")
  val lblEngineDurCost = new Label("???")
  val lblEngineMolCost = new Label("???")
  val lblBeamsMoneyCost = new Label("???")
  val lblBeamsTriCost = new Label("???")
  val lblBeamsDurCost = new Label("???")
  val lblBeamsMolCost = new Label("???")
  val lblTorpsMoneyCost = new Label("???")
  val lblTorpsTriCost = new Label("???")
  val lblTorpsDurCost = new Label("???")
  val lblTorpsMolCost = new Label("???")
  val lblTechMoneyCost = new Label("???")
  val lblTotalMoneyCost = new Label("???")
  val lblTotalTriCost = new Label("???")
  val lblTotalDurCost = new Label("???")
  val lblTotalMolCost = new Label("???")
  val lblPlanetMoney = new Label("???")
  val lblPlanetTri = new Label("???")
  val lblPlanetDur = new Label("???")
  val lblPlanetMol = new Label("???")
  val lblRemainingMoney = new NonNegativeIntLabel()
  val lblRemainingTri = new NonNegativeIntLabel()
  val lblRemainingDur = new NonNegativeIntLabel()
  val lblRemainingMol = new NonNegativeIntLabel()

  add(Label("Hull"), 1, 0)
  add(Label("Engines"), 2, 0)
  add(Label("Beams"), 3, 0)
  add(Label("Torps"), 4, 0)
  add(Label("Tech"), 5, 0)
  add(Label("TOTAL"), 6, 0)
  add(Label("Planet"), 7, 0)
  add(Label("Remaining"), 8, 0)
  add(Label("Money:"), 0, 1)
  add(lblHullMoneyCost, 1, 1)
  add(lblEngineMoneyCost, 2, 1)
  add(lblBeamsMoneyCost, 3, 1)
  add(lblTorpsMoneyCost, 4, 1)
  add(lblTechMoneyCost, 5, 1)
  add(lblTotalMoneyCost, 6, 1)
  add(lblPlanetMoney, 7, 1)
  add(lblRemainingMoney, 8, 1)
  add(Label("Tri:"), 0, 2)
  add(lblHullTriCost, 1, 2)
  add(lblEngineTriCost, 2, 2)
  add(lblBeamsTriCost, 3, 2)
  add(lblTorpsTriCost, 4, 2)
  add(lblTotalTriCost, 6, 2)
  add(lblPlanetTri, 7, 2)
  add(lblRemainingTri, 8, 2)
  add(Label("Dur:"), 0, 3)
  add(lblHullDurCost, 1, 3)
  add(lblEngineDurCost, 2, 3)
  add(lblBeamsDurCost, 3, 3)
  add(lblTorpsDurCost, 4, 3)
  add(lblTotalDurCost, 6, 3)
  add(lblPlanetDur, 7, 3)
  add(lblRemainingDur, 8, 3)
  add(Label("Mol:"), 0, 4)
  add(lblHullMolCost, 1, 4)
  add(lblEngineMolCost, 2, 4)
  add(lblBeamsMolCost, 3, 4)
  add(lblTorpsMolCost, 4, 4)
  add(lblTotalMolCost, 6, 4)
  add(lblPlanetMol, 7, 4)
  add(lblRemainingMol, 8, 4)

  private def bindCostLabels(money: Label, tri: Label, dur: Label, mol: Label, cost: Cost) = {
    money.text = cost.money.toString
    tri.text = cost.tri.toString
    dur.text = cost.dur.toString
    mol.text = cost.mol.toString
  }

  private def renewData(): Unit = {
    val hull = selectedHull.value
    val engine = selectedEngines.value
    val beam = selectedBeams.value
    val torps = selectedLaunchers.value
    val numberOfBeams = hull.maxBeamWeapons
    val numberOfLaunchers = hull.maxTorpedoLaunchers

    for (
      thisbase <- base.value;
      shipCost = thisbase.shipCostAtStarbase(hull, engine, beam, numberOfBeams, torps, numberOfLaunchers)
    ) {
      bindCostLabels(lblHullMoneyCost, lblHullTriCost, lblHullDurCost, lblHullMolCost, shipCost.hullCost)
      bindCostLabels(lblEngineMoneyCost, lblEngineTriCost, lblEngineDurCost, lblEngineMolCost, shipCost.enginesCost)
      bindCostLabels(lblBeamsMoneyCost, lblBeamsTriCost, lblBeamsDurCost, lblBeamsMolCost, shipCost.beamsCost)
      bindCostLabels(lblTorpsMoneyCost, lblTorpsTriCost, lblTorpsDurCost, lblTorpsMolCost, shipCost.launcherCost)
      lblTechMoneyCost.text = shipCost.techCost.money.toString
      bindCostLabels(lblTotalMoneyCost, lblTotalTriCost, lblTotalDurCost, lblTotalMolCost, shipCost.total)
      val planetAmount = Cost(
        thisbase.planet.surfaceMinerals.tritanium,
        thisbase.planet.surfaceMinerals.duranium,
        thisbase.planet.surfaceMinerals.molybdenium,
        thisbase.planet.money + thisbase.planet.supplies
      )
      bindCostLabels(lblPlanetMoney, lblPlanetTri, lblPlanetDur, lblPlanetMol, planetAmount)
      val remaining = planetAmount.sub(shipCost.total)
      lblRemainingMoney setValue remaining.money
      lblRemainingTri setValue remaining.tri
      lblRemainingDur setValue remaining.dur
      lblRemainingMol setValue remaining.mol
    }
  }

  base.onChange(renewData())
  selectedHull.onChange(renewData())
  selectedEngines.onChange(renewData())
  selectedBeams.onChange(renewData())
  selectedLaunchers.onChange(renewData())

  renewData()
}
