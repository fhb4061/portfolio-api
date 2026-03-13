package aus.tane.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "name is mandatory") String name,
        String email
) {
}
