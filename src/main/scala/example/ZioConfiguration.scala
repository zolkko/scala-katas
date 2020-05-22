package example

import zio.{Has, Task}

case class ApiConfig(endpoint: String, port: Int)
case class DbConfig(url: String, user: String, password: String)
case class Config(api: ApiConfig, dbConfig: DbConfig)

object configuration {

  type Configuration = Has[Configuration.Service]

  object Configuration {
    trait Service {
      val load: Task[Config]
    }
  }

}
