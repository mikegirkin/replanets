package replanets

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.Paths

import replanets.common._

object ConsoleRunner {
  def run = {
    val reg = new Reg(Paths.get("./testfiles"))
    val rk = reg.regkey
    val isValid = reg.checkRegKeyValidity(rk.get)
    println(isValid)

    val decoder = Charset.forName("ASCII")
    val strs = reg.readRegStr(rk.get)

    val str1 = decoder.decode(ByteBuffer.wrap(strs(0).toArray)).toString
    val str2 = decoder.decode(ByteBuffer.wrap(strs(1).toArray)).toString

    println(s"$str1, $str2")
  }
}