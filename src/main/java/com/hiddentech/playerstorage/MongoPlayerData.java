package com.hiddentech.playerstorage;

import com.hiddentech.playerstorage.types.PlayerData;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MongoPlayerData extends Document {

    private final Mongo mongo;
    private final String uuid;
    private final Document document = this;
    private final PlayerStorageAPI plugin;
    private final PlayerData data;

    public MongoPlayerData(UUID uuid, PlayerData data, Mongo mongo, PlayerStorageAPI plugin) {
        this(uuid.toString(),data,mongo,plugin);
    }
    public MongoPlayerData(String uuid,PlayerData data, Mongo mongo,PlayerStorageAPI plugin) {
        this.mongo = mongo;
        this.uuid = uuid;
        this.plugin = plugin;
        put("uuid", uuid);
        put("bools", data.getBooleans());
        put("ints",data.getInts());
        put("strings",data.getStrings());
        this.data=data;
    }

    public void save() {
        new BukkitRunnable() {
            @Override
            public void run() {
                //delete if no data is unique
                if(data.getBooleans().isEmpty()&&data.getInts().isEmpty()&&data.getStrings().isEmpty()){
                    mongo.getCollection().deleteOne(Filters.eq("uuid", uuid));
                    return;
                }
                mongo.getCollection().replaceOne(Filters.eq("uuid", uuid), document, new ReplaceOptions().upsert(true));
            }
        }.runTaskAsynchronously(plugin.getPlugin());
    }
}
