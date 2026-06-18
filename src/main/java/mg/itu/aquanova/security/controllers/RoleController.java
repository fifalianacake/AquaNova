package mg.itu.aquanova.security.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import java.util.List;
import mg.itu.aquanova.security.models.RoleModels;

import mg.itu.aquanova.security.services.RoleService;

@Controller
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String getAllRoles(Model model) {
        List<RoleModels> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        return "security/roles/list";
    }

    @GetMapping("/new")
    public String showCreateRoleForm(Model model) {
        model.addAttribute("role", new RoleModels());
        return "security/roles/form";
    }

    @PostMapping("/save")
    public String saveRole(@ModelAttribute("role") RoleModels role) {
        roleService.saveRole(role);
        return "redirect:/roles";
    }

    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable("id") Long id) {
        roleService.deleteRole(id);
        return "redirect:/roles";
    }

}
