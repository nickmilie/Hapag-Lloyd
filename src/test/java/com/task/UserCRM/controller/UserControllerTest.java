package com.task.UserCRM.controller;

import com.task.UserCRM.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RestTemplate restTemplate;

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

}
