package com.example.demo.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Entity
public class Task {
	
@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (completed ? 1231 : 1237);
		result = prime * result + ((creationTimestamp == null) ? 0 : creationTimestamp.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastUpdateTimestamp == null) ? 0 : lastUpdateTimestamp.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Task other = (Task) obj;
		if (completed != other.completed)
			return false;
		if (creationTimestamp == null) {
			if (other.creationTimestamp != null)
				return false;
		} else if (!creationTimestamp.equals(other.creationTimestamp))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastUpdateTimestamp == null) {
			if (other.lastUpdateTimestamp != null)
				return false;
		} else if (!lastUpdateTimestamp.equals(other.lastUpdateTimestamp))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

@Id
@GeneratedValue(strategy=GenerationType.AUTO)
private Long id;

@Column(nullable=false, length=100)
private String name;

private String description;

@Column(nullable=false)
private boolean completed;

@Column(updatable=false, nullable=false)
private LocalDateTime creationTimestamp;

@Column(nullable=false)
private LocalDateTime lastUpdateTimestamp;


@PrePersist
protected void onPersist() { //ogni volta che una entity viene creata jpa esegue il metodo
	this.creationTimestamp=LocalDateTime.now();
	this.lastUpdateTimestamp=LocalDateTime.now(); //data e ora di creazione sono anche di ultima modifica
}

@PreUpdate
protected void onUpdate() { //ogni volta che una entity viene aggiornata jpa esegue il metodo
	this.lastUpdateTimestamp=LocalDateTime.now();
}

public Task() {};

public Task(String name, String description, boolean completed) {
	this.name=name;
	this.description=description;
	this.completed=completed;
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

public boolean isCompleted() {
	return completed;
}

public void setCompleted(boolean completed) {
	this.completed = completed;
}

public LocalDateTime getCreationTimestamp() {
	return creationTimestamp;
}

public void setCreationTimestamp(LocalDateTime creationTimestamp) {
	this.creationTimestamp = creationTimestamp;
}

public LocalDateTime getLastUpdateTimestamp() {
	return lastUpdateTimestamp;
}

public void setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
	this.lastUpdateTimestamp = lastUpdateTimestamp;
}

@Override
public String toString() {
	return "Task [id=" + id + ", name=" + name + ", description=" + description + ", completed=" + completed
			+ ", creationTimestamp=" + creationTimestamp + ", lastUpdateTimestamp=" + lastUpdateTimestamp + "]";
}




}
