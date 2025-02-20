# Job Matching Application

## Overview
This application is a **Spring Boot-based job matching platform** that integrates **MongoDB vector search**, **Spring Security with JWT authentication**, **AOP-based logging**, and **Swagger API documentation**. It provides features for **creating, searching, and managing job posts** while ensuring secure access through token-based authentication.

## Features
- **User Authentication**: Secure login and registration with **BCrypt hashing** and JWT.
- **CRUD Operations**: Create, read, update, and delete job posts.
- **Full-Text Search**: Find jobs using MongoDB's text search.
- **Vector Search for Job Matching**: AI-powered job recommendations.
- **Spring AOP Logging**: Tracks API calls for debugging.
- **Swagger UI**: Interactive API documentation.

---

## Vector Search for Job Matching
Vector Search & NLP-based Matching

This API leverages Hugging Face NLP models to transform job descriptions, required technologies, and job titles into high-dimensional vector embeddings. These embeddings are stored in MongoDB using vector search indexing, allowing for semantic similarity matching rather than simple keyword-based search.

- **How it Works**

- **Data Embedding**: Each job post is processed through a Hugging Face NLP model, generating vector embeddings.

- **Storage in MongoDB**: These embeddings are stored alongside job posts in MongoDB.

- **Semantic Search**: When a user searches for jobs, their input is also converted into an embedding, and vector similarity search finds the most relevant job postings using knn.

- **Example**

If a user searches for "Software Engineer with Java experience", the system retrieves jobs that are semantically similar even if they don’t explicitly contain the words "Software Engineer" but mention related concepts.



## Tech Stack
- **Java 17**
- **Spring Boot**
- **Spring Security (JWT Authentication)**
- **Spring Data MongoDB**
- **PostgresSQL** (for hashed user info)
- **MongoDB Atlas** (cloud-hosted database)
- **Swagger (OpenAPI 3.0)**
- **Aspect-Oriented Programming (AOP) for Logging**

---

## Installation
### Prerequisites
- Java 17+
- Maven
- MongoDB (Local or Atlas)
- Kafka (if enabling real-time messaging)

### Setup
1. **Clone the repository**
```sh
   git clone https://github.com/Jujuwryy/job-matching-app.git
   cd job-matching-app
```

2. **Configure MongoDB in `application.properties`**
```properties
spring.data.mongodb.uri=mongodb+srv://your_user:your_password@your_cluster.mongodb.net/...
```

3. **Run the application**
```sh
   mvn spring-boot:run
```

4. **Access Swagger UI** at your local host.

---

## API Endpoints

### Authentication
| Method | Endpoint    | Description        |
|--------|------------|--------------------|
| `POST` | `/register` | Register new user |
| `POST` | `/login`    | Authenticate user, returns JWT |
| `GET`  | `/users`    | Get all users (Admin only) |

### Job Posts
| Method | Endpoint             | Description |
|--------|----------------------|-------------|
| `GET`  | `/posts`             | Get all posts |
| `GET`  | `/posts/{id}`        | Get post by ID |
| `GET`  | `/posts/search/{text}` | Search posts by text |
| `POST` | `/post`              | Create a new post |
| `POST` | `/posts`             | Create multiple posts |
| `PUT`  | `/updatepost/{id}`   | Update a post |
| `DELETE` | `/post/{id}`       | Delete post by ID |

### Job Matching
| Method | Endpoint         | Description |
|--------|----------------|-------------|
| `POST` | `/jobs/match`  | Find jobs matching user profile |

---

## Security & Authentication
- Uses **Spring Security with JWT**.
- Passwords are **hashed with BCrypt** before storage.
- JWT tokens are generated on successful login and required for accessing secure endpoints.

---

## Logging with AOP
Aspect-Oriented Programming (AOP) is used to **log method calls** and **track API requests**.

Example logging output:
```sh
INFO [com.george.controller.PostController] - Entering method: getAllPosts
INFO [com.george.controller.UserController] - User george logged in successfully.
```

---

## Future Enhancements
- **Implement Kafka for real-time job alerts**.
- **Add Role-Based Access Control (RBAC)** for different user roles.
- **Optimize vector search for better recommendations, currently using knn vectors**.

---

## Contributing
Feel free to open issues and pull requests. Contributions are welcome!

---

## License
This project is licensed under the [MIT License](LICENSE).

