package net.threader.openmarket.model

import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack

import java.util.UUID

case class ItemBoxItem(holder: OfflinePlayer, id: UUID, stack: ItemStack)
