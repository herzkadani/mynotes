package ch.bbzbl.mynotes.views.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;

import ch.bbzbl.mynotes.bl.controller.AccountController;
import ch.bbzbl.mynotes.components.NotificationFactory;
import ch.bbzbl.mynotes.components.UserDetailsForm;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.bbzbl.mynotes.views.MainLayout;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;

/**
 * Account View
 * 
 *
 * @author Dani Herzka
 */
@PageTitle("Account")
@Route(value = "account", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class AccountView extends HorizontalLayout {

	// Components
	UserDetailsForm layForm;
	
	private Button btnSave;

	// Members
	@Autowired
	private AuthenticatedUser authenticatedUser;
	@Autowired
	private AccountController accountController;
	private User user;

	public AccountView() {
		
		btnSave = new Button("Save");
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

		layForm = new UserDetailsForm(user, false, accountController);
		
		// load existing data from user object
		layForm.getUserBinder().readBean(user);

		// save button
		btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		btnSave.addClickListener(event -> saveButtonClickEvent(event));

		// form layout
		layForm.setWidth("600px");
		layForm.add(btnSave);
		add(layForm);
	}

	private void saveButtonClickEvent(ClickEvent<Button> event) {
		try {
			String oldUsername = user.getUsername();


			// write data in database
			layForm.getUserBinder().writeBean(user);

			accountController.updateUser(user);

			// get data from database to retrieve the new version
			user = accountController.getUserById(user.getId());
			layForm.getUserBinder().readBean(user);

			NotificationFactory.successNotification("data saved").open();

			// logout if username has been changed
			String newUsername = user.getUsername();
			if (!oldUsername.equals(newUsername))
				authenticatedUser.logout();

		} catch (ValidationException e) {
			NotificationFactory.errorNotification("Some fields are incorrect").open();
		} catch (ObjectOptimisticLockingFailureException e) {
			Dialog versionConflictDialog = new Dialog();
			versionConflictDialog.add(new Html(
					"<p>There has been a version conflict. Someone has just edited the same data and saved it before you. Click <a onClick=\"window.location.reload()\" style=\"color:#0044CC;\">here</a> to reload the page.</p>"));
			versionConflictDialog.setModal(true);
			versionConflictDialog.setCloseOnEsc(false);
			versionConflictDialog.setCloseOnOutsideClick(false);
			versionConflictDialog.open();
		}
	}

}
