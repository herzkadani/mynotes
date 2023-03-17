package ch.bbzbl.mynotes.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Note extends AbstractEntity {

	private String titel;

	@Lob
	private String content;

	@ManyToOne
	@JoinColumn(name = "folder", foreignKey = @jakarta.persistence.ForeignKey(name = "folder_fk"))
	private Folder folder;

	public Note(String titel, String content, Folder folder) {
		this.titel = titel;
		this.content = content;
		this.folder = folder;
	}

	public Note() {
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

}
