package net.threader.openmarket.ui

import net.arzio.simplegui.SimpleGUI.Rows
import net.arzio.simplegui.{GUIItem, SimpleGUI}
import net.threader.openmarket.OpenMarket
import net.threader.openmarket.model.MarketItem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

import java.time.format.DateTimeFormatter
import java.util
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable

case class MarketPage(items: Seq[MarketItem]) {

  def build(holder: Player): SimpleGUI = {
    val guiItems = new util.ArrayList[GUIItem]()
    items foreach { marketItem =>
      val index = new AtomicInteger(9)
      val seller = Bukkit.getOfflinePlayer(marketItem.holder)
      val clonedStack = marketItem.item.clone()
      val lore = clonedStack.getItemMeta.getLore
      lore.add("")
      lore.add(s"§7Sendo vendido por: §a${seller.getName}")
      lore.add(s"§7Expira em: §a${DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(marketItem.expireAt)}h")
      guiItems.add(new GUIItem(clonedStack, index.getAndIncrement(), (event: InventoryClickEvent, player: Player) => {
        //Perform confirmation, then transaction
      }))
    }
    new SimpleGUI(OpenMarket.instance, holder, "Player Market", Rows.SIX, guiItems)
  }

}
