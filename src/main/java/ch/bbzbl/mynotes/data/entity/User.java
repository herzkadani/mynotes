package ch.bbzbl.mynotes.data.entity;

import ch.bbzbl.mynotes.data.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Folder> folders = null;
    private boolean isOAuthUser;

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
    
    


}
