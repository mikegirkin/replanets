package replanets.ui

import replanets.common.HullspecItem

import scalafx.beans.property.ObjectProperty
import scalafx.geometry.HPos
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{ColumnConstraints, GridPane, VBox}

class CurrentHullInfoView(
  val currentHull: ObjectProperty[HullspecItem]
) extends VBox {

  styleClass = Seq("currentHullInfoView")

  val lblHullName = new Label("???") {
    styleClass = Seq("hullName")
  }
  val lblTechLevel = new Label("???")
  val lblEnginesNumber = new Label("???")
  val lblBeamsNumber = new Label("???")
  val lblLaunchersNumber = new Label("???")
  val lblHullMass = new Label("???")
  val lblCrew = new Label("???")
  val lblFueltank = new Label("???")
  val lblCargoSpace = new Label("???")
  val lblSpecialFunction = new Label("???") {
    styleClass = Seq("specialFunction")
  }
  val lblTorpedoTubesHeader = new Label("???")
  val lblBeamWeaponsHeader = new Label("???")

  children = Seq(
    lblHullName,
    new GridPane {
      columnConstraints = Seq(
        new ColumnConstraints {
          minWidth = 150
          halignment = HPos.Right
        }
      )

      add(new Label("TechLevel:"), 0, 0)
      add(lblTechLevel, 1, 0)
      add(new Label("Engines:"), 0, 1)
      add(lblEnginesNumber, 1, 1)
      add(lblBeamWeaponsHeader, 0, 2)
      add(lblBeamsNumber, 1, 2)
      add(lblTorpedoTubesHeader, 0, 3)
      add(lblLaunchersNumber, 1, 3)
      add(new Label("Hull mass:"), 0, 4)
      add(lblHullMass, 1, 4)
      add(new Label("Crew:"), 0, 5)
      add(lblCrew, 1, 5)
      add(new Label("Fuel tank:"), 0, 6)
      add(lblFueltank, 1, 6)
      add(new Label("Cargo space:"), 0, 7)
      add(lblCargoSpace, 1, 7)
    },
    lblSpecialFunction
  )


  private def renewData(): Unit = {
    val hull = currentHull.value
    lblHullName.text = hull.name
    lblTechLevel.text = hull.techLevel.toString
    lblEnginesNumber.text = hull.enginesNumber.toString

    if(hull.maxBeamWeapons > 0) {
      lblBeamWeaponsHeader.text = "Beam weapons:"
      lblBeamsNumber.text = hull.maxBeamWeapons.toString
    } else {
      lblBeamWeaponsHeader.text = ""
      lblBeamsNumber.text = ""
    }

    if(hull.maxTorpedoLaunchers > 0) {
      lblTorpedoTubesHeader.text = "Torpedo launchers:"
      lblLaunchersNumber.text = hull.maxTorpedoLaunchers.toString
    } else if(hull.fighterBaysNumber > 0) {
      lblTorpedoTubesHeader.text = "Fighter bays:"
      lblLaunchersNumber.text = hull.fighterBaysNumber.toString
    } else {
      lblTorpedoTubesHeader.text = ""
      lblLaunchersNumber.text = ""
    }

    lblHullMass.text = hull.mass.toString
    lblCrew.text = hull.crewSize.toString
    lblFueltank.text = hull.fuelTankSize.toString
    lblCargoSpace.text = hull.cargo.toString
  }

  currentHull.onChange(renewData())

  renewData()
}
