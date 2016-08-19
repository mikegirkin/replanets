package replanets.ui

import scala.reflect.runtime.universe._
import scalafxml.core.{DependenciesByType, FXMLLoader}

object FXmlHelper {
  def loadFxml[TController](resouce: String, dependencies: Map[Type, Any] = Map()): TController = {
    val loader = new FXMLLoader(
      getClass.getResource(resouce),
      new DependenciesByType(dependencies)
    )
    loader.load()
    loader.getController[TController]
  }
}
