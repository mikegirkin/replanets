package replanets.model

import play.api.libs.json.Json._
import play.api.libs.json.{Format, JsObject, Reads, Writes}
import replanets.common.Fcode
/**
  * Created by mgirkin on 09/08/2016.
  */
object JsonUtils {

  implicit val fcodeFormat = format[Fcode]
  val setPlanetFcodeFormat = format[SetPlanetFcode]
  val setShipFcodeFormat = format[SetShipFcode]

  val playerCommandWrites = Writes[PlayerCommand] {
    case x: SetPlanetFcode => JsObject(Seq(
      "type" -> toJson("SetPlanetFcode"),
      "value" -> toJson(x)(setPlanetFcodeFormat)))
    case x: SetShipFcode => JsObject(Map(
      "type" -> toJson("SetShipFcode"),
      "value" -> toJson(x)(setShipFcodeFormat)))
  }

  val playerCommandReads = Reads[PlayerCommand] { json =>
    val commandType = (json \ "type").as[String]
    commandType match {
      case "SetPlanetFcode" => (json \ "value").validate[SetPlanetFcode](setPlanetFcodeFormat)
      case "SetShipFcode" => (json \ "value").validate[SetShipFcode](setShipFcodeFormat)
    }
  }

  implicit val playerCommandFormat: Format[PlayerCommand] = Format(playerCommandReads, playerCommandWrites)
}
