package ch.bbzbl.mynotes.bl.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;
import jakarta.transaction.Transactional;

@Controller
public class ManageUsersController {
	
	@Autowired
	private UserService service;
	
	public List<User> getAllUsers(){
		return service.list();
	}

	public void update(User user) {
		service.update(user);
		
	}

	public Optional<User> getUserById(Long id) {
		return service.get(id);
	}

	public void updateUser(User user) {
		service.update(user);		
	}

	@Transactional
	public void delete(User user) {
		service.delete(user.getId());
	}


}
