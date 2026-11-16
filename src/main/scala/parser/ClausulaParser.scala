package parser

import modelo.{Clausula, Predicado}

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

        caracter match {

          case '(' =>
            profundidad += 1

          case ')' =>
            profundidad -= 1

          case ',' if profundidad == 0 =>
            resultado = resultado :+ texto.substring(inicio, i).trim
            inicio = i + 1

          case _ =>
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

      val cuerpo = condiciones.map {
        case "false" =>
          Predicado("false", List())

        case "true" =>
          Predicado("true", List())

        case condicion if condicion.contains("=") =>
          val partes = condicion.split("=", 2)
          val parteIzquierda = TerminoParser.parsear(partes(0).trim)
          val parteDerecha = TerminoParser.parsear(partes(1).trim)
          Predicado("=", List(parteIzquierda, parteDerecha))

        case condicion =>
          PredicadoParser.parsear(condicion)
      }

      Clausula(cabeza, cuerpo)

    }else{
      val cabeza = PredicadoParser.parsear(sinPunto)
      Clausula(cabeza, List())
    }
  }
}