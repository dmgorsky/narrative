package com.rahasak.doobie

import doobie.implicits._

object Query {

  def createDb = {
    sql"""
         |CREATE DATABASE IF NOT EXISTS mystiko
       """
      .update
  }

  def createTable = {
    sql"""
         |CREATE TABLE IF NOT EXISTS documents (
         |  id VARCHAR(100) PRIMARY KEY,
         |  name VARCHAR(100),
         |  timestamp Long
         |)
       """.stripMargin
      .update
  }

  // insert query
  def insert(document: Document): doobie.Update0 = {
    sql"""
         |INSERT INTO documents (
         |  id,
         |  name,
         |  timestamp
         |)
         |VALUES (
         |  ${document.id},
         |  ${document.name},
         |  ${document.timestamp}
         |)
        """.stripMargin
      .update
  }

  // search document
  def search(id: String): doobie.Query0[Document] = {
    sql"""
         |SELECT * FROM documents
         |WHERE name = $id
       """.stripMargin
      .query[Document]
  }

  // search with fragments
  def searchWithFragment(name: String, asc: Boolean): doobie.Query0[Document] = {
    val f1 = fr"SELECT id, name, timestamp FROM documents"
    val f2 = fr"WHERE name = $name"
    val f3 = fr"ORDER BY timestamp" ++ (if (asc) fr"ASC" else fr"DESC")
    val q = (f1 ++ f2 ++ f3).query[Document]
    q
  }

  // update query
  def update(id: String, name: String): doobie.Update0 = {
    sql"""
         |UPDATE documents
         |SET name = $name
         |WHERE id = $id
       """.stripMargin
      .update
  }

  // delete query
  def delete(id: String): doobie.Update0 = {
    sql"""
         |DELETE FROM documents
         |WHERE id=$id
       """.stripMargin
      .update
  }

}