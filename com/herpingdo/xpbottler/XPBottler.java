package com.herpingdo.xpbottler;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class XPBottler extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this,  this);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent evt)
	{
		if (evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getClickedBlock().getType() == Material.DIAMOND_BLOCK)
		{
			Player p = evt.getPlayer();
			if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.GLASS_BOTTLE)
			{
				if (p.isSneaking())
				{
					int xp = getTotalExperience(p);
					int usedXP = 0;
					int bottles = (int)Math.floor(xp / 11);
					int stuff = bottles;
					int bottlesGiven = 0;
					//p.sendMessage(ChatColor.GREEN+"Successfully bottled your "+ChatColor.RED+xp+ChatColor.GREEN+" xp into "+bottles+" bottles!");
					HashMap<Integer, ? extends ItemStack> items = p.getInventory().all(Material.GLASS_BOTTLE);
					if (items == null || items.size() == 0)
					{
						p.sendMessage(ChatColor.DARK_RED+"You don't have enough bottles for that!");
					}
					else
					{
						for (ItemStack i : items.values())
						{
							int amt = i.getAmount();
							for (int ii = 0; ii < i.getAmount(); ii++)
							{
								if (bottlesGiven >= bottles) break;
								ItemStack it = new ItemStack(Material.EXP_BOTTLE);
								it.setAmount(1);
								p.getInventory().addItem(it);
								p.updateInventory();
								bottlesGiven++;
								usedXP+=11;
								amt--;
								if (usedXP > xp) break;
							}
							i.setAmount(amt);
							if (bottlesGiven >= bottles) break;
							if (usedXP > xp) break;
						}
						if (usedXP > xp) usedXP = xp;
						setTotalExperience(p, xp - usedXP);
						p.sendMessage(ChatColor.GREEN+"Successfully bottled "+ChatColor.RED+(usedXP)+ChatColor.GREEN+" of your xp into "+ChatColor.RED+bottlesGiven+ChatColor.GREEN+" bottles!");
					}
				}
				else
				{
					int xp = getTotalExperience(p);
					if (xp < 11)
					{
						p.sendMessage(ChatColor.DARK_RED+"You have too little XP to do that!");
					}
					else
					{
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
					if (p.getItemInHand().getAmount() == 1) p.setItemInHand(null);
					p.sendMessage(ChatColor.GREEN+"Successfully bottled "+ChatColor.RED+"11"+ChatColor.GREEN+" of your XP into "+ChatColor.RED+"1"+ChatColor.GREEN+" XP bottle!");
					ItemStack it = new ItemStack(Material.EXP_BOTTLE);
					it.setAmount(1);
					p.getInventory().addItem(it);
					p.updateInventory();
					setTotalExperience(p, getTotalExperience(p)-11);
					}
				}
			}
		}
	}
	
	public static void setTotalExperience(final Player player, final int exp)
	{
		if (exp < 0)
		{
			throw new IllegalArgumentException("Experience is negative!");
		}
		player.setExp(0);
		player.setLevel(0);
		player.setTotalExperience(0);

		//This following code is technically redundant now, as bukkit now calulcates levels more or less correctly
		//At larger numbers however... player.getExp(3000), only seems to give 2999, putting the below calculations off.
		int amount = exp;
		while (amount > 0)
		{
			final int expToLevel = getExpAtLevel(player);
			amount -= expToLevel;
			if (amount >= 0)
			{
				// give until next level
				player.giveExp(expToLevel);
			}
			else
			{
				// give the rest
				amount += expToLevel;
				player.giveExp(amount);
				amount = 0;
			}
		}
	}
	
	private static int getExpAtLevel(final Player player)
	{
		return getExpAtLevel(player.getLevel());
	}

	public static int getExpAtLevel(final int level)
	{
		if (level > 29)
		{
			return 62 + (level - 30) * 7;
		}
		if (level > 15)
		{
			return 17 + (level - 15) * 3;
		}
		return 17;
	}

	public static int getExpToLevel(final int level)
	{
		int currentLevel = 0;
		int exp = 0;

		while (currentLevel < level)
		{
			exp += getExpAtLevel(currentLevel);
			currentLevel++;
		}
		return exp;
	}

	public static int getTotalExperience(final Player player)
	{
		int exp = (int)Math.round(getExpAtLevel(player) * player.getExp());
		int currentLevel = player.getLevel();

		while (currentLevel > 0)
		{
			currentLevel--;
			exp += getExpAtLevel(currentLevel);
		}
		return exp;
	}

}
