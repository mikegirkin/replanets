package replanets.model

import play.api.libs.json.Json._
import play.api.libs.json._
import replanets.common._
import replanets.model.commands.{SetNativeTax, _}

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
  val stopShipConstructionFormat = format[StopShipConstruction]
  val setColonistTaxFormat = format[SetColonistTax]
  val setNativeTaxFormat = format[SetNativeTax]

  val playerCommandWrites = Writes[PlayerCommand] { cmd =>
    val typeValue = JsString(cmd.getClass.getSimpleName)
    val value= cmd match {
      case x: SetPlanetFcode => toJson(x)(setPlanetFcodeFormat)
      case x: SetShipFcode => toJson(x)(setShipFcodeFormat)
      case x: StartShipConstruction => toJson(x)(buildShipFormat)
      case x: StopShipConstruction => toJson(x)(stopShipConstructionFormat)
      case x: SetColonistTax => toJson(x)(setColonistTaxFormat)
      case x: SetNativeTax => toJson(x)(setNativeTaxFormat)
    }
    JsObject(Seq(
      "type" -> typeValue,
      "value" -> value
    ))
  }

  val readers = Map[String, (JsLookupResult) => JsResult[PlayerCommand]](
    classOf[SetPlanetFcode].getSimpleName -> { _.validate[SetPlanetFcode](setPlanetFcodeFormat) },
    classOf[SetShipFcode].getSimpleName -> { _.validate[SetShipFcode](setShipFcodeFormat) },
    classOf[SetShipFcode].getSimpleName -> { _.validate[SetShipFcode](setShipFcodeFormat) },
    classOf[StartShipConstruction].getSimpleName -> { _.validate[StartShipConstruction](buildShipFormat) },
    classOf[StopShipConstruction].getSimpleName -> { _.validate[StopShipConstruction](stopShipConstructionFormat) },
    classOf[SetColonistTax].getSimpleName -> { _.validate[SetColonistTax](setColonistTaxFormat) },
    classOf[SetNativeTax].getSimpleName -> { _.validate[SetNativeTax](setNativeTaxFormat) }
  )

  val playerCommandReads = Reads[PlayerCommand] { json =>
    val commandType = (json \ "type").as[String]
    val valueJson = json \ "value"
    readers.get(commandType).map(_.apply(valueJson))getOrElse(JsError())
  }

  implicit val playerCommandFormat: Format[PlayerCommand] = Format(playerCommandReads, playerCommandWrites)
}
