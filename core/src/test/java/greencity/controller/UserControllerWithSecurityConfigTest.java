package greencity.controller;

import greencity.ModelUtils;
import greencity.config.SecurityConfig;
import greencity.repository.UserRepo;
import greencity.security.jwt.JwtTool;
import greencity.service.EmailService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SecurityConfig.class, UserController.class})
@WebAppConfiguration
@EnableWebMvc
@Import({JwtTool.class})
@TestPropertySource(properties = {
        "accessTokenValidTimeInMinutes=60",
        "refreshTokenValidTimeInMinutes=1440",
        "tokenKey=secretTokenKey"
})
public class UserControllerWithSecurityConfigTest {
    private static final String userLink = "/user";

    private MockMvc mockMvc;

    private UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private EmailService emailService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        when(userService.findByEmail(anyString())).thenReturn(ModelUtils.getUserVO());
    }

    @Test
    @WithMockUser(username = "testmail@gmail.com", roles = "USER")
    void getEmailNotificationsTest_isOk() throws Exception {
        mockMvc.perform(get(userLink + "/emailNotifications"))
                .andExpect(status().isOk());

        verify(userService).getEmailNotificationsStatuses();
    }

    @Test
    @WithAnonymousUser
    void getEmailNotificationsTest_isUnauthorized() throws Exception {
        mockMvc.perform(get(userLink + "/emailNotifications")
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
        verify(userService, times(0)).getEmailNotificationsStatuses();
    }

    @Test
    @WithAnonymousUser
    void updateUserProfilePicture_isUnauthorized() throws Exception {
        mockMvc.perform(patch(userLink + "/profilePicture")
                .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
}
