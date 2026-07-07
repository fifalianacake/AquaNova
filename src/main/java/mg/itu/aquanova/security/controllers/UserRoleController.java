package mg.itu.aquanova.security.controllers;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.itu.aquanova.security.models.UserRoleModels;
import mg.itu.aquanova.security.services.UserRoleService;
import mg.itu.aquanova.security.services.UserService;
import mg.itu.aquanova.security.services.RoleService;

@Controller
@RequestMapping("/user-roles")
public class UserRoleController {

    private final UserRoleService userRoleService;
    private final UserService userService;
    private final RoleService roleService;

    public UserRoleController(UserRoleService userRoleService, UserService userService, RoleService roleService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String getAllUserRoles(Model model) {
        List<UserRoleModels> userRoles = userRoleService.getAllUserRoles();
        model.addAttribute("userRoles", userRoles);
        return "security/user-roles/list";
    }

    @GetMapping("/new")
    public String showAssignRoleForm(Model model) {
        model.addAttribute("userRole", new UserRoleModels());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.getAllRoles());
        return "security/user-roles/form";
    }

    @PostMapping("/save")
    public String saveUserRole(@ModelAttribute("userRole") UserRoleModels userRole, Model model) {
        try {
            userRoleService.saveUserRole(userRole);
            return "redirect:/user-roles";
        } catch (IllegalArgumentException e) {
            model.addAttribute("userRole", userRole);
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("error", e.getMessage());
            return "security/user-roles/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUserRole(@PathVariable("id") Long id) {
        userRoleService.deleteUserRole(id);
        return "redirect:/user-roles";
    }
}