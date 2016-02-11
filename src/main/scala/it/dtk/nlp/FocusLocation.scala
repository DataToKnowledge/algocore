package it.dtk.nlp

import it.dtk.model.Annotation

/**
  * Created by fabiofumarola on 11/02/16.
  */
object FocusLocation {

  val locationConsts = List("PopulatedPlace","Place")

  def isLocation(a: Annotation): Boolean = {
    a.`types`
      .exists(t => locationConsts.exists(_.contains(t.value)))
  }

  

}
