package mg.itu.aquanova.security.controllers;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import mg.itu.aquanova.security.models.RoleModels;
import mg.itu.aquanova.security.models.User;
import mg.itu.aquanova.security.models.UserRoleModels;
import mg.itu.aquanova.security.repositories.RoleRepository;
import mg.itu.aquanova.security.services.UserRoleService;
import mg.itu.aquanova.security.services.UserService;

@Controller
public class LoginController {

    /** Rôle attribué par défaut aux comptes créés via l'inscription publique. */
    private static final String ROLE_INSCRIPTION = "TECHNICIEN";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleRepository roleRepository;

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

    @GetMapping("/register")
    public String registerPage() {
        return "security/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmation,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            if (name == null || name.isBlank() || lastname == null || lastname.isBlank()) {
                throw new IllegalArgumentException("Le nom et le prénom sont obligatoires.");
            }
            if (email == null || email.isBlank() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("L'adresse e-mail n'est pas valide.");
            }
            if (password == null || password.length() < 6) {
                throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
            }
            if (!password.equals(confirmation)) {
                throw new IllegalArgumentException("Les deux mots de passe ne correspondent pas.");
            }

            User user = new User(name.trim(), lastname.trim(), email.trim(), password,
                    new Date(System.currentTimeMillis()));
            User sauvegarde = userService.saveUser(user);

            RoleModels role = roleRepository.findByName(ROLE_INSCRIPTION)
                    .orElseThrow(() -> new IllegalStateException(
                            "Rôle " + ROLE_INSCRIPTION + " introuvable : contactez un administrateur."));
            userRoleService.saveUserRole(new UserRoleModels(sauvegarde, role));

            redirectAttributes.addFlashAttribute("success",
                    "Compte créé avec succès. Vous pouvez maintenant vous connecter.");
            return "redirect:/login";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("lastname", lastname);
            model.addAttribute("email", email);
            return "security/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}
