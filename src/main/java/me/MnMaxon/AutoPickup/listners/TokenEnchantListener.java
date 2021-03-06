package me.MnMaxon.AutoPickup.listners;

import com.vk2gpz.tokenenchant.event.TEBlockExplodeEvent;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.Config;
import me.MnMaxon.AutoPickup.actions.LocationActions;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler; 
import org.bukkit.event.Listener; 
import org.bukkit.inventory.ItemStack; 

import java.util.List; 

/**
 * Created by MnMaxon on 2/16/2016.  Aren't I great?
 */
public class TokenEnchantListener implements Listener
{
    
    @EventHandler
    public void onTokenEnchantExplode(TEBlockExplodeEvent e)
    {
        ItemStack inhand = e.getPlayer().getInventory().getItemInMainHand();
        if (Config.fortuneData != null)
        {
            String worldId = e.getBlock().getWorld().getUID().toString();
            List<String> list = Config.fortuneData.getStringList(worldId);
            String vecString = e.getBlock().getLocation().toVector().toString();
            if (list.contains(vecString))
            {
                inhand = null;
                list.remove(vecString);
                Config.fortuneData.set(worldId, list);
            }
        }
        if (Config.getBlockedWorlds().contains(e.getPlayer().getWorld()))
        {
            return;
        }
        String name = e.getPlayer().getName();
        for (Block b : e.blockList())
        {
            LocationActions.add(b.getLocation(), e.getPlayer(), AutoPickupPlugin.autoPickup.contains(name), AutoPickupPlugin.autoSmelt.contains(name), AutoPickupPlugin.autoBlock.contains(name), inhand);
        }
    }
}
