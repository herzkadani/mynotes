package ch.bbzbl.mynotes.data.service;

import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ch.bbzbl.mynotes.data.entity.User;


@Service
public class UserService {

	private final UserRepository repository;

	public UserService(UserRepository repository) {
		this.repository = repository;
	}

	public Optional<User> get(Long id) {
		return repository.findById(id);
	}

	public User update(User entity) {
		return repository.save(entity);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public Page<User> list(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Page<User> list(Pageable pageable, Specification<User> filter) {
		return repository.findAll(filter, pageable);
	}

	public int count() {
		return (int) repository.count();
	}

	public int countByUsername(String username) {
		return repository.countByUsername(username);

	}

	public List<Folder> getFoldersByUserId(Long id){
		Optional<User> optionalUser = repository.findById(id);
		if (optionalUser.isPresent()){
			User user = optionalUser.get();
			return user.getFolders();
		}
		return null;

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<User> list(){
    	return repository.findAll();
    }

    public int count() {
        return (int) repository.count();
    }
    
    public int countByUsername(String username) {
    	return repository.countByUsername(username);
    }
    
    public User findByEmail(String email) {
    	return repository.findByEmail(email);
    }
    
    public User save(User user) {
    	return repository.save(user);
    }

	public List<User> findByUsername(String username) {
		return repository.findByUsername(username);
	}
	
	public Optional<User> getByUsername(String username) {
		return repository.getByUsername(username);

	}

}
