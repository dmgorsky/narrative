package com.rahasak.cats

import com.rahasak.cats.Dobbie.Document
import doobie.implicits._

object Queries {

  def createDb = {
    sql"CREATE TABLE documents (id varchar(100), name varchar(100), timestamp Long)"
  }

  // insert query
  def insert(document: Document): doobie.Update0 = {
    sql"insert into documents (id, name, timestamp) values (${document.id}, ${document.name}, ${document.timestamp})".update
  }

  // find document
  def find(id: String) = {
    sql"select * from documents where name = $id".query[Document]
  }

  // find with fragments
  def fragmentFind(name: String, asc: Boolean) = {
    val f1 = fr"select id, name from documents"
    val f2 = fr"where name = $name"
    val f3 = fr"order by timestamp" ++ (if (asc) fr"asc" else fr"desc")
    val q = (f1 ++ f2 ++ f3).query[Document]
    q
  }

  // update query
  def update(id: String, name: String): doobie.Update0 = {
    sql"update documents set name = $name where id = $id".update
  }

  // delete query
  def delete(id: String): doobie.Update0 = {
    sql"delete from documents where id=$id".update
  }

}
