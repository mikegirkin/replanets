package replanets.ui.viewmodels

import replanets.common._
import replanets.model.{Game, SetPlanetFcode}

/**
  * Created by mgirkin on 09/08/2016.
  */
class PlanetInfoVM(game: Game, turn: TurnId, planetId: PlanetId) {
  val planetRecord = game.turnSeverData(turn).planets.get(planetId).get
  val commands = game.turns(turn)(game.playingRace).commands

  def ownerId: Short = planetRecord.ownerId.value.toShort

  def fcode: Fcode = {
    commands.find {
      case SetPlanetFcode(pId, newFcode) if pId == planetId => true
      case _ => false
    }.map {
      case SetPlanetFcode(pId, newFcode) => newFcode
    }.getOrElse(planetRecord.fcode)
  }

  def minesNumber: Short = planetRecord.minesNumber
  def factoriesNumber: Short = planetRecord.factoriesNumber
  def defencesNumber: Short = planetRecord.defencesNumber
  def surfaceMinerals: Minerals = planetRecord.surfaceMinerals
  def colonistClans: Int = planetRecord.colonistClans
  def supplies: Int = planetRecord.supplies
  def money: Int = planetRecord.money
  def coreMinerals: Minerals = planetRecord.coreMinerals
  def densityMinerals: Minerals = planetRecord.densityMinerals
  def colonistTax: Short = planetRecord.colonistTax
  def colonistIncome: Int = game.formulas.colonistTaxIncome(
    game.playingRace, planetRecord.colonistClans, planetRecord.colonistTax, planetRecord.colonistHappiness
  )
  def nativeTax: Short = planetRecord.nativeTax
  def nativeIncome: Int = game.formulas.nativeTaxIncome(
    game.playingRace, planetRecord.nativeRace, planetRecord.nativeGovernment,
    planetRecord.nativeClans, planetRecord.nativeTax, planetRecord.nativeHappiness,
    planetRecord.colonistClans
  )
  def colonistHappiness: Short = planetRecord.colonistHappiness
  def colonistHappinessChange = game.formulas.colonistHappinessChange(
    game.playingRace, planetRecord.colonistClans,
    planetRecord.temperature,
    planetRecord.minesNumber, planetRecord.factoriesNumber,
    planetRecord.colonistTax
  )

  def nativeHappiness: Short = planetRecord.nativeHappiness
  def nativeHappinessChange = game.formulas.nativeHappinessChange(
    planetRecord.nativeRace, planetRecord.nativeGovernment,
    planetRecord.nativeClans, planetRecord.minesNumber,
    planetRecord.factoriesNumber, planetRecord.nativeTax
  )
  def nativeGovernment = planetRecord.nativeGovernment
  def nativeClans: Int = planetRecord.nativeClans
  def nativeRace = planetRecord.nativeRace
  def temperature: Short = planetRecord.temperature
  def buildBase: Short = planetRecord.buildBase
}
