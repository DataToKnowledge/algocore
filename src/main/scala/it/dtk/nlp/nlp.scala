package it.dtk.nlp

import opennlp.tools.postag.{POSModel, POSTaggerME}
import opennlp.tools.sentdetect.{SentenceDetectorME, SentenceModel}
import opennlp.tools.tokenize.{TokenizerME, TokenizerModel}
import opennlp.tools.util.Span

object nlp {
  val it = "it"

  val tokenizerIt = new Tokenizer(it)
  val sentenceIt = new SentenceDetector(it)
  val posTaggerIt = new PosTagger(it)
}

class Tokenizer(lang: String) {

  val model = lang match {
    case "it" =>
      val in = nlp.getClass.getResource("/models/it-token.bin")
      new TokenizerME(new TokenizerModel(in))

    case _ =>
      throw new Error("model not implemented")

  }

  /**
    *
    * @param text
    * @return the text tokenized in a array
    */
  def tokenize(text: String): Seq[String] = model.tokenize(text)

  /**
    *
    * @param text
    * @return return the a Seq of Span
    */
  def tokenizePos(text: String): Seq[Span] = model.tokenizePos(text)


}


class SentenceDetector(lang: String) {
  val model = lang match {
    case "it" =>
      val in = nlp.getClass.getResourceAsStream("/models/it-sent.bin")
      new SentenceDetectorME(new SentenceModel(in))

    case _ =>
      throw new Error("model not implemented")
  }

  def sentences(text: String): Seq[String] = model.sentDetect(text)

  def sentencePos(text: String): Seq[Span] = model.sentPosDetect(text)
}

/**
  * The original tagset can be found at http://medialab.di.unipi.it/wiki/Tanl_POS_Tagset
  *
  * @param lang
  */
class PosTagger(lang: String) {

  case class TokenPos(token: String, pos: String)

  val model = lang match {
    case "it" =>
      val in = nlp.getClass.getResourceAsStream("/models/it-pos_perceptron.bin")
      new POSTaggerME(new POSModel(in))

    case _ =>
      throw new Error("model not implemented")
  }

  /**
    *
    * @param tokens
    * @return a list of object token pos
    */
  def posTag(tokens: Seq[String]): Seq[TokenPos] = {
    tokens
      .zip(model.tag(tokens.toArray))
      .map(tp => TokenPos(tp._1, tp._2))
  }
}

