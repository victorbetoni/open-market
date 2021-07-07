package net.threader.openmarket.ui.market

import net.threader.openmarket._
import net.threader.openmarket.model.{ItemBoxItem, MarketItem}
import net.threader.openmarket.ui.{GUIItem, SimpleGUI}
import org.bukkit.{Bukkit, Material, Sound}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import java.util
import scala.collection.mutable.ArrayBuffer

case class RemoveItemUI(player: Player, parent: MarketUI, item: MarketItem) {
  val retrieve = new ItemStack(Material.LEAD)
  val meta = retrieve.getItemMeta
  meta.setDisplayName("§9§lREMOVER ITEM DO MERCADO")
  val lore = new util.ArrayList[String]()
  lore.add("")
  lore.add("§7O item será redirecionado para a sua box")
  lore.add("§7de items (báu da página inicial do mercado).")
  lore.add("§7Você pode retirá-lo por lá.")
  meta.setLore(lore)
  retrieve.setItemMeta(meta)

  val guiItems = new ArrayBuffer[GUIItem]()
  guiItems += GUIItem(13, retrieve, player => {
    Market.remove(item)
    ItemBox.add(ItemBoxItem(player, item.id, item.item))
    player.sendMessage("§aItem enviado para a item box com sucesso!")
    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f)
    parent.reopen()
  })

  val glassIndexes = Seq(27, 28, 29, 30, 32, 33, 34, 35)

  val glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE)
  val glassMeta = glass.getItemMeta
  glassMeta.setDisplayName(" ")
  glass.setItemMeta(glassMeta)

  for(index <- glassIndexes) guiItems += GUIItem(index, glass.clone(), _ => {})

  val back = new ItemStack(Material.NETHER_BRICK_SLAB)
  val backMeta = back.getItemMeta
  backMeta.setDisplayName("§c§lRETORNAR")
  back.setItemMeta(backMeta)
  guiItems += GUIItem(31, back, _ => parent.reopen())

  def open(): Unit = SimpleGUI(OpenMarket.instance, player, "Remover item", 4, guiItems).openInventory()
}
