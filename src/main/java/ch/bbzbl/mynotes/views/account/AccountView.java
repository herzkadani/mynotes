package ch.bbzbl.mynotes.views.account;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ch.bbzbl.mynotes.bl.controller.AccountController;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.bbzbl.mynotes.security.PasswordEncoder;
import ch.bbzbl.mynotes.views.MainLayout;
import ch.bbzbl.mynotes.views.components.NotificationFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;

/**
 * Account View
 * @author Dani Herzka
 *
 */
@PageTitle("Account")
@Route(value = "account/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class AccountView extends HorizontalLayout {

	// Components
	FormLayout layForm;
	private TextField txtUsername;
	private TextField txtName;
	private EmailField txtEmail;
	private PasswordField txtPassword;
	private PasswordField txtRepeatPassword;
	private Button btnSave;

	// Members
	@Autowired
	private AuthenticatedUser authenticatedUser;
	@Autowired
	private AccountController accountController;
	private User user;
	private Binder<User> userBinder;

	public AccountView() {
		layForm = new FormLayout();
		txtUsername = new TextField("Username");
		txtName = new TextField("Full Name");
		txtEmail = new EmailField("E-Mail");
		txtPassword = new PasswordField("New Password");
		txtRepeatPassword = new PasswordField("Repeat New Password");
		btnSave = new Button("Save");

		userBinder = new Binder<>(User.class);
	}

	/**
	 * initialize the user interface
	 */
	@PostConstruct
	private void initUi() {

		// style UI
		setJustifyContentMode(JustifyContentMode.CENTER);
		getStyle().set("text-align", "center");
		getStyle().set("padding-top", "100px");
		setSizeFull();

		// get authenticated user
		if (authenticatedUser.get().isPresent()) {
			user = authenticatedUser.get().get();
		} else {
			authenticatedUser.logout();
			UI.getCurrent().navigate("login");
		}

		// configure fields
		txtUsername.setRequired(true);
		txtName.setRequired(true);
		txtEmail.setRequired(true);

		// bind fields
		userBinder.forField(txtUsername).withValidator(v -> {
			// username hasn't been changed, no validation needed
			if (user.getUsername().equals(v))
				return true;
			// if username already taken, return false
			return !accountController.usernameAlreadyTaken(v);
		}, "Username already taken").bind(User::getUsername, User::setUsername);

		
		userBinder.forField(txtName).bind(User::getName, User::setName);
		
		userBinder.forField(txtEmail).withValidator(new EmailValidator("Enter a valid E-Mail")).bind(User::getEmail,
				User::setEmail);
		
		userBinder.bind(txtPassword, bindUser -> null,
				(bindUser, value) -> bindUser.setHashedPassword(PasswordEncoder.encodePassoword(value)));
		
		userBinder.forField(txtRepeatPassword)
				.withValidator(value -> value.equals(txtPassword.getValue()), "Passwords don't match") //validate if first and second passwords match
				.bind(bindUser -> null,
						(bindUser, value) -> bindUser.setHashedPassword(PasswordEncoder.encodePassoword(value)));

		// load existing data from user object
		userBinder.readBean(user);

		// save button
		btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		btnSave.addClickListener(event -> saveButtonClickEvent(event));

		// form layout
		layForm.setWidth("600px");
		layForm.add(txtUsername, txtName, txtEmail, txtPassword, txtRepeatPassword, btnSave);
		add(layForm);
	}
	
	private void saveButtonClickEvent(ClickEvent<Button> event) {
		try {
			String oldUsername = user.getUsername();
			
			//write data in database
			userBinder.writeBean(user);
			accountController.updateUser(user);

			//get data from database to retrieve the new version
			user = accountController.getUserById(user.getId());
			userBinder.readBean(user);

			NotificationFactory.successNotification("data saved").open();

			// logout if username has been changed
			String newUsername = user.getUsername();
			if (!oldUsername.equals(newUsername))
				authenticatedUser.logout();

		} catch (ValidationException e) {
			NotificationFactory.errorNotification("Some fields are incorrect").open();
		}
	}

}
