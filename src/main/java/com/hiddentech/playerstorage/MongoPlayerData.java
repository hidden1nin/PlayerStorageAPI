package com.hiddentech.playerstorage;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerData extends Document {

    private final Mongo mongo;
    private final String uuid;
    private final Document document = this;
    public PlayerData(UUID uuid, Mongo mongo) {
        this.mongo = mongo;
        this.uuid = uuid.toString();
        put("uuid", uuid.toString());
        put("data", new ArrayList<String>());
        put("ouch", "yesysh");

        Bukkit.broadcastMessage("saving");
        save();
    }
    public PlayerData(String uuid, Mongo mongo) {
        this.mongo = mongo;
        this.uuid = uuid;
        put("uuid", uuid.toString());
        put("data", new ArrayList<String>());
        put("ouch", "yesysh");

        Bukkit.broadcastMessage("saving");
        save();
    }

    public void save() {
        new BukkitRunnable() {
            @Override
            public void run() {
                mongo.getCollection().replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
                Bukkit.broadcastMessage(" yes"+ document);
            }
        };
    }
}
