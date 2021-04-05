package ca.kirti.jwt.api.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service //don't foget
public class JwtUtil {

	// secrete password
	private String secret = "password";

	/**
	 * Create a token for given user.
	 * Set the claims in (payload) in empty Map
	 * Set expire date and time
	 * Sign with HS256 Algorithm
	 * @param claims
	 * @param subject
	 * @return
	 * 	A compact URL-safe JWT string.
	 */
	private String createToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //setting it for next 10 hours from the 1st login
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	/**
	 * Generate tokens for given userName
	 * @param userName
	 * @return
	 * 		A compact URL-safe JWT string.
	 */
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims,userName);
	}

	/**
	 * Validate given token
	 * 		extract the user name and compare it with userdetails' userName
	 * check for expire of token
	 * @param token
	 * @param userDetails
	 * @return
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	/**
	 * extract user name from token
	 * @param token
	 * @return
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * 
	 * @param <T>
	 * @param token
	 * @param claimsResolver
	 * @return
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	
	
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
}
