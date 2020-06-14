package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name="Users")
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	
	@Column(nullable=false, length=100)
	private String firstName;
	
	@Column(nullable=false, length=100)
	private String lastName;

	@OneToMany(mappedBy="owner", cascade = {CascadeType.REMOVE})
	private List <Project> ownedProjects;
	
	@ManyToMany(mappedBy="members")
	private List <Project> visibleProjects;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationTimestamp == null) ? 0 : creationTimestamp.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((lastUpdateTimestamp == null) ? 0 : lastUpdateTimestamp.hashCode());
		result = prime * result + ((ownedProjects == null) ? 0 : ownedProjects.hashCode());
		result = prime * result + ((visibleProjects == null) ? 0 : visibleProjects.hashCode());
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
		User other = (User) obj;
		if (creationTimestamp == null) {
			if (other.creationTimestamp != null)
				return false;
		} else if (!creationTimestamp.equals(other.creationTimestamp))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (lastUpdateTimestamp == null) {
			if (other.lastUpdateTimestamp != null)
				return false;
		} else if (!lastUpdateTimestamp.equals(other.lastUpdateTimestamp))
			return false;
		if (ownedProjects == null) {
			if (other.ownedProjects != null)
				return false;
		} else if (!ownedProjects.equals(other.ownedProjects))
			return false;
		if (visibleProjects == null) {
			if (other.visibleProjects != null)
				return false;
		} else if (!visibleProjects.equals(other.visibleProjects))
			return false;
		return true;
	}

	@Column(updatable=false, nullable=false)
	private LocalDateTime creationTimestamp;
	
	@Column(nullable=false)
	private LocalDateTime lastUpdateTimestamp;
	
	public User() {
		this.ownedProjects=new ArrayList<>();
		this.visibleProjects=new ArrayList<>();
	}
	
	public User(String firstName, String lastName) {
		this();
		this.firstName=firstName;
		this.lastName=lastName;
	}
	
	@PrePersist
	protected void onPersist() { //ogni volta che una entity viene creata jpa esegue il metodo
		this.creationTimestamp=LocalDateTime.now();
		this.lastUpdateTimestamp=LocalDateTime.now(); //data e ora di creazione sono anche di ultima modifica
	}
	
	@PreUpdate
	protected void onUpdate() { //ogni volta che una entity viene aggiornata jpa esegue il metodo
		this.lastUpdateTimestamp=LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<Project> getOwnedProjects() {
		return ownedProjects;
	}

	public void setOwnedProjects(List<Project> ownedProjects) {
		this.ownedProjects = ownedProjects;
	}

	public List<Project> getVisibleProjects() {
		return visibleProjects;
	}

	public void setVisibleProjects(List<Project> visibleProjects) {
		this.visibleProjects = visibleProjects;
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

	public void setLastUpdateTimestamp(LocalDateTime lastUpdatestamp) {
		this.lastUpdateTimestamp = lastUpdatestamp;
	}

	@Override
	public String toString() {
		
		return "User{"+
				"id=" + id + 
				", firstName=" + firstName + 
				", lastName=" + lastName + 
				", ownedProjects=" + ownedProjects +
				", visibleProjects=" + visibleProjects + 
				", creationTimestamp=" + creationTimestamp + 
				", lastUpdateTimestamp=" + lastUpdateTimestamp + 
				'}';
	}
	
	
	
	
	
}
