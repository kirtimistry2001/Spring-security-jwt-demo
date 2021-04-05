package ca.kirti.jwt.api.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ca.kirti.jwt.api.service.CustomUserDetailService;
import ca.kirti.jwt.api.util.JwtUtil;

/**
 * Filter is used to extract the Token and get the user name and password 
 * and authenticate/validate the user. If user authentication success then 
 * allow to access the request end point
 * 
 * (OncePerRequestFilter) This will execute for every request.
 * NOTE: register this with security config 
 */
@Component
public class JwtFilter extends OncePerRequestFilter{

	/**
	 * used to extract username
	 * 
	 */
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private CustomUserDetailService service;
	
	/**
	 * 
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//First extract authorization header from the HttpServletRequest
		String authorizationHeader = request.getHeader("Authorization"); //this will return the generate JWT token String
		System.out.println("authorizationHeader :"+authorizationHeader);
		/* get the example from jwt.io
		 * for example: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
		 * 
		 * In the above the 1st part: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9 is a header
		 * 2nd part : eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ  is  payload
		 * The 2nd part has  user name
		 * 
		 */
		String token = null;
		String userName = null;
		//validate authorizationHeader
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			token = authorizationHeader.substring(7); 
			userName = jwtUtil.extractUsername(token);
		}
		//System.out.println("token :"+token);
		//System.out.println("userName :"+userName);
		// get the userDetails
		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = service.loadUserByUsername(userName);
			//validate token for name and expire
			if(jwtUtil.validateToken(token, userDetails)) {
				/* After satisfied with valid token
				 * producing a trusted authentication token.*/
				UsernamePasswordAuthenticationToken authenticationToken = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
				//Changes the currently authenticated principal with new token
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
			
		}
		filterChain.doFilter(request,response);
	}

}
