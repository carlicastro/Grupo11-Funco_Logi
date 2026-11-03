package parser

import modelo.Programa

object ProgramaParser {
  def parsear(lineas: List[String]): Programa = {
    val lineasValidas = lineas
      .map(_.trim)
      .filter(linea => linea.nonEmpty && !linea.startsWith("%%"))

    val clausulas = lineasValidas.map{linea =>
      ClausulaParser.parsear(linea)
    }
    
    Programa(clausulas)
  }
}
