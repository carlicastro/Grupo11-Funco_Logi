package modelo

trait Termino

case class Variable(nombre: String) extends Termino

case class Atomo(valor: String) extends Termino

case class Numero(valor: Int) extends Termino

case class Texto(valor: String) extends Termino