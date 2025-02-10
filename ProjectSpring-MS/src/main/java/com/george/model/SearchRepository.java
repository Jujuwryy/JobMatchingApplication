package com.george.model;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository {
	
	List<Post> findByText(String text);
	

}
