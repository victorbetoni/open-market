package net.threader.openmarket.ui.market

import net.threader.openmarket.OpenMarket
import net.threader.openmarket.model.Purchase
import net.threader.openmarket.ui.{GUIItem, SimpleGUI}
import org.bukkit.{Bukkit, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import scala.collection.mutable.ArrayBuffer

case class PaymentUI(purchase: Purchase) {
  def build(player: Player): SimpleGUI = {
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

    val confirmItem = GUIItem(11, confirm, player => {
      val result = purchase.perform()
      if (result._1) {
        if(seller.isOnline) {
          seller.getPlayer.sendMessage(s"§a${player.getName} comprou seu(a) ${purchase.item.item.getItemMeta.getDisplayName}!")
        }
      }
      player.sendMessage(result._2)
    })

    SimpleGUI(OpenMarket.instance, player, "Confirm purchase", 3, ArrayBuffer(confirmItem, GUIItem(13, cancel, p => p.closeInventory())))
  }
}
