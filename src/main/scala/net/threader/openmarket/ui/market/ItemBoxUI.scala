package net.threader.openmarket.ui.market

import net.threader.openmarket.{ItemBox, OpenMarket}
import net.threader.openmarket.model.ItemBoxItem
import net.threader.openmarket.ui.{GUIItem, SimpleGUI}
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

case class ItemBoxUI(player: Player, parent: MarketUI) {
  val guiItems = new ArrayBuffer[GUIItem]()
  val index = new AtomicInteger(-1)
  ItemBox.cached.get(player.getUniqueId) forEach { item =>
    if(item.available.get() && index.incrementAndGet() < 45) {
      guiItems += GUIItem(index.get(), item.stack, player => {
        val freeSlot = player.getInventory.firstEmpty()
        if(freeSlot != -1) {
          player.getInventory.setItem(freeSlot, item.stack)
          ItemBox.remove(item)
          item.available.set(false)
          parent.reopen()
        } else {
          player.sendMessage("§cVocê não tem nenhum slot livre no inventário.")
        }
      })
    }
  }

  val glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE)
  val glassMeta = glass.getItemMeta
  glassMeta.setDisplayName(" ")
  glass.setItemMeta(glassMeta)

  val glassIndexes = Seq(45, 46, 47, 48, 50, 51, 52, 53)

  for(index <- glassIndexes) guiItems += GUIItem(index, glass.clone(), _ => {})

  val back = new ItemStack(Material.NETHER_BRICK_SLAB)
  val backMeta = back.getItemMeta
  backMeta.setDisplayName("§c§lRETORNAR")
  back.setItemMeta(backMeta)
  guiItems += GUIItem(49, back, _ => parent.reopen())

  def open(): Unit = SimpleGUI(OpenMarket.instance, player, "Item Box", 6, guiItems).openInventory()
}
