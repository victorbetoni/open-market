package net.threader.openmarket.util

import org.bukkit.util.io.BukkitObjectInputStream
import java.io.ByteArrayInputStream
import org.bukkit.inventory.ItemStack

import com.google.common.io.BaseEncoding
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayOutputStream

object Util {
  def toB64(item: ItemStack): String = {
    try {
      val outputStream = new ByteArrayOutputStream
      val dataOutput = new BukkitObjectOutputStream(outputStream)
      dataOutput.writeObject(item)
      dataOutput.close()
      val data = outputStream.toByteArray
      BaseEncoding.base32.encode(data)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new IllegalStateException("Unable to save item stacks.")
    }
  }

  def fromB64(str: String): ItemStack = {
    try {
      val base = str.toUpperCase
      val data = BaseEncoding.base32.decode(base)
      val inputStream = new ByteArrayInputStream(data)
      val dataInput = new BukkitObjectInputStream(inputStream)
      val stack = dataInput.readObject.asInstanceOf[ItemStack]
      dataInput.close()
      stack
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException("Unable to create ItemStack.", e)
    }
  }
}
