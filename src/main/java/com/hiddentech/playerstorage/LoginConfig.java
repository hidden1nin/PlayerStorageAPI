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
        if(!plugin.getConfig().isSet("Connection")){
            plugin.getConfig().set("Connection", "change this!");
            //redis-14639.c266.us-east-1-3.ec2.cloud.redislabs.com:14639
            plugin.getConfig().set("Port", 12345);
            plugin.getConfig().set("Password", "and this too!");
            //rbZX3oKRmEroQ7XOWRtUb25cCMRtM2Fr
            plugin.saveConfig();
        }
    }
}
