package net.threader.openmarket.model

import net.threader.openmarket.OpenMarket
import org.bukkit.OfflinePlayer

case class Purchase(buyer: OfflinePlayer, item: MarketItem) {
  def perform(): (Boolean, String) = {
    val economy = OpenMarket.economy
    if(economy.getBalance(buyer) < item.price) {
      return (false, "§cVocê não tem dinheiro o suficiente.")
    }
    if(item.sold.get()) {
      return (false, "§cEsse item já foi vendido.")
    }
    economy.depositPlayer(item.seller, item.price)
    economy.withdrawPlayer(buyer, item.price)
    item.sold.set(true)
    (true, "§aCompra realizada com sucesso!")
  }

}
