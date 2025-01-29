package com.authentication.borghi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static com.authentication.borghi.constants.TestConstants.*;

@WebMvcTest(controllers = ErrorController.class)
@AutoConfigureMockMvc(addFilters = false)
class ErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenAccessDeniedUrlWhenPerformGeThenReturnAccessDeniedView() throws Exception {
        // when & then
        mockMvc.perform(get(ACCESS_DENIED_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(ACCESS_DENIED_VIEW));
    }
}

