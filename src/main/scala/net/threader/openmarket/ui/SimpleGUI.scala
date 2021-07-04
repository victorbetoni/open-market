package net.threader.openmarket.ui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.{InventoryClickEvent, InventoryCloseEvent}
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.{EventHandler, HandlerList, Listener}
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

import java.util.UUID
import scala.collection.mutable.ArrayBuffer

case class SimpleGUI(plugin: Plugin, holder: Player, chestName: String, rows: Int, items: ArrayBuffer[GUIItem]) extends Listener {

  var inventory: Inventory = Bukkit.createInventory(null, rows*9, chestName)
  var holderId: UUID = holder.getUniqueId

  def this(plugin: Plugin, holder: Player, chestName: String, rows: Int) {
    this(plugin, holder, chestName, rows, new ArrayBuffer[GUIItem]())
  }

  def openInventory(): Unit = {
    this.putItems()
    holder.openInventory(this.inventory)
    try Bukkit.getPluginManager.registerEvents(this, this.plugin)
    catch {
      case e: Exception =>
        holder.closeInventory()
        holder.sendMessage("§cThere was an error when trying to register the inventory.")
        e.printStackTrace()
    }
  }

  def putItems(): Unit = {
    inventory.clear()
    for (guiItem <- items) inventory.setItem(guiItem.index, guiItem.stack)
  }

  @EventHandler def onClick(event: InventoryClickEvent): Unit = {
    event.setCancelled(true)
    event.getWhoClicked match {
      case player: Player =>
        if (player.getUniqueId == holderId) {
          event.setCancelled(true)
          if (this.items == null) return
          for (item <- this.items) {
            if (item.index == event.getSlot && item.stack == event.getCurrentItem) {
              item.block(player)
            }
          }
        }
      case _ =>
    }
  }

  @EventHandler def onClose(event: InventoryCloseEvent): Unit = {
    if (event.getPlayer.getUniqueId == holderId) this.destroyListeners()
  }

  @EventHandler def onQuit(event: PlayerQuitEvent): Unit = {
    if (event.getPlayer.getUniqueId == holderId) this.destroyListeners()
  }

  @EventHandler def pluginDisable(event: PluginDisableEvent): Unit = {
    if (event.getPlugin ne this.plugin) return
    val player = Bukkit.getPlayer(this.holderId)
    if (player != null) player.closeInventory()
  }

  def destroyListeners(): Unit = {
    HandlerList.unregisterAll(this)
    this.holderId = null
    this.inventory = null
  }

}
