package replanets.ui.viewmodels

import replanets.common.{Fcode, Minerals, TurnId}
import replanets.model.{Game, SetPlanetFcode}

/**
  * Created by mgirkin on 09/08/2016.
  */
class PlanetInfoVM(game: Game, turn: TurnId, planetId: Int) {
  val planetRecord = game.turnSeverData(turn).planets.find(_.planetId == planetId).get
  val commands = game.turns(turn)(game.playingRace).commands

  def ownerId: Short = planetRecord.ownerId

  def fcode: Fcode = {
    commands.find {
      case SetPlanetFcode(pId, newFcode) if pId.id == planetId => true
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
  def nativeTax: Short = planetRecord.nativeTax
  def colonistHappiness: Short = planetRecord.colonistHappiness
  def nativeHappiness: Short = planetRecord.nativeHappiness
  def nativeGovernment: Short = planetRecord.nativeGovernment
  def nativeClans: Int = planetRecord.nativeClans
  def nativeRace: Short = planetRecord.nativeRace
  def temperature: Short = planetRecord.temperature
  def buildBase: Short = planetRecord.buildBase
}
