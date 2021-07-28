package com.hiddentech.playerstorage;


import com.hiddentech.playerstorage.events.PlayerDataChangeEvent;
import com.hiddentech.playerstorage.listeners.PlayerJoinListener;
import com.hiddentech.playerstorage.listeners.PlayerQuitListener;
import com.hiddentech.playerstorage.types.DataType;
import com.hiddentech.playerstorage.types.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerStorageAPI {

    public boolean redisEnabled;
    public boolean mongoEnabled;
    public long expireTime;

    public boolean isRedisEnabled() {
        return redisEnabled;
    }

    public boolean isMongoEnabled() {
        return mongoEnabled;
    }

    public Mongo getMongo() {
        return mongo;
    }

    public JedisPool getPool() {
        return pool;
    }

    private Mongo mongo;
    private JedisPool pool;

    public Plugin getPlugin() {
        return plugin;
    }

    private final JavaPlugin plugin;

    public PlayerRegistry getRegistry() {
        return registry;
    }

    private final PlayerRegistry registry;
    private static PlayerStorageAPI instance;

    private PlayerStorageAPI(JavaPlugin plugin) {
        this.plugin = plugin;
        //unsure why this is not being found in my ide
        this.registry = new PlayerRegistry(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        int pluginId = 12037; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(plugin, pluginId);
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, null, null);
            SSLContext.setDefault(ctx);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            new LoginConfig(this);
            setStatus();
            enableRedis(redisEnabled);
            enableMongo(mongoEnabled);
        });

    }
    public static PlayerStorageAPI getInstance(JavaPlugin plugin){
        if(instance==null){
            instance = new PlayerStorageAPI(plugin);
        }
        return instance;
    }

    private void enableMongo(boolean mongoEnabled) {
        File file = new File(getPlugin().getDataFolder() + "/Database.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (mongoEnabled) {
            mongo = new Mongo(yml.getString("Mongo_DB_ConnectionString"), yml.getString("Mongo_DB_Name"), "PlayerStorageAPI");
            mongo.connect();
            long documents = mongo.getCollection().countDocuments();
            Bukkit.getLogger().log(Level.INFO, "[PlayerStorage] Connected to MongoDB " + documents + " documents");
        } else {
            Bukkit.getLogger().log(Level.INFO, "[PlayerStorage] Not connected to MongoDB");
        }
    }

    private void enableRedis(boolean redisEnabled) {
        File file = new File(getPlugin().getDataFolder() + "/Database.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (redisEnabled) {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMinIdle(2);
            poolConfig.setMaxTotal(8);
            this.pool = new JedisPool(poolConfig,
                    yml.getString("Redis_Connection"),
                    yml.getInt("Redis_Port"), 5000,
                    yml.getString("Redis_Password"));
            try {
                Jedis jedis = pool.getResource();
                Bukkit.getLogger().log(Level.INFO, "[PlayerStorage] Connected to Redis");
                // Is connected
            } catch (JedisConnectionException e) {
                Bukkit.getLogger().log(Level.INFO, "[PlayerStorage] Not connected to Redis");
                // Not connected
            }

            //Here's where we setup our mongo connection.
            expireTime = yml.getLong("Redis_Data_Expire_After");
        }
    }

    private void setStatus() {
        File file = new File(getPlugin().getDataFolder() + "/Database.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        redisEnabled = Objects.equals(yml.getString("Storage_Configuration"), "redis");
        mongoEnabled = Objects.equals(yml.getString("Storage_Configuration"), "mongo");
        if (redisEnabled == mongoEnabled) {
            redisEnabled = true;
            mongoEnabled = true;
        }
    }


    public void registerValue(String key, Boolean defaultValue) {
        getRegistry().getTypes().put(key, DataType.BOOLEAN);
        getRegistry().getDefaultBools().put(key, defaultValue);
    }

    public void registerValue(String key, Integer defaultValue) {
        getRegistry().getTypes().put(key, DataType.INTEGER);
        getRegistry().getDefaultInts().put(key, defaultValue);
    }

    public void registerValue(String key, String defaultValue) {
        getRegistry().getTypes().put(key, DataType.BOOLEAN);
        getRegistry().getDefaultStrings().put(key, defaultValue);
    }

    public Boolean getBool(UUID uuid, String key) {
        if (!getRegistry().getDefaultBools().containsKey(key)) return false;
        if (!getRegistry().getPlayers().containsKey(uuid)) return getRegistry().getDefaultBools().get(key);
        if (!getRegistry().getPlayers().get(uuid).getBooleans().containsKey(key))
            return getRegistry().getDefaultBools().get(key);
        return getRegistry().getPlayers().get(uuid).getBooleans().get(key);
    }

    public Integer getInt(UUID uuid, String key) {
        if (!getRegistry().getDefaultInts().containsKey(key)) return 0;
        if (!getRegistry().getPlayers().containsKey(uuid)) return getRegistry().getDefaultInts().get(key);
        if (!getRegistry().getPlayers().get(uuid).getInts().containsKey(key))
            return getRegistry().getDefaultInts().get(key);
        return getRegistry().getPlayers().get(uuid).getInts().get(key);
    }

    public String getString(UUID uuid, String key) {
        if (!getRegistry().getDefaultStrings().containsKey(key)) return null;
        if (!getRegistry().getPlayers().containsKey(uuid)) return getRegistry().getDefaultStrings().get(key);
        if (!getRegistry().getPlayers().get(uuid).getStrings().containsKey(key))
            return getRegistry().getDefaultStrings().get(key);
        return getRegistry().getPlayers().get(uuid).getStrings().get(key);
    }

    public void set(UUID uuid, String key, Boolean value) {
        if (!getRegistry().getDefaultBools().containsKey(key)) return;
        if (!getRegistry().getPlayers().containsKey(uuid)) return;
        PlayerData data = getRegistry().getPlayers().get(uuid);
        this.getPlugin().getServer().getPluginManager().callEvent(new PlayerDataChangeEvent(this.getPlugin().getServer().getPlayer(uuid), data,key));

        Boolean defaultValue = getRegistry().getDefaultBools().get(key);
        if (value.equals(defaultValue)) {
            data.getBooleans().remove(key);
            getRegistry().savePlayer(uuid);
            return;
        }
        data.getBooleans().put(key, value);
        getRegistry().savePlayer(uuid);

    }

    public void set(UUID uuid, String key, Integer value) {
        if (!getRegistry().getDefaultInts().containsKey(key)) return;
        if (!getRegistry().getPlayers().containsKey(uuid)) return;

        PlayerData data = getRegistry().getPlayers().get(uuid);
        this.getPlugin().getServer().getPluginManager().callEvent(new PlayerDataChangeEvent(this.getPlugin().getServer().getPlayer(uuid), data,key));

        Integer defaultValue = getRegistry().getDefaultInts().get(key);
        if (value.equals(defaultValue)) {
            data.getInts().remove(key);
            getRegistry().savePlayer(uuid);
            return;
        }
        data.getInts().put(key, value);
        getRegistry().savePlayer(uuid);

    }

    public void set(UUID uuid, String key, String value) {
        if (!getRegistry().getDefaultStrings().containsKey(key)) return;
        if (!getRegistry().getPlayers().containsKey(uuid)) return;

        PlayerData data = getRegistry().getPlayers().get(uuid);
        this.getPlugin().getServer().getPluginManager().callEvent(new PlayerDataChangeEvent(this.getPlugin().getServer().getPlayer(uuid), data,key));

        String defaultValue = getRegistry().getDefaultStrings().get(key);
        if (value.equals(defaultValue)) {
            data.getStrings().remove(key);
            getRegistry().savePlayer(uuid);
            return;
        }
        data.getStrings().put(key, value);
        getRegistry().savePlayer(uuid);

    }
}
