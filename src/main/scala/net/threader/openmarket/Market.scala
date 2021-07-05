package net.threader.openmarket

import com.google.common.collect.{ArrayListMultimap, Multimap}
import net.threader.openmarket.db.Database
import net.threader.openmarket.model.MarketItem
import net.threader.openmarket.util.Util
import org.bukkit.Bukkit

import java.sql.{Connection, Timestamp}
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.util.Using

object Market {
  implicit val ec = ExecutionContext
  val cached: Multimap[UUID, MarketItem] = new ArrayListMultimap[UUID, MarketItem]()

  def load(): Unit = {
    cached.clear()
    Using(Database.connection.createStatement().executeQuery("SELECT * FROM market")) { rs =>
      while(rs.next()) {
        val holder = UUID.fromString(rs.getString("holder"))
        val item = rs.getString("item")
        val query = s"SELECT item, expire_at FROM market_items WHERE unique_id = $item"
        Using(Database.connection.createStatement().executeQuery(query)) { rs =>
          if(rs.next()) {
            val itemStack = Util.deserialize(rs.getString("item"))
            val date = rs.getDate("expire_at")
            cached.put(holder, MarketItem(holder, UUID.fromString(item), itemStack, new Timestamp(date.getTime).toLocalDateTime))
          }
        }
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
    Using(conn.prepareStatement("INSERT INTO market VALUES (?, ?)")) { statement =>
      statement.setString(1, user.toString)
      statement.setString(2, item.toString)
      statement.executeUpdate()
    }
  }

}
