package ch.bbzbl.mynotes.views.login;

import ch.bbzbl.mynotes.components.NotificationFactory;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver, HasUrlParameter<String> {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        LoginI18n loginI18n = LoginI18n.createDefault();
        loginI18n.setHeader(new LoginI18n.Header());
        loginI18n.getHeader().setTitle("MyNotes");
        setI18n(loginI18n);
        setForgotPasswordButtonVisible(false);

        IntegerField code = new IntegerField("TOTP");
        code.getElement().setAttribute("name", "code");
        getCustomFormArea().add(code);

        //Sign in with Google
        HorizontalLayout laySignInWithGoogle = new HorizontalLayout();
        laySignInWithGoogle.setJustifyContentMode(JustifyContentMode.CENTER);
        Paragraph p = new Paragraph("Sign in with Google");
        Image googleIcon = new Image("images/google.svg", "Google Icon");
        googleIcon.setWidth("30px");
        laySignInWithGoogle.add(googleIcon, p);
        Anchor loginLink = new Anchor("/oauth2/authorization/google");
        loginLink.add(laySignInWithGoogle);
        // Set router-ignore attribute so that Vaadin router doesn't handle the login request
        loginLink.getElement().setAttribute("router-ignore", true);

        Button registerButton = new Button("Register");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.addClickListener(e -> {
            registerButton.getUI().ifPresent(ui -> ui.navigate("register"));
            setOpened(false);
        });
        registerButton.setWidthFull();
        registerButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        getFooter().add(registerButton, loginLink);

        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null) {
            if (parameter.equals("OAuthErrorEmailAlreadyExists"))
                NotificationFactory.errorNotification("Signing in with Google is not possible, because the E-Mail is already in use by another account.").open();
        }

    }
}
