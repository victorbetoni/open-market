package net.threader.openmarket.ui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

case class GUIItem(index: Int, stack: ItemStack, block: Player => Unit)
