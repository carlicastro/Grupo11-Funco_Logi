
package parser

import modelo.Predicado
import scala.collection.mutable.ListBuffer

object PredicadoParser {

  // Separa los argumentos respetando los paréntesis anidados
  private def separarArgumentos(contenido: String): List[String] = {

    val argumentos = ListBuffer[String]()

    var profundidad = 0
    var dentroDeTexto = false
    var inicio = 0

    for (i <- contenido.indices) {

      val caracter = contenido(i)

      // Detectamos si entramos o salimos de un texto entre comillas
      if (caracter == '"') {
        dentroDeTexto = !dentroDeTexto
      }

      // Solo analizamos paréntesis y comas fuera de los textos
      else if (!dentroDeTexto) {

        caracter match {

          case '(' =>
            profundidad += 1

          case ')' =>
            profundidad -= 1

          case ',' if profundidad == 0 =>
            argumentos += contenido.substring(inicio, i).trim
            inicio = i + 1

          case _ =>
        }
      }
    }

    // Agregamos el último argumento
    argumentos += contenido.substring(inicio).trim

    argumentos.toList
  }


  def parsear(texto: String): Predicado = {

    val limpio = texto.trim

    val posicionParentesis = limpio.indexOf("(")
    val nombre = limpio.substring(0, posicionParentesis)

    val contenido = limpio.substring(posicionParentesis + 1, limpio.length - 1)

    val argumentos =
      if (contenido.trim.isEmpty) {
        List()
      }
      else {
        separarArgumentos(contenido)
          .map(argumento => TerminoParser.parsear(argumento))
      }

    Predicado(nombre, argumentos)
  }
}
