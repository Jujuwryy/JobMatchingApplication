package com.george.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;

import com.george.Service.JobMatchingService;
import com.george.Service.PostService;
import com.george.model.JobMatch;
import com.george.model.Post;

/**
 * Controller for managing blog posts.
 * Provides endpoints for CRUD operations on posts.
 */
@RestController
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private JobMatchingService jobMatchingService;

    /**
     * Redirects the root URL to the Swagger UI.
     *
     * @param response the HTTP servlet response
     * @throws IOException if an input or output exception occurs
     */
    @Hidden
    @RequestMapping("/") 
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }
    
    /**
     * Retrieves all posts.
     *
     * @return ResponseEntity containing a list of posts
     */
    @Operation(summary = "Get all posts", description = "Returns a list of all posts")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved posts")
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Searches for posts that contain the given text.
     *
     * @param text the search text
     * @return ResponseEntity containing a list of matching posts
     */
    @Operation(summary = "Search posts by text", description = "Returns posts matching the search text")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching posts")
    @GetMapping("/posts/search/{text}")
    public ResponseEntity<List<Post>> getPostByText(@PathVariable String text) {
        try {
            List<Post> posts = postService.findByText(text);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param id the ID of the post
     * @return ResponseEntity containing the requested post or a NOT_FOUND status
     */
    @Operation(summary = "Get post by ID", description = "Returns a post by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the post")
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        try {
            Optional<Post> post = postService.findById(id);
            return post.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Creates a new post.
     *
     * @param post the post object to be created
     * @return ResponseEntity containing the created post
     */
    @Operation(summary = "Create a post", description = "Creates a new post")
    @ApiResponse(responseCode = "201", description = "Successfully created the post")
    @PostMapping("/post")
    public ResponseEntity<Post> addPost(@RequestBody Post post) {
        try {
            Post createdPost = postService.addPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Creates multiple posts.
     *
     * @param posts the list of posts to be created
     * @return ResponseEntity containing the created posts
     */
    @Operation(summary = "Create multiple posts", description = "Creates multiple new posts")
    @ApiResponse(responseCode = "201", description = "Successfully created the posts")
    @PostMapping("/posts")
    public ResponseEntity<List<Post>> addPosts(@RequestBody List<Post> posts) {
        try {
            List<Post> createdPosts = postService.addPosts(posts);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPosts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing post.
     *
     * @param post the updated post object
     * @param id   the ID of the post to be updated
     * @return ResponseEntity containing the updated post
     */
    @Operation(summary = "Update a post", description = "Updates an existing post")
    @ApiResponse(responseCode = "200", description = "Successfully updated the post")
    @PutMapping("/updatepost/{id}")
    public ResponseEntity<Post> updatePost(@RequestBody Post post, @PathVariable String id) {
        try {
            return postService.updatePost(post, id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a post by its ID.
     *
     * @param id the ID of the post to be deleted
     * @return ResponseEntity with NO_CONTENT status if deleted, NOT_FOUND if not found
     */
    @Operation(summary = "Delete a post", description = "Deletes a post by its ID")
    @ApiResponse(responseCode = "204", description = "Successfully deleted the post")
    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> deletePostById(@PathVariable String id) {
        try {
            boolean isDeleted = postService.deleteById(id);
            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Find matching jobs", description = "Returns jobs matching the user profile")
    @ApiResponse(responseCode = "200", description = "Successfully found matching jobs")
    @PostMapping("/jobs/match")
    public ResponseEntity<List<JobMatch>> findMatchingJobs(@RequestBody String userProfile) {
        try {
            List<JobMatch> matches = jobMatchingService.findMatchingJobs(userProfile);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
