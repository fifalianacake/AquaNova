package mg.itu.aquanova.config.securite;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Branche le contrôle d'accès sur toutes les requêtes. */
@Configuration
public class SecuriteWebConfig implements WebMvcConfigurer {

    private final AutorisationInterceptor autorisationInterceptor;

    public SecuriteWebConfig(AutorisationInterceptor autorisationInterceptor) {
        this.autorisationInterceptor = autorisationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registre) {
        registre.addInterceptor(autorisationInterceptor).addPathPatterns("/**");
    }
}
