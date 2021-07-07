package net.threader.openmarket.model

import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack

import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

case class ItemBoxItem(holder: OfflinePlayer, id: UUID, stack: ItemStack, available: AtomicBoolean)
