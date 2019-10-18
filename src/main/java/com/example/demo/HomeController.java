package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
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

    @Autowired
    RoleRepository roleRepository;

    @PostConstruct
    public void load() {
        if(!roleRepository.findAll().iterator().hasNext()) {
            roleRepository.save(new Role("USER"));
            roleRepository.save(new Role("ADMIN"));
        }
    }

    @RequestMapping("/")
    public String listJobs(Principal principal, Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        model.addAttribute("anyjob", jobRepository.findAll().iterator().hasNext());

        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
            String tempusername = principal.getName();
            model.addAttribute("user", userRepository.findByUsername(tempusername));
        }
        return "base";
    }

    @GetMapping("/add")
    public String addJob(Principal principal, Model model) {
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
            String tempusername = principal.getName();
            model.addAttribute("user", userRepository.findByUsername(tempusername));
        }

        model.addAttribute("job", new Job());
        return "jobform";
    }

    @PostMapping("/processjob")
    public String processJob(@ModelAttribute Job job) {
        LocalDate tempDate = LocalDate.now();
        job.setPostedDate(tempDate);
        job.setUser(userService.getUser());
        jobRepository.save(job);
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showJob(Principal principal, @PathVariable("id") long id, Model model) {
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
            String tempusername = principal.getName();
            model.addAttribute("user", userRepository.findByUsername(tempusername));
        }
        model.addAttribute("job", jobRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateJob(Principal principal, @PathVariable("id") long id, Model model) {
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
            String tempusername = principal.getName();
            model.addAttribute("user", userRepository.findByUsername(tempusername));
        }
        model.addAttribute("job", jobRepository.findById(id).get());
        return "jobform";
    }

    @RequestMapping("/delete/{id}")
    public String delFlight(@PathVariable("id") long id) {
        jobRepository.deleteById(id);
        return "redirect:/";
    }

    // methods for processing the search
    @PostMapping("/processSearchbyTitle")
    public String processSearchbyTitle(Principal principal, Model model, @RequestParam(name = "search") String titleSearch) {
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
            String tempusername = principal.getName();
            model.addAttribute("user", userRepository.findByUsername(tempusername));
        }
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
        return "redirect:/login";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }


}