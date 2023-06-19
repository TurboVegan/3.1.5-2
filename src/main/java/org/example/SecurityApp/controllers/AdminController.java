package org.example.SecurityApp.controllers;


import org.example.SecurityApp.dto.UserDTO;
import org.example.SecurityApp.models.Role;
import org.example.SecurityApp.models.User;
import org.example.SecurityApp.repositories.RolesRepository;
import org.example.SecurityApp.services.UsersService;
import org.example.SecurityApp.util.UserErrorResponse;
import org.example.SecurityApp.util.UserNotCreatedException;
import org.example.SecurityApp.util.UserNotFoundException;
import org.example.SecurityApp.util.UserValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {




    private final UsersService usersService;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminController(UsersService usersService, ModelMapper modelMapper) {
        this.usersService = usersService;
        this.modelMapper = modelMapper;
    }


    @GetMapping
    public List<User> getUsers() {
        return usersService.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable ("id") int id) {
        return usersService.findOne(id);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable ("id") int id) {

        usersService.delete(id);
        return "User with ID = " + id + " was deleted";
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable ("id") int id, @RequestBody User user) {

        usersService.update(id, user);
        return user;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid UserDTO userDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error: errors){
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new UserNotCreatedException(errorMsg.toString());
        }
        usersService.save(convertToUser(userDTO));

        return ResponseEntity.ok(HttpStatus.OK);
    }



    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse response = new UserErrorResponse(
                "User with this id was not found!", System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(), System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);

    }






//
//    @GetMapping()
//    public String index(ModelMap model, Principal principal) {
//
//        User LoggedInUser = usersService.findByUsername(principal.getName());
//
//        model.addAttribute("LoggedInUser", LoggedInUser);
//        model.addAttribute("users", usersService.findAll());
//        List<Role> roles = (List<Role>) rolesRepository.findAll();
//        model.addAttribute("allRoles", roles);
//
//        return "admin/index";
//    }
//
//    @GetMapping("/user/{id}")
//    public String show(@PathVariable("id") int id, Model model) {
//        model.addAttribute("user", usersService.findOne(id));
//        return "admin/show";
//    }
//
//    @GetMapping("/new")
//    public ModelAndView newUser(Principal principal) {
//
//        User user = new User();
//        ModelAndView mav = new ModelAndView("admin/new");
//        mav.addObject("user", user);
//        User LoggedInUser = usersService.findByUsername(principal.getName());
//
//        mav.addObject("LoggedInUser", LoggedInUser);
//
//        List<Role> roles = (List<Role>) rolesRepository.findAll();
//
//        mav.addObject("allRoles", roles);
//
//        return mav;
//    }
//
//    @PostMapping()
//    public String create(@ModelAttribute("user") @Valid User user,
//                         BindingResult bindingResult, RedirectAttributes ra) {
//        userValidator.validate(user, bindingResult);
//
//        if (bindingResult.hasErrors()) {
//            return "/admin/new";
//        }
//
//        usersService.register(user);
//        ra.addFlashAttribute("message", "The user has been saved successfully.");
//        return "redirect:/admin";
//    }
//
//    @GetMapping("/{id}/edit")
//    public ModelAndView editUser(@PathVariable(name = "id") Integer id) {
//        User user = usersService.findOne(id);
//        ModelAndView mav = new ModelAndView("admin/edit");
//        mav.addObject("user", user);
//
//        List<Role> roles = (List<Role>) rolesRepository.findAll();
//
//        mav.addObject("allRoles", roles);
//
//        return mav;
//    }
//
//    @PatchMapping()
//    public String update(@ModelAttribute("user") User user) {
//        int id = user.getId();
//        usersService.update(id, user);
//        return "redirect:/admin";
//    }
//
//    @DeleteMapping("/user")
//    public String delete(@ModelAttribute("user") User user) {
//        int id = user.getId();
//        usersService.delete(id);
//        return "redirect:/admin";
//    }
//
//    @GetMapping("/findOne")
//    @ResponseBody
//    public User findOne(Integer id) {
//        return usersService.findOne(id);
//    }
}