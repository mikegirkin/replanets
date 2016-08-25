package replanets.ui.viewmodels

import replanets.common._
import replanets.model.Game
import replanets.model.commands.SetPlanetFcode

class PlanetInfoVM(game: Game, turn: TurnId, planetId: PlanetId) {
  val planetRecord = game.turnInfo(turn).stateAfterCommands.planets(planetId)

  def ownerId: Short = planetRecord.ownerId.value.toShort

  def fcode: Fcode = planetRecord.fcode
  def minesNumber = planetRecord.minesNumber
  def maxMines = game.formulas.maxMines(colonistClans)
  def factoriesNumber = planetRecord.factoriesNumber
  def maxFactories = game.formulas.maxFactories(colonistClans)
  def defencesNumber = planetRecord.defencesNumber
  def maxDefences = game.formulas.maxDefences(colonistClans)
  def surfaceMinerals: Minerals = planetRecord.surfaceMinerals
  def colonistClans: Int = planetRecord.colonistClans
  def supplies: Int = planetRecord.supplies
  def money: Int = planetRecord.money
  def coreMinerals: Minerals = planetRecord.coreMinerals
  def densityMinerals: Minerals = planetRecord.densityMinerals
  def colonistTax = planetRecord.colonistTax
  def colonistIncome: Int = game.formulas.colonistTaxIncome(
    game.playingRace, planetRecord.colonistClans, planetRecord.colonistTax, planetRecord.colonistHappiness
  )
  def nativeTax = planetRecord.nativeTax
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
