package greencity.message;

import java.io.Serializable;

import greencity.constant.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public final class SendChangePlaceStatusEmailMessage implements Serializable {
    @NotBlank
    @Pattern(regexp = ValidationConstants.USERNAME_REGEXP, message = ValidationConstants.USERNAME_MESSAGE)
    private String authorFirstName;

    @NotBlank
    @Size(max = ValidationConstants.PLACE_NAME_MAX_LENGTH, message = "Place Name should contain maximum {max} symbols")
    private String placeName;

    @NotBlank
    private String placeStatus;

    @NotBlank
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = ValidationConstants.INVALID_EMAIL)
    private String authorEmail;
}
