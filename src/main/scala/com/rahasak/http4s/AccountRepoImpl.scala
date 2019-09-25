package com.rahasak.http4s

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor

class AccountRepoImpl(xa: Transactor[IO]) extends AccountRepo {
  override def createAccount(account: Account) = {
    Query.insert(account).run.transact(xa)
  }

  override def updateAccount(id: String, account: Account) = {
    Query.update(account.id, account.name).run.transact(xa)
  }

  override def getAccount(id: String) = {
    Query.searchWithId(id).option.transact(xa)
  }

  override def getAccounts() = {
    Query.searchWithRange(0, 10).to[List].transact(xa)
  }
}


