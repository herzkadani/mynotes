package ch.bbzbl.mynotes.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Folder extends AbstractEntity {

	private String titel;
	private boolean isPublic;

	@OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Note> notes = null;

	@ManyToOne
	@JoinColumn(name = "user", foreignKey = @jakarta.persistence.ForeignKey(name = "user_fk"))
	private User user;

	public Folder() {
	}

	public Folder(String titel, boolean isPublic, List<Note> notes, User user) {
		this.titel = titel;
		this.isPublic = isPublic;
		this.notes = notes;
		this.user = user;
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean aPublic) {
		isPublic = aPublic;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public void addNote(Note n) {
		if (notes == null) {
			notes = new ArrayList<Note>();
		}
		notes.add(n);
		n.setFolder(this);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


}
