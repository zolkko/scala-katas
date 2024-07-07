//> using dep com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-core:2.30.4
//> using dep com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-macros:2.30.4

import com.github.plokhotnyuk.jsoniter_scala.core.writeToString
import com.github.plokhotnyuk.jsoniter_scala.core.readFromString
import com.github.plokhotnyuk.jsoniter_scala.macros.ConfiguredJsonValueCodec


case class Meal(name: String, servings: Int, ingredients: List[String])
    derives ConfiguredJsonValueCodec

case class Nutrition(name: String, healthy: Boolean, calories: Int)
    derives ConfiguredJsonValueCodec

@main def tapirSample(): Unit =
    val meal1 = Meal("Test", 1, List("one", "two"))
    val meal1String = writeToString(meal1)

    val nut1 = Nutrition("nut1", true, 123)
    val nut1String = writeToString(nut1)

    println(s"The result string is = $meal1String, and = $nut1String")

    val meal1Read = readFromString[Meal](meal1String)
    val nut1Read = readFromString[Nutrition](nut1String)

    println(s"Read data $meal1Read, $nut1Read")
