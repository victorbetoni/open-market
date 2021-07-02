package net.threader.openmarket.model

import java.time.LocalDateTime
import java.util.UUID

case class MarketItem(holder: UUID, id: UUID, expireAt: LocalDateTime)
