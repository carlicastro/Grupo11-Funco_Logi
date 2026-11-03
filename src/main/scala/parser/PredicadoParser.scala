package parser

import modelo.Predicado

object PredicadoParser {
  def parsear(texto: String): Predicado={
    val limpio = texto.trim

    val posicionParentesis = limpio.indexOf("(")
    val nombre = limpio.substring(0, posicionParentesis)

    val contenido = limpio.substring(posicionParentesis+1, limpio.length-1)

    val argumentos =
      if (contenido.trim.isEmpty){
        List()
      }
      else{
        contenido.split(",").toList
          .map{ argumento => TerminoParser.parsear(argumento)
          }
      }

    Predicado(nombre, argumentos)
  }
}