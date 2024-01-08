package ch.bbzbl.mynotes.data.service;


import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.Note;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class NoteService {

	private NoteRepository repository;
	@Autowired
	private FolderService folderService;
	@Autowired
	private AuthenticatedUser authenticatedUser;
	private Logger LOGGER = LoggerFactory.getLogger(NoteService.class);


	public NoteService(NoteRepository repository, FolderService folderService) {
		this.repository = repository;
	}

	@Transactional
	public Optional<Note> get(Long id) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ " performed get(id) in NoteService");
		return repository.findById(id);
	}

	@Transactional
	public Note update(Note entity) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ " performed update in NoteService");
		return repository.save(entity);
	}

	@Transactional
	public void delete(Note entity) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ " performed delete in NoteService");
		repository.delete(entity);
		Folder parent = entity.getFolder();
		parent.getNotes().remove(entity);
		folderService.updateFolderAfterNoteDelete(parent);
	}

	@Transactional
	public Page<Note> list(Pageable pageable) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ " performed list in NoteService");
		return repository.findAll(pageable);
	}

	@Transactional
	public Page<Note> list(Pageable pageable, Specification<Note> filter) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ " performed list with filter in NoteService");
		return repository.findAll(filter, pageable);
	}

	public int count() {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ " performed count in NoteService");
		return (int) repository.count();
	}

	public String getUsernameFromAuthenticatedUser(){
		Optional<User> user = authenticatedUser.get();
		if (user.isPresent()) {
			return user.get().getUsername();
		}
		return "No AuthenticatedUser";
	}
}
