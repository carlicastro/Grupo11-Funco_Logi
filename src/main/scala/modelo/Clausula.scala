package modelo

case class Clausula(cabeza: Predicado, cuerpo: List[Predicado]) {

  def esHecho: Boolean = cuerpo.isEmpty

  def esRegla: Boolean = cuerpo.nonEmpty
}