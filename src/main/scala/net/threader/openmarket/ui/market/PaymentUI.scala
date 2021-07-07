package net.threader.openmarket.ui.market

import net.threader.openmarket.OpenMarket
import net.threader.openmarket.model.Purchase
import net.threader.openmarket.ui.{GUIItem, SimpleGUI}
import net.threader.openmarket.ui.market.MarketUI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.{Material, Sound}

import scala.collection.mutable.ArrayBuffer

case class PaymentUI(player: Player, parent: MarketUI ,purchase: Purchase) {
  val guiItems = ArrayBuffer[GUIItem]()
  val seller = purchase.item.seller
  val confirm = new ItemStack(Material.LIME_TERRACOTTA)
  val meta = confirm.getItemMeta
  meta.setDisplayName("§a§lCONFIRMAR")
  val lore = meta.getLore
  lore.add("")
  lore.add("§7Confirmando a compra você está ciente que:")
  lore.add("§7- Você não poderá exigir reembolso")
  lore.add("§7- R$" + purchase.item.price + " será sacado da sua conta")
  lore.add("§7- R$" + purchase.item.price + " será depositado na conta de " + purchase.item.seller.getName)
  meta.setLore(lore)
  confirm.setItemMeta(meta)

  val cancel = new ItemStack(Material.RED_TERRACOTTA)
  val cMeta = cancel.getItemMeta
  cMeta.setDisplayName("§c§lCANCELAR")
  val cLore = cMeta.getLore
  cLore.add("")
  cLore.add("§7Cancelar a compra")
  cMeta.setLore(cLore)
  cancel.setItemMeta(cMeta)

  guiItems += GUIItem(12, confirm, player => {
    val result = purchase.perform()
    if (result._1) {
      player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
      if (seller.isOnline) {
        seller.getPlayer.playSound(seller.getPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        seller.getPlayer.sendMessage(s"§a${player.getName} comprou seu(a) ${purchase.item.item.getItemMeta.getDisplayName}!")
      }
    } else {
      player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f)
    }
    player.sendMessage(result._2)
  })

  guiItems += GUIItem(14, cancel, _.closeInventory())

  val glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE)
  val glassMeta = glass.getItemMeta
  glassMeta.setDisplayName(" ")
  glass.setItemMeta(glassMeta)

  val glassIndexes = Seq(27, 28, 29, 30, 32, 33, 34, 35)

  for(index <- glassIndexes) guiItems += GUIItem(index, glass.clone(), _ => {})

  val back = new ItemStack(Material.NETHER_BRICK_SLAB)
  val backMeta = back.getItemMeta
  backMeta.setDisplayName("§c§lRETORNAR")
  back.setItemMeta(backMeta)
  guiItems += GUIItem(49, back, _ => parent.reopen())

  def open(): Unit = SimpleGUI(OpenMarket.instance, player, "Confirm purchase", 3, ArrayBuffer(confirmItem, GUIItem(13, cancel, p => p.closeInventory()))).openInventory()
}
