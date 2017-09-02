package com.logiston.controllers;

import com.logiston.entity.Comment;
import com.logiston.entity.Theme;
import com.logiston.entity.User;
import com.logiston.services.ThemeService;
import com.logiston.services.UserService;
import lombok.AllArgsConstructor;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@AllArgsConstructor
public class ThemeController {

    private ThemeService themeService;

    private UserService userService;


    @GetMapping(value = "/themes")
    public String list(Model model) {
        Iterable<Theme> themes = themeService.listAllThemes();
        model.addAttribute("themes", themes);
        return "themes";
    }

    @GetMapping("theme/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        model.addAttribute("theme", themeService.getThemeById(id));
        Comment comment = new Comment();
        model.addAttribute("comment", comment);

        return "themeshow";
    }

    @GetMapping("theme/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("theme", themeService.getThemeById(id));
        return "themeEdit";
    }

    @GetMapping("theme/delete/{id}")
    public String delete(@PathVariable Long id) {
        System.out.println(id);
        themeService.delete(id);
        return "redirect:/themes";
    }

    @GetMapping("theme/new")
    public String newProduct(Model model) {
        Theme theme = new Theme();
        model.addAttribute("theme", theme);

        return "themeform";
    }


    @PostMapping(value = "theme")
    public String save(HttpServletRequest request, Theme theme) {
        String message = request.getParameter("message");
        if (theme.getThemeId() == null) {
            Comment comment = new Comment();
            comment.setMessage(message);
            comment.setDateTime(LocalDateTime.now());

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByEmail(auth.getName());
            comment.setUser(user);
            theme.getComments().add(comment);

            themeService.saveTheme(theme);
        } else {
            Theme themeById = themeService.getThemeById(theme.getThemeId());
            themeById.setTitle(theme.getTitle());

            themeService.saveTheme(themeById);
        }

        return "redirect:/themes";
    }

    @PostMapping(value = "theme/{id}")
    public String saveProduct(@PathVariable Long id, Comment comment) {
        Theme themeById = themeService.getThemeById(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        comment.setUser(user);
        comment.setDateTime(LocalDateTime.now());
        themeById.getComments().add(comment);
        themeService.saveTheme(themeById);

        return "redirect:/theme/" + id;
    }

}