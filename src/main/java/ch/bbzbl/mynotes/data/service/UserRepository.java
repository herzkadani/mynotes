package ch.bbzbl.mynotes.data.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ch.bbzbl.mynotes.data.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    List<User> findByUsername(String username);
    Integer countByUsername(String username);
}
