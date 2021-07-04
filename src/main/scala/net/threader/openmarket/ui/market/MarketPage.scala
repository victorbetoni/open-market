package net.threader.openmarket.ui.market

import net.threader.openmarket.OpenMarket
import net.threader.openmarket.model.{MarketItem, Purchase}
import net.threader.openmarket.ui.{GUIItem, Rows, SimpleGUI}
import org.bukkit.Bukkit
import org.bukkit.entity.Player

import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

case class MarketPage(items: Seq[MarketItem]) {

  def build(holder: Player): SimpleGUI = {
    val guiItems = ArrayBuffer[GUIItem]()
    items foreach { marketItem =>
      val index = new AtomicInteger(9)
      val clonedStack = marketItem.item.clone()
      val lore = clonedStack.getItemMeta.getLore
      lore.add("")
      lore.add(s"§7Sendo vendido por: §a${marketItem.seller.getName}")
      lore.add(s"§7Expira em: §a${DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(marketItem.expireAt)}h")
      guiItems += GUIItem(index.getAndIncrement(), clonedStack, player => {
        PaymentUI(Purchase(holder, marketItem)).build(holder).openInventory()
      })
    }
    SimpleGUI(OpenMarket.instance, holder, "Player Market", 6, guiItems)
  }

}
