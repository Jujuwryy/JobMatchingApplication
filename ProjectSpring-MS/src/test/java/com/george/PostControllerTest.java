package com.george;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.controller.PostController;
import com.george.model.Post;
import com.george.model.PostRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PostRepository postRepo;

    @InjectMocks
    private PostController postController;

    // Test data
    private static final Post POST_1 = new Post("1", "Software Engineer", "Develops applications", 3, 
            List.of("Java", "Spring", "MongoDB"));
    private static final Post POST_2 = new Post("2", "Data Scientist", "Analyzes data and builds models", 5, 
            List.of("Python", "Machine Learning", "Pandas"));
    private static final Post POST_3 = new Post("3", "DevOps Engineer", "Manages infrastructure", 4, 
            List.of("Docker", "Kubernetes", "AWS"));

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(postController)
                .build();
    }

    @Test
    void getAllPosts_ShouldReturnListOfPosts() throws Exception {
        // Given
        List<Post> posts = List.of(POST_1, POST_2, POST_3);
        when(postRepo.findAll()).thenReturn(posts);

        // When/Then
        mockMvc.perform(get("/posts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].jobTitle", is("DevOps Engineer")));
    }

    @Test
    void getPostById_ShouldReturnPost() throws Exception {
        // Given
        when(postRepo.findById(POST_1.getId())).thenReturn(Optional.of(POST_1));

        // When/Then
        mockMvc.perform(get("/posts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.jobTitle", is("Software Engineer")));
    }

    @Test
    void deletePostById_ShouldReturnOk() throws Exception {
        // Given
        when(postRepo.findById(POST_2.getId())).thenReturn(Optional.of(POST_2));

        // When/Then
        mockMvc.perform(delete("/post/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createPost_ShouldReturnCreatedPost() throws Exception {
        // Given
        Post newPost = new Post("4", "Product Manager", "Oversees product development", 6, 
                List.of("Leadership", "Agile", "Strategy"));
        when(postRepo.save(any(Post.class))).thenReturn(newPost);

        // When/Then
        mockMvc.perform(post("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPost)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobTitle", is("Product Manager")))
                .andExpect(jsonPath("$.jobDescription", is("Oversees product development")))
                .andExpect(jsonPath("$.experience", is(6)))
                .andExpect(jsonPath("$.id", is("4")));
    }

    @Test
    void updatePost_ShouldReturnUpdatedPost() throws Exception {
        // Given
        Post updatedPost = new Post("1", "Senior Software Engineer", 
                "Develops applications and mentors junior developers", 5, 
                List.of("Java", "Spring", "MongoDB", "Leadership"));
        
        when(postRepo.findById(updatedPost.getId())).thenReturn(Optional.of(POST_1));
        when(postRepo.save(any(Post.class))).thenReturn(updatedPost);

        // When/Then
        mockMvc.perform(put("/updatepost/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPost)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobTitle", is("Senior Software Engineer")))
                .andExpect(jsonPath("$.jobDescription", 
                        is("Develops applications and mentors junior developers")))
                .andExpect(jsonPath("$.experience", is(5)))
                .andExpect(jsonPath("$.id", is("1")));
    }
}
