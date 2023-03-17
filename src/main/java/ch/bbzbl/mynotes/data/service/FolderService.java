package ch.bbzbl.mynotes.data.service;

import ch.bbzbl.mynotes.data.entity.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

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

    public Page<Folder> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Folder> list(Pageable pageable, Specification<Folder> filter) {
        return repository.findAll(filter, pageable);
    }
    public int count() {
        return (int) repository.count();
    }

}
