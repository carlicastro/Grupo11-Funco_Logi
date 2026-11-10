
package unificacion

import modelo._

object Unificador {

  private def contieneVariable(termino: Termino, nombre: String): Boolean = {
    termino match {

      case Variable(n) =>
        n == nombre

      case p: Predicado =>
        p.argumentos.exists(arg => contieneVariable(arg, nombre))

      case _ =>
        false
    }
  }

  def unificarTerminos(t1: Termino, t2: Termino, sigma: Sustitucion = Sustitucion()): Option[Sustitucion] = {
    val termino1 = sigma.aplicar(t1)
    val termino2 = sigma.aplicar(t2)

    (termino1, termino2) match {
      case (Variable(x), Variable(y)) if x == y =>
        Some(sigma)

      case (Variable(x), termino) =>
        if (contieneVariable(termino, x)) {
          None
        } else {
          Some(sigma.agregar(Variable(x), termino))
        }

      case (termino, Variable(y)) =>
        if (contieneVariable(termino, y)) {
          None
        } else {
          Some(sigma.agregar(Variable(y), termino))
        }

      case (p1: Predicado, p2: Predicado) =>
        unificarPredicados(p1, p2, sigma)

      case (Atomo(x), Atomo(y)) if x == y =>
        Some(sigma)

      case (Numero(x), Numero(y)) if x == y =>
        Some(sigma)

      case (Texto(x), Texto(y)) if x == y =>
        Some(sigma)

      case _ =>
        None
    }
  }


  def unificarPredicados(p1: Predicado, p2: Predicado, sigma: Sustitucion = Sustitucion()): Option[Sustitucion] = {
    if (p1.nombre != p2.nombre || p1.aridad != p2.aridad) {
      None
    } else {
      p1.argumentos.zip(p2.argumentos).foldLeft(
        Some(sigma): Option[Sustitucion]
      ) {
        case (Some(s), (t1, t2)) =>
          unificarTerminos(t1, t2, s)

        case (None, _) =>
          None
      }
    }
  }
}


