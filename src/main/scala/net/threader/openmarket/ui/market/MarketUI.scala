package net.threader.openmarket.ui.market

import net.threader.openmarket.Market
import net.threader.openmarket.model.MarketItem
import org.bukkit.entity.Player

import java.util
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

case class MarketUI(player: Player, initialPage: Int, itemSupplier: Unit => Iterable[MarketItem]) {
  val pages = new util.ArrayList[MarketPageUI]()
  val currentIndex = new AtomicInteger(initialPage)

  var items = ArrayBuffer[MarketItem]()
  val count = new AtomicInteger(0)
  itemSupplier() foreach { item =>
    if (count.incrementAndGet() <= 28) {
      items += item
    } else {
      count.set(1)
      pages.add(MarketPageUI(player, this, items))
      items = new ArrayBuffer[MarketItem]()
      items += item
    }
  }
  pages.add(MarketPageUI(player, this, items))

  def openCurrentPage(): Unit = {
    if(currentIndex.get() > pages.size() - 1) {
      currentIndex.decrementAndGet()
      openCurrentPage()
    } else {
      pages.get(currentIndex.get()).open()
    }
  }

  def reopen(): Unit = {
    player.closeInventory()
    MarketUI(player, currentIndex.get(), itemSupplier).openCurrentPage()
  }
}
