package modelo

case class Predicado(nombre: String, argumentos: List[Termino]) extends Termino:
  def aridad: Int = argumentos.length

  
