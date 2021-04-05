package ca.kirti.jwt.api.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	
	@GetMapping("/")
	public String welcome() {
		return "Hello from JWT- Spring demo";
	}

}
