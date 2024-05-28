package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.config.SecurityConfig;
import greencity.message.SendHabitNotification;
import greencity.security.jwt.JwtTool;
import greencity.service.EmailService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SecurityConfig.class, EmailController.class})
@WebAppConfiguration
@EnableWebMvc
@Import({JwtTool.class})
@TestPropertySource(properties = {
        "accessTokenValidTimeInMinutes=60",
        "refreshTokenValidTimeInMinutes=1440",
        "tokenKey=secretTokenKey"
})
class EmailControllerWithSecurityConfigTest {
    private static final String LINK = "/email";

    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        when(userService.findByEmail(anyString())).thenReturn(ModelUtils.getUserVO());
    }

    @Test
    @WithMockUser(username = "TestAdmin", roles = "ADMIN")
    void sendHabitNotification_ReturnsIsOk() throws Exception {
        String content = "{" +
                "\"email\":\"test.email@gmail.com\"," +
                "\"name\":\"String\"" +
                "}";

        sentPostRequest(content, "/sendHabitNotification")
                .andExpect(status().isOk());

        SendHabitNotification notification =
                new ObjectMapper().readValue(content, SendHabitNotification.class);

        verify(emailService).sendHabitNotification(notification.getName(), notification.getEmail());
    }

    @Test
    @WithAnonymousUser
    void sendHabitNotification_ReturnsIsUnauthorized() throws Exception {
        String content = "{" +
                "\"email\":\"test.email@gmail.com\"," +
                "\"name\":\"String\"" +
                "}";

        sentPostRequest(content, "/sendHabitNotification")
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(emailService);
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "USER")
    void sendHabitNotification_ReturnsIsForbidden() throws Exception {
        String content = "{" +
                "\"email\":\"test.email@gmail.com\"," +
                "\"name\":\"String\"" +
                "}";

        sentPostRequest(content, "/sendHabitNotification")
                .andExpect(status().isForbidden());

        verifyNoInteractions(emailService);
    }

    private ResultActions sentPostRequest(String content, String subLink) throws Exception {
        return mockMvc.perform(post(LINK + subLink)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

}
