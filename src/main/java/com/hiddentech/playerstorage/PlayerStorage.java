package com.hiddentech.playerstorage;

import com.hiddentech.playerstorage.listeners.PlayerDataLoadListener;
import com.hiddentech.playerstorage.listeners.PlayerJoinListener;
import com.hiddentech.playerstorage.listeners.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.net.ssl.SSLContext;
import java.io.Console;
import java.util.Objects;
import java.util.logging.Level;

public final class PlayerStorage extends JavaPlugin {

    public Mongo getMongo() {
        return mongo;
    }

    private Mongo mongo;

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

    public long expireTime;

    public boolean mongoEnabled = false;
    public boolean redisEnabled = false;

    @Override
    public void onEnable() {
        plugin = this;
        this.registry = new PlayerRegistry(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new PlayerDataLoadListener(this);
        new GetDataCommand(this);

        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, null, null);
            SSLContext.setDefault(ctx);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            new LoginConfig(getPlugin());
            setStatus();
            enableRedis(redisEnabled);
            enableMongo(mongoEnabled);
        });

//        PlayerStorageAPI.registerValue("jump-count",0);
//        PlayerStorageAPI.registerValue("jump",false);
//        new Jump(this);

    }

    private void enableMongo(boolean mongoEnabled) {
        if(mongoEnabled){
            mongo = new Mongo(getConfig().getString("Mongo_DB_ConnectionString"),getConfig().getString("Mongo_DB_Name"),"PlayerStorageAPI");
            mongo.connect();
            long documents = mongo.getCollection().countDocuments();
            Bukkit.getLogger().log(Level.INFO,"[PlayerStorage] Connected to MongoDB "+documents+" documents");
        }else{
            Bukkit.getLogger().log(Level.INFO,"[PlayerStorage] Not connected to MongoDB");
        }
    }

    private void enableRedis(boolean redisEnabled) {
        if(redisEnabled) {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMinIdle(2);
            poolConfig.setMaxTotal(8);
            this.pool = new JedisPool(poolConfig,
                    getPlugin().getConfig().getString("Redis_Connection"),
                    getPlugin().getConfig().getInt("Redis_Port"), 5000,
                    getPlugin().getConfig().getString("Redis_Password"));
//            Jedis jedis = new Jedis("redis-14639.c266.us-east-1-3.ec2.cloud.redislabs.com",14639,5000);
//            jedis.auth("rbZX3oKRmEroQ7XOWRtUb25cCMRtM2Fr");
            try {
                Jedis jedis = pool.getResource();
                Bukkit.getLogger().log(Level.INFO,"[PlayerStorage] Connected to Redis");
                // Is connected
            } catch (JedisConnectionException e) {
                Bukkit.getLogger().log(Level.INFO,"[PlayerStorage] Not connected to Redis");
                // Not connected
            }

            //Here's where we setup our mongo connection.
            expireTime = getConfig().getLong("Redis_Data_Expire_After");
        }
    }

    private void setStatus() {
        redisEnabled = Objects.equals(getConfig().getString("Storage_Configuration"), "redis");
        mongoEnabled = Objects.equals(getConfig().getString("Storage_Configuration"), "mongo");
        if(redisEnabled==mongoEnabled) {
            redisEnabled = Objects.equals(getConfig().getString("Storage_Configuration"), "both");
            mongoEnabled = Objects.equals(getConfig().getString("Storage_Configuration"), "both");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
