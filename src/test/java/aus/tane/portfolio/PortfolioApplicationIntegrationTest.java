package aus.tane.portfolio;

import aus.tane.portfolio.dto.TodoStatus;
import aus.tane.portfolio.dto.request.TodoRequest;
import aus.tane.portfolio.dto.request.UserPatchRequest;
import aus.tane.portfolio.dto.request.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class PortfolioApplicationIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createAndPatchUser_thenCreateTodo() {
        String baseUrl = "http://localhost:" + port;

        UserRequest createUser = new UserRequest("Integration User", "integration@example.com");
        ResponseEntity<String> createResponse = restTemplate.postForEntity(baseUrl + "/users", createUser, String.class);
        assertThat(createResponse.getStatusCode().is2xxSuccessful()).isTrue();

        String body = createResponse.getBody();
        assertThat(body).isNotNull();
        String userId = body.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1");
        assertThat(userId).isNotEmpty();

        UserPatchRequest patch = new UserPatchRequest("Updated Integration", null);
        HttpEntity<UserPatchRequest> patchEntity = new HttpEntity<>(patch);
        ResponseEntity<String> patchResponse = restTemplate.exchange(
                baseUrl + "/users/" + userId,
                HttpMethod.PATCH,
                patchEntity,
                String.class
        );
        assertThat(patchResponse.getStatusCode().is2xxSuccessful()).isTrue();

        TodoRequest todoRequest = new TodoRequest("Integration Todo", TodoStatus.PENDING, Long.parseLong(userId));
        ResponseEntity<String> todoResponse = restTemplate.postForEntity(baseUrl + "/todos", todoRequest, String.class);
        assertThat(todoResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
