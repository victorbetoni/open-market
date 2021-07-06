package net.threader.openmarket.ui.market

import net.threader.openmarket.Market
import net.threader.openmarket.model.MarketItem
import org.bukkit.entity.Player

import java.util
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer

case class MarketUI(player: Player, initialPage: Int) {
  val pages = new util.ArrayList[MarketPageUI]()
  val iterator = pages.listIterator(initialPage)
  var currentIndex: Int = initialPage

  var items = ArrayBuffer[MarketItem]()
  Market.cached foreach { item =>
    val count = new AtomicInteger(0)
    if (count.getAndIncrement() < 28) {
      items += item._2
    } else {
      count.set(0)
      pages.add(MarketPageUI(player, this, items))
      items = new ArrayBuffer[MarketItem]()
    }
  }
  pages.add(MarketPageUI(player, this, items))

  def openInCurrentIndex(): Unit = pages.get(currentIndex).open()

  def reopen(): Unit = {
    player.closeInventory()
    MarketUI(player, currentIndex).openInCurrentIndex()
  }
}
