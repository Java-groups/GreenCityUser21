package greencity.dto.econews;

import greencity.dto.user.PlaceAuthorDto;
import java.time.ZonedDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EcoNewsForSendEmailDto {
    @NotBlank
    private String unsubscribeToken;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime creationDate;

    private String imagePath;

    private String source;

    @NotNull
    @Valid
    private PlaceAuthorDto author;

    @NotBlank
    @Size(max = 170, message = "Title should contain maximum {max} symbols")
    private String title;

    @NotBlank
    @Size(min = 20, max = 63206, message = "Text should contain minimum {min} symbols and maximum {max} symbols")
    private String text;
}
