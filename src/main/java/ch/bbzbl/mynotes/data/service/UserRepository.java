package ch.bbzbl.mynotes.data.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ch.bbzbl.mynotes.data.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	Optional<User> getByUsername(String username);
    List<User> findByUsername(String username);
    Integer countByUsername(String username);
	User findByEmail(String email);
	
}
