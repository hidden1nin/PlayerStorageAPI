package com.hiddentech.playerstorage;

public class LoginConfig {
    private final PlayerStorage plugin;

    public LoginConfig(PlayerStorage plugin){
        this.plugin = plugin;
        setConfig();
    }

    private void setConfig() {
        //make directory
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        //add config for connection
        if(!plugin.getConfig().isSet("Redis_Connection")){
            plugin.getConfig().set("Redis_Connection", "change this!");
            //redis-14639.c266.us-east-1-3.ec2.cloud.redislabs.com:14639
            plugin.getConfig().set("Redis_Port", 12345);
            plugin.getConfig().set("Redis_Password", "and this too!");
            plugin.getConfig().set("Redis_Data_Expire_After", 86400);
            //rbZX3oKRmEroQ7XOWRtUb25cCMRtM2Fr
        }
        if(!plugin.getConfig().isSet("Storage_Configuration")){
            plugin.getConfig().set("Storage_Configuration","both");
            plugin.getConfig().set("Mongo_DB_Name","PlayerStorageAPI");
            plugin.getConfig().set("Mongo_DB_ConnectionString","Change Me Too!");
        }
        plugin.saveConfig();
    }
}
