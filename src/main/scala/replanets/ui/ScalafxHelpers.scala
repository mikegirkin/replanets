package replanets.ui

import scalafxml.core.{FXMLLoader, NoDependencyResolver}

/**
  * Created by mgirkin on 06/08/2016.
  */
object ScalafxHelpers {
  def loadController[T](resource: String): T = {
    val loader = new FXMLLoader(getClass.getResource(resource), NoDependencyResolver)
    loader.load()
    loader.getController[T]
  }
}
