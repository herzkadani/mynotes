package ch.bbzbl.mynotes.views.register;

import ch.bbzbl.mynotes.bl.controller.AccountController;
import ch.bbzbl.mynotes.components.UserDetailsForm;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "register")
@AnonymousAllowed
@PageTitle("Register")
public class RegisterView extends HorizontalLayout {

    // UI components
    private UserDetailsForm userDetailsForm;
    private Button registerButton;

    // Services
    private AccountController accountController;
    private UserService userService;

    public RegisterView(@Autowired AccountController accountController, @Autowired UserService userService) {
        this.accountController = accountController;
        this.userService = userService;

    }

    @PostConstruct
    public void initUI() {
        // style UI
		setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		getStyle().set("text-align", "center");
		getStyle().set("padding-top", "100px");
		setSizeFull();


        initUserDetailsForm();
        initRegisterButton();


    }
    public void initUserDetailsForm() {
        User user = new User();
        userDetailsForm = new UserDetailsForm(user, accountController, false, true);
        userDetailsForm.setWidth("600px");
        add(userDetailsForm);
    }

    public void initRegisterButton() {
        registerButton = new Button("Register");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.addClickListener(e -> {
            if (userDetailsForm.getUserBinder().isValid()) {
                //TODO add mfa registration
                userService.save(userDetailsForm.getUserBinder().getBean());
            }
        });
        userDetailsForm.add(registerButton);
    }
}
