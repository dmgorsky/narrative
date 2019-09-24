package com.rahasak.http4s

import cats.effect.IO
import com.rahasak.doobie.{Document, Query}
import doobie.implicits._
import doobie.util.transactor.Transactor

class DocumentRepoImpl(xa: Transactor[IO]) extends DocumentRepo {
  override def createDocument(document: Document) = {
    Query.insert(document).run.transact(xa)
  }

  override def updateDocument(id: String, document: Document) = {
    Query.update(document.id, document.name).run.transact(xa)
  }

  override def getDocument(id: String) = {
    Query.searchId(id).option.transact(xa)
  }

  override def getDocuments() = {
    Query.searchAll(0, 10).to[List].transact(xa)
  }
}


