package greencity.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.constant.ErrorMessage;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.message.SendChangePlaceStatusEmailMessage;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;
import greencity.service.EmailService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {
    private static final String LINK = "/email";
    private MockMvc mockMvc;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Mock
    private ErrorAttributes errorAttributes;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(emailController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes))
            .build();
    }

    @Test
    void addEcoNews() throws Exception {
        String content =
            "{\"unsubscribeToken\":\"string\"," +
                "\"creationDate\":\"2021-02-05T15:10:22.434Z\"," +
                "\"imagePath\":\"string\"," +
                "\"source\":\"string\"," +
                "\"author\":{\"id\":0,\"name\":\"string\",\"email\":\"test.email@gmail.com\" }," +
                "\"title\":\"string\"," +
                "\"text\":\"string123456789012345678\"}";

        mockPerform(content, "/addEcoNews");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        EcoNewsForSendEmailDto message = objectMapper.readValue(content, EcoNewsForSendEmailDto.class);

        verify(emailService).sendCreatedNewsForAuthor(message);
    }

    @Test
    void addEcoNews_isBadRequest() throws Exception {
        String content = "{\n" +
                "\"author\": {\n" +
                "\"email\": \"Test3421gmail.com\",\n" +
                "\"id\": 154,\n" +
                "\"name\": \"Test1526435\"\n" +
                "},\n" +
                "\"creationDate\": \"2023-08-23T11:46:06.482Z\",\n" +
                "\"imagePath\": \"string\",\n" +
                "\"source\": \"string\",\n" +
                "\"text\": \"Test1241254125125125124\",\n" +
                "\"title\": \"Test1111\",\n" +
                "\"unsubscribeToken\": \"string\"\n" +
                "}";
        mockMvc.perform(post(LINK + "/addEcoNews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }
  
  @Test
    void addEcoNewsTest_NotFound() throws Exception {
        String content = "{\n" +
                "\"author\": {\n" +
                "\"email\": \"Test34211@gmail.com\",\n" +
                "\"id\": 15,\n" +
                "\"name\": \"Test1526435\"\n" +
                "},\n" +
                "\"creationDate\": \"2023-08-23T11:46:06.482Z\",\n" +
                "\"imagePath\": \"string\",\n" +
                "\"source\": \"string\",\n" +
                "\"text\": \"Test1241254125125125124\",\n" +
                "\"title\": \"Test1111\",\n" +
                "\"unsubscribeToken\": \"string\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        EcoNewsForSendEmailDto dto = mapper.readValue(content, EcoNewsForSendEmailDto.class);

        String expectedErrorMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + "Test34211@gmail.com";
        doThrow(new NotFoundException(expectedErrorMessage))
                .when(emailService)
                .sendCreatedNewsForAuthor(dto);

        mockErrorAttributes();

        mockMvc.perform(post(LINK + "/addEcoNews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    private void mockErrorAttributes() {
        Map<String, Object> errorAttributesMap = new HashMap<>();
        errorAttributesMap.put("timestamp", "timestamp");
        errorAttributesMap.put("trace", "trace");
        errorAttributesMap.put("path", "path");
        errorAttributesMap.put("message", "message");
        when(errorAttributes.getErrorAttributes(any(), any())).thenReturn(errorAttributesMap);
    }

    @Test
    void sendReport() throws Exception {
        String content = "{" +
            "\"categoriesDtoWithPlacesDtoMap\":" +
            "{\"additionalProp1\":" +
            "[{\"category\":{\"name\":\"string\",\"parentCategoryId\":0}," +
            "\"name\":\"string\"}]," +
            "\"additionalProp2\":" +
            "[{\"category\":{\"name\":\"string\",\"parentCategoryId\":0}," +
            "\"name\":\"string\"}]," +
            "\"additionalProp3\":[{\"category\":{\"name\":\"string\",\"parentCategoryId\":0}," +
            "\"name\":\"string\"}]}," +
            "\"emailNotification\":\"string\"," +
            "\"subscribers\":[{\"email\":\"string\",\"id\":0,\"name\":\"string\"}]}";

        mockPerform(content, "/sendReport");

        SendReportEmailMessage message =
            new ObjectMapper().readValue(content, SendReportEmailMessage.class);

        verify(emailService).sendAddedNewPlacesReportEmail(
            message.getSubscribers(), message.getCategoriesDtoWithPlacesDtoMap(),
            message.getEmailNotification());
    }

    @Test
    void changePlaceStatus() throws Exception {
        String content = "{" +
            "\"authorEmail\":\"test.email@gmail.com\"," +
            "\"authorFirstName\":\"String\"," +
            "\"placeName\":\"string\"," +
            "\"placeStatus\":\"string\"" +
            "}";

        mockPerform(content, "/changePlaceStatus");

        SendChangePlaceStatusEmailMessage message =
            new ObjectMapper().readValue(content, SendChangePlaceStatusEmailMessage.class);

        verify(emailService).sendChangePlaceStatusEmail(
            message.getAuthorFirstName(), message.getPlaceName(),
            message.getPlaceStatus(), message.getAuthorEmail());
    }

    @Test
    void changePlaceStatusTest_NotFound() throws Exception {
        String content = "{" +
                "\"authorEmail\":\"Admin1@gmail.com\"," +
                "\"authorFirstName\":\"String\"," +
                "\"placeName\":\"string\"," +
                "\"placeStatus\":\"string\"" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        SendChangePlaceStatusEmailMessage message =
                mapper.readValue(content, SendChangePlaceStatusEmailMessage.class);

        String expectedErrorMessage =
                String.format("%s/%s", ErrorMessage.USER_NOT_FOUND_BY_EMAIL, "Admin1@gmail.com");
        doThrow(new NotFoundException(expectedErrorMessage))
                .when(emailService)
                .sendChangePlaceStatusEmail(message.getAuthorFirstName(), message.getPlaceName(),
                message.getPlaceStatus(), message.getAuthorEmail());

        mockErrorAttributes();

        mockMvc.perform(post(String.format("%s/%s", LINK , "/changePlaceStatus"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNotFound());
    }

    @Test
    void changePlaceStatus_ExpectedBadRequest() throws Exception {
        String content = "{" +
            "\"authorEmail\":\"string\"," +
            "\"authorFirstName\":\"string\"," +
            "\"placeName\":\"string\"," +
            "\"placeStatus\":\"string\"" +
            "}";

        sentPostRequest(content, "/changePlaceStatus")
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);

    }

    @Test
    void sendHabitNotification() throws Exception {
        String content = "{" +
            "\"email\":\"test.email@gmail.com\"," +
            "\"name\":\"String\"" +
            "}";

        mockPerform(content, "/sendHabitNotification");

        SendHabitNotification notification =
            new ObjectMapper().readValue(content, SendHabitNotification.class);

        verify(emailService).sendHabitNotification(notification.getName(), notification.getEmail());
    }

    @Test
    void sendHabitNotification_ExpectedBadRequest() throws Exception {
        String content = "{" +
                "\"email\":\"string\"," +
                "\"name\":\"string\"" +
                "}";

        sentPostRequest(content, "/sendHabitNotification")
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }

    @Test
    void sendHabitNotification_ExpectedNotFound() throws Exception {
        String content = "{" +
                "\"email\":\"1111@gmail.com\"," +
                "\"name\":\"String\"" +
                "}";

        String email = "1111@gmail.com";
        String name = "String";

        doThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email)).when(emailService).sendHabitNotification(name, email);
        HashMap<String, Object> map = new HashMap<>();
        map.put("timestamp", "timestamp");
        map.put("trace", "trace");
        map.put("path", "path");
        map.put("message", "message");
        when(errorAttributes.getErrorAttributes(any(), any())).thenReturn(map);

        sentPostRequest(content, "/sendHabitNotification")
                .andExpect(status().isNotFound());
    }

    private ResultActions sentPostRequest(String content, String subLink) throws Exception {
        return mockMvc.perform(post(LINK + subLink)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    private void mockPerform(String content, String subLink) throws Exception {
        mockMvc.perform(post(LINK + subLink)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

    @Test
    void sendUserViolationEmailTest() throws Exception {
        String content = "{" +
            "\"name\":\"String\"," +
            "\"email\":\"String@gmail.com\"," +
                "\"language\":\"en\"," +
            "\"violationDescription\":\"string string\"" +
            "}";

        mockPerform(content, "/sendUserViolation");

        UserViolationMailDto userViolationMailDto = new ObjectMapper().readValue(content, UserViolationMailDto.class);
        verify(emailService).sendUserViolationEmail(userViolationMailDto);
    }

    @Test
    @SneakyThrows
    void sendUserNotification() {
        String content = "{" +
            "\"title\":\"title\"," +
            "\"body\":\"body\"" +
            "}";
        String email = "email@mail.com";

        mockMvc.perform(post(LINK + "/notification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
            .param("email", email))
            .andExpect(status().isOk());

        NotificationDto notification = new ObjectMapper().readValue(content, NotificationDto.class);
        verify(emailService).sendNotificationByEmail(notification, email);
    }
}
