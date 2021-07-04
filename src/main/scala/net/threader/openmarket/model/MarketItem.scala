package net.threader.openmarket.model

import org.bukkit.inventory.ItemStack

import java.time.LocalDateTime
import java.util.UUID

case class MarketItem(holder: UUID, id: UUID, item: ItemStack, expireAt: LocalDateTime)
