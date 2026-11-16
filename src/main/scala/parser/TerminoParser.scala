package parser

import modelo.{Atomo, Numero, Termino, Texto, Variable}

object TerminoParser {

  def parsear(texto: String): Termino = {

    val limpio = texto.trim
    limpio match {
      case t if t.startsWith("\"") && t.endsWith("\"") =>
        Texto(t.tail.init)

      case t if t.toIntOption.isDefined =>
        Numero(t.toInt)

      case t if t.head.isUpper || t.head == '_' =>
        Variable(t)

      case t if t.contains("(") &&  t.endsWith(")") =>
        PredicadoParser.parsear(t)

      case t =>
        Atomo(t)
    }
  }
}
