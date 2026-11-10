package unificacion

import modelo._

case class Sustitucion(asignaciones: Map[String, Termino] = Map()) {

  def aplicar(termino: Termino): Termino = {
    termino match {

      case Variable(nombre) =>
        asignaciones.get(nombre) match {
          case Some(valor) => aplicar(valor)
          case None => termino
        }

      case p: Predicado =>
        aplicar(p)

      case otro => otro
    }
  }

  def aplicar(predicado: Predicado): Predicado = {
    Predicado(
      predicado.nombre,
      predicado.argumentos.map(aplicar)
    )
  }

  def agregar(variable: Variable, termino: Termino): Sustitucion = {
    Sustitucion(asignaciones + (variable.nombre -> termino))
  }
}
