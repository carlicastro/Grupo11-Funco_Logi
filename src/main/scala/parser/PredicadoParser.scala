package parser

import modelo.Predicado

object PredicadoParser {
  private def separarArgumentos(texto: String): List[String] = {
    var profundidad = 0
    var dentroDeTexto = false
    var inicio = 0
    var resultado = List[String]()

    for (i <- texto.indices) {
      val caracter = texto(i)

      if (caracter == '"') {
        dentroDeTexto = !dentroDeTexto
      }
      else if (!dentroDeTexto) {
        if (caracter == '(') {
          profundidad += 1
        }
        else if (caracter == ')') {
          profundidad -= 1
        }
        else if (caracter == ',' && profundidad == 0) {
          resultado = resultado :+ texto.substring(inicio, i).trim
          inicio = i + 1
        }
      }
    }

    resultado = resultado :+ texto.substring(inicio).trim
    resultado
  }

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
        separarArgumentos(contenido)
          .map{ argumento => TerminoParser.parsear(argumento)
          }
      }

    Predicado(nombre, argumentos)
  }
}