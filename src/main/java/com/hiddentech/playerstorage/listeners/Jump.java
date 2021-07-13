package com.hiddentech.playerstorage.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.hiddentech.playerstorage.PlayerStorage;
import com.hiddentech.playerstorage.PlayerStorageAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
/*
Example of what the API can do.
 */
public class Jump implements Listener {
    private final PlayerStorage plugin;

    public Jump(PlayerStorage plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void PlayerJump(PlayerJumpEvent event){
        //get values from redis
        Integer integer =PlayerStorageAPI.getInt(event.getPlayer().getUniqueId(), "jump-count");
        Boolean bool = PlayerStorageAPI.getBool(event.getPlayer().getUniqueId(), "jump");
        //set new values to redis
        PlayerStorageAPI.set(event.getPlayer().getUniqueId(), "jump",!bool);
        PlayerStorageAPI.set(event.getPlayer().getUniqueId(), "jump-count",integer+1);
        //send the player a message with the new values.
       event.getPlayer().sendMessage("jump "+PlayerStorageAPI.getBool(event.getPlayer().getUniqueId(),"jump"));
       event.getPlayer().sendMessage("jump "+PlayerStorageAPI.getInt(event.getPlayer().getUniqueId(),"jump-count"));
    }
}
