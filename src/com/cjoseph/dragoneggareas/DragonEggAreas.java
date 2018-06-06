package com.cjoseph.dragoneggareas;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.lucko.helper.Commands;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.List;

public class DragonEggAreas extends ExtendedJavaPlugin implements Listener {
    private List<String> badRegions;
    private String c(String msg){
        return ChatColor.translateAlternateColorCodes('&',msg);
    }

    public void enable() {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            getLogger().info("Dependency 'WorldGuard' has been found!");
            saveDefaultConfig();
            badRegions = getConfig().getStringList("bad-regions");
            Bukkit.getPluginManager().registerEvents(this, this);
        } else {
            getLogger().info("Dependency 'WorldGuard' hasn't been found! Disabling..");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Commands.create()
                .assertPermission("dragoneggareas.admin")
                .handler(c -> {
                    if(c.rawArg(0) != null && c.rawArg(0).equals("reload")){
                        reloadConfig();
                        c.reply(ChatColor.translateAlternateColorCodes('&', getConfig().getString("reloaded")));
                    } else
                        c.reply(c("&7Use &c/dea reload &7to reload configuration."));
                })
                .register("dea");
    }

    @Override
    public void disable() {
        badRegions = null;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDragonTeleport(BlockFromToEvent e) {
        Block newBlock = e.getToBlock();

        for (ProtectedRegion r : WGBukkit.getRegionManager(newBlock.getWorld()).getApplicableRegions(newBlock.getLocation())) {
            getLogger().info(r.getId());
            if(badRegions.contains(r.getId())){
                e.setCancelled(true);
                break;
            }
        }
    }
}
