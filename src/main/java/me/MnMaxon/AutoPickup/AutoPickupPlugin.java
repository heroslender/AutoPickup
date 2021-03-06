package me.MnMaxon.AutoPickup;

import me.MnMaxon.AutoPickup.commands.AutoBlockCommand;
import me.MnMaxon.AutoPickup.commands.AutoPickup;
import me.MnMaxon.AutoPickup.commands.AutoSell;
import me.MnMaxon.AutoPickup.commands.AutoSmeltCommand;
import me.MnMaxon.AutoPickup.commands.FullNotify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.MnMaxon.AutoPickup.listners.MainListener;
import me.MnMaxon.AutoPickup.listners.MythicListener;
import me.MnMaxon.AutoPickup.listners.MythicMobListener;
import me.MnMaxon.AutoPickup.listners.TokenEnchantListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoPickupPlugin extends JavaPlugin
{

    //TODO: move these
    public static final List < String > autoSmelt = new ArrayList<>();
    public static final List < String > autoPickup = new ArrayList<>();
    public static final List < String > autoBlock = new ArrayList<>();
    public static final List < String > autoSell = new ArrayList<>();
    public static final List < String > fullNotify = new ArrayList<>();
    public static final HashMap < String, Long > warnCooldown = new HashMap<>();

    @Override
    public void onDisable()
    {
        Config.saveAll();
    }

    @Override
    public void onEnable()
    {
        Config.setConfigFolder(this.getDataFolder().getAbsolutePath());
        Config.reloadConfigs();

        getServer().getPluginManager().registerEvents(new MainListener(), this);

        ArrayList < String > plugins = new ArrayList <> ();

        this.getCommand("AutoPickup").setExecutor(new AutoPickup());
        this.getCommand("AutoSmelt").setExecutor(new AutoSmeltCommand());
        this.getCommand("AutoBlock").setExecutor(new AutoBlockCommand());
        this.getCommand("AutoSell").setExecutor(new AutoSell());
        this.getCommand("FullNotify").setExecutor(new FullNotify());

        //Set up compatablility Listeners
        if (getServer().getPluginManager().getPlugin("MythicMobs") != null)
        {
            getServer().getPluginManager().registerEvents(new MythicMobListener(), this);
        }
        if (getServer().getPluginManager().getPlugin("TokenEnchant") != null)
        {
            getServer().getPluginManager().registerEvents(new TokenEnchantListener(), this);
        }
        if (getServer().getPluginManager().getPlugin("QuickSell") != null)
        {
            plugins.add("QuickSell");
            Config.usingQuickSell = true;
        }
        if (getServer().getPluginManager().getPlugin("StackableItems") != null)
        {
            plugins.add("StackableItems");
            Config.usingStackableItems = true;
        }
        if (getServer().getPluginManager().getPlugin("AutoSell") != null)
        {
            plugins.add("AutoSell");
            Config.usingAutoSell = true;
        }
        if (getServer().getPluginManager().getPlugin("MythicDrops") != null)
        {
            plugins.add("MythicDrops");
            getServer().getPluginManager().registerEvents(new MythicListener(), this);
        }
        if (getServer().getPluginManager().getPlugin("MyPet") != null)
        {
            plugins.add("MyPet");
            if ( ! plugins.contains("MythicDrops"))
            {
                getServer().getPluginManager().registerEvents(new MythicListener(), this);
            }
        }
        if (getServer().getPluginManager().getPlugin("FortuneBlocks") != null)
        {
            plugins.add("FortuneBlocks");
        }
        if (getServer().getPluginManager().getPlugin("PrisonGems") != null)
        {
            plugins.add("PrisonGems");
            Config.usingPrisonGems = true;
        }
        if ( ! plugins.isEmpty())
        {
            StringBuilder message = new StringBuilder("[AutoPickup] Detected you are using ");
            for (String pName:plugins)
            {
                if ( ! message.toString().endsWith(" "))
                {
                    message.append(", ");
                }
                message.append(pName);
            }
            Bukkit.getLogger().info(message.toString());
        }

        for (Player p:Bukkit.getOnlinePlayers())
        {
            if (p.hasPermission("AutoPickup.enabled"))
            {
                AutoPickupPlugin.autoPickup.add(p.getName());
            }
            if (p.hasPermission("AutoBlock.enabled"))
            {
                AutoPickupPlugin.autoBlock.add(p.getName());
            }
            if (p.hasPermission("AutoSmelt.enabled"))
            {
                AutoPickupPlugin.autoSmelt.add(p.getName());
            }

            if (p.hasPermission("AutoSell.enabled") && Config.usingQuickSell)
            {
                AutoPickupPlugin.autoSell.add(p.getName());
            }
        }
    }
}