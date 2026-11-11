/*
import modelo._
import parser._
import unificacion._
import motor._
import operaciones_built_in.Operaciones

object PruebasTP2 {
  private var total = 0
  private var exitosas = 0

  private def probar(nombre: String)(prueba: => Unit): Unit = {
    total += 1
    try {
      prueba
      exitosas += 1
      println(s"[OK] $nombre")
    } catch {
      case error: Throwable =>
        println(s"[ERROR] $nombre")
        println(s"       ${error.getMessage}")
    }
  }

  def main(args: Array[String]): Unit = {
    // 1. PRUEBAS DEL MODELO
    println("\n--- 1. MODELO ---")

    probar("Crear una variable") {
      val x: Termino = Variable("X")
      assert(x == Variable("X"))
    }

    probar("Crear un átomo") {
      val juan: Termino = Atomo("juan")
      assert(juan == Atomo("juan"))
    }

    probar("Crear un número") {
      val numero: Termino = Numero(25)
      assert(numero == Numero(25))
    }

    probar("Crear un texto") {
      val texto: Termino = Texto("hola")
      assert(texto == Texto("hola"))
    }

    probar("Crear un predicado con argumentos") {
      val padre = Predicado(
        "padre",
        List(Atomo("juan"), Atomo("maria"))
      )

      assert(padre.nombre == "padre")
      assert(padre.aridad == 2)
    }

    probar("Crear un hecho") {
      val hecho = Clausula(
        Predicado("padre", List(Atomo("juan"), Atomo("maria"))),
        List()
      )

      assert(hecho.esHecho)
      assert(!hecho.esRegla)
    }

    probar("Crear una regla") {
      val regla = Clausula(
        Predicado("abuelo", List(Variable("X"), Variable("Y"))),
        List(
          Predicado("padre", List(Variable("X"), Variable("Z"))),
          Predicado("padre", List(Variable("Z"), Variable("Y")))
        )
      )

      assert(regla.esRegla)
      assert(!regla.esHecho)
      assert(regla.cuerpo.length == 2)
    }

    // 2. PRUEBAS DEL PARSER
    println("\n--- 2. PARSER ---")

    probar("Parsear una variable") {
      assert(TerminoParser.parsear("X") == Variable("X"))
    }

    probar("Parsear un átomo") {
      assert(TerminoParser.parsear("juan") == Atomo("juan"))
    }

    probar("Parsear un número") {
      assert(TerminoParser.parsear("25") == Numero(25))
    }

    probar("Parsear un texto") {
      assert(TerminoParser.parsear("\"hola\"") == Texto("hola"))
    }

    probar("Parsear un predicado") {
      val resultado = PredicadoParser.parsear("padre(juan,maria)")

      assert(
        resultado ==
          Predicado("padre", List(Atomo("juan"), Atomo("maria")))
      )
    }

    probar("Parsear un predicado sin argumentos") {
      val resultado = PredicadoParser.parsear("llueve()")

      assert(resultado == Predicado("llueve", List()))
    }

    probar("Parsear predicados anidados") {
      val resultado =
        PredicadoParser.parsear("not(eq(len(\"pepe\"),4))")

      assert(resultado.nombre == "not")
      assert(resultado.argumentos.length == 1)

      val interno = resultado.argumentos.head.asInstanceOf[Predicado]
      assert(interno.nombre == "eq")
    }


    // 3. PRUEBAS DE SUSTITUCIONES
    println("\n--- 3. SUSTITUCIONES ---")

    probar("Agregar una sustitución") {
      val sigma = Sustitucion()
        .agregar(Variable("X"), Atomo("juan"))

      assert(sigma.aplicar(Variable("X")) == Atomo("juan"))
    }

    probar("Aplicar una sustitución dentro de un predicado") {
      val sigma = Sustitucion()
        .agregar(Variable("X"), Atomo("juan"))

      val padre = Predicado(
        "padre",
        List(Variable("X"), Atomo("maria"))
      )

      val resultado = sigma.aplicar(padre)

      assert(
        resultado ==
          Predicado("padre", List(Atomo("juan"), Atomo("maria")))
      )
    }

    probar("Aplicar sustituciones encadenadas") {
      val sigma = Sustitucion()
        .agregar(Variable("X"), Variable("Y"))
        .agregar(Variable("Y"), Atomo("juan"))

      assert(sigma.aplicar(Variable("X")) == Atomo("juan"))
    }

    probar("Una variable sin asignación permanece igual") {
      val sigma = Sustitucion()

      assert(sigma.aplicar(Variable("X")) == Variable("X"))
    }


    // 4. PRUEBAS DE UNIFICACIÓN
    println("\n--- 4. UNIFICACIÓN ---")

    probar("Unificar dos variables iguales") {
      val resultado = Unificador.unificarTerminos(
        Variable("X"),
        Variable("X")
      )

      assert(resultado.isDefined)
    }

    probar("Unificar una variable con un átomo") {
      val resultado = Unificador.unificarTerminos(
        Variable("X"),
        Atomo("juan")
      )

      assert(resultado.isDefined)
      assert(resultado.get.aplicar(Variable("X")) == Atomo("juan"))
    }

    probar("Unificar dos átomos iguales") {
      val resultado = Unificador.unificarTerminos(
        Atomo("juan"),
        Atomo("juan")
      )

      assert(resultado.isDefined)
    }

    probar("No unificar dos átomos diferentes") {
      val resultado = Unificador.unificarTerminos(
        Atomo("juan"),
        Atomo("maria")
      )

      assert(resultado.isEmpty)
    }

    probar("Unificar dos predicados correctamente") {
      val p1 = Predicado(
        "padre",
        List(Variable("X"), Atomo("maria"))
      )

      val p2 = Predicado(
        "padre",
        List(Atomo("juan"), Variable("Y"))
      )

      val resultado = Unificador.unificarPredicados(p1, p2)

      assert(resultado.isDefined)
      assert(resultado.get.aplicar(Variable("X")) == Atomo("juan"))
      assert(resultado.get.aplicar(Variable("Y")) == Atomo("maria"))
    }

    probar("No unificar predicados con distinto nombre") {
      val p1 = Predicado("padre", List(Variable("X")))
      val p2 = Predicado("madre", List(Variable("X")))

      assert(
        Unificador.unificarPredicados(p1, p2).isEmpty
      )
    }

    probar("No unificar predicados con distinta aridad") {
      val p1 = Predicado("padre", List(Variable("X")))
      val p2 = Predicado("padre", List(Variable("X"), Variable("Y")))

      assert(
        Unificador.unificarPredicados(p1, p2).isEmpty
      )
    }

    probar("Detectar una unificación circular (occurs check)") {
      val resultado = Unificador.unificarTerminos(
        Variable("X"),
        Predicado("padre", List(Variable("X")))
      )

      assert(resultado.isEmpty)
    }

    probar("Unificar predicados anidados") {
      val p1 = Predicado(
        "padre",
        List(
          Predicado("persona", List(Variable("X")))
        )
      )

      val p2 = Predicado(
        "padre",
        List(
          Predicado("persona", List(Atomo("juan")))
        )
      )

      val resultado = Unificador.unificarPredicados(p1, p2)

      assert(resultado.isDefined)
      assert(resultado.get.aplicar(Variable("X")) == Atomo("juan"))
    }

    // 5. PRUEBAS DEL MOTOR DE INFERENCIA
    println("\n--- 5. MOTOR DE INFERENCIA ---")

    val hechos = Programa(
      List(
        Clausula(
          Predicado("padre", List(Atomo("juan"), Atomo("maria"))),
          List()
        ),
        Clausula(
          Predicado("padre", List(Atomo("juan"), Atomo("pedro"))),
          List()
        ),
        Clausula(
          Predicado("padre", List(Atomo("maria"), Atomo("sofia"))),
          List()
        )
      )
    )

    probar("Encontrar un hecho existente") {
      val consulta = Predicado(
        "padre",
        List(Atomo("juan"), Atomo("maria"))
      )

      val resultados = MotorInferencia.resolver(consulta, hechos)

      assert(resultados.nonEmpty)
    }

    probar("No encontrar un hecho inexistente") {
      val consulta = Predicado(
        "padre",
        List(Atomo("pedro"), Atomo("maria"))
      )

      val resultados = MotorInferencia.resolver(consulta, hechos)

      assert(resultados.isEmpty)
    }

    probar("Obtener una variable a partir de un hecho") {
      val consulta = Predicado(
        "padre",
        List(Atomo("juan"), Variable("X"))
      )

      val resultados = MotorInferencia.resolver(consulta, hechos)

      val valores = resultados.map(
        _.aplicar(Variable("X"))
      ).toSet

      assert(valores == Set(Atomo("maria"), Atomo("pedro")))
    }

    val programaConRegla = Programa(
      hechos.clausulas ++ List(
        Clausula(
          Predicado("abuelo", List(Variable("X"), Variable("Y"))),
          List(
            Predicado("padre", List(Variable("X"), Variable("Z"))),
            Predicado("padre", List(Variable("Z"), Variable("Y")))
          )
        )
      )
    )

    probar("Resolver una regla con dos objetivos") {
      val consulta = Predicado(
        "abuelo",
        List(Atomo("juan"), Variable("Y"))
      )

      val resultados = MotorInferencia.resolver(
        consulta,
        programaConRegla
      )

      val valores = resultados.map(
        _.aplicar(Variable("Y"))
      ).toSet

      assert(valores == Set(Atomo("sofia")))
    }


    // 6. PRUEBAS DE BUILT-INS
    println("\n--- 6. BUILT-INS ---")

    probar("add: sumar dos números") {
      assert(
        Operaciones.evaluar(
          Predicado("add", List(Numero(2), Numero(3)))
        ) == Right(Numero(5))
      )
    }

    probar("sub: restar dos números") {
      assert(
        Operaciones.evaluar(
          Predicado("sub", List(Numero(10), Numero(4)))
        ) == Right(Numero(6))
      )
    }

    probar("mult: multiplicar dos números") {
      assert(
        Operaciones.evaluar(
          Predicado("mult", List(Numero(2), Numero(5)))
        ) == Right(Numero(10))
      )
    }

    probar("eq: comparar números iguales") {
      assert(
        Operaciones.evaluar(
          Predicado("eq", List(Numero(5), Numero(5)))
        ) == Right(Atomo("true"))
      )
    }

    probar("eq: comparar números diferentes") {
      assert(
        Operaciones.evaluar(
          Predicado("eq", List(Numero(5), Numero(3)))
        ) == Right(Atomo("false"))
      )
    }

    probar("gt: comparar números") {
      assert(
        Operaciones.evaluar(
          Predicado("gt", List(Numero(8), Numero(3)))
        ) == Right(Atomo("true"))
      )
    }

    probar("eqStr: comparar textos") {
      assert(
        Operaciones.evaluar(
          Predicado("eqStr", List(Texto("hola"), Texto("hola")))
        ) == Right(Atomo("true"))
      )
    }

    probar("len: obtener longitud de un texto") {
      assert(
        Operaciones.evaluar(
          Predicado("len", List(Texto("pepe")))
        ) == Right(Numero(4))
      )
    }

    probar("not: negar un resultado booleano") {
      assert(
        Operaciones.evaluar(
          Predicado("not", List(
            Predicado("eq", List(Numero(2), Numero(3)))
          ))
        ) == Right(Atomo("true"))
      )
    }

    probar("Evaluar built-ins anidados") {
      val expresion = Predicado(
        "not",
        List(
          Predicado(
            "eq",
            List(
              Predicado("len", List(Texto("pepe"))),
              Numero(4)
            )
          )
        )
      )

      assert(
        Operaciones.evaluar(expresion) == Right(Atomo("false"))
      )
    }

    probar("Detectar un tipo incorrecto en una operación") {
      val resultado = Operaciones.evaluar(
        Predicado("add", List(Texto("hola"), Numero(3)))
      )

      assert(resultado.isLeft)
    }

    println("\n--- RESULTADO FINAL ---")
    println(s"Pruebas ejecutadas: $total")
    println(s"Pruebas exitosas:   $exitosas")
    println(s"Pruebas fallidas:   ${total - exitosas}")
  }
}

 */