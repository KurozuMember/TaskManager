package com.example.demo.model;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.persistence.*;

@Entity
public class Project {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(nullable=false, length=100)
	private String name;

	@Column(nullable=false)
	private String description;
	
	@Column(nullable=false)
	private LocalDateTime projectStartDate;

	@ManyToOne(fetch=FetchType.EAGER)
	private User owner;

	@ManyToMany											//fetch type is LAZY by default
	private List<User> members;
  
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	@JoinColumn(name="project_id")
  
	private List<Task> tasks;

	@OneToMany
	private List<Tag> tags;
	

	public void addMember(User u) {
		this.members.add(u);
	}

	public void addTask(Task t) {
		this.tasks.add(t);
	}

	public void addTag(Tag tag) {
		this.tags.add(tag);
	}

	@PrePersist
	protected void onPersist() {
		this.projectStartDate = LocalDateTime.now();
	}
	public Project() {
		this.members = new ArrayList<>();
		this.tasks = new ArrayList<>();
		this.tags = new ArrayList<>();
	}

	public Project(String name) {
		this();
		this.name = name;
	}

	public Project(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getProjectStartDate() {
		return projectStartDate;
	}

	public void setProjectStartDate(LocalDateTime projectStartDate) {
		this.projectStartDate = projectStartDate;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<User> getMembers() {
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((projectStartDate == null) ? 0 : projectStartDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (projectStartDate == null) {
			if (other.projectStartDate != null)
				return false;
		} else if (!projectStartDate.equals(other.projectStartDate))
			return false;
		return true;
	}

}
