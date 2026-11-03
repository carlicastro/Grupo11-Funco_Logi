package modelo

case class Predicado(nombre: String, argumentos: List[Termino]) {
  def aridad: Int = argumentos.length
}

  
