package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired JobRepository jobRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/")
    public String listJobs(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        return "base";
    }

    @GetMapping("/add")
    public String addJob(Model model) {
        model.addAttribute("job", new Job());
        return "tobedel";
    }

    @PostMapping("/processjob")
    public String processJob(@ModelAttribute Job job) {
        LocalDate tempDate = LocalDate.now();
        job.setPostedDate(tempDate);
        jobRepository.save(job);
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showJob(@PathVariable("id") long id, Model model) {
        model.addAttribute("job", jobRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateJob(@PathVariable("id") long id, Model model) {
        model.addAttribute("job", jobRepository.findById(id).get());
        return "tobedel";
    }

    @RequestMapping("/delete/{id}")
    public String delFlight(@PathVariable("id") long id) {
        jobRepository.deleteById(id);
        return "redirect:/";
    }

    // methods for processing the search
    @PostMapping("/processSearchbyTitle")
    public String processSearchbyTitle(Model model, @RequestParam(name = "search") String titleSearch) {
        model.addAttribute("jobsByTitle", jobRepository.findByTitleContainingIgnoreCase(titleSearch));
        return "searchList";
    }


//    for security
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid
                                          @ModelAttribute("user") User user, BindingResult result,
                                          Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors())
        {
            return "registration";
        }
        else
        {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "index";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";
    }
}