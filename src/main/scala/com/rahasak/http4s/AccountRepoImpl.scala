package com.rahasak.http4s

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor

class AccountRepoImpl(xa: Transactor[IO]) extends AccountRepo {
  override def createAccount(account: Account) = {
    AccountQuery.insert(account).run.transact(xa)
  }

  override def updateAccount(id: String, account: Account) = {
    AccountQuery.update(account.id, account.name).run.transact(xa)
  }

  override def getAccount(id: String) = {
    AccountQuery.searchWithId(id).option.transact(xa)
  }

  override def getAccounts() = {
    AccountQuery.searchWithRange(0, 10).to[List].transact(xa)
  }
}


