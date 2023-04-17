package ch.bbzbl.mynotes.data.service;


import ch.bbzbl.mynotes.data.entity.Note;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class NoteService {

	private NoteRepository repository;

	public NoteService(NoteRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public Optional<Note> get(Long id) {
		return repository.findById(id);
	}

	@Transactional
	public Note update(Note entity) {
		return repository.save(entity);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Transactional
	public Page<Note> list(Pageable pageable) {
		return repository.findAll(pageable);
	}

	@Transactional
	public Page<Note> list(Pageable pageable, Specification<Note> filter) {
		return repository.findAll(filter, pageable);
	}

	public int count() {
		return (int) repository.count();
	}
}
