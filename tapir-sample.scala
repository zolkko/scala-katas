//> using dep com.softwaremill.sttp.tapir::tapir-core:1.10.12
//> using dep com.softwaremill.sttp.tapir::tapir-netty-server-sync:1.10.12
//> using dep com.softwaremill.sttp.tapir::tapir-swagger-ui-bundle:1.10.12
//> using dep com.softwaremill.sttp.tapir::tapir-jsoniter-scala:1.10.12

//> using dep com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-core:2.30.4
//> using dep com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-macros:2.30.4

import com.github.plokhotnyuk.jsoniter_scala.core.{writeToString, readFromString}
import com.github.plokhotnyuk.jsoniter_scala.macros.ConfiguredJsonValueCodec

import sttp.tapir.*
import sttp.tapir.json.jsoniter.*
import sttp.tapir.server.netty.sync.NettySyncServer
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.shared.Identity


case class Meal(name: String, servings: Int, ingredients: List[String])
    derives ConfiguredJsonValueCodec, Schema

case class Nutrition(name: String, healthy: Boolean, calories: Int)
    derives ConfiguredJsonValueCodec, Schema

@main def tapirSample(): Unit =
    val meal1 = Meal("Test", 1, List("one", "two"))
    val meal1String = writeToString(meal1)

    val nut1 = Nutrition("nut1", true, 123)
    val nut1String = writeToString(nut1)

    println(s"The result string is = $meal1String, and = $nut1String")

    val meal1Read = readFromString[Meal](meal1String)
    val nut1Read = readFromString[Nutrition](nut1String)

    println(s"Read data $meal1Read, $nut1Read")

    println(s"Schema Meal: ${summon[Schema[Meal]]}")
    println(s"Schema Nut: ${summon[Schema[Nutrition]]}")

    val mealEndpoint = endpoint
        .post
        .in(jsonBody[Meal])
        .out(jsonBody[Nutrition])
        .handleSuccess { meal =>
            Nutrition(s"Nutrition of ${meal.name}", meal.servings < 2, 123)
        }

    val swaggerEndpoints = SwaggerInterpreter().fromServerEndpoints[Identity](List(mealEndpoint), "Test App", "1.0")

    NettySyncServer()
        .port(8182)
        .addEndpoint(mealEndpoint)
        .addEndpoints(swaggerEndpoints)
        .startAndWait()
