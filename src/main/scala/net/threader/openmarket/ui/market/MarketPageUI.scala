package net.threader.openmarket.ui.market

import net.threader.openmarket.OpenMarket
import net.threader.openmarket.model.{MarketItem, Purchase}
import net.threader.openmarket.ui.{GUIItem, SimpleGUI}
import org.bukkit.{Bukkit, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import java.time.format.DateTimeFormatter
import java.util
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

case class MarketPageUI(player: Player, parent: MarketUI, items: ArrayBuffer[MarketItem]) {

  val guiItems = ArrayBuffer[GUIItem]()
  val glassIndexes = Seq(0, 1, 2, 3, 5, 6, 7, 8, 45, 46, 47, 51, 52, 53)

  items foreach { marketItem =>
    val index = new AtomicInteger(9)
    val clonedStack = marketItem.item.clone()
    val meta = clonedStack.getItemMeta
    val lore = meta.getLore
    lore.add("")
    lore.add("§7Preço: §a$" + marketItem.price)
    lore.add(s"§7Sendo vendido por: §a${marketItem.seller.getName}")
    lore.add(s"§7Expira em: §a${DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(marketItem.expireAt)}h")
    meta.setLore(lore)
    clonedStack.setItemMeta(meta)
    guiItems += GUIItem(index.getAndIncrement(), clonedStack, player => PaymentUI(player, Purchase(player, marketItem)).open())
  }

  val glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE)
  val glassMeta = glass.getItemMeta
  glassMeta.setDisplayName("")
  glass.setItemMeta(glassMeta)

  for(index <- glassIndexes) guiItems += GUIItem(index, glass.clone(), _ => {})

  val previous = new ItemStack(Material.ARROW)
  val previousMeta = previous.getItemMeta
  previousMeta.setDisplayName("§e◀")
  previous.setItemMeta(previousMeta)

  guiItems += GUIItem(48, previous, player => {
    if(parent.iterator.hasPrevious) {
      parent.currentPage = parent.iterator.previous()
      player.closeInventory()
      parent.currentPage.open()
    } else {
      player.sendMessage("§cNenhuma pagina encontrada")
    }
  })

  val next = new ItemStack(Material.ARROW)
  val nextMeta = next.getItemMeta
  nextMeta.setDisplayName("§e▶")
  next.setItemMeta(nextMeta)

  guiItems += GUIItem(48, next, player => {
    if(parent.iterator.hasNext) {
      parent.currentPage = parent.iterator.next()
      player.closeInventory()
      parent.currentPage.open()
    } else {
      player.sendMessage("§cNenhuma pagina encontrada")
    }
  })

  val close = new ItemStack(Material.NETHER_BRICK_SLAB)
  val closeMeta = close.getItemMeta
  closeMeta.setDisplayName("§c§lFECHAR")
  close.setItemMeta(closeMeta)

  guiItems += GUIItem(48, next, _.closeInventory())

  val infos = new ItemStack(Material.SIGN)
  val infosMeta = infos.getItemMeta
  infosMeta.setDisplayName("§c§lMERCADO INTERNO")
  infos.setItemMeta(infosMeta)
  val infoLore = new util.ArrayList[String]()
  infoLore.add("")
  infoLore.add("§7No mercado interno você pode vender itens pelo")
  infoLore.add("§7seu preço. Todas as mercadorias expiram depois")
  infoLore.add("§7de uma semana caso não sejam compradas.")
  infosMeta.setLore(infoLore)
  infos.setItemMeta(infosMeta)

  guiItems += GUIItem(4, infos, _)

  def open(): Unit = SimpleGUI(OpenMarket.instance, player, "Mercado interno", 6, guiItems).openInventory()

}
