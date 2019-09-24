package com.rahasak.http4s

import cats.effect.IO
import com.rahasak.doobie.Document


trait DocumentRepo {
  def createDocument(document: Document): IO[Int]

  def getDocument(id: String): IO[Option[Document]]

  def getDocuments(): IO[List[Document]]
}


