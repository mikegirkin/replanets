package replanets.ui

import scala.collection.mutable

/**
  * Created by mgirkin on 04/08/2016.
  */
class Event {
  private val registeredHandlers = mutable.ListBuffer[() => Unit]()

  def fire(): Unit = {
    registeredHandlers.foreach(_.apply())
  }

  def +=(handler: () => Unit) = registeredHandlers += handler

  def -=(handler: () => Unit) = registeredHandlers -= handler
}
