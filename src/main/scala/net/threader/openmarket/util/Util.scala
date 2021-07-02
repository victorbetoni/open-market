package net.threader.openmarket.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.inventory.ItemStack

object Util {
  def serialize(item: ItemStack): String = new Gson().toJson(item.serialize())

  def deserialize(json: String): ItemStack = new Gson().fromJson(json, new TypeToken[Map[String, Object]]().getType)
}
