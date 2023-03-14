package ch.bbzbl.mynotes.data.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
@Entity
public class Folder extends AbstractEntity{

    private String Titel;

    private boolean isPublic;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<Note> notes = null;

    @ManyToOne
    @JoinColumn(name = "user", foreignKey = @jakarta.persistence.ForeignKey(name = "user_fk"))
    private User user;

    public Folder() {
    }

    public String getTitel() {
        return Titel;
    }

    public void setTitel(String titel) {
        Titel = titel;
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

    public void addNote(Note n){
        if(notes == null){
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
