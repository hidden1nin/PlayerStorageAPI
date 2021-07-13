package com.hiddentech.playerstorage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Mongo {
    private final String connectionString;
    private final String databaseString;
    private final String collectionString;

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    public Mongo(String connection,String database,String collection) {
        this.connectionString = connection;
        this.databaseString = database;
        this.collectionString = collection;
    }
    public void connect() {
        client = MongoClients.create(connectionString);
        database = client.getDatabase(databaseString);
        collection = database.getCollection(collectionString);
    }
}
