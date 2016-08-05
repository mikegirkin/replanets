package replanets.ui


import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane
import scalafxml.core.macros.sfxml

/**
  * Created by mgirkin on 05/08/2016.
  */
@sfxml
class IonStormInformationView(
  private val lblId: Label
) extends GridPane {

  def setId(id: String): Unit = {
    lblId.text = id
  }
}
