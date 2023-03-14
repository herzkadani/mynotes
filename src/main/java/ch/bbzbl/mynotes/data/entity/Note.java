package ch.bbzbl.mynotes.data.entity;

import jakarta.persistence.*;

@Entity
public class Note extends AbstractEntity {

    private String titel;
    @Lob
    @Column(columnDefinition = "CLOB")
    private String content;
    @ManyToOne
    @JoinColumn(name = "folder", foreignKey = @jakarta.persistence.ForeignKey(name = "folder_fk"))
    private Folder folder;


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
