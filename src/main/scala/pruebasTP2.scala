
import parser._
import modelo._

object pruebasTP2 {
  def main(args: Array[String]): Unit = {

    val consulta1 = ConsultaParser.parsear("padre(juan,maria)")
    val consulta2 = ConsultaParser.parsear("padre(juan,X)")
    val consulta3 = ConsultaParser.parsear("abuelo(P,H)")

    println(consulta1)
    println(consulta2)
    println(consulta3)
  }
}
