package com.authentication.borghi.integration;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OTTIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldCreateToken(){
        //GIVEN
        //WHEN
        //THEN

    }
}
