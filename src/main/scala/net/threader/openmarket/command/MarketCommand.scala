package net.threader.openmarket.command

import net.threader.openmarket.model.MarketItem
import net.threader.openmarket.ui.market.MarketUI
import net.threader.openmarket.{ItemBox, Market}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.{Bukkit, Material}

import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.control.Exception.allCatch

class MarketCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    if(!sender.isInstanceOf[Player]) {
      sender.sendMessage("§cComando apenas para jogadores.")
      return false
    }
    val player = sender.asInstanceOf[Player]
    if (args.length < 1) {
      val marketGui = MarketUI(player, 0, _ => Market.cached.values)
      marketGui.reopen()
      return false
    }
    if(args.apply(0) == "sell" || args.apply(0) == "vender") {
      if(player.getItemInHand.getType == Material.AIR) {
        player.sendMessage("§cVocê deve estar com um item na mão!")
        return false
      }
      if(ItemBox.cached.containsKey(player.getUniqueId)) {
        player.sendMessage("§eEsvazie sua item box do mercado antes de vender algo!")
        return false
      }
      if(Market.cached.values.count(_.seller.getUniqueId.equals(player.getUniqueId)) >= 30) {
        player.sendMessage("§eVocê atingiu o número máximo de itens no mercado (30). Retire alguns ou espere que eles expirem.")
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
      player.setItemInHand(new ItemStack(Material.AIR))
      Market.add(MarketItem(Bukkit.getOfflinePlayer(player.getUniqueId),
                  UUID.randomUUID(),
                  selling,
                  value,
                  LocalDateTime.now().plusWeeks(1),
                  new AtomicBoolean(true)))
      return false
    }
    true
  }

  def isDoubleNumber(s: String): Boolean = (allCatch opt s.toDouble).isDefined
}
