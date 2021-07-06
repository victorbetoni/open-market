package net.threader.openmarket

import net.milkbowl.vault.economy.Economy
import net.threader.openmarket.OpenMarket.{_economy, _openMarket, log}
import net.threader.openmarket.command.MarketCommand
import net.threader.openmarket.db.Database
import org.bukkit.plugin.java.JavaPlugin

import java.util.logging.Logger

object OpenMarket {
  implicit var log: Logger = Logger.getLogger("Minecraft")
  implicit var _openMarket: OpenMarket = _
  implicit var _economy: Economy = _
  val instance: OpenMarket = _openMarket
  val economy: Economy = _economy
}

class OpenMarket extends JavaPlugin {
  override def onEnable(): Unit = {
    _openMarket = this
    getCommand("market").setExecutor(new MarketCommand)
    if (!setupEconomy) {
      log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription.getName))
      getServer.getPluginManager.disablePlugin(this)
      return
    }
    Database.connect()
    Market.load()
  }

  private def setupEconomy: Boolean = {
    if (getServer.getPluginManager.getPlugin("Vault") == null) return false
    val rsp = getServer.getServicesManager.getRegistration(classOf[Economy])
    if (rsp == null) return false
    _economy = rsp.getProvider
    _economy != null
  }
}
