package net.threader.openmarket.ui.market

import net.threader.openmarket.Market
import net.threader.openmarket.model.MarketItem
import net.threader.openmarket.ui.GUIItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

case class ItemBoxUI(player: Player, parent: MarketUI, items: ArrayBuffer[MarketItem]) {
  val guiItems = new ArrayBuffer[GUIItem]()
  val index = new AtomicInteger(0)
  items foreach { item =>
    if(index.getAndIncrement() < 45) {
      guiItems += GUIItem(index.get(), item.item, player => {
        val freeSlot = player.getInventory.firstEmpty()
        if(freeSlot != -1) {
          player.getInventory.setItem(freeSlot, item.item)
          Market.itemBox.remove(item.id)
          player.closeInventory()
          parent.openInCurrentIndex()
        } else {
          player.sendMessage("§cVocê não tem nenhum slot livre no inventário.")
        }
      })
    }
  }
}
