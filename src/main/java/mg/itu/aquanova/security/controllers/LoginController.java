package mg.itu.aquanova.security.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import mg.itu.aquanova.security.models.User;
import mg.itu.aquanova.security.models.UserRoleModels;
import mg.itu.aquanova.security.services.UserRoleService;
import mg.itu.aquanova.security.services.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "security/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService.getUserByEmail(email);

        if (user == null) {
            model.addAttribute("error", "Utilisateur introuvable");
            return "security/login";
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Mot de passe incorrect");
            return "security/login";
        }

        UserRoleModels userRole = userRoleService.getUserRoleByUserId(user.getId());

        if (userRole == null) {
            model.addAttribute("error", "Aucun rôle n'est assigné à cet utilisateur. Contactez un administrateur.");
            return "security/login";
        }

        session.setAttribute("user", user);
        session.setAttribute("role", userRole.getRole().getName());

        return "redirect:/releves-eau";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}