package com.george.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.george.model.Post;
import com.george.model.SearchRepository;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * SearchRepositoryController implements the SearchRepository interface 
 * to provide search functionality for job posts stored in a MongoDB collection.
 * <p>
 * This class utilizes MongoDB's full-text search capabilities to retrieve job posts 
 * based on search queries matching specific fields.
 * </p>
 */
@Component
@Repository
public class SearchRepositoryController implements SearchRepository {

    @Autowired
    private MongoClient client;  // MongoDB client for database connection
    
    @Autowired
    private MongoConverter converter;  // MongoConverter for converting MongoDB documents to Java objects

    /**
     * Performs a text-based search in the MongoDB collection "JobPost".
     * <p>
     * This method uses MongoDB's aggregation pipeline with the `$search` stage 
     * to find job posts that match the given text in specific fields.
     * </p>
     *
     * @param text The search query text
     * @return A list of matching {@link Post} objects
     */
    @Override
    public List<Post> findByText(String text) {  
        final List<Post> posts = new ArrayList<>();

        // Access the "george" database and "JobPost" collection
        MongoDatabase database = client.getDatabase("george");
        MongoCollection<Document> collection = database.getCollection("JobPost");

        // Perform a full-text search on specified fields: requiredTechs, jobDescription, and jobTitle
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
            new Document("$search", 
                new Document("text", 
                    new Document("query", text)
                        .append("path", Arrays.asList("requiredTechs", "jobDescription", "jobTitle"))
                )
            ),
            new Document("$sort", new Document("exp", 1L)) // Sort results by experience level in ascending order
        ));

        // Convert MongoDB documents to Post objects
        result.forEach(doc -> posts.add(converter.read(Post.class, doc)));

        return posts;
    }
}
