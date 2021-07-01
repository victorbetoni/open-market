package net.threader.openmarket

import net.threader.openmarket.OpenMarket._openMarket
import org.bukkit.plugin.java.JavaPlugin

object OpenMarket {
  implicit var _openMarket: OpenMarket = _
  val instance: OpenMarket = _openMarket
}

class OpenMarket extends JavaPlugin {

  override def onEnable(): Unit = {
    _openMarket = this
  }

}
