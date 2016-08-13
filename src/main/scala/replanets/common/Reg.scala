package replanets.common

import java.nio.file.{Files, Path}

import replanets.recipes.DWORD

class Reg(directory: Path) {

  import replanets.recipes.ByteExtensions._

  val regkey: Option[Array[Byte]] = {
    val filePath = directory.resolve("REG.KEY")
    if(Files.exists(filePath)) Some(Files.readAllBytes(filePath))
    else None
  }

  def checkRegKeyValidity(regkey: Array[Byte]): Boolean = {
    val controlSum = regkey.iterator.slice(0xD4, 0x47D + 1).map { _.toUnsignedInt }.sum
    val filecs = DWORD.read(regkey.iterator.slice(0xCC, 0xCC + 4))
    filecs == controlSum
  }

  def readRegStr(regkey: Array[Byte]): IndexedSeq[Iterable[Byte]] = {
    val xors1 = regkey.drop(0x291).take(25)
    val regstr1 = regkey.drop(0x1F9).take(25).zip(xors1).map { case (rg1, xor) => (rg1 ^ xor).toByte }
    val xors2 = regkey.drop(0x2AA).take(25)
    val regstr2 = regkey.drop(0x1C3).take(25).zip(xors2).map { case (rg2, xor) => (rg2 ^ xor).toByte }
    IndexedSeq(regstr1, regstr2)
  }

}
