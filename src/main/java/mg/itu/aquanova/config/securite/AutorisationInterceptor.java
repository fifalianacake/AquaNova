package mg.itu.aquanova.config.securite;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AutorisationInterceptor implements HandlerInterceptor {

    private final ReglesAcces reglesAcces;

    public AutorisationInterceptor(ReglesAcces reglesAcces) {
        this.reglesAcces = reglesAcces;
    }

    @Override
    public boolean preHandle(HttpServletRequest requete, HttpServletResponse reponse, Object handler)
            throws Exception {

        String uri = requete.getRequestURI();
        if (reglesAcces.estPublique(uri)) {
            return true;
        }

        HttpSession session = requete.getSession(false);
        Object utilisateur = session != null ? session.getAttribute("user") : null;
        if (utilisateur == null) {
            reponse.sendRedirect(requete.getContextPath() + "/login");
            return false;
        }

        String role = (String) session.getAttribute("role");
        if (!reglesAcces.estAutorise(role, uri)) {
            reponse.sendRedirect(requete.getContextPath() + "/acces-refuse");
            return false;
        }

        return true;
    }
}
