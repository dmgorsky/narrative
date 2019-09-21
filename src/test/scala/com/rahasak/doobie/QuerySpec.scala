package com.rahasak.doobie

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.scalatest._
import org.scalatest._

class QuerySpec extends WordSpec with Matchers with IOChecker {

  val transactor = {
    val tx = Transactor
      .fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    )

    Query.createTable
      .run
      .transact(tx)
      .unsafeRunSync()

    tx
  }

  "check search" in {
    check(Query.search("002"))
  }

  "check searchWithFragment" in {
    check(Query.searchWithFragment("002", asc = true))
  }

  "check insert" in {
    check(Query.insert(Document("002", "lamabda", System.currentTimeMillis() / 1000)))
  }

  "check update" in {
    check(Query.update("002", "ops"))
  }

}
