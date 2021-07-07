package net.threader.openmarket.ui.market

import net.threader.openmarket.model.{ItemBoxItem, MarketItem, Purchase}
import net.threader.openmarket.ui.{GUIItem, SimpleGUI}
import net.threader.openmarket.{ItemBox, OpenMarket}
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

import java.time.format.DateTimeFormatter
import java.util
import scala.collection.mutable.ArrayBuffer

case class MarketPageUI(player: Player, parent: MarketUI, items: ArrayBuffer[MarketItem]) {

  val guiItems = ArrayBuffer[GUIItem]()
  val glassIndexes = Seq(0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 27, 36, 44, 45, 46, 47, 51, 52, 53)
  val itemIndexes = Seq(10, 11, 12, 13, 14, 15, 16,
                        19, 20, 21, 22, 23, 24, 25,
                        28, 29, 30, 31, 32, 33, 34,
                        37, 38, 39, 40, 41, 42, 43)
  val itemIterator: Iterator[Int] = itemIndexes.iterator

  items foreach { marketItem =>
    val index = itemIterator.next()
    val clonedStack = marketItem.item.clone()
    val meta = clonedStack.getItemMeta
    val lore = if (meta.hasLore) meta.getLore else new util.ArrayList[String]()
    lore.add("")
    lore.add("§7Preço: §a$" + marketItem.price)
    lore.add(s"§7Sendo vendido por: §a${marketItem.seller.getName}")
    lore.add(s"§7Expira em: §a${DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(marketItem.expireAt)}h")
    meta.setLore(lore)
    clonedStack.setItemMeta(meta)
    guiItems += GUIItem(index, clonedStack, player => {
      if(!marketItem.available.get()) {
        player.sendMessage("§cEsse item não está mais no mercado.")
        parent.reopen()
      } else if(player.getUniqueId.equals(marketItem.seller.getUniqueId)) {
        RemoveItemUI(player, parent, marketItem).open()
      } else {
        PaymentUI(player, parent, Purchase(player, marketItem)).open()
      }
    })
  }

  val glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE)
  val glassMeta: ItemMeta = glass.getItemMeta
  glassMeta.setDisplayName(" ")
  glass.setItemMeta(glassMeta)

  for(index <- glassIndexes) guiItems += GUIItem(index, glass.clone(), _ => {})

  val previous = new ItemStack(Material.ARROW)
  val previousMeta: ItemMeta = previous.getItemMeta
  previousMeta.setDisplayName("§e◀")
  previous.setItemMeta(previousMeta)

  guiItems += GUIItem(48, previous, player => {
    if(parent.iterator.hasPrevious) {
      parent.currentIndex = parent.iterator.previousIndex()
      parent.reopen()
    } else {
      player.sendMessage("§cNenhuma pagina encontrada")
    }
  })

  val next = new ItemStack(Material.ARROW)
  val nextMeta: ItemMeta = next.getItemMeta
  nextMeta.setDisplayName("§e▶")
  next.setItemMeta(nextMeta)

  guiItems += GUIItem(50, next, player => {
    if(parent.iterator.hasNext) {
      parent.currentIndex = parent.iterator.nextIndex()
      parent.reopen()
    } else {
      player.sendMessage("§cNenhuma pagina encontrada")
    }
  })

  val close = new ItemStack(Material.NETHER_BRICK_SLAB)
  val closeMeta: ItemMeta = close.getItemMeta
  closeMeta.setDisplayName("§c§lFECHAR")
  close.setItemMeta(closeMeta)

  guiItems += GUIItem(49, close, _.closeInventory())

  val info = new ItemStack(Material.PAINTING)
  val infoMeta: ItemMeta = info.getItemMeta
  infoMeta.setDisplayName("§a§lMERCADO INTERNO")
  info.setItemMeta(infoMeta)
  val infoLore = new util.ArrayList[String]()
  infoLore.add("")
  infoLore.add("§7No mercado interno você pode vender itens pelo")
  infoLore.add("§7seu preço. Todas as mercadorias expiram depois")
  infoLore.add("§7de uma semana caso não sejam compradas.")
  infoMeta.setLore(infoLore)
  info.setItemMeta(infoMeta)
  guiItems += GUIItem(4, info, p => {})

  val itemBox = new ItemStack(Material.CHEST)
  val itemBoxMeta: ItemMeta = itemBox.getItemMeta
  itemBoxMeta.setDisplayName("§2§lITEM BOX")
  val boxLore = new util.ArrayList[String]()
  boxLore.add("")
  boxLore.add("§7Seus itens do mercado vem para cá quando")
  boxLore.add("§7expiram. Lembre-se de esvaziar ela antes")
  boxLore.add("§7de colocar algum item a venda.")
  itemBoxMeta.setLore(boxLore)
  itemBox.setItemMeta(itemBoxMeta)
  val itemsInBox = new ArrayBuffer[ItemBoxItem]()
  ItemBox.cached.get(player.getUniqueId).forEach(x => itemsInBox += x)

  guiItems += GUIItem(26, itemBox, player => ItemBoxUI(player, parent, itemsInBox).open())

  def open(): Unit = SimpleGUI(OpenMarket.instance, player, "Mercado interno", 6, guiItems).openInventory()
}
