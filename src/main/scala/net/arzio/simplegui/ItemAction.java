package net.arzio.simplegui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ItemAction {
	
	/**
	 * Runs the action implemented for this item.
	 */
	public void execute(InventoryClickEvent event, Player player);
	
}