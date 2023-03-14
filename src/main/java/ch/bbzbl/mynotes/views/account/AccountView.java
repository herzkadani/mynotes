package ch.bbzbl.mynotes.views.account;

import java.util.Optional;

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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.bbzbl.mynotes.views.MainLayout;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;

@PageTitle("Account")
@Route(value = "account/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class AccountView extends HorizontalLayout {
	
	//Components
	FormLayout layForm;
	private TextField txtUsername;
	private TextField txtName;
	private EmailField txtEmail;
	private PasswordField txtPassword;
	private PasswordField txtRepeatPassword;
	private Button btnSave;
	
	//Members
	@Autowired
	private AuthenticatedUser authenticatedUser;
	@Autowired
	private UserService userService;

    public AccountView() {
    	layForm = new FormLayout();
    	txtUsername = new TextField("Username");
    	txtName = new TextField("Full Name");
    	txtEmail = new EmailField("E-Mail");
    	txtPassword = new PasswordField("New Password");
    	txtRepeatPassword = new PasswordField("Repeat New Password");
    	btnSave = new Button("Save");
    }
    
    @PostConstruct
    private void initUi() {
    	
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("text-align", "center");
        getStyle().set("padding-top", "100px");
        setSizeFull();
    	
        txtUsername.setRequired(true);
        txtName.setRequired(true);
        txtEmail.setRequired(true);
        
        User loggedInUser = null;
    	if (authenticatedUser.get().isPresent()) {
    		loggedInUser = authenticatedUser.get().get();
    	}else {
    		UI.getCurrent().navigate("login");
    	}
    	
    	if(loggedInUser!=null) {
    	txtUsername.setValue(loggedInUser.getUsername());
    	txtName.setValue(loggedInUser.getName());
    	txtEmail.setValue(loggedInUser.getEmail());
    	}
        
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSave.addClickListener(event->saveButtonClickEvent(event));
    	
    	layForm.setWidth("600px");
    	layForm.add(txtUsername, txtName, txtEmail, txtPassword, txtRepeatPassword, btnSave);
    	add(layForm);
    }

	private void saveButtonClickEvent(ClickEvent<Button> event) {
		User loggedInUser=null;
		if(authenticatedUser.get().isPresent()) {
			loggedInUser = authenticatedUser.get().get();
		}else {
			UI.getCurrent().navigate("login");
		}
		
		
		Optional<User> optionalUserFromDatabase = userService.get(loggedInUser.getId());
		if(optionalUserFromDatabase.isPresent()) {
			User userFromDatabase = optionalUserFromDatabase.get();
			userFromDatabase.setUsername(txtUsername.getValue());
			userFromDatabase.setName(txtName.getValue());
			userFromDatabase.setEmail(txtEmail.getValue());
			userService.update(userFromDatabase);
		}else {
			authenticatedUser.logout();
			UI.getCurrent().navigate("login");
		}
		
	}
}
