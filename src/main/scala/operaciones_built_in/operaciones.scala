package operaciones_built_in

import modelo.*

object Operaciones:
  private val ADD   = "add"
  private val SUB   = "sub"
  private val MULT  = "mult"
  private val EQ    = "eq"
  private val GT    = "gt"
  private val EQSTR = "eqStr"
  private val LEN   = "len"
  private val NOT   = "not"

  private def bool(b: Boolean): Termino =
    if b then Atomo("true") else Atomo("false")

  def evaluar(t: Termino): Either[String, Termino] = t match
    case Predicado(ADD, List(a, b)) =>
      evaluarNum(a).flatMap(x => evaluarNum(b).map(y => Numero(x + y)))

    case Predicado(SUB, List(a, b)) =>
      evaluarNum(a).flatMap(x => evaluarNum(b).map(y => Numero(x - y)))

    case Predicado(MULT, List(a, b)) =>
      evaluarNum(a).flatMap(x => evaluarNum(b).map(y => Numero(x * y)))

    case Predicado(EQ, List(a, b)) =>
      evaluarNum(a).flatMap(x => evaluarNum(b).map(y => bool(x == y)))

    case Predicado(GT, List(a, b)) =>
      evaluarNum(a).flatMap(x => evaluarNum(b).map(y => bool(x > y)))

    case Predicado(EQSTR, List(a, b)) =>
      evaluarTexto(a).flatMap(x => evaluarTexto(b).map(y => bool(x == y)))

    case Predicado(LEN, List(a)) =>
      evaluarTexto(a).map(s => Numero(s.length))

    case Predicado(NOT, List(a)) =>
      evaluarBool(a).map(b => bool(!b))

    case otro => Right(otro)

  private def evaluarNum(t: Termino): Either[String, Int] =
    evaluar(t).flatMap:
      case Numero(n) => Right(n)
      case otro => Left(s"Se esperaba un numero, se obtuvo: $otro")

  private def evaluarTexto(t: Termino): Either[String, String] =
    evaluar(t).flatMap:
      case Texto(s) => Right(s)
      case otro => Left(s"Se esperaba un texto, se obtuvo: $otro")

  private def evaluarBool(t: Termino): Either[String, Boolean] =
    evaluar(t).flatMap:
      case Atomo("true")  => Right(true)
      case Atomo("false") => Right(false)
      case otro => Left(s"No se obtuvo un valor booleano, se obtuvo: $otro")