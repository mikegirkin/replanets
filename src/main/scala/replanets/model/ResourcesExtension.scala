package replanets.model

import java.nio.file.{Files, Path, Paths}

/**
  * Created by mgirkin on 13/08/2016.
  */
object ResourcesExtension {
  def getFromResourcesIfInexistent(filepath: Path, resource: String) = {
    if(Files.exists(filepath)) filepath
    else Paths.get(getClass.getResource(resource).toURI)
  }
}
