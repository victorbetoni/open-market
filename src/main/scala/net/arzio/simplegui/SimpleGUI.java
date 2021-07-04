package net.arzio.simplegui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * To open a Simple GUI for a player, just call <code>SimpleGUI.createGUI()</code>.
 * @author Arzio
 */
public class SimpleGUI implements Listener {

	private String playerName;
	private Inventory inventory;
	private List<GUIItem> items;
	private Plugin plugin;

	public SimpleGUI(Plugin plugin, Player player, String chestName, Rows rows, List<GUIItem> items){
		this(plugin, player, chestName, rows, items.toArray(new GUIItem[0]));
	}
	
	/**
	 * Right after being instantiated, opens the GUI to the player.
	 */
	public SimpleGUI(Plugin plugin, Player player, String chestName, Rows rows, GUIItem... items){
		this.plugin = plugin;
		this.playerName = player.getName();
		this.inventory = Bukkit.createInventory(null, rows.slots, chestName);
		this.items = new ArrayList<>(Arrays.asList(items));
	}
	
	public void addItem(GUIItem item) {
		this.items.add(item);
	}
	
	private static GUIItem[] createGuiItemsFromStacks(List<ItemStack> stacks) {
		GUIItem[] guiItems = new GUIItem[stacks.size()];
		
		for (int i = 0; i < stacks.size(); i++) {
			guiItems[i] = new GUIItem(stacks.get(i), i, null);
		}

		return guiItems;
	}
	
	/**
	 * Opens the inventory, registering the inventory event
	 * @param player
	 */
	public void openInventory(Player player) {
		this.putItems();
		player.openInventory(this.inventory);
		
		try {
			Bukkit.getPluginManager().registerEvents(this, this.plugin);
		} catch (Exception e) {
			player.closeInventory();
			player.sendMessage("§cThere was an error when trying to register the inventory.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Automatically runs the action associated to a clicked menu item.
	 */
	@EventHandler
	public void onClick(InventoryClickEvent event){
		event.setCancelled(true);
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			
			if (player.getName().equals(playerName)){
				event.setCancelled(true);
				
				if (this.items == null)
					return;
				
				for (GUIItem item : this.items) {
					if (item.getSlot() == event.getSlot() && item.getItem().equals(event.getCurrentItem())) {
						ItemAction action = item.getAction();
						if (action != null) action.execute(event, player);
					}
				}
			}
		}
	}
	
	/**
	 * Automatically destroys the listeners when being closed.
	 */
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		if (event.getPlayer().getName().equals(playerName)){
			this.destroyListeners();
		}
	}
	
	/**
	 * Automatically destroys the listeners when logging out.
	 */
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		if (event.getPlayer().getName().equals(playerName)){
			this.destroyListeners();
		}
	}
	
	/**
	 * Automatically destroys the listeners when the plugin
	 */
	@EventHandler
	public void pluginDisable(PluginDisableEvent event){
		if (event.getPlugin() != this.plugin){
			return;
		}
		Player player = Bukkit.getPlayerExact(this.playerName);
		if (player != null){
			player.closeInventory();
		}
	}
	
	/**
	 * Put all the menu items in the inventory, cleaning the inventory before it.
	 */
	private void putItems(){
		inventory.clear();
		for (GUIItem guiItem : items){
			inventory.setItem(guiItem.getSlot(), guiItem.getItem());
		}
	}
	
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Disables and destroys the listener, removing every reference to it.
	 */
	public void destroyListeners(){
		HandlerList.unregisterAll(this);
		this.playerName = null;
		this.inventory = null;
		this.items = null;
	}
	
	/**
	 * Represents a number of rows.
	 * This is used on the creating of a new GUI.
	 * @author Arzio
	 */
	public static enum Rows{
		ONE(9),
		TWO(18),
		THREE(27),
		FOUR(36),
		FIVE(45),
		SIX(54);
		
		/**
		 * Total of slots in this row
		 */
		public final int slots;
		
		Rows(int slots){
			this.slots = slots;
		}
	}
	
}
