package ca.kirti.jwt.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.kirti.jwt.api.Entity.AuthRequest;
import ca.kirti.jwt.api.util.JwtUtil;

@RestController
public class UserController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private AuthenticationManager authManager;
	
	@GetMapping("/")
	public String welcome() {
		return "Hello from JWT- Spring demo";
	}

	//As we apply Spring security its now apply on all end points,
	//but we need to disble for this specific url.
	// tell spring security not to apply
	// the security for this specific method
	/**
	 * create a JWT WebToken for user with given Authrequest.
	 * @param authReqest
	 * 		Get the AuthRequest from the user to authenticate
	 * @return
	 * 		Encrypted string which contains user name and password with 
	 * 		expire date-time and signature
	 * @throws Exception
	 */
	@PostMapping("/authenticate")
	public String generateToken(@RequestBody AuthRequest authReqest) throws Exception{
		// using AuthenticationManager authenticate user name and password
		try {
		authManager.authenticate(
				new UsernamePasswordAuthenticationToken(authReqest.getUserName(), authReqest.getPassword())
				);
		} catch (Exception e) {
			throw new Exception("Invalid userName ot passeord");
		}
		//generate WebToken (encrypted String with user name and password)
		return jwtUtil.generateToken(authReqest.getUserName());
	}
}
