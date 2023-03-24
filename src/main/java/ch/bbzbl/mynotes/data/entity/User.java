package ch.bbzbl.mynotes.data.entity;

import ch.bbzbl.mynotes.data.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "application_user")
public class User extends AbstractEntity {

	private String username;
	private String name;
	@JsonIgnore
	private String hashedPassword;
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<Role> roles;
	@Email
	private String email;
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Folder> folders = null;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}

	public void addFolder(Folder f) {
		if (folders == null) {
			folders = new ArrayList<Folder>();
		}
		folders.add(f);
		f.setUser(this);
	}

	public boolean isOAuthUser() {
		return isOAuthUser;
	}

	public void setOAuthUser(boolean isOAuthUser) {
		this.isOAuthUser = isOAuthUser;
	}

	public void addRole(Role user) {
		// add role
        if (roles == null) {
            roles = Set.of(user);
        } else {
        	roles.add(user);
        }
	}
}
