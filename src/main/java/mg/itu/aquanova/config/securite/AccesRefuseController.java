package mg.itu.aquanova.config.securite;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class AccesRefuseController {

    @GetMapping("/acces-refuse")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accesRefuse() {
        return "securite/acces-refuse";
    }
}
