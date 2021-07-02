package net.threader.openmarket

import com.google.common.collect.{ArrayListMultimap, Multimap, Multimaps}
import org.bukkit.inventory.ItemStack

import java.sql.Connection
import java.util.UUID
import scala.concurrent.{ExecutionContext, blocking}

object Market {
  implicit val ec = ExecutionContext
  val cached: Multimap[UUID, ItemStack] = new ArrayListMultimap[UUID, ItemStack]()

  def load(): Unit = {
    //TODO Load market from db
  }

  def async(block: Connection => Unit): Unit = {
    blocking(
      //TODO Call block with db connection
    )
  }

  def add(user: UUID, item: ItemStack): Unit = async {
    cached.put(user, item)
  }

}
