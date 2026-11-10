package motor

import modelo.*
import operaciones_built_in.Operaciones
import unificacion.*

import scala.collection.mutable

object MotorInferencia {
  private var siguienteId = 0
  private def renombrarVariables(clausula: Clausula): Clausula = {
    siguienteId += 1
    val id = siguienteId
    val variablesNuevas = mutable.Map[String, Variable]()

    def renombrarTermino(termino: Termino): Termino = {
      termino match {
        case Variable(nombre) =>
          variablesNuevas.getOrElseUpdate(nombre, Variable(s"${nombre}_fresh_$id"))
        case p: Predicado =>
          Predicado(p.nombre, p.argumentos.map(renombrarTermino))
        case otro =>
          otro
      }
    }

    val nuevaCabeza = renombrarTermino(clausula.cabeza).asInstanceOf[Predicado]
    val nuevoCuerpo = clausula.cuerpo.map(renombrarTermino).map(_.asInstanceOf[Predicado])

    Clausula(nuevaCabeza, nuevoCuerpo)
  }

  def resolver(consulta: Predicado, programa: Programa): List[Sustitucion] = {
    resolverObjetivos(List(consulta), programa, Sustitucion())
  }

  private def resolverObjetivos(objetivos: List[Predicado], programa: Programa, sigma: Sustitucion): List[Sustitucion] = {
    objetivos match {
      case Nil =>
        List(sigma)

      case objetivo :: resto =>
        val objetivoAplicado = sigma.aplicar(objetivo)

        if (esBuiltIn(objetivoAplicado)) {
          Operaciones.evaluar(objetivoAplicado) match {
            case Right(Atomo("true")) =>
              resolverObjetivos(resto, programa, sigma)

            case Right(Atomo("false")) =>
              List()

            case Right(resultado) =>
              throw new IllegalArgumentException(s"Una built-in usada como condición devolvió $resultado")

            case Left(error) =>
              throw new IllegalArgumentException(error)
          }
        } else {
          programa.clausulas.flatMap { clausula =>
            val clausulaNueva = renombrarVariables(clausula)
            Unificador.unificarPredicados(objetivoAplicado, clausulaNueva.cabeza, sigma).toList.flatMap { nuevaSigma =>
              
              val cuerpoAplicado = clausulaNueva.cuerpo.map(nuevaSigma.aplicar)
              val restoAplicado = resto.map(nuevaSigma.aplicar)
              resolverObjetivos(cuerpoAplicado ++ restoAplicado, programa, nuevaSigma)
            }

          }
        }

    }
  }

  private val builtIns = Set(
    "add",
    "sub",
    "mult",
    "eq",
    "gt",
    "eqStr",
    "len",
    "not"
  )

  private def esBuiltIn(predicado: Predicado): Boolean =
    builtIns.contains(predicado.nombre)

}
