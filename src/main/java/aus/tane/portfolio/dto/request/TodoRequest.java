package aus.tane.portfolio.dto.request;

import aus.tane.portfolio.dto.TodoStatus;

public record TodoRequest(String title, TodoStatus status, Long userId) {
}
