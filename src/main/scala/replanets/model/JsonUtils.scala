package replanets.model

import play.api.libs.json.Json._
import play.api.libs.json._
import replanets.common._
import replanets.model.commands._

object JsonUtils {

  implicit val fcodeWrites = Writes[Fcode] { fcode =>
    JsString(fcode.value)
  }

  implicit val fcodeReads = Reads[Fcode] { json =>
    json.validate[String].map(v => Fcode(v))
  }

  implicit val fcodeFormat = Format(fcodeReads, fcodeWrites)

  def fromSingleIntFormat[T <: Index](creation: Int => T) = Format[T](
    Reads { json => json.validate[Int].map(x => creation(x)) },
    Writes { id => JsNumber(id.value)}
  )

  implicit val planetIdFormat = fromSingleIntFormat(x => PlanetId(x))
  implicit val shipIdFormat = fromSingleIntFormat(x => ShipId(x))
  implicit val hullIdFormat = fromSingleIntFormat(x => HullId(x))
  implicit val engineIdFormat = fromSingleIntFormat(x => EngineId(x))
  implicit val beamIdFormat = fromSingleIntFormat(x => BeamId(x))
  implicit val launcherIdFormat = fromSingleIntFormat(x => LauncherId(x))

  val setPlanetFcodeFormat = format[SetPlanetFcode]
  val setShipFcodeFormat = format[SetShipFcode]
  val buildShipFormat = format[StartShipConstruction]
  implicit val stopShipConstructionFormat = format[StopShipConstruction]

  val playerCommandWrites = Writes[PlayerCommand] { cmd =>
    val typeRow = "type" -> JsString(cmd.getClass.getSimpleName)
    val valueRow = cmd match {
      case x: SetPlanetFcode =>
        "value" -> toJson(x)(setPlanetFcodeFormat)
      case x: SetShipFcode =>
        "value" -> toJson(x)(setShipFcodeFormat)
      case x: StartShipConstruction =>
        "value" -> toJson(x)(buildShipFormat)
      case x: StopShipConstruction =>
        "value" -> toJson(x)(stopShipConstructionFormat)
    }
    JsObject(Seq(typeRow, valueRow))
  }

  val playerCommandReads = Reads[PlayerCommand] { json =>
    val commandType = (json \ "type").as[String]
    if(commandType == classOf[SetPlanetFcode].getSimpleName) (json \ "value").validate[SetPlanetFcode](setPlanetFcodeFormat)
    else if(commandType == classOf[SetShipFcode].getSimpleName) (json \ "value").validate[SetShipFcode](setShipFcodeFormat)
    else if(commandType == classOf[StartShipConstruction].getSimpleName) (json \ "value").validate[StartShipConstruction](buildShipFormat)
    else if(commandType == classOf[StopShipConstruction].getSimpleName) (json \ "value").validate[StopShipConstruction]
    else JsError()
  }

  implicit val playerCommandFormat: Format[PlayerCommand] = Format(playerCommandReads, playerCommandWrites)
}
