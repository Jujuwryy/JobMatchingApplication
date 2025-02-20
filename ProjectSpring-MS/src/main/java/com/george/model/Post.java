package com.george.model;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;


@Document(collection ="JobPost")
public class Post {
	
	public Post() {
		
	}
	
	@Id
    private String id;
	private String jobTitle;
	private String jobDescription;
	private int experience;
	private List<String> requiredTechs;
	
	
	public Post(String id, String jobTitle, String jobDescription, int experience, List<String> requiredTechs) {
		super();
		this.id = id;
		this.jobTitle = jobTitle;
		this.jobDescription = jobDescription;
		this.experience = experience;
		this.requiredTechs = requiredTechs;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public List<String> getRequiredTechs() {
		return requiredTechs;
	}
	public void setRequiredTechs(List<String> requiredTechs) {
		this.requiredTechs = requiredTechs;
	}


	@Override
	public String toString() {
		return "Post [id=" + id + ", jobTitle=" + jobTitle + ", jobDescription=" + jobDescription + ", experience="
				+ experience + ", requiredTechs=" + requiredTechs + "]";
	}
	
	
	
	
	
	
}
