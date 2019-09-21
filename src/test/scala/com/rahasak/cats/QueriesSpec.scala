package com.rahasak.cats

import cats.effect.IO
import com.rahasak.cats.Dobbie.Document
import doobie._
import doobie.implicits._
import doobie.scalatest._
import org.scalatest._

class QueriesSpec extends WordSpec with Matchers with IOChecker {
  val transactor = {
    val tx = Transactor
      .fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    )

    Queries.createDb
      .update
      .run
      .transact(tx)
      .unsafeRunSync()

    tx
  }

  "check find" in {
    check(Queries.find("002"))
  }

  "check fragmentFind" in {
    check(Queries.fragmentFind("002", asc = true))
  }

  "check insert" in {
    check(Queries.insert(Document("002", "lamabda", System.currentTimeMillis() / 1000)))
  }

  "check update" in {
    check(Queries.update("002", "ops"))
  }

}
