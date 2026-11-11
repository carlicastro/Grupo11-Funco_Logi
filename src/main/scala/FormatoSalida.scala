import modelo._
object FormatoSalida {
  def obtenerVariables(termino: Termino): List[String] = termino match {
    case Variable(nombre) =>
      List(nombre)
    case Predicado(_, argumentos) =>
      argumentos.flatMap(obtenerVariables)
    case _ =>
      List()
  }
  def mostrarTermino(termino: Termino): String = termino match {
    case Atomo(valor) =>
      valor
    case Numero(valor) =>
      valor.toString
    case Texto(valor) =>
      valor
    case Variable(nombre) =>
      nombre
    case Predicado(nombre, _) =>
      nombre
  }
}
