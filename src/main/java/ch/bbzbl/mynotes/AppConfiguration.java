package ch.bbzbl.mynotes;

import dev.samstevens.totp.spring.autoconfigure.TotpAutoConfiguration;
import dev.samstevens.totp.spring.autoconfigure.TotpProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration extends TotpAutoConfiguration {

    public AppConfiguration(TotpProperties props) {
        super(props);
    }
}
