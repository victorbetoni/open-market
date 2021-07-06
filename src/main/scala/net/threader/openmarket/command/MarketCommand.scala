package net.threader.openmarket.command

import net.threader.openmarket.Market
import net.threader.openmarket.model.MarketItem
import net.threader.openmarket.ui.market.MarketUI
import org.bukkit.{Bukkit, Material}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import java.time.LocalDateTime
import java.util.UUID
import scala.util.control.Exception.allCatch

class MarketCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    if(!sender.isInstanceOf[Player]) {
      sender.sendMessage("§cComando apenas para jogadores.")
      return false
    }
    val player = sender.asInstanceOf[Player]
    if (args.length < 1) {
      val marketGui = MarketUI(player, 0)
      marketGui.openInCurrentIndex()
      return false
    }
    if(args.apply(0) == "sell" || args.apply(0) == "vender") {
      if(player.getItemInHand.getType == Material.AIR) {
        player.sendMessage("§cVocê deve estar com um item na mão!")
        return false
      }
      if(Market.itemBox.values.exists(_.seller.getUniqueId.equals(player.getUniqueId))) {
        player.sendMessage("§eEsvazie sua item box do mercado antes de vender algo!")
        return false
      }
      if(Market.itemBox.values.count(_.seller.getUniqueId.equals(player.getUniqueId)) >= 20) {
        player.sendMessage("§eVocê atingiu o número máximo de itens no mercado (20). Retire alguns ou espere que eles expirem.")
        return false
      }
      if(args.length < 2) {
        player.sendMessage("§cSintaxe: /mercado vender <preço>")
        return false
      }
      if(!isDoubleNumber(args.apply(1)) || args.apply(1).toDouble <= 0) {
        player.sendMessage("§cInsira um preço válido")
        return false
      }
      if(!isDoubleNumber(args.apply(1)) || args.apply(1).toDouble <= 0) {
        player.sendMessage("§cInsira um preço válido")
        return false
      }
      val selling = player.getItemInHand
      val value = args.apply(1).toDouble
      val marketItem = MarketItem(Bukkit.getOfflinePlayer(player.getUniqueId), UUID.randomUUID(), selling, value, LocalDateTime.now().plusWeeks(1))
      Market.add(player.getUniqueId, marketItem)
      return false
    }
    true
  }

  def isDoubleNumber(s: String): Boolean = (allCatch opt s.toDouble).isDefined
}
