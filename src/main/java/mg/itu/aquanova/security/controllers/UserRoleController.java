package mg.itu.aquanova.security.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.itu.aquanova.security.models.UserRoleModels;
import mg.itu.aquanova.security.services.UserRoleService;

@Controller
@RequestMapping("/user-roles")
public class UserRoleController {
    
    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public String getAllUserRoles() {
        userRoleService.getAllUserRoles();
        return "user-role-list";
    }

    @GetMapping("/new")
    public String showCreateUserRoleForm() {
        return "user-role-form";
    }

    @GetMapping("/save")
    public String saveUserRole(@ModelAttribute("userRole") UserRoleModels userRole) {
        userRoleService.saveUserRole(userRole);
        return "redirect:/user-roles";
    }

    @GetMapping("/delete/{id}")
    public String deleteUserRole(@PathVariable("id") Long id) {
        userRoleService.deleteUserRole(id);
        return "redirect:/user-roles";
    }
}
