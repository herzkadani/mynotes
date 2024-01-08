package ch.bbzbl.mynotes.views.register;

import ch.bbzbl.mynotes.bl.controller.AccountController;
import ch.bbzbl.mynotes.components.MfaRegister;
import ch.bbzbl.mynotes.components.NotificationFactory;
import ch.bbzbl.mynotes.components.UserDetailsForm;
import ch.bbzbl.mynotes.data.Role;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.security.exceptions.BadInputException;
import ch.bbzbl.mynotes.security.mfa.MFATokenService;
import ch.bbzbl.mynotes.security.mfa.data.MFATokenData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Route(value = "register")
@AnonymousAllowed
@PageTitle("Register")
public class RegisterView extends HorizontalLayout {

    // UI components
    private UserDetailsForm userDetailsForm;
    private Button registerButton;

    // Services
    private final AccountController accountController;
    private final UserService userService;
    private final MFATokenService mfaTokenService;
    private Logger LOGGER = LoggerFactory.getLogger(RegisterView.class);

    public RegisterView(@Autowired AccountController accountController, @Autowired UserService userService, MFATokenService mfaTokenService) {
        this.accountController = accountController;
        this.userService = userService;
        this.mfaTokenService = mfaTokenService;
    }

    @PostConstruct
    public void initUI() {
        // style UI
		setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
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
                User userInput = userDetailsForm.getUserBinder().getBean();
               ArrayList<String> error = new ArrayList<>();
                if (userInput.getUsername().isEmpty() || userService.countByUsername(userInput.getUsername()) > 0) {
                    LOGGER.error("Input of Field Username is invalid");
                    error.add("Username");
                }
                if (userInput.getEmail().isEmpty() || userInput.getEmail().matches("^([a-zA-Z0-9_\\.\\-+])+@([a-zA-Z0-9-]+\\.)+[a-zA-Z0-9-]{2,}$")){
                    LOGGER.error("Input of Field Email is invalid");
                    error.add("Email");
                }
                if (userInput.getName().isEmpty()) {
                    LOGGER.error("Input of Field Name is invalid");
                    error.add("Name");
                }
                if (userInput.getHashedPassword().isEmpty()) {
                    error.add("Password");
                    LOGGER.error("Input of Field Password is invalid");
                }
                if(error.isEmpty()){
                    try{
                        User user = userService.register(userDetailsForm.getUserBinder().getBean());
                        user.addRole(Role.USER);
                        userService.save(user);
                        MFATokenData mfaTokenData = userService.mfaSetup(userDetailsForm.getUserBinder().getBean().getEmail());
                        userDetailsForm.setVisible(false);
                        MfaRegister mfaRegister = new MfaRegister(user, mfaTokenData.getQrCode(), mfaTokenData.getMfaCode(), mfaTokenService);
                        add(mfaRegister);
                        LOGGER.info("Registration successful");
                        NotificationFactory.successNotification("Thanks for your registration").open();
                    } catch (Exception ex) {
                        LOGGER.error("Error with Registration", ex);
                        NotificationFactory.errorNotification("Register with this Email is not possible, because the E-Mail is already in use by another account.").open();
                    }
                }
            }
        });
        userDetailsForm.add(registerButton);
    }

}
