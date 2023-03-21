package ch.bbzbl.mynotes.bl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;

@Controller
public class AccountController {

	@Autowired
	private UserService userService;
	
//	public User getUserFromDatabase(AuthenticatedUser authenticatedUser) {
//		
//	}
	
	public boolean usernameAlreadyTaken(String username) {
		if (userService.countByUsername(username)>0) return true;
		return false;
	}

	public void updateUser(User user) {
		userService.update(user);
		
	}
	
	public User getUserById(long id) {
		return userService.get(id).get();
	}
	
	public List<User> getAllUsers(){
		return userService.list();
	}
	
}
