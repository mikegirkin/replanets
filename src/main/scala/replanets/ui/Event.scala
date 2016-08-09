package replanets.ui

import scala.collection.mutable

/**
  * Created by mgirkin on 04/08/2016.
  */
class Event[T] {
  private val registeredHandlers = mutable.ListBuffer[(T) => Unit]()

  def fire(arg: T): Unit = {
    registeredHandlers.foreach(_.apply(arg))
  }

  def +=(handler: (T) => Unit) = registeredHandlers += handler

  def -=(handler: (T) => Unit) = registeredHandlers -= handler
}

object Event {
  def apply[T] = new Event[T]()
}
