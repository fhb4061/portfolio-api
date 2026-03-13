package aus.tane.portfolio.dto.request;

import aus.tane.portfolio.dto.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodoRequest(
        @NotBlank(message = "title is mandatory") String title,
        @NotNull(message = "status is mandatory") TodoStatus status,
        @NotNull(message = "userId is mandatory") Long userId
) {
}
