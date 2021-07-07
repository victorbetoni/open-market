package net.threader.openmarket.model

import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack

import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

case class MarketItem(seller: OfflinePlayer, id: UUID, item: ItemStack, price: Double, expireAt: LocalDateTime, available: AtomicBoolean)
