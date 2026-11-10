package parser

import modelo.{Atomo, Numero, Termino, Texto, Variable}

object TerminoParser {

  def parsear(texto: String): Termino = {

    val limpio = texto.trim

    if(limpio.startsWith("\"") && limpio.endsWith("\"")){
      Texto(limpio.tail.init)
    }
    else if(limpio.toIntOption.isDefined){
      Numero(limpio.toInt)
    }
    else if(limpio.head.isUpper || limpio.head == '_'){
      Variable(limpio)
    }
    else if( limpio.contains("(") && limpio.contains(")")){
      PredicadoParser.parsear(limpio)
    }
    else{
      Atomo(limpio)
    }
  }
}
