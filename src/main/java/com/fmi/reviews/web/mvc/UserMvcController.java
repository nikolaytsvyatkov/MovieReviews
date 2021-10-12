package com.fmi.reviews.web.mvc;

import com.fmi.reviews.model.User;
import com.fmi.reviews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.regex.Pattern;

@Controller
public class UserMvcController {
    private static final String UPLOADS_DIR = "uploads";

    UserService userService;

    @Autowired
    public UserMvcController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    String getUsers(Model model){
        model.addAttribute("users", userService.getUsers());
        return "users";
    }

    @GetMapping("/register")
    String register(@ModelAttribute("user") User user,
                    Model model){
        return "user-form";
    }

    @GetMapping("/user-form")
    String addUser(Model model,
                   @RequestParam(value="mode", required=false) String mode,
                   @RequestParam(value="userId", required=false) Long userId){
        String title = "Register user";
        if("edit".equals(mode)){
            title = "Edit user";
            User edit = userService.getUser(userId);
            model.addAttribute("mode", "update");
            model.addAttribute("user", edit);
        }else{
            model.addAttribute("mode", "create");
            if (!model.containsAttribute("user")) {
                model.addAttribute("user", new User());
            }
        }

        model.addAttribute("title", title);
        return "user-form";
    }

    @PostMapping(value = "/users", params = "delete")
    String deleteUser(@RequestParam("delete")Long userId){
        userService.deleteUser(userId);
        return "redirect:users";
    }

    @PostMapping(value = "/users", params = "edit")
    public String editUser(@RequestParam("edit") Long editId,
                           Model model, UriComponentsBuilder uriBuilder) {
        URI uri = uriBuilder.path("/user-form")
                .query("mode=edit&userId={id}").buildAndExpand(editId).toUri();
        return "redirect:" + uri.toString();
    }

    @PostMapping("/user-form")
    String postUser(@Valid @ModelAttribute("user") User user,
                    BindingResult bindingResult,
                    @RequestParam(value = "file") MultipartFile file,
                    Model model,
                    @ModelAttribute("mode")String mode,
                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("mode", model.getAttribute("mode"));
            redirectAttributes.addFlashAttribute("user",user);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            return "redirect:user-form";
        }

        if (file != null && !file.isEmpty() && file.getOriginalFilename().length() > 4) {
            if (Pattern.matches(".+\\.(png|jpg|jpeg)", file.getOriginalFilename())) {
                handleFile(file, user);
            } else {
                redirectAttributes.addFlashAttribute("fileError", "Submit PNG or JPG picture please!");
                return "redirect:user-form";
            }
        }

        if(bindingResult.hasErrors()
                && !(user.getId() != null && "".equals(user.getPassword()) && bindingResult.getFieldErrorCount()==1 && bindingResult.getFieldError("password") != null)) {
            return "redirect:user-form";
        }

        if (user.getId() == null) {
            userService.addUser(user);
        } else {
            userService.updateUser(user.getId(), user);
        }

        return "redirect:/users";
    }

    @GetMapping(value = "/403")
    public ModelAndView accesssDenied(Principal user) {

        ModelAndView model = new ModelAndView();

        model.addObject("message", "Access denied");
        model.addObject("continueUrl", "/recipes");

        model.setViewName("errors");
        return model;
    }

    private void handleFile(MultipartFile file, User user) {
        String oldName = user.getPhoto();
        if (oldName != null && oldName.length() > 0) { //delete old image file
            Path oldPath = Paths.get(getUploadsDir(), oldName).toAbsolutePath();
            if (Files.exists(oldPath)) {
                try {
                    Files.delete(oldPath);
                } catch (IOException ex) {
                }
            }
        }
        if (file != null && file.getOriginalFilename().length() > 4) {
            String newName = file.getOriginalFilename();
            Path newPath = Paths.get(getUploadsDir(), newName).toAbsolutePath();
            int n = 0;
            String finalName = newName;
            while (Files.exists(newPath)) {   // change destination file name if it already exists
                finalName = newName.substring(0, newName.length() - 4) + "_" + ++n + newName.substring(newName.length() - 4);
                newPath = Paths.get(getUploadsDir(), finalName).toAbsolutePath();
            }
            try {
                FileCopyUtils.copy(file.getInputStream(), Files.newOutputStream(newPath));
                user.setPhoto(finalName);
            } catch (IOException ex) {
            }
        }
    }

    protected String getUploadsDir() {
        File uploadsDir = new File(UPLOADS_DIR);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }
        return uploadsDir.getAbsolutePath();
    }


    @ModelAttribute("loggedUser")
    public User getLoggedUser(Authentication authentication) {
        if (authentication != null) {
            User loggedId = (User) authentication.getPrincipal();
            return loggedId;
        } else {
            return null;
        }
    }

    @ModelAttribute("loggedUserName")
    public String getLoggedUserName(Authentication authentication) {
        if (authentication != null) {
            User loggedId = (User) authentication.getPrincipal();
            return loggedId.getUsername();
        } else {
            return null;
        }
    }
}
