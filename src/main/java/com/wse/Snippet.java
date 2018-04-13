package com.wse;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.json.JsonObject;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by chaoqunhuang on 10/28/17.
 */
public class Snippet {
    private static MongoClient mongoClient = new MongoClient( "localhost" , 27017 );


    public static String generateSnippet(int docId, String word) {
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("web");
        JsonObject jsonObject = new JsonObject(collection.find(eq("docId", docId)).first().toJson());
        String content = jsonObject.getString("content");
        int wordIndex = content.indexOf(" " + word + " ");
        String res = "";
        if (wordIndex == -1) {
            return res;
        } else {
            String prefix = content.substring(wordIndex - 60 < 0 ? 0 : wordIndex - 60, wordIndex);
            String suffix = content.substring(wordIndex + word.length() + 1, wordIndex + 60 > content.length() ? content.length() : wordIndex + 60);
            res = prefix + "<b><font size='6px'> " + word + " </font></b>" + suffix;
        }
        return res;
    }

    public static String generateOrSnippets(int docId, String[] words) {
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("web");
        JsonObject jsonObject = new JsonObject(collection.find(eq("docId", docId)).first().toJson());
        String content = jsonObject.getString("content");
        String res = "";
        for (String word : words) {
            int wordIndex = content.indexOf(" " + word + " ");
            if (wordIndex == -1) continue;
            String prefix = content.substring(wordIndex - 60 < 0 ? 0 : wordIndex - 60, wordIndex);
            String suffix = content.substring(wordIndex + word.length() + 1, wordIndex + 60 > content.length() ? content.length() : wordIndex + 60);
            res = prefix + "<b><font size='6px'> " + word + " </font></b>" + suffix;
        }
        return res;
    }
}
