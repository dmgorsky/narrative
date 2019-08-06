package com.rahasak.connect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object Main extends App {

  await()

  /**
    * Mock funcation to download blob
    * @param uri uri of the blob
    * @return base64 encoded blob string
    */
  def downloadBlob(uri: String) = {
    Future {
      Thread.sleep(5000)

      if (!uri.startsWith("http")) throw new IllegalArgumentException("invalid uri")
      Option("cmFoYXNhayBsYWJh")
    }
  }

  /**
    * Handle future complete
    */
  def onComplete(): Unit = {
    //    def future(wait: Long) = Future {
    //      println("future executing")
    //      Thread.sleep(wait)
    //      s"future completed after ${wait / 1000} seconds"
    //    }
    //
    //    future(-12).onComplete {
    //      case Success(r) =>
    //        println(r)
    //      case Failure(e) =>
    //        println(s"future failed, ${e.getMessage}")
    //        e.printStackTrace()
    //    }

    // wait till few seconds on main thread until future executes and finish its operation
    // otherwise main thread will exit before future execute, then you won't see anything
    Thread.sleep(10000)
  }

  /**
    * Wait till future complete with Await
    */
  def await(): Unit = {
    // wait till future complete, we don't need to have sleep on main thread as with onComplete
    val out = Await.result(downloadBlob("http://blobs/41221"), 10.seconds)
    println(out)
  }

  /**
    * Handle future error on Await with Try
    */
  def tryAwait(): Unit = {
    def future(wait: Long) = Future {
      println("future executing")
      Thread.sleep(wait)
      s"future completed after ${wait / 1000} seconds"
    }

    Try(Await.result(future(-10), 10.seconds)) match {
      case Success(r) =>
        println(r)
      case Failure(e) =>
        println(s"future failed, ${e.getMessage}")
    }
  }

  /**
    * Handle error thrown on future, return default value on error
    */
  def recover() = {
    def future(wait: Long) = Future {
      println("future executing")
      Thread.sleep(wait)
      s"future completed after ${wait / 1000} seconds"
    }

    val f = future(-10).recover {
      case e =>
        println("error caught")
        e.getMessage
    }
    println(Await.result(f, 10.seconds))
  }

  /**
    * Execute anothre future operation on failure
    */
  def recoverWith() = {
    def future(wait: Long) = Future {
      println("future executing")
      Thread.sleep(wait)
      s"future completed after ${wait / 1000} seconds"
    }

    val f = future(-10).recoverWith {
      case e =>
        println("error caught")
        Future(e.getMessage)
    }
    println(Await.result(f, 10.seconds))
  }

  /**
    * provide alteranative method to execute when future failed
    *
    * @return
    */
  def fallbackTo() = {
    def future(wait: Long) = Future {
      println("future executing")
      Thread.sleep(wait)
      s"future completed after ${wait / 1000} seconds"
    }

    // alteranative future to excute if first future failed
    val f2 = Future(10)

    val f3 = future(-10).fallbackTo(f2)
    println(Await.result(f3, 10.seconds))
  }

  def map() = {
    val f1 = Future(10)

    // map will return new future
    val f2 = f1.map(p => p * 2)
    println(Await.result(f2, 10.seconds))
  }

  def flatMap() = {
    val f1 = Future(10)

    // flatMap will remove future, if want another future we need to wrap the result with Future again
    val f2 = f1.flatMap(p => Future(p * 2))
    println(Await.result(f2, 10.seconds))

    // we can remove nested futures and obtains one future with flatMap
    val f3 = Future(Future(10))
    val f4 = f3.flatMap(_.map(_ * 3))
    println(Await.result(f4, 10.seconds))
  }

  def chain() = {
    // we can handle sequence ftures with flatmap
    def getUri(id: Int): Future[String] = Future(s"http://dev.localhost:8761/blobs/$id")

    def downloadBlob(uri: String): Future[String] = Future("base64 encoded blob")

    val f1 = getUri(1801).flatMap(uri => downloadBlob(uri))
    println(Await.result(f1, 10.seconds))

    // alos we can handle chain of gureus with for yield
    val f2 = for {
      uri <- getUri(1899)
      blob <- downloadBlob(uri)
    } yield blob
    println(Await.result(f2, 10.seconds))
  }

}
