package net.threader.openmarket

import net.milkbowl.vault.economy.Economy
import net.threader.openmarket.OpenMarket.log
import net.threader.openmarket.command.MarketCommand
import net.threader.openmarket.db.Database
import net.threader.openmarket.model.{ItemBoxItem, MarketItem}
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

import java.time.{LocalDate, LocalDateTime}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Logger
import scala.collection.mutable.ArrayBuffer

object OpenMarket {
  implicit var log: Logger = Logger.getLogger("Minecraft")
  private var _openMarket: OpenMarket = _
  private var _economy: Economy = _

  implicit def instance: OpenMarket = _openMarket
  implicit def economy: Economy = _economy

  def issues: String = "https://github.com/localthreader/open-market/issues"
}

class OpenMarket extends JavaPlugin {
  override def onEnable(): Unit = {
    OpenMarket._openMarket = this
    saveDefaultConfig()
    getCommand("market").setExecutor(new MarketCommand)
    if (!setupEconomy) {
      log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription.getName))
      getServer.getPluginManager.disablePlugin(this)
      return
    }
    Database.connect()
    Market.load()
    ItemBox.load()
    Bukkit.getScheduler.runTaskTimer(this, new Runnable {
      override def run(): Unit = {
        val remove = ArrayBuffer[MarketItem]()
        Market.cached.values.foreach(item => {
          if (item.expireAt.isEqual(LocalDateTime.now()) ||
            item.expireAt.isBefore(LocalDateTime.now())) {
            remove += item
            item.available.set(false)
            if (item.seller.isOnline) {
              item.seller.asInstanceOf[Player].sendMessage("§aUm item seu no mercado expirou e foi mandado para sua item box.")
            }
          }
        })
        remove.foreach(item => {
          Market.remove(item)
          ItemBox.add(ItemBoxItem(item.seller, item.id, item.item, new AtomicBoolean(true)))
        })
      }
    }, 20, 200)
  }

  private def setupEconomy: Boolean = {
    if (getServer.getPluginManager.getPlugin("Vault") == null) return false
    val rsp = getServer.getServicesManager.getRegistration(classOf[Economy])
    if (rsp == null) return false
    OpenMarket._economy = rsp.getProvider
    OpenMarket._economy != null
  }
}
