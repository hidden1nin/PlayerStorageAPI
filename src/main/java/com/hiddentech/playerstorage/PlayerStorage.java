package com.hiddentech.playerstorage;

import com.hiddentech.playerstorage.listeners.PlayerJoinListener;
import com.hiddentech.playerstorage.listeners.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;

public final class PlayerStorage extends JavaPlugin {

    public JedisPool getPool() {
        return pool;
    }

    private JedisPool pool;

    public PlayerRegistry getRegistry() {
        return registry;
    }

    private PlayerRegistry registry;

    public static PlayerStorage getPlugin() {
        return plugin;
    }

    private static PlayerStorage plugin;

    @Override
    public void onEnable() {
        plugin = this;
        this.registry = new PlayerRegistry(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new GetDataCommand(this);
        // Plugin startup logic
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            new LoginConfig(getPlugin());
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMinIdle(2);
            poolConfig.setMaxTotal(8);
            this.pool = new JedisPool(poolConfig,
                    getPlugin().getConfig().getString("Connection"),
                    getPlugin().getConfig().getInt("Port"),5000,
                    getPlugin().getConfig().getString("Password"));
//            Jedis jedis = new Jedis("redis-14639.c266.us-east-1-3.ec2.cloud.redislabs.com",14639,5000);
//            jedis.auth("rbZX3oKRmEroQ7XOWRtUb25cCMRtM2Fr");
            try {
                Jedis jedis = pool.getResource();
                Bukkit.broadcastMessage("[PlayerStorage] Connected to redis");
                // Is connected
            } catch (JedisConnectionException e) {
                Bukkit.broadcastMessage("[PlayerStorage] Not Connected to redis");
                // Not connected
            }

//            jedis.close();
        });

//        PlayerStorageAPI.registerValue("jump-count",0);
//        PlayerStorageAPI.registerValue("jump",false);
//        new Jump(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
