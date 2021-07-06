package net.threader.openmarket.db

import net.threader.openmarket.OpenMarket

import java.io.{BufferedReader, File, IOException, InputStreamReader}
import java.sql.{Connection, DriverManager, SQLException}
import java.util
import java.util.{Objects, Properties}
import scala.concurrent.ExecutionContext
import java.sql.DriverManager
import java.sql.SQLException

object Database {
  var _connection: Connection = _
  var connection: Connection = if (_connection == null) connect() else _connection
  implicit val ec: ExecutionContext = ExecutionContext.global

  private def executeScript(): Unit = {
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

    util.Arrays.stream(queries).forEach(query => {
      try {
        connection.createStatement.execute(query)
      } catch {
        case ex: SQLException =>
          ex.printStackTrace()
      }
    })
  }

  def connect(): Connection = {
    val file = new File(OpenMarket.instance.getDataFolder, "database.db")
    try {
      if (!file.exists) file.createNewFile
      _connection = DriverManager.getConnection("jdbc:sqlite:" + file)
      executeScript()
      return connection
    } catch {
      case throwables@(_: SQLException | _: IOException) =>
        throwables.printStackTrace()
        OpenMarket.instance.getPluginLoader.disablePlugin(OpenMarket.instance)
    }
    null
  }

}
