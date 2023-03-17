package ch.bbzbl.mynotes.data.service;

import ch.bbzbl.mynotes.data.entity.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {

	private FolderRepository repository;

	public FolderService(FolderRepository repository) {
		this.repository = repository;
	}

	public Optional<Folder> get(Long id) {
		return repository.findById(id);
	}

	public Folder update(Folder entity) {
		return repository.save(entity);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public List<Folder> list() {
		return repository.findAll();
	}

	public Page<Folder> list(Pageable pageable, Specification<Folder> filter) {
		return repository.findAll(filter, pageable);
	}

	public int count() {
		return (int) repository.count();
	}
}
