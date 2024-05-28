package com.task.UserCRM.controller;

import com.task.UserCRM.Enum.Gender;
import com.task.UserCRM.entity.User;
import com.task.UserCRM.model.UserModel;
import com.task.UserCRM.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController {

    UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/user/create")
    public ResponseEntity<String> createUser(@ModelAttribute UserModel userModel){

        if(userRepository.findByUsername(userModel.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken",
                    HttpStatus.BAD_REQUEST);
        };
        if(verifyUsername(userModel.getUsername())) {
            return new ResponseEntity<>("Please do not use special characters in username",
                    HttpStatus.BAD_REQUEST);
        }
        if(verifyAge(userModel.getAge())) return new ResponseEntity<>("Please reconsider your age", HttpStatus.BAD_REQUEST);

        User user = new User();

        user.setUsername(userModel.getUsername());

        if(userModel.getGender().equals(Gender.MALE.name())) user.setGender(Gender.MALE.name());
        if(userModel.getGender().equals(Gender.FEMALE.name())) user.setGender(Gender.FEMALE.name());

        user.setAge(userModel.getAge());
        user.setCreationTimestamp(LocalDateTime.now());


        userRepository.save(user);

        User confirmationUser = userRepository.findByUsername(userModel.getUsername()).orElse(new User());

        senConfirmationEmail(confirmationUser.getId());

        return ResponseEntity.ok("User created");
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id){

        User user = new User();

        if(userRepository.findById(Integer.valueOf(id)).isPresent()) {
            user = userRepository.findById(Integer.valueOf(id)).get();
        } else {
            return new ResponseEntity("There is no such user", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(user);
    }


    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @ModelAttribute UserModel userModel){

        User user = new User();

        if(userRepository.findById(Integer.valueOf(id)).isPresent()) {
            user = userRepository.findById(Integer.valueOf(id)).get();
        } else {
            return new ResponseEntity("There is no such user", HttpStatus.NOT_FOUND);
        }

        if(userModel.getGender().equals(Gender.MALE.name())) user.setGender(Gender.MALE.name());
        if(userModel.getGender().equals(Gender.FEMALE.name())) user.setGender(Gender.FEMALE.name());

        user.setAge(userModel.getAge());

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){

        userRepository.deleteById(Integer.valueOf(id));

        return ResponseEntity.ok("User has been deleted");
    }

    @GetMapping("/registrationConfirm")
    public ResponseEntity<String> confirm(@RequestParam("id") String id) {

        User user = userRepository.findById(Integer.valueOf(id)).orElse(new User());

        user.setActive(true);

        userRepository.save(user);

        return ResponseEntity.ok("User activated");
    }

    private boolean verifyUsername(String username){
        Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
        Matcher matcher = pattern.matcher(username);
        return matcher.find();
    }
    private boolean verifyAge(int age){
        return age < 0 || 120 < age;
    }

    private void senConfirmationEmail(int id){

        String recipientAddress = "USERNAME@gmail.com";
        String subject = "Registration confirmation";

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("http://localhost:8080/registrationConfirm?=" + id);
        mailSender.send(email);
    }

}
