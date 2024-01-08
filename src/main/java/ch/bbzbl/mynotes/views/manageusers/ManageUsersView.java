package ch.bbzbl.mynotes.views.manageusers;

import java.util.Optional;
import java.util.Set;

import ch.bbzbl.mynotes.security.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ch.bbzbl.mynotes.bl.controller.AccountController;
import ch.bbzbl.mynotes.components.NotificationFactory;
import ch.bbzbl.mynotes.components.UserDetailsForm;
import ch.bbzbl.mynotes.data.Role;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

/**
 * view to manage user accounts
 * @author Dani Herzka
 *
 */
@PageTitle("Manage Users")
@Route(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ManageUsersView extends Div {

	private final Grid<User> grid = new Grid<>(User.class, false);

	private final AccountController accountController;

	private Logger LOGGER = LoggerFactory.getLogger(ManageUsersView.class);
	@Autowired
	private AuthenticatedUser authenticatedUser;
	private User user;

	public ManageUsersView(AccountController accountController) {
		this.accountController = accountController;

		// get authenticated user
		Optional<User> user = authenticatedUser.get();
		if (user.isPresent()) {
			this.user = user.get();
			LOGGER.info(user.get().getUsername() + " opend Manage Users View");
		} else {
			authenticatedUser.logout();
			getUI().ifPresent(ui -> ui.navigate("login"));
		}

		addClassNames("account-view");

		// Configure Grid

		grid.addColumn(User::getUsername).setAutoWidth(true).setHeader("Username");
		grid.addColumn(User::getName).setAutoWidth(true).setHeader("Name");
		grid.addColumn(User::getEmail).setAutoWidth(true).setHeader("E-Mail");
		grid.addComponentColumn(currentuser -> createRoleBadges(currentuser.getRoles())).setAutoWidth(true).setHeader("Roles");
		grid.addComponentColumn(currentuser -> {
			Checkbox chkIsOauth = new Checkbox(currentuser.isOAuthUser());
			chkIsOauth.setReadOnly(true);
			return chkIsOauth;
		}).setAutoWidth(true).setHeader("OAuth User");
		grid.addComponentColumn(currentuser -> {
			Button editButton = new Button(new Icon(VaadinIcon.EDIT));
			editButton.addClickListener(event -> editButtonClickEvent(event, currentuser));
			return editButton;
		}).setFrozenToEnd(true).setHeader("Edit");
		grid.addComponentColumn(currentuser -> {
			Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
			deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
			deleteButton.addClickListener(event -> deleteButtonClickEvent(event, currentuser));
			return deleteButton;
		}).setHeader("Delete");
		grid.setItems(accountController.getAllUsers());
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		add(grid);

		//TODO add button to add new user

	}

	/**
	 * Click Event Method on delete button click, open a dialog do confirm or cancel the deletion
	 * @param event
	 * @param user to be deleted
	 */
	private void deleteButtonClickEvent(ClickEvent<Button> event, User user) {
		ConfirmDialog dialogConfirmDelete = new ConfirmDialog();
		dialogConfirmDelete.setText("Are you sure, you want do delete " + user.getUsername() + "?");
		dialogConfirmDelete.setCancelable(true);
		dialogConfirmDelete.addCancelListener(ev -> dialogConfirmDelete.close());

		dialogConfirmDelete.setConfirmText("Delete");
		dialogConfirmDelete.setConfirmButtonTheme("error primary");
		dialogConfirmDelete.addConfirmListener(ev->{
			accountController.delete(user);
			refreshGrid();
		});
		
		dialogConfirmDelete.open();
	}

	/**
	 * Click event on edit button, open a dialog to edit user details
	 * @param event
	 * @param user to be edited
	 */
	private void editButtonClickEvent(ClickEvent<Button> event, User user) {
		Dialog editUserDialog = new Dialog();
		UserDetailsForm userForm = new UserDetailsForm(user, accountController, true, false);

				
		Button cancel = new Button("Cancel");
		Button save = new Button("Save");
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setClassName("button-layout");
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonLayout.add(save, cancel);
		

		cancel.addClickListener(e -> editUserDialog.close());

		save.addClickListener(e -> {
			try {
				userForm.getUserBinder().writeBean(user);
				accountController.updateUser(user);
				editUserDialog.close();
				refreshGrid();
				NotificationFactory.successNotification("Data updated").open();
			} catch (ObjectOptimisticLockingFailureException exception) {
				LOGGER.error("Concurrency error", exception);
				NotificationFactory.errorNotification(
						"Error updating the data. Somebody else has updated the record while you were making changes.").open();
			} catch (ValidationException validationException) {
				LOGGER.error("Invalid Input", validationException);
				NotificationFactory.errorNotification("Failed to update the data. Check again that all values are valid").open();
			}
		});
		
		editUserDialog.add(userForm, buttonLayout);
		editUserDialog.open();
	}


	/**
	 * refresh the user grid
	 */
	private void refreshGrid() {
		grid.select(null);
		grid.setItems(accountController.getAllUsers());
	}

	private Component createRoleBadges(Set<Role> roles) {
		HorizontalLayout badgeWrapper = new HorizontalLayout();
		for (Role role : roles) {
			Span badge = new Span(role.toString());
			badge.getElement().getThemeList().add("badge contrast");
			badgeWrapper.add(badge);

		}
		return badgeWrapper;

	}
}