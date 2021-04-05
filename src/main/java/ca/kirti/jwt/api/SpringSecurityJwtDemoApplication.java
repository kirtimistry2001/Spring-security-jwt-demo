package ca.kirti.jwt.api;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ca.kirti.jwt.api.Entity.User;
import ca.kirti.jwt.api.Repository.UserRepository;

@SpringBootApplication
public class SpringSecurityJwtDemoApplication {

	@Autowired
	private UserRepository userRepo;
	
	@PostConstruct
	public void initUser() {
		List<User> users = Stream.of(
				new User(1, "Kirti","k@a.com","password"),
				new User(2, "ena", "e@a.com","password2"),
				new User(3, "Riya" ,"ar@a.com","password3"),
				new User(4, "Ganesha", "g@a.com","password4"))
				.collect(Collectors.toList());
		userRepo.saveAll(users);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtDemoApplication.class, args);
	}

}
