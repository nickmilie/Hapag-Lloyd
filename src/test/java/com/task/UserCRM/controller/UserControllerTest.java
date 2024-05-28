package com.task.UserCRM.controller;

import com.task.UserCRM.entity.User;
import com.task.UserCRM.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;


    @AfterEach
    void cleanDatabase(){
        userRepository.deleteAll();
    }

    @Test
    @Sql(scripts = {"classpath:sql/initiate_user.sql"})
    void should_return_user_response() throws Exception {
        this.mockMvc.perform(get("/user/1")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":1,\"username\":\"TestUser\",\"gender\":\"MALE\",\"age\":19,\"creationTimestamp\":null,\"active\":true}")));
    }

    @Test
    @Sql(scripts = {"classpath:sql/initiate_user.sql"})
    void should_return_user_not_found_response() throws Exception {
        this.mockMvc.perform(get("/user/2")).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString("There is no such user")));
    }

    @Test
    void should_create_user() throws Exception {
        this.mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"TestUser\",\"gender\":\"MALE\",\"age\":19}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User created")));

        User user = userRepository.findById(1).orElse(new User());

        assertThat(user.getUsername()).isEqualTo("TestUser");
    }

    @Test
    void should_throw_username_special_characters_exception() throws Exception {
        this.mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"TestUser!!!\",\"gender\":\"MALE\",\"age\":19}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Please do not use special characters in username")));
    }

    @Test
    @Sql(scripts = {"classpath:sql/initiate_user.sql"})
    void should_throw_username_is_taken() throws Exception {
        this.mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"TestUser\",\"gender\":\"MALE\",\"age\":19}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username is already taken")));
    }

    @Test
    @Sql(scripts = {"classpath:sql/initiate_user.sql"})
    void should_delete_user() throws Exception {
        this.mockMvc.perform(delete("/user/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User has been deleted")));
    }

    @Test
    @Sql(scripts = {"classpath:sql/initiate_user.sql"})
    void should_update_user() throws Exception {
        this.mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"TestUser\",\"gender\":\"MALE\",\"age\":21}"))
                .andDo(print())
                .andExpect(status().isOk());

        User user = userRepository.findById(1).orElse(new User());

        assertThat(user.getAge()).isEqualTo(21);
    }

}
