package com.rahasak.http4s

import cats.effect.IO
import com.zaxxer.hikari.HikariConfig

object Config {
  def load(conf: String = "application.conf") = {
    IO {
      val config = new HikariConfig()
      config.setJdbcUrl("jdbc:mysql://localhost:3306/mystiko")
      config.setUsername("root")
      config.setPassword("root")
      config.setMaximumPoolSize(5)

      config
    }
  }
}
