package net.threader.openmarket

import com.google.common.collect.{ArrayListMultimap, Multimap, Multimaps}
import net.threader.openmarket.db.Database
import org.bukkit.inventory.ItemStack

import java.sql.{Connection, PreparedStatement}
import java.util.UUID
import scala.concurrent.{ExecutionContext, blocking}
import scala.util.Using

object Market {
  implicit val ec = ExecutionContext
  val cached: Multimap[UUID, ItemStack] = new ArrayListMultimap[UUID, ItemStack]()

  def load(): Unit = {
    //TODO Load market from db
  }

  def async(block: Connection => Unit): Unit = {
    blocking(
      block(Database.connection)
    )
  }

  def add(user: UUID, item: ItemStack): Unit = async { conn =>
    cached.put(user, item)
    Using(conn.prepareStatement("INSERT INTO market VALUES (?, ?)")) { statement =>
      statement.setString(1, user.toString)
      statement.setString(2, item.toString)
      statement.executeUpdate()
    }
  }

}
