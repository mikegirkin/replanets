package replanets.ui

import replanets.common._

import scalafx.beans.property.ObjectProperty
import scalafx.geometry.HPos
import scalafx.scene.control.Label
import scalafx.scene.layout.{ColumnConstraints, GridPane}

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

  add(Label("Hull"), 1, 0)
  add(Label("Engines"), 2, 0)
  add(Label("Beams"), 3, 0)
  add(Label("Torps"), 4, 0)
  add(Label("Tech"), 5, 0)
  add(Label("TOTAL"), 6, 0)
  add(Label("Planet"), 7, 0)
  add(Label("Money:"), 0, 1)
  add(lblHullMoneyCost, 1, 1)
  add(lblEngineMoneyCost, 2, 1)
  add(lblBeamsMoneyCost, 3, 1)
  add(lblTorpsMoneyCost, 4, 1)
  add(lblTechMoneyCost, 5, 1)
  add(lblTotalMoneyCost, 6, 1)
  add(Label("Tri:"), 0, 2)
  add(lblHullTriCost, 1, 2)
  add(lblEngineTriCost, 2, 2)
  add(lblBeamsTriCost, 3, 2)
  add(lblTorpsTriCost, 4, 2)
  add(lblTotalTriCost, 6, 2)
  add(Label("Dur:"), 0, 3)
  add(lblHullDurCost, 1, 3)
  add(lblEngineDurCost, 2, 3)
  add(lblBeamsDurCost, 3, 3)
  add(lblTorpsDurCost, 4, 3)
  add(lblTotalDurCost, 6, 3)
  add(Label("Mol:"), 0, 4)
  add(lblHullMolCost, 1, 4)
  add(lblEngineMolCost, 2, 4)
  add(lblBeamsMolCost, 3, 4)
  add(lblTorpsMolCost, 4, 4)
  add(lblTotalMolCost, 6, 4)

  private def renewData(): Unit = {
    val hull = selectedHull.value
    lblHullMoneyCost.text = hull.cost.money.toString
    lblHullTriCost.text = hull.cost.tri.toString
    lblHullDurCost.text = hull.cost.dur.toString
    lblHullMolCost.text = hull.cost.mol.toString

    val engine = selectedEngines.value
    val engineNumber = hull.enginesNumber
    lblEngineMoneyCost.text = (engine.cost.money * engineNumber).toString
    lblEngineTriCost.text = (engine.cost.tri * engineNumber).toString
    lblEngineDurCost.text = (engine.cost.dur * engineNumber).toString
    lblEngineMolCost.text = (engine.cost.mol * engineNumber).toString

    val beam = selectedBeams.value
    val beamNumber = hull.maxBeamWeapons
    lblBeamsMoneyCost.text = (beam.cost.money * beamNumber).toString
    lblBeamsTriCost.text = (beam.cost.tri * beamNumber).toString
    lblBeamsDurCost.text = (beam.cost.dur * beamNumber).toString
    lblBeamsMolCost.text = (beam.cost.mol * beamNumber).toString

    val torps = selectedLaunchers.value
    val torpNumber = hull.maxTorpedoLaunchers
    lblTorpsMoneyCost.text = (torps.launcherCost.money * torpNumber).toString
    lblTorpsTriCost.text = (torps.launcherCost.tri * torpNumber).toString
    lblTorpsDurCost.text = (torps.launcherCost.dur * torpNumber).toString
    lblTorpsMolCost.text = (torps.launcherCost.mol * torpNumber).toString

    val techCost = 0
  }

  renewData()

  selectedHull.onChange(renewData())
  selectedEngines.onChange(renewData())
  selectedBeams.onChange(renewData())
  selectedLaunchers.onChange(renewData())
}
