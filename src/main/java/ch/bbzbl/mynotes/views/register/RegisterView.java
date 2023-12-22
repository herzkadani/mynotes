package ch.bbzbl.mynotes.views.register;

import ch.bbzbl.mynotes.bl.controller.AccountController;
import ch.bbzbl.mynotes.components.MfaRegister;
import ch.bbzbl.mynotes.components.NotificationFactory;
import ch.bbzbl.mynotes.components.UserDetailsForm;
import ch.bbzbl.mynotes.data.Role;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.security.mfa.MFATokenService;
import ch.bbzbl.mynotes.security.mfa.data.MFATokenData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
    private final AccountController accountController;
    private final UserService userService;
    private final MFATokenService mfaTokenService;

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
                try{
                    User user = userService.register(userDetailsForm.getUserBinder().getBean());
                    user.addRole(Role.USER);
                    userService.save(user);
                    MFATokenData mfaTokenData = userService.mfaSetup(userDetailsForm.getUserBinder().getBean().getEmail());
                    userDetailsForm.setVisible(false);
                    MfaRegister mfaRegister = new MfaRegister(user, mfaTokenData.getQrCode(), mfaTokenData.getMfaCode(), mfaTokenService);
                    add(mfaRegister);
                    NotificationFactory.successNotification("Thanks for your registration").open();
                } catch (Exception ex) {
                    NotificationFactory.errorNotification("Register with this Email is not possible, because the E-Mail is already in use by another account.").open();
                }
            }
        });
        userDetailsForm.add(registerButton);
    }

}
