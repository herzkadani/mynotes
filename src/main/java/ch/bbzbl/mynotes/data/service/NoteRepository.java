package ch.bbzbl.mynotes.data.service;

import ch.bbzbl.mynotes.data.entity.Note;
import ch.bbzbl.mynotes.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NoteRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {

}
