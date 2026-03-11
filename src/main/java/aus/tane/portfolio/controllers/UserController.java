package aus.tane.portfolio.controllers;

import aus.tane.portfolio.dto.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController(value = "/users")
public class UserController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping()
    public User getUser(@RequestParam(defaultValue = "Sam") String name) {
        return new User(counter.incrementAndGet(), template.formatted(name));
    }
}
