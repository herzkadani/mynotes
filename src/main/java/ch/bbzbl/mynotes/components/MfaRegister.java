package ch.bbzbl.mynotes.components;

import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.security.mfa.MFATokenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

public class MfaRegister extends HorizontalLayout {

    private final User user;

    private final MFATokenService mfaTokenService;

    private IntegerField code;

    public MfaRegister(User user, String qrCode, String code, MFATokenService mfaTokenService){
        this.user = user;
        this.mfaTokenService = mfaTokenService;
        setWidth("400px");
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(createQrDiv(qrCode, code));

    }

    private VerticalLayout createQrDiv(String qrCode, String code) {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setAlignItems(Alignment.STRETCH);
        verticalLayout.add(new Span("Bitte lesen Sie den Qr Code mit der Google Authenticatior App ein"));
        verticalLayout.add(createQrCode(qrCode));
        verticalLayout.add(new Span(code));
        verticalLayout.add(createTokenInputField());
        verticalLayout.add(createSubmitButton());
        return verticalLayout;
    }

    private Button createSubmitButton() {
        Button button = new Button("Verify Token");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(l -> {
            boolean success = mfaTokenService.verifyTotp(code.getValue().toString(), user.getSecret());
            if(success) {
                getUI().ifPresent(ui -> ui.navigate("login"));
            } else {
                NotificationFactory.errorNotification("Totp not correct").open();
            }
        });
        return button;
    }

    private IntegerField createTokenInputField() {
        code = new IntegerField("TOTP");
        code.setPlaceholder("XXXXXX");
        code.setMax(999999);
        return code;
    }

    public Image createQrCode(String qrCode){
        return new Image(qrCode, "QrCode");
    }
}
