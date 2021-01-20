package com.rhm.demo.contollers;
import com.rhm.demo.models.User;
import com.rhm.demo.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
public class MainController {
    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage";
    }
    @RequestMapping("/login")
    public String login() {
        return "loginPage";
    }

    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
        if(result.hasErrors()) {
            return "registrationPage.jsp";
        }
        else {
            User RegUser = userService.registerUser(user);
            session.setAttribute("user_id", RegUser.getId());
            System.out.println("here");
            return "redirect:/home";
        }
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
       boolean isAuthenticated = userService.authenticateUser(email,password);
        if(isAuthenticated) {
            User loggedInUser = this.userService.findByEmail(email);
            session.setAttribute("user_id",loggedInUser.getId());
            return "redirect:/home";
        }
        else {
            model.addAttribute("error", "Invalid credentials. Please try again!");
            return "loginPage";
        }
    }

    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");
        User u = userService.findUserById(userId);
        model.addAttribute("user", u);
        return "homePage";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
            session.invalidate();
            return "redirect:login";
    }

}