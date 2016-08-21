package replanets.ui

import replanets.common.HullspecItem

import scalafx.Includes._
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.event.ActionEvent
import scalafx.geometry.{HPos, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{ColumnConstraints, GridPane, VBox}

class CurrentHullInfoView(
  val currentHull: ObjectProperty[HullspecItem],
  val beamsToBuild: IntegerProperty,
  val launchersToBuild: IntegerProperty
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
  val lblLaunchersHeader = new Label("???")
  val lblBeamWeaponsHeader = new Label("Beam weapons:")

  val btnAddBeam = new Button("+") {
    styleClass = Seq("inGridButton")
    onAction = (e: ActionEvent) => {
      if(beamsToBuild.value < currentHull.value.maxBeamWeapons) beamsToBuild.value = beamsToBuild.value + 1
    }
  }
  val btnRemoveBeam = new Button("-") {
    styleClass = Seq("inGridButton")
    onAction = (e: ActionEvent) => {
      if(beamsToBuild.value > 0) beamsToBuild.value = beamsToBuild.value - 1
    }
  }
  val btnAddLauncher = new Button("+") {
    styleClass = Seq("inGridButton")
    onAction = (e: ActionEvent) => {
      if(launchersToBuild.value < currentHull.value.maxTorpedoLaunchers) launchersToBuild.value = launchersToBuild.value + 1
    }
  }
  val btnRemoveLauncher = new Button("-"){
    styleClass = Seq("inGridButton")
    onAction = (e: ActionEvent) => {
      if(launchersToBuild.value > 0) launchersToBuild.value = launchersToBuild.value - 1
    }
  }

  val bottomGrid = new GridPane {
    fillWidth = false
    alignment = Pos.Center
    columnConstraints = Seq(
      new ColumnConstraints {
        halignment = HPos.Right
      },
      new ColumnConstraints {
        minWidth = 40
      },
      new ColumnConstraints {
        halignment = HPos.Right
      },
      new ColumnConstraints {
        minWidth = 40
      }
    )
    add(new Label("Hull mass:"), 0, 0)
    add(lblHullMass, 1, 0)
    add(new Label("Crew:"), 0, 1)
    add(lblCrew, 1, 1)
    add(new Label("Fuel tank:"), 2, 0)
    add(lblFueltank, 3, 0)
    add(new Label("Cargo space:"), 2, 1)
    add(lblCargoSpace, 3, 1)
  }

  children = Seq(
    lblHullName,
    new GridPane {
      fillWidth = true
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
      add(btnAddBeam, 2, 2)
      add(btnRemoveBeam, 3, 2)
      add(lblLaunchersHeader, 0, 3)
      add(lblLaunchersNumber, 1, 3)
      add(btnAddLauncher, 2, 3)
      add(btnRemoveLauncher, 3, 3)
    },
    bottomGrid,
    lblSpecialFunction
  )

  val beamWeaponsVisibility = createBooleanBinding(() => currentHull.value.maxBeamWeapons > 0, currentHull)
  btnAddBeam.visible <== beamWeaponsVisibility
  btnRemoveBeam.visible <== beamWeaponsVisibility
  lblBeamWeaponsHeader.visible <== beamWeaponsVisibility
  lblBeamsNumber.visible <== beamWeaponsVisibility

  val launchersButtonVisibility = createBooleanBinding(() => currentHull.value.maxTorpedoLaunchers > 0, currentHull)
  btnAddLauncher.visible <== launchersButtonVisibility
  btnRemoveLauncher.visible <== launchersButtonVisibility
  val launchersTextsVisibility = createBooleanBinding(() => currentHull.value.maxTorpedoLaunchers > 0 || currentHull.value.fighterBaysNumber > 0, currentHull)
  lblLaunchersNumber.visible <== launchersTextsVisibility
  lblLaunchersHeader.visible <== launchersTextsVisibility

  private def renewData(): Unit = {
    val hull = currentHull.value
    lblHullName.text = hull.name
    lblTechLevel.text = hull.techLevel.toString
    lblEnginesNumber.text = hull.enginesNumber.toString
    lblBeamsNumber.text = beamsToBuild.value.toString

    if(hull.maxTorpedoLaunchers > 0) {
      lblLaunchersHeader.text = "Torpedo launchers:"
      lblLaunchersNumber.text = launchersToBuild.value.toString
    } else if(hull.fighterBaysNumber > 0) {
      lblLaunchersHeader.text = "Fighter bays:"
      lblLaunchersNumber.text = hull.fighterBaysNumber.toString
    }

    lblHullMass.text = hull.mass.toString
    lblCrew.text = hull.crewSize.toString
    lblFueltank.text = hull.fuelTankSize.toString
    lblCargoSpace.text = hull.cargo.toString
  }

  currentHull.onChange(renewData())
  beamsToBuild.onChange(renewData())
  launchersToBuild.onChange(renewData())

  renewData()
}
