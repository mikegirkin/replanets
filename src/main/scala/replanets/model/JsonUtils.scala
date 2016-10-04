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
  implicit val raceIdFormat = fromSingleIntFormat(x => RaceId(x))
  implicit val shipIdFormat = fromSingleIntFormat(x => ShipId(x))
  implicit val hullIdFormat = fromSingleIntFormat(x => HullId(x))
  implicit val engineIdFormat = fromSingleIntFormat(x => EngineId(x))
  implicit val beamIdFormat = fromSingleIntFormat(x => BeamId(x))
  implicit val launcherIdFormat = fromSingleIntFormat(x => LauncherId(x))

  implicit val cargoFormat = format[Cargo]

  //ships
  val setShipFcodeFormat = format[SetShipFcode]
  val setShipWarpFormat = format[SetShipWarp]
  val shipToOwnPlanetTransferFormat = format[ShipToOwnPlanetTransfer]
  val shipToOtherPlanetTransferFormat = format[ShipToOtherPlanetTransfer]
  val shipToOwnShipTransferFormat = format[ShipToOwnShipTransfer]
  val shipToOtherShipTransferFormat = format[ShipToOtherShipTransfer]
  val setPrimaryEnemyFormat = format[SetPrimaryEnemy]
  val setMissionFormat = format[SetMission]
  val setShipNameFormat = format[SetShipName]
  //bases
  val buildShipFormat = format[StartShipConstruction]
  val stopShipConstructionFormat = format[StopShipConstruction]
  val changeBasePrimaryOrderFormat = format[ChangeBasePrimaryOrder]
  //planets
  val setPlanetFcodeFormat = format[SetPlanetFcode]
  val setColonistTaxFormat = format[SetColonistTax]
  val setNativeTaxFormat = format[SetNativeTax]
  val buildFactoriesFormat = format[BuildFactories]
  val buildMinesFormat = format[BuildMines]
  val buildDefencesFormat = format[BuildDefences]

  val playerCommandWrites = Writes[PlayerCommand] { cmd =>
    val typeValue = JsString(cmd.getClass.getSimpleName)
    val value = cmd match {
      case x: SetShipFcode => toJson(x)(setShipFcodeFormat)
      case x: SetShipWarp => toJson(x)(setShipWarpFormat)
      case x: ShipToOwnPlanetTransfer => toJson(x)(shipToOwnPlanetTransferFormat)
      case x: ShipToOtherPlanetTransfer => toJson(x)(shipToOtherPlanetTransferFormat)
      case x: ShipToOwnShipTransfer => toJson(x)(shipToOwnShipTransferFormat)
      case x: ShipToOtherShipTransfer => toJson(x)(shipToOtherShipTransferFormat)
      case x: SetPrimaryEnemy => toJson(x)(setPrimaryEnemyFormat)
      case x: SetMission => toJson(x)(setMissionFormat)
      case x: SetShipName => toJson(x)(setShipNameFormat)
      case x: StartShipConstruction => toJson(x)(buildShipFormat)
      case x: StopShipConstruction => toJson(x)(stopShipConstructionFormat)
      case x: ChangeBasePrimaryOrder => toJson(x)(changeBasePrimaryOrderFormat)
      case x: SetPlanetFcode => toJson(x)(setPlanetFcodeFormat)
      case x: SetColonistTax => toJson(x)(setColonistTaxFormat)
      case x: SetNativeTax => toJson(x)(setNativeTaxFormat)
      case x: BuildMines => toJson(x)(buildMinesFormat)
      case x: BuildFactories => toJson(x)(buildFactoriesFormat)
      case x: BuildDefences => toJson(x)(buildDefencesFormat)
    }
    JsObject(Seq(
      "type" -> typeValue,
      "value" -> value
    ))
  }

  val readers = Map[String, (JsLookupResult) => JsResult[PlayerCommand]](
    classOf[SetShipFcode].getSimpleName -> { _.validate[SetShipFcode](setShipFcodeFormat) },
    classOf[SetShipWarp].getSimpleName -> { _.validate[SetShipWarp](setShipWarpFormat) },
    classOf[ShipToOwnPlanetTransfer].getSimpleName -> { _.validate[ShipToOwnPlanetTransfer](shipToOwnPlanetTransferFormat) },
    classOf[ShipToOtherPlanetTransfer].getSimpleName -> { _.validate[ShipToOtherPlanetTransfer](shipToOtherPlanetTransferFormat) },
    classOf[ShipToOwnShipTransfer].getSimpleName -> { _.validate[ShipToOwnShipTransfer](shipToOwnShipTransferFormat) },
    classOf[ShipToOtherShipTransfer].getSimpleName -> { _.validate[ShipToOtherShipTransfer](shipToOtherShipTransferFormat) },
    classOf[SetPrimaryEnemy].getSimpleName -> { _.validate[SetPrimaryEnemy](setPrimaryEnemyFormat) },
    classOf[SetMission].getSimpleName -> { _.validate[SetMission](setMissionFormat) },
    classOf[SetShipName].getSimpleName -> { _.validate[SetShipName](setShipNameFormat) },
    classOf[StartShipConstruction].getSimpleName -> { _.validate[StartShipConstruction](buildShipFormat) },
    classOf[StopShipConstruction].getSimpleName -> { _.validate[StopShipConstruction](stopShipConstructionFormat) },
    classOf[ChangeBasePrimaryOrder].getSimpleName -> { _.validate[ChangeBasePrimaryOrder](changeBasePrimaryOrderFormat) },
    classOf[SetPlanetFcode].getSimpleName -> { _.validate[SetPlanetFcode](setPlanetFcodeFormat) },
    classOf[SetColonistTax].getSimpleName -> { _.validate[SetColonistTax](setColonistTaxFormat) },
    classOf[SetNativeTax].getSimpleName -> { _.validate[SetNativeTax](setNativeTaxFormat) },
    classOf[BuildMines].getSimpleName -> { _.validate[BuildMines](buildMinesFormat) },
    classOf[BuildFactories].getSimpleName -> { _.validate[BuildFactories](buildFactoriesFormat) },
    classOf[BuildDefences].getSimpleName -> { _.validate[BuildDefences](buildDefencesFormat) }
  )

  val playerCommandReads = Reads[PlayerCommand] { json =>
    val commandType = (json \ "type").as[String]
    val valueJson = json \ "value"
    readers.get(commandType).map(_.apply(valueJson))getOrElse(JsError())
  }

  implicit val playerCommandFormat: Format[PlayerCommand] = Format(playerCommandReads, playerCommandWrites)
}
