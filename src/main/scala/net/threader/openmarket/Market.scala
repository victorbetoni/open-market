package net.threader.openmarket

import com.google.common.collect.{ArrayListMultimap, Multimap}
import net.threader.openmarket.Market.{cached, instance, itemsOwner}
import net.threader.openmarket.db.Database
import net.threader.openmarket.model.MarketItem
import net.threader.openmarket.util.Util
import org.bukkit.Bukkit

import java.sql.{Connection, Timestamp}
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.Using
import java.util.Date

object Market {
  implicit val ec = ExecutionContext
  val cached: mutable.LinkedHashMap[UUID, MarketItem] = mutable.LinkedHashMap[UUID, MarketItem]()
  val itemsOwner: Multimap[UUID, MarketItem] = ArrayListMultimap.create()
  val itemBox: mutable.HashMap[UUID, MarketItem] = mutable.HashMap[UUID, MarketItem]()

  def load(): Unit = {
    cached.clear()
    itemsOwner.clear()
    Using(Database.connection.createStatement().executeQuery("SELECT * FROM market_items")) { rs =>
      while (rs.next()) {
        val holder = UUID.fromString(rs.getString("holder"))
        val id = UUID.fromString(rs.getString("id"))
        val itemStack = Util.fromB64(rs.getString("item"))
        val date = rs.getDate("expire_at")
        val price = rs.getDouble("price")
        val item = MarketItem(Bukkit.getOfflinePlayer(holder), id, itemStack, price, new Timestamp(date.getTime).toLocalDateTime)
        cached.put(id, item)
        itemsOwner.put(holder, item)
      }
    }
  }

  def asynConnection(block: Connection => Unit): Unit = {
    Bukkit.getScheduler.runTaskAsynchronously(OpenMarket.instance, new Runnable {
      override def run(): Unit = block(Database.connection)
    })
  }

  def add(user: UUID, item: MarketItem): Unit = asynConnection { conn =>
    cached.put(user, item)
    Using(conn.prepareStatement("INSERT INTO market_items VALUES (?, ?, ?, ?, ?)")) { statement =>
      statement.setString(1, user.toString)
      statement.setString(2, item.id.toString)
      statement.setString(3, Util.toB64(item.item))
      statement.setDouble(4, item.price)
      statement.setDate(5, Date.from(item.expireAt.atZone(ZoneId.systemDefault()).toInstant).asInstanceOf[java.sql.Date])
      statement.executeUpdate()
    }
  }
}
