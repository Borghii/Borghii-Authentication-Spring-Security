//package com.authentication.borghi.integration;
//
//
//
//import com.authentication.borghi.service.onetimetoken.PersistentOneTimeTokenService;
//import com.authentication.borghi.service.user.UserService;
//import com.icegreen.greenmail.configuration.GreenMailConfiguration;
//
//
//import com.icegreen.greenmail.util.ServerSetupTest;
//
//import jakarta.transaction.Transactional;
//
//import org.awaitility.Durations;
//import org.junit.jupiter.api.extension.RegisterExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.SpyBean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import static com.authentication.borghi.constants.TestConstants.*;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.awaitility.Awaitility.await;
//import static org.mockito.Mockito.atLeast;
//import static org.mockito.Mockito.verify;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import com.icegreen.greenmail.junit5.GreenMailExtension;
//import org.junit.jupiter.api.Test;
//
//import java.util.concurrent.TimeUnit;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//public class OTTIntegrationTest {
//
//    // GreenMail configuration (official recommended setup)
//
//    @RegisterExtension
//    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
//            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
//            .withPerMethodLifecycle(false);
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserService userService;
//
//
//
//
//
//    @Test
//    void shouldCreateTokenAndSendEmail() throws Exception {
//        // Given
//        userService.saveUserFromDTO(TEST_USER_DTO);
//
//
//        // When & Then
//        mockMvc.perform(post("/ott/generate")
//                        .with(csrf())
//                        .param("username", TEST_USER_DTO.getUsername()))
//                .andExpect(status().is3xxRedirection());
//
//
//    }
//
//
//
//
//
//
//}
