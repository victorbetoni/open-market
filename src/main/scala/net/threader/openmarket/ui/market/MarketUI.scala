package net.threader.openmarket.ui.market

import org.bukkit.entity.Player
import net.threader.openmarket.Market
import net.threader.openmarket.model.MarketItem
import net.threader.openmarket.ui.SimpleGUI

import java.util
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

case class MarketUI(player: Player, initialPage: Int) {
  val pages = new util.ArrayList[MarketPageUI]()
  val iterator = pages.listIterator(initialPage)
  var currentPage: MarketPageUI = _

  var count = new AtomicInteger(0)
  Market.cached foreach { item =>
    var items = ArrayBuffer[MarketItem]()
    if (count.getAndIncrement() > 28) {
      items += item._2
    } else {
      pages.add(MarketPageUI(player, this, items))
      items = new ArrayBuffer[MarketItem]()
    }
  }
}
