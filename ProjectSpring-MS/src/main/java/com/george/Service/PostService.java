package com.george.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.george.model.Post;
import com.george.model.PostRepository;
import com.george.model.SearchRepository;

/**
 * Service class that handles business logic for Post entities.
 * Provides CRUD operations and search functionality for Posts using PostRepository
 * and SearchRepository.
 * 
 * This service implements exception handling for all operations and provides
 * meaningful error messages when operations fail.
 */
@Service
public class PostService {
    
    /** Repository for basic CRUD operations on Post entities */
    @Autowired
    private PostRepository repo;
    
    /** Repository for search operations on Post entities */
    @Autowired
    private SearchRepository srepo;

    /**
     * Retrieves all posts from the database.
     * 
     * @return List of all Post entities
     * @throws RuntimeException if there's an error fetching posts
     */
    public List<Post> getAllPosts() {
        try {
            return repo.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching posts", e);
        }
    }

    /**
     * Searches for posts containing specific text.
     * 
     * @param text the search text to find in posts
     * @return List of Post entities matching the search criteria
     * @throws RuntimeException if there's an error during the search operation
     */
    public List<Post> findByText(String text) {
        try {
            return srepo.findByText(text);
        } catch (Exception e) {
            throw new RuntimeException("Error searching posts by text", e);
        }
    }

    /**
     * Adds a new post to the database.
     * 
     * @param post the Post entity to be saved
     * @return the saved Post entity with generated ID
     * @throws RuntimeException if there's an error saving the post
     */
    public Post addPost(Post post) {
        try {
            return repo.save(post);
        } catch (Exception e) {
            throw new RuntimeException("Error saving post", e);
        }
    }

    /**
     * Updates an existing post in the database.
     * 
     * @param post the updated Post entity
     * @param id the ID of the post to update
     * @return ResponseEntity containing the updated Post if found, or NOT_FOUND status
     * @throws RuntimeException if there's an error updating the post
     */
    public ResponseEntity<Post> updatePost(Post post, String id) {
        try {
            Optional<Post> existingPost = repo.findById(id);
            if (existingPost.isPresent()) {
                post.setId(id); // Ensure the post ID remains unchanged during update
                Post updatedPost = repo.save(post);
                return ResponseEntity.ok(updatedPost);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating post", e);
        }
    }

    /**
     * Finds a post by its ID.
     * 
     * @param id the ID of the post to find
     * @return Optional containing the Post if found, empty Optional otherwise
     * @throws RuntimeException if there's an error finding the post
     */
    public Optional<Post> findById(String id) {
        try {
            return repo.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding post by ID", e);
        }
    }

    /**
     * Deletes a post by its ID.
     * 
     * @param id the ID of the post to delete
     * @return true if post was found and deleted, false if post was not found
     * @throws RuntimeException if there's an error deleting the post
     */
    public boolean deleteById(String id) {
        try {
            if (repo.existsById(id)) {
                repo.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting post", e);
        }
    }

    /**
     * Adds multiple posts to the database in a batch operation.
     * 
     * @param posts List of Post entities to be saved
     * @return List of saved Post entities with generated IDs
     * @throws RuntimeException if there's an error saving the posts
     */
    public List<Post> addPosts(List<Post> posts) {
        try {
            return repo.saveAll(posts);  
        } catch (Exception e) {
            throw new RuntimeException("Error saving posts", e);
        }
    }
}