package parser

import modelo.Predicado

object ConsultaParser {
  def parsear(texto: String):Predicado={
    val limpio = texto.trim
    PredicadoParser.parsear(limpio)
  }
}
