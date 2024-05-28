package com.task.UserCRM.repository;

import com.task.UserCRM.Enum.Gender;
import com.task.UserCRM.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class UserRepositoryTest {

    UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @AfterEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    @Sql(scripts = {"classpath:sql/initiate_user.sql"})
    public void get_user(){

        User testUser = userRepository.findById(1).get();

        assertThat(testUser.getUsername()).isEqualTo("TestUser");
    }

    @Test
    public void create_user(){

        User user = new User();

        user.setUsername("test");
        user.setAge(19);
        user.setGender(Gender.MALE.name());
        user.setActive(true);

        userRepository.save(user);

        User testUser = userRepository.findById(1).orElse(new User());

        assertThat(testUser.getUsername()).isEqualTo(user.getUsername());
    }


    @Test
    @Sql(scripts = {"classpath:sql/initiate_user.sql"})
    public void delete_user(){

        userRepository.deleteById(1);

        User testUser = userRepository.findById(1).orElse(new User());

        assertThat(testUser.getUsername()).isEqualTo(null);
    }

}
