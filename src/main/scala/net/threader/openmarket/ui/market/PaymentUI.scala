package net.threader.openmarket.ui.market

import net.threader.openmarket.OpenMarket
import net.threader.openmarket.model.Purchase
import net.threader.openmarket.ui.{GUIItem, SimpleGUI}
import org.bukkit.{Bukkit, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

case class PaymentUI(purchase: Purchase) {
  def build(player: Player): SimpleGUI = {
    val confirm = new ItemStack(Material.LIME_TERRACOTTA)
    val meta = confirm.getItemMeta
    meta.setDisplayName("§a§lCONFIRMAR")
    val lore = meta.getLore
    lore.add("")
    lore.add("§7Confirmando a compra você está ciente que:")
    lore.add("§7- Você não poderá pedir reembolso")
    lore.add(s"§7- ${purchase.item.price} de money será sacado da sua conta")
    lore.add(s"§7- ${purchase.item.price} será depositado na conta de ${purchase.seller.getName}")
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

    SimpleGUI(OpenMarket.instance, player, "Confirm purchase", 3, ArrayBuffer(GUIItem(11, confirm, p => {}), GUIItem(13, cancel, p => {})))
  }
}
