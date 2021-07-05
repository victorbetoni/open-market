package net.threader.openmarket.ui.market

import net.threader.openmarket.OpenMarket
import net.threader.openmarket.model.MarketItem
import net.threader.openmarket.ui.{GUIItem, SimpleGUI}
import org.bukkit.Material
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
  lore.add("O item será redirecionado para a sua box")
  lore.add("de items (báu da página inicial do mercado).")
  lore.add("Você pode retirá-lo por lá.")
  meta.setLore(lore)
  retrieve.setItemMeta(meta)

  val guiItems = new ArrayBuffer[GUIItem]()
  guiItems += GUIItem(12, retrieve, player => {
    //TODO Send the item to the item box
  })

  val glassIndexes = Seq(27, 28, 29, 30, 32, 33, 34, 35)

  val glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE)
  val glassMeta = glass.getItemMeta
  glassMeta.setDisplayName("")
  glass.setItemMeta(glassMeta)

  for(index <- glassIndexes) guiItems += GUIItem(index, glass.clone(), _ => {})

  val back = new ItemStack(Material.NETHER_BRICK_SLAB)
  val backMeta = back.getItemMeta
  backMeta.setDisplayName("§c§lRETORNAR")
  back.setItemMeta(backMeta)
  guiItems += GUIItem(31, back, _ => parent.currentPage.open())

  def open(): Unit = SimpleGUI(OpenMarket.instance, player, "Remover item", 4, guiItems)
}
