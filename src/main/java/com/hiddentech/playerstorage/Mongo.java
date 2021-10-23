package com.hiddentech.playerstorage;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ConnectionPoolSettings;
import org.bson.Document;

import java.io.IOException;

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
