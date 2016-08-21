package replanets.model

import play.api.libs.json.Json._
import play.api.libs.json._
import replanets.common._

object JsonUtils {

  implicit val fcodeWrites = Writes[Fcode] { fcode =>
    JsString(fcode.value)
  }

  implicit val fcodeReads = Reads[Fcode] { json =>
    json.validate[String].map(v => Fcode(v))
  }

  implicit val fcodeFormat = Format(fcodeReads, fcodeWrites)

  def oneBasedIndexFormat[T <: OneBasedIndex](creation: Int => T) = Format[T](
    Reads { json => json.validate[Int].map(x => creation(x)) },
    Writes { id => JsNumber(id.value)}
  )

  implicit val planetIdFormat = oneBasedIndexFormat(x => PlanetId(x))
  implicit val shipIdFormat = oneBasedIndexFormat(x => ShipId(x))
  implicit val hullIdFormat = oneBasedIndexFormat(x => HullId(x))
  implicit val engineIdFormat = oneBasedIndexFormat(x => EngineId(x))
  implicit val beamIdFormat = oneBasedIndexFormat(x => BeamId(x))
  implicit val launcherIdFormat = oneBasedIndexFormat(x => LauncherId(x))

  val setPlanetFcodeFormat = format[SetPlanetFcode]
  val setShipFcodeFormat = format[SetShipFcode]

  val buildShipFormat = format[BuildShip]

  val playerCommandWrites = Writes[PlayerCommand] {
    case x: SetPlanetFcode => JsObject(Seq(
      "type" -> toJson("SetPlanetFcode"),
      "value" -> toJson(x)(setPlanetFcodeFormat)))
    case x: SetShipFcode => JsObject(Seq(
      "type" -> toJson("SetShipFcode"),
      "value" -> toJson(x)(setShipFcodeFormat)))
    case x: BuildShip => JsObject(Seq(
      "type" -> toJson("BuildShip"),
      "value" -> toJson(x)(buildShipFormat)
    ))
  }

  val playerCommandReads = Reads[PlayerCommand] { json =>
    val commandType = (json \ "type").as[String]
    commandType match {
      case "SetPlanetFcode" => (json \ "value").validate[SetPlanetFcode](setPlanetFcodeFormat)
      case "SetShipFcode" => (json \ "value").validate[SetShipFcode](setShipFcodeFormat)
      case "BuildShip" => (json \ "value").validate[BuildShip](buildShipFormat)
    }
  }

  implicit val playerCommandFormat: Format[PlayerCommand] = Format(playerCommandReads, playerCommandWrites)
}
