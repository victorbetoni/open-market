package net.threader.openmarket.db

import java.io.{BufferedReader, InputStreamReader}
import java.sql.{Connection, DriverManager, ResultSet, SQLException}
import java.util
import java.util.{Arrays, Objects, Properties}
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.ExecutionContext

object Database {
  var connection: Connection = _
  val props = new Properties()
  implicit val ec: ExecutionContext = ExecutionContext.global

  props.load(Database.getClass.getResourceAsStream("/db/database.properties"))

  connect()

  val fileContent = new StringBuilder

  val fileReader = new BufferedReader(new InputStreamReader(
    Objects.requireNonNull(Database.getClass.getResourceAsStream("/db/setup.sql"))))

  try {
    var line = ""
    line = fileReader.readLine()
    while (line != null) {
      fileContent.append(line)
      line = fileReader.readLine()
    }
  } catch {
    case ex: Throwable =>
      ex.printStackTrace()
  }

  val queries: Array[String] = fileContent.toString.split(";")

  util.Arrays.stream(queries).forEach((query: String) => {
    try {
      connection.createStatement.execute(query)
    } catch {
      case ex: SQLException =>
        ex.printStackTrace()
    }
  })

  def connect(): Unit = {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver")
      connection = DriverManager.getConnection(props.getProperty("address"), props.getProperty("user"), props.getProperty("password"))
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

}
