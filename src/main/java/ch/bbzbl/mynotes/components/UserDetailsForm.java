package ch.bbzbl.mynotes.components;

import java.util.Set;

import com.vaadin.flow.data.validator.StringLengthValidator;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;

import ch.bbzbl.mynotes.bl.controller.AccountController;
import ch.bbzbl.mynotes.data.Role;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.security.PasswordEncoder;

/**
 * a {@link FormLayout} specialized on showing and editing user details
 * @author Dani Herzka
 *
 */
public class UserDetailsForm extends FormLayout {

	private TextField txtUsername;
	private TextField txtName;
	private EmailField txtEmail;
	private PasswordField txtPassword;
	private PasswordField txtRepeatPassword;
	private MultiSelectComboBox<Role> cmbRole;
	private Binder<User> userBinder;

	private AccountController accountController;
	
	

	/**
	 * Constructor
	 * @param user User to be loaded in the binder
	 * @param isAdminView true if role select should be visible
	 * @param accountController the account controller to execute operations
	 */
	public UserDetailsForm(User user, AccountController accountController, boolean isAdminView, boolean isRegisterView) {
		this.accountController = accountController;
		
		userBinder = new Binder<>(User.class);
		userBinder.setBean(user);
		txtUsername = new TextField("Username");
		txtName = new TextField("Full Name");
		txtEmail = new EmailField("E-Mail");
		txtPassword = new PasswordField("New Password");
		txtRepeatPassword = new PasswordField("Repeat New Password");
		cmbRole = new MultiSelectComboBox<>("Roles", Set.of(Role.values()));

		// configure fields
		txtUsername.setRequired(true);
		txtName.setRequired(true);
		txtEmail.setRequired(true);
		cmbRole.setRequired(true);

		if (isRegisterView) {
			txtPassword.setRequired(true);
			txtRepeatPassword.setRequired(true);
		}

		if (user.isOAuthUser()) {
			txtUsername.setReadOnly(true);
			txtEmail.setReadOnly(true);
			txtPassword.setVisible(false);
			txtRepeatPassword.setVisible(false);
		}
		
		if(!isAdminView) {
			cmbRole.setVisible(false);
		}

		// bind fields
		userBinder.forField(txtUsername).withValidator(v -> {
			// username hasn't been changed, no validation needed
			if (user.getUsername() != null && user.getUsername().equals(v))
				return true;
			// if username already taken, return false
			return !accountController.usernameAlreadyTaken(v);
		}, "Username already taken").bind(User::getUsername, (bindUser, string) -> {
			// OAuth users can't change username
			if (!bindUser.isOAuthUser())
				bindUser.setUsername(string);
		});

		userBinder.forField(txtName).bind(User::getName, User::setName);

		userBinder.forField(txtEmail).withValidator(new EmailValidator("Enter a valid E-Mail")).bind(User::getEmail,
				(bindUser, string) -> {
					// OAuth users can't change email
					if (!bindUser.isOAuthUser())
						bindUser.setEmail(string);
				});
		userBinder.forField(txtPassword)
				.withValidator(
						new StringLengthValidator("Password must be at least 8 characters long", 8, null))
				.withValidator(value -> value.matches(".*\\d.*"), "Password must contain at least one digit")
				.withValidator(value -> value.matches(".*[a-z].*"), "Password must contain at least one lowercase letter")
				.withValidator(value -> value.matches(".*[A-Z].*"), "Password must contain at least one uppercase letter")
				.withValidator(value -> value.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?].*"),
						"Password must contain at least one special character");


		userBinder.bind(txtPassword, bindUser -> null, (bindUser, value) -> {
			// OAuth Users can't set a password
			
			if (!bindUser.isOAuthUser() && !txtPassword.getValue().isBlank())
				bindUser.setHashedPassword(PasswordEncoder.encodePassoword(value));
		});

		userBinder.forField(txtRepeatPassword)
				// validate if first and second passwords match
				.withValidator(value -> value.equals(txtPassword.getValue()), "Passwords don't match")
				.bind(bindUser -> null, (bindUser, value) -> {
					// OAuth Users can't set a password
					if (!bindUser.isOAuthUser() && !txtPassword.getValue().isBlank())
						bindUser.setHashedPassword(PasswordEncoder.encodePassoword(value));

				});
		
		if(isAdminView) {
			userBinder.bind(cmbRole, User::getRoles
			, User::setRoles);
		}
		
		userBinder.readBean(user);
		
		add(txtUsername, txtName, txtEmail, txtPassword, txtRepeatPassword, cmbRole);
	}

	public TextField getTxtUsername() {
		return txtUsername;
	}

	public void setTxtUsername(TextField txtUsername) {
		this.txtUsername = txtUsername;
	}

	public TextField getTxtName() {
		return txtName;
	}

	public void setTxtName(TextField txtName) {
		this.txtName = txtName;
	}

	public EmailField getTxtEmail() {
		return txtEmail;
	}

	public void setTxtEmail(EmailField txtEmail) {
		this.txtEmail = txtEmail;
	}

	public PasswordField getTxtPassword() {
		return txtPassword;
	}

	public void setTxtPassword(PasswordField txtPassword) {
		this.txtPassword = txtPassword;
	}

	public PasswordField getTxtRepeatPassword() {
		return txtRepeatPassword;
	}

	public void setTxtRepeatPassword(PasswordField txtRepeatPassword) {
		this.txtRepeatPassword = txtRepeatPassword;
	}

	public Binder<User> getUserBinder() {
		return userBinder;
	}

	public void setUserBinder(Binder<User> userBinder) {
		this.userBinder = userBinder;
	}

	public AccountController getAccountController() {
		return accountController;
	}

	public void setAccountController(AccountController accountController) {
		this.accountController = accountController;
	}

}
