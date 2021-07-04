package net.arzio.simplegui;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents a item in the GUI.
 * A GUI can have multiple Items.
 * 
 * @author Arzio
 *
 */
public class GUIItem {

	private int slotNumber;
	private ItemAction action;
	private ItemStack stack;
	
	/**
	 * Constructor for the GUI Item.
	 * You can put multiple of it in <code>SimpleGUI.createGUI()</code>.
	 * 
	 * @param item Item you want to show
	 * @param itemName Name of the item
	 * @param slotNumber Slot number of the item
	 * @param action Action which will be executed on clicking (any implementation you want)
	 * @param lore The lore for the item
	 */
	public GUIItem(Material item, String itemName, int slotNumber, ItemAction action, String... lore){
		this.setItem(item, itemName, lore);
		this.slotNumber = slotNumber;
		this.action = action;
	}
	
	public GUIItem(ItemStack stack, int slotNumber, ItemAction action){
		this.setItem(stack);
		this.slotNumber = slotNumber;
		this.action = action;
	}
	
	public void setItem(Material item, String itemName, String... lore) {
		ItemStack stack = new ItemStack(item);
		
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(itemName);
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		
		this.setItem(stack);
	}
	
	public void setItem(ItemStack stack) {
		this.stack = stack;
	}
	
	public int getSlot(){
		return this.slotNumber;
	}
	
	public ItemStack getItem(){
		return this.stack;
	}
	
	public ItemAction getAction() {
		return this.action;
	}
	
}
