package it.dtk

import it.dtk.nlp.DBpediaSpotLight

/**
  * Created by fabiofumarola on 07/02/16.
  */
object DBPediaTest extends App {

  val baseUrl = "http://192.168.99.100:2230"
  val text = "Il tribunale di Bari ha condannato Luigi Farace, ex presidente della Federcommercio di Bari, alla pena di tre anni e sette mesi di reclusione e al pagamento di 700 euro di multa, oltre alla pena accessoria dell'interdizione dai pubblici uffici per cinque anni, per i reati di estorsione e tentata violenza privata. Lo rende noto l'avvocato Claudio Spagnoletti, difensore di Antonio Fiore e Cesarea Spagnoletti, parti civili nel processo, che hanno ottenuto anche un risarcimento danni da liquidarsi in sede civile. Farace è stato parlamentare democristiano per due legislature, sindaco di Bari e sottosegretario all'Industria nel primo governo di Giuliano Amato."

  val service = new DBpediaSpotLight(baseUrl,"it")
  val tags = service.tagText(text)
  tags.foreach(println)
}
