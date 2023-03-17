package ch.bbzbl.mynotes.data.service;

import ch.bbzbl.mynotes.data.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public class NoteService {

    private NoteRepository repository;
    public NoteService(NoteRepository repository) {
        this.repository = repository;
    }

    public Optional<Note> get(Long id) {
        return repository.findById(id);
    }

    public Note update(Note entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Note> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Note> list(Pageable pageable, Specification<Note> filter) {
        return repository.findAll(filter, pageable);
    }
    public int count() {
        return (int) repository.count();
    }
}