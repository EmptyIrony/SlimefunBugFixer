package me.zhanshi123.sbf;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Listeners implements Listener {
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Action act = e.getAction();
		if (!(act.equals(Action.RIGHT_CLICK_AIR) || act.equals(Action.RIGHT_CLICK_BLOCK)))
			return;
		ItemStack item = e.getItem();
		if (item == null)
			return;
		if (!item.hasItemMeta())
			return;
		if (!item.getItemMeta().hasDisplayName())
			return;
		if (!item.getItemMeta().getDisplayName().contains(ConfigManager.getInstance().getBackpackName()))
			return;
		Player p = e.getPlayer();
		String name = p.getName();
		if (BackpackCooldown.getInstance().isReady(name, 500)) {
			BackpackCooldown.getInstance().put(name);
		} else {
			e.setCancelled(true);
			p.closeInventory();
			p.sendMessage(Messages.getMessages().getNoQuickOpen().replace("&", "§"));
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (!e.getBlock().getType().equals(Material.CAULDRON)) {
			return;
		}
		Block b = e.getBlock();
		Location loc = b.getLocation();
		loc.setY(loc.getY() + 1);
		Block upon = loc.getBlock();
		if (upon.getType().equals(Material.AIR)) {
			return;
		}
		e.setCancelled(true);
		e.getPlayer().sendMessage(Messages.getMessages().getNoPlace().replace("&", "§"));
	}

	@EventHandler
	public void onPlaceWithInv(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (!p.getOpenInventory().getTitle().contains(ConfigManager.getInstance().getBackpackTitle())) {
			return;
		}
		e.setCancelled(true);
		e.getPlayer().sendMessage(Messages.getMessages().getNoPlaceWithInv().replace("&", "§"));
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player p = e.getPlayer();
		if (!p.getOpenInventory().getTitle().contains(ConfigManager.getInstance().getBackpackTitle())) {
			return;
		}
		e.setCancelled(true);
		p.sendMessage(Messages.getMessages().getNoDrop().replace("&", "§"));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (player.getOpenInventory().getTitle().contains("Slimefun")) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (e.getItem() == null) {
			return;
		}
		ItemStack itemStack = e.getItem();
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta.getDisplayName() == null) {
			return;
		}

		//修复镐子刷物品
		if (Main.getInstance().getConfig().getStringList("pickaxe_item_name").contains(itemMeta.getDisplayName()) && itemStack.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
			itemStack.removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
			player.getInventory().setItemInHand(itemStack);
			return;
		}

		//修复背包刷物品
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			for (String name : Main.getInstance().getConfig().getStringList("backpack_item_name")) {
				if (itemMeta.getDisplayName().contains(name) && !itemMeta.getDisplayName().contains(Main.getInstance().getConfig().getString("ender_backpack_item_name"))) {
					player.closeInventory();
					break;
				}
			}
		}
	}

	//修复方块放置机刷物品
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMoveItem(InventoryMoveItemEvent e) {
		if (e.getSource().getTitle().equals(Main.getInstance().getConfig().getString("block_placer_item_name"))) {
			e.setCancelled(true);
		}
	}


}
