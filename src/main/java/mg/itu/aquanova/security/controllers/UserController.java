package mg.itu.aquanova.security.controllers;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import mg.itu.aquanova.security.models.User;
import mg.itu.aquanova.security.services.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "security/users/list";
    }

    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "security/users/form"; 
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.saveUser(user);
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("error", e.getMessage());
            return "security/users/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        try {
            userService.deleteUser(id);
            return "redirect:/users";
        } catch (RuntimeException e) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("error", "Impossible de supprimer cet utilisateur : " + e.getMessage());
            return "security/users/list";
        }
    }
}