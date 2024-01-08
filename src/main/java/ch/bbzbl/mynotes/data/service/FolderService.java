package ch.bbzbl.mynotes.data.service;

import ch.bbzbl.mynotes.data.entity.Folder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FolderService {

	private FolderRepository repository;
	@Autowired
	private AuthenticatedUser authenticatedUser;
	private Logger LOGGER = LoggerFactory.getLogger(FolderService.class);


	public FolderService(FolderRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public Optional<Folder> get(Long id) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed get(id) in FolderService");
		return repository.findById(id);
	}

	@Transactional
	public Folder update(Folder entity) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed update in FolderService");
		return repository.save(entity);
	}

	public void delete(Folder entity) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed delete in FolderService");
		repository.delete(entity);
	}

	@Transactional
	public List<Folder> list() {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed list in FolderService");
		return repository.findAll();
	}

	@Transactional
	public Page<Folder> list(Pageable pageable, Specification<Folder> filter) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed list with filter in FolderService");
		return repository.findAll(filter, pageable);
	}

	public int count() {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed count in FolderService");
		return (int) repository.count();
	}

	@Transactional
	public List<Folder> getPublicFolders() {
		LOGGER.info(getUsernameFromAuthenticatedUser() + "performed getPublicFolders in FolderService");
		List<Folder> folders = new ArrayList<>();

		for (Folder folder : repository.findAll()) {
			if (folder.isPublic()) {
				folders.add(folder);
			}
		}
		return folders;
	}

	public String getUsernameFromAuthenticatedUser(){
		Optional<User> user = authenticatedUser.get();
		if (user.isPresent()) {
			return user.get().getUsername();
		}
		return "No AuthenticatedUser";
	}

	public void updateFolderAfterNoteDelete(Folder folder){
		repository.save(folder);
	}
}
