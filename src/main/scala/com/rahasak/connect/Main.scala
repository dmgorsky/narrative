package com.rahasak.connect

import java.util.Base64

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object Main extends App {

  flatMap()

  /**
    * Get uri of blob
    *
    * @param id - blob id
    */
  def blobUri(id: Long): Future[String] = {
    Future {
      Thread.sleep(1000)

      s"http://blobs/$id"
    }
  }

  /**
    * Download blob from http
    *
    * @param uri uri of the blob
    * @return base64 encoded blob string
    */
  def downloadBlob(uri: String): Future[String] = {
    Future {
      Thread.sleep(2000)

      if (!uri.startsWith("http")) throw new IllegalArgumentException("invalid uri")
      "cmFoYXNhayBsYWJz"
    }
  }

  /**
    * Fetch blob from ftp
    *
    * @param uri ftp uri
    * @return
    */
  def fetchBlob(uri: String): Future[String] = {
    Future {
      Thread.sleep(2000)

      "cmFoYXNhayBsYWJz"
    }
  }

  /**
    * Decode base64 encoded blob
    *
    * @param blob blob
    */
  def decodeBlob(blob: String) = {
    Future {
      Thread.sleep(2000)

      new String(Base64.getDecoder.decode(blob))
    }
  }

  /**
    * Handle future complete
    */
  def onComplete(): Unit = {
    downloadBlob("http://blobs/19301").onComplete {
      case Success(b) =>
        println(s"success download $b")
      case Failure(e) =>
        println(s"fail download, ${e.getMessage}")
    }

    // wait till few seconds on main thread until future executes and finish its operation
    // otherwise main thread will exit before future execute, then you won't see anything
    Thread.sleep(10000)
  }

  /**
    * Wait till future complete
    */
  def await(): Unit = {
    // wait till future complete, we don't need to have sleep on main thread as with onComplete
    val blob = Await.result(downloadBlob("http://blobs/41221"), 10.seconds)
    println(blob)

    // handle future error with Try
    Try(Await.result(downloadBlob("ftp://blobs/8671"), 10.seconds)) match {
      case Success(b) =>
        println(s"success download $b")
      case Failure(e) =>
        println(s"fail download, ${e.getMessage}")
    }
  }

  /**
    * Handle future error on Await with Try
    */
  def tryAwait(): Unit = {
    Try(Await.result(downloadBlob("ftp://blobs/8671"), 10.seconds)) match {
      case Success(b) =>
        println(s"success download $b")
      case Failure(e) =>
        println(s"fail download, ${e.getMessage}")
    }
  }

  /**
    * Handle error throws in future, return default value on error
    */
  def recover(): Unit = {
    val f: Future[String] = downloadBlob("ftp://blobs/8671").recover {
      case e =>
        println(s"fail download, ${e.getMessage}")
        "empty blob"
    }
    println(Await.result(f, 10.seconds))
  }

  /**
    * Execute another future operation on failure
    */
  def recoverWith(): Unit = {
    val f: Future[String] = downloadBlob("ftp://blobs/8671").recoverWith {
      case e =>
        println(s"fail download, ${e.getMessage}")
        Future {
          "empty blob"
        }
    }
    println(Await.result(f, 10.seconds))
  }

  /**
    * Provide alternative method to execute when future failed
    */
  def fallbackTo(): Unit = {
    val f: Future[String] = downloadBlob("ftp://blobs/8671").fallbackTo {
      println(s"fetch from ftp")
      // fetch blob via ftp
      fetchBlob("ftp://blobs/8671")
    }
    println(Await.result(f, 10.seconds))
  }

  /**
    * Map future output and return new future
    */
  def map(): Unit = {
    // map will return new future
    val f = downloadBlob("http://blobs/8671").map(p => new String(Base64.getDecoder.decode(p)))
    println(Await.result(f, 10.seconds))
  }

  /**
    * Handle nested futures
    */
  def flatMap(): Unit = {
    // flatMap requires to returns the value wrapping it in Future
    val f1: Future[String] = downloadBlob("http://blobs/8671").flatMap { p =>
      Future {
        new String(Base64.getDecoder.decode(p))
      }
    }
    println(Await.result(f1, 10.seconds))

    // remove nested future and obtains one future with flatMap
    val f2: Future[Future[String]] = Future(downloadBlob("http://blobs/8671"))
    val f3: Future[String] = f2.flatMap(_.map(p => new String(Base64.getDecoder.decode(p))))
    println(Await.result(f3, 10.seconds))
  }

  /**
    * Chain multiple futures
    */
  def chain(): Unit = {
    // handle sequence future with flatMap
    val f1: Future[String] = blobUri(34192).flatMap(uri => downloadBlob(uri)).flatMap(blob => decodeBlob(blob))
    println(Await.result(f1, 10.seconds))

    // we can handle chain of futures with for yield
    val f2: Future[String] = for {
      uri <- blobUri(54513)
      blob <- downloadBlob(uri)
      decoded <- decodeBlob(blob)
    } yield decoded
    println(Await.result(f2, 10.seconds))
  }

}
