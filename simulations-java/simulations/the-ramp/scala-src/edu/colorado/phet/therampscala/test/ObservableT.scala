package edu.colorado.phet.therampscala.test


import scalacommon.util.Observable

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Apr 22, 2009
 * Time: 2:20:42 PM
 * To change this template use File | Settings | File Templates.
 */

class ObservableT[T](private var _value: T) extends Observable {
  def value = _value

  def value_=(_val: T) = {
    _value = _val
    notifyListeners()
  }
}

object TestMain{
  def main(args: Array[String]) {
    object model extends ObservableT(1.4)
    model.addListenerByName(println(model.value))
    model.value=5

    object modelString extends ObservableT("Hello")
    modelString.addListenerByName(println(modelString.value))
    modelString.value="Testing"
  }
}