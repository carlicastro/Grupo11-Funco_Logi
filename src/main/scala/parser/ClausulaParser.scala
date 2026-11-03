package parser

import modelo.Clausula

object ClausulaParser {

  private def separarCondiciones(texto: String): List[String] = {
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

  def parsear(texto: String): Clausula = {
    val limpio = texto.trim
    val sinPunto = limpio.stripSuffix(".")
    if(sinPunto.contains(":-")){
      val partes = sinPunto.split(":-",2)
      val textoCabeza = partes(0)
      val textoCuerpo = partes(1)

      val cabeza = PredicadoParser.parsear(textoCabeza)
      val condiciones = separarCondiciones(textoCuerpo)

      val cuerpo = condiciones.map{condicion =>
        PredicadoParser.parsear(condicion)
      }

      Clausula(cabeza, cuerpo)

    }else{
      val cabeza = PredicadoParser.parsear(sinPunto)
      Clausula(cabeza, List())
    }
  }
}