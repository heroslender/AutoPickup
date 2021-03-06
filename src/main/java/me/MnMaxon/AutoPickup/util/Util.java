package me.MnMaxon.AutoPickup.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.Config;
import me.MnMaxon.AutoPickup.actions.AutoSmelt;
import org.bukkit.Material;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import haveric.stackableItems.util.InventoryUtil;

public class Util
{

    public static void warn(Player p)
    {
        if (Config.warnOnFull
        && p != null && p.isValid()
        && ( ! AutoPickupPlugin.warnCooldown.containsKey(p.getName())
            || AutoPickupPlugin.warnCooldown.get(p.getName()) < Calendar.getInstance().getTimeInMillis())
        && AutoPickupPlugin.fullNotify.contains(p.getName()))
        {
            p.sendMessage(Message.ERROR0FULL_INVENTORY + "");
            AutoPickupPlugin.warnCooldown.put(p.getName(), 5000 + Calendar.getInstance().getTimeInMillis());
        }
    }


    private static HashMap < Integer, ItemStack > giveItem(Player p, Inventory inv, ItemStack is)
    {
        if (is == null)
        {
            return new HashMap <> ();
        }

        if ( ! Config.usingStackableItems || p == null)
        {
            return inv.addItem(is);
        }
        ItemStack toSend = is.clone();
        ItemStack remaining = null;
        int freeSpaces = InventoryUtil.getPlayerFreeSpaces(p, toSend);
        if (freeSpaces < toSend.getAmount())
        {
            remaining = toSend.clone();
            remaining.setAmount(toSend.getAmount() - freeSpaces);
            toSend.setAmount(freeSpaces);
        }

        if (toSend.getAmount() > 0)
        {
            InventoryUtil.addItemsToPlayer(p, toSend, "pickup");
        }
        HashMap < Integer, ItemStack > map = new HashMap <> ();
        if (remaining != null)
        {
            map.put(0, remaining);
        }
        return map;
    }

    public static HashMap < Integer, ItemStack > giveItem(Player p, ItemStack is)
    {
        return giveItem(p, p.getInventory(), is);
    }

    public static ItemStack easyItem(String name, Material material, int amount, int durability, String... lore)
    {
        ItemStack is = new ItemStack(material);
        if (durability > 0)
        {
            is.setDurability((short)durability);
        }

        if (amount > 1)
        {
            is.setAmount(amount);
        }

        if (is.getItemMeta() != null)
        {
            ItemMeta im = is.getItemMeta();
            if (name != null)
            {
                im.setDisplayName(name);
            }

            if (lore != null)
            {
                ArrayList < String > loreList = new ArrayList <> ();
                Collections.addAll(loreList, lore);
                im.setLore(loreList);
            }
            is.setItemMeta(im);
        }
        return is;
    }

    public static Item doFortune(Player player, Item item, ItemStack tool)
    {
        boolean doFortune = false;

        ItemStack is = item.getItemStack();

        //if smelting with fortune is enabled
        if (AutoPickupPlugin.autoSmelt.contains(player.getName()))
        {
            item.setItemStack(AutoSmelt.smelt(is).getNewItem());
            if (Config.smeltFortune && Arrays.asList(Material.IRON_INGOT, Material.GOLD_INGOT).contains(item.getItemStack().getType()))
            {
                doFortune = true;
            }
        }

        //if vanilla fortune applies
        if (Config.fortuneList.contains(item.getItemStack().getType()))
        {
            doFortune = true;
        }

        // For coal, diamond, emerald, nether quartz, and lapis lazuli, level I gives a 33% chance to multiply drops by 2
        // (averaging 33% increase), level II gives a chance to multiply drops by 2 or 3 (25% chance each, averaging 75% increase),
        // and level III gives a chance to multiply drops by 2, 3, or 4 (20% chance each, averaging 120% increase).
        if (doFortune && tool != null && tool.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS))
        {
            int level = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            int multiplier = new Random().nextInt(level + 2) + 1;
            item.getItemStack().setAmount(item.getItemStack().getAmount() * multiplier);
        }
        return item;
    }

}