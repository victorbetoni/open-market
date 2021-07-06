package net.threader.openmarket.ui.market

import net.threader.openmarket.ItemBox
import net.threader.openmarket.model.ItemBoxItem
import net.threader.openmarket.ui.GUIItem
import org.bukkit.entity.Player

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

case class ItemBoxUI(player: Player, parent: MarketUI, items: ArrayBuffer[ItemBoxItem]) {
  val guiItems = new ArrayBuffer[GUIItem]()
  val index = new AtomicInteger(0)
  items foreach { item =>
    if(index.getAndIncrement() < 45) {
      guiItems += GUIItem(index.get(), item.stack, player => {
        val freeSlot = player.getInventory.firstEmpty()
        if(freeSlot != -1) {
          player.getInventory.setItem(freeSlot, item.stack)
          ItemBox.cached.get(player.getUniqueId).remove(item)
          parent.reopen()
        } else {
          player.sendMessage("§cVocê não tem nenhum slot livre no inventário.")
        }
      })
    }
  }
}
