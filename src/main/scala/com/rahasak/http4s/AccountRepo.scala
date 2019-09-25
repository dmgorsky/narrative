package com.rahasak.http4s

import cats.effect.IO


trait AccountRepo {
  def createAccount(account: Account): IO[String]

  def updateAccount(id: String, account: Account): IO[Int]

  def getAccount(id: String): IO[Option[Account]]

  def getAccounts(): IO[List[Account]]
}


