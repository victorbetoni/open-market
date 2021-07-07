package net.threader.openmarket

import com.google.common.collect.{ArrayListMultimap, Multimap}
import net.threader.openmarket.db.Database
import net.threader.openmarket.model.MarketItem
import net.threader.openmarket.util.Util
import org.bukkit.Bukkit

import java.sql.{Connection, SQLException}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import scala.collection.mutable
import scala.util.Using

object Market {
  val cached: mutable.LinkedHashMap[UUID, MarketItem] = mutable.LinkedHashMap[UUID, MarketItem]()

  def load(): Unit = {
    cached.clear()
    Using(Database.connection.createStatement().executeQuery("SELECT * FROM market_items")) { rs =>
      while (rs.next()) {
        val holder = UUID.fromString(rs.getString("holder"))
        val id = UUID.fromString(rs.getString("unique_id"))
        val itemStack = Util.fromB64(rs.getString("item_stack"))
        val date = LocalDateTime.parse(rs.getString("expire_at"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
        val price = rs.getDouble("price")
        val item = MarketItem(Bukkit.getOfflinePlayer(holder), id, itemStack, price, date, new AtomicBoolean(true))
        cached.put(id, item)
      }
    }
  }

  def asyncConnection(block: Connection => Unit): Unit = {
    Bukkit.getScheduler.runTaskAsynchronously(OpenMarket.instance, new Runnable {
      override def run(): Unit = block(Database.connection)
    })
  }

  def add(item: MarketItem): Unit = {
    cached.put(item.id, item)
    asyncConnection { conn =>
      try {
        val statement = conn.prepareStatement("INSERT INTO market_items VALUES (?, ?, ?, ?, ?)")
        statement.setString(1, item.seller.getUniqueId.toString)
        statement.setString(2, item.id.toString)
        statement.setString(3, Util.toB64(item.item))
        statement.setDouble(4, item.price)
        statement.setString(5, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(item.expireAt))
        statement.executeUpdate()
      } catch {
        case ex: SQLException => ex.printStackTrace()
      }
    }
  }


  def remove(item: MarketItem): Unit = {
    cached.remove(item.id)
    asyncConnection { conn =>
      try {
        val statement = conn.prepareStatement("DELETE FROM market_items WHERE unique_id=?")
        statement.setString(1, item.id.toString)
        statement.executeUpdate()
      } catch {
        case ex: SQLException => ex.printStackTrace()
      }
    }
  }
}
