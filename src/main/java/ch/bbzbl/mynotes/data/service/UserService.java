package ch.bbzbl.mynotes.data.service;

import ch.bbzbl.mynotes.bl.controller.AccountController;
import ch.bbzbl.mynotes.components.NotificationFactory;
import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.bbzbl.mynotes.security.exceptions.UnkownIdentifierException;
import ch.bbzbl.mynotes.security.exceptions.UserAlreadyExistException;
import ch.bbzbl.mynotes.security.mfa.MFATokenService;
import ch.bbzbl.mynotes.security.mfa.data.MFATokenData;
import ch.bbzbl.mynotes.views.account.AccountView;
import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	private final UserRepository repository;
	@Autowired
	private AuthenticatedUser authenticatedUser;
	private Logger LOGGER = LoggerFactory.getLogger(UserService.class);


	@Resource
	private MFATokenService mfaTokenManager;

	public UserService(UserRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public Optional<User> get(Long id) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed get(id) in UserService");
		return repository.findById(id);
	}

	@Transactional
	public User update(User entity) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed update in UserService");
		return repository.save(entity);
	}

	@Transactional
	public void delete(Long id) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed delete in UserService");
		repository.deleteById(id);
	}

	@Transactional
	public Page<User> list(Pageable pageable) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed list in UserService");
		return repository.findAll(pageable);
	}

	@Transactional
	public Page<User> list(Pageable pageable, Specification<User> filter) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed list with Filter in UserService");
		return repository.findAll(filter, pageable);
	}

	@Transactional
	public List<User> list() {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed findAll in UserService");
		return repository.findAll();
	}

	public int count() {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed count in UserService");
		return (int) repository.count();
	}

	@Transactional
	public int countByUsername(String username) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed countByUsername in UserService");
		return repository.countByUsername(username);

	}

	@Transactional
	public List<Folder> getFoldersByUserId(Long id) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed getFoldersByUserId in UserService");
		Optional<User> optionalUser = repository.findById(id);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			return user.getFolders();
		}
		return null;
	}

	@Transactional
	public User findByEmail(String email) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed findByEmail in UserService");
		return repository.findByEmail(email);
	}

	@Transactional
	public void save(User user) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed save in UserService");
		repository.save(user);
	}

	public List<User> findByUsername(String username) {
		LOGGER.info(getUsernameFromAuthenticatedUser()+ "performed findByUsername in UserService");
		return repository.findByUsername(username);
	}

	@Transactional
	public Optional<User> getByUsername(String username) {
		return repository.getByUsername(username);
	}

	public User register(User user) throws UserAlreadyExistException {
		if (findByEmail(user.getEmail()) != null) {
			throw new UserAlreadyExistException("User already exists for this email");
		}
		//some additional work
		user.setSecret(mfaTokenManager.generateSecretKey()); //generating the secret and store with profile
		NotificationFactory.successNotification("Registration was successful").open();
		return user;
	}

	public MFATokenData mfaSetup(String email) throws UnkownIdentifierException, QrGenerationException {
		User user = repository.findByEmail(email);
		if (user == null) {
			// we will ignore in case account is not verified or account does not exists
			throw new UnkownIdentifierException("Unable to find account or account is not active");
		}
		return new MFATokenData(mfaTokenManager.getQRCode(user.getSecret()), user.getSecret());
	}

	public String getUsernameFromAuthenticatedUser(){
		Optional<User> user = authenticatedUser.get();
		if (user.isPresent()) {
			return user.get().getUsername();
		}
		return "No AuthenticatedUser";
	}

}
