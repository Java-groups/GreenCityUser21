package greencity.dto.shoppinglist;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CustomShoppingListItemResponseDto {
    @NonNull
    @Min(1)
    private Long id;
    @NotEmpty
    private String text;
}
