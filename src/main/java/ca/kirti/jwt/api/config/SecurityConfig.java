package ca.kirti.jwt.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ca.kirti.jwt.api.filter.JwtFilter;
import ca.kirti.jwt.api.service.CustomUserDetailService;

/**
 * This is a web level security class which authenticate the user
 * @author Kirti
 * https://spring.io/guides/gs/securing-web/
 */
@Configuration
@EnableWebSecurity //tells Spring that this is webSecurity configuration. There is another type security like appliation/method level security
public class SecurityConfig  extends WebSecurityConfigurerAdapter {
	
	
	@Autowired
	private JwtFilter jwtFilter;
	
	@Autowired	
	private CustomUserDetailService userDetailService;
	
/*	@Autowired
	DataSource dataSource; // used with jdbcAuthentication
	*/
	/**
	 * Add the Authentication
	 * Customizing Authentication Managers.
	 * Spring Security provides some configuration helpers 
	 * to quickly get common authentication manager features set up in your application. 
	 * The most commonly used helper is the AuthenticationManagerBuilder, 
	 * which is great for setting up in-memory, JDBC, or LDAP user details for adding a custom UserDetailsService
	 * see : https://spring.io/guides/topicals/spring-security-architecture#web-security
	 */
	@Override // used only to build a “local” AuthenticationManager, which would be a child of the global one
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService);
		// you can also configure with inmemoryAuthentication
	/*	auth.inMemoryAuthentication()
			.withUser("blah")
			.password("blah")
			.roles("USER") //help in role based authentication
			.and()		// use method chaining to add more Inmemory users
			.withUser("foo")
			.password("foo")
			.roles("ADMIN");*/
			
	//configures the global (parent) AuthenticationManager:	
	// you have autowire the @Autowired   DataSource dataSource;	
	// using jdbcAuthentication
		/*	 auth.jdbcAuthentication().dataSource(dataSource).withUser("dave")
	      .password("secret").roles("USER");*/
	}
	
	@SuppressWarnings("deprecation")
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance(); // this actually doing nothing. Should not use in production.
	}
	
	/**
	 * Override this method to expose the {@link AuthenticationManager} from
	 * {@link #configure(AuthenticationManagerBuilder)} to be exposed as a Bean.
	 *
	 * The "global" AuthenticationManager instance, registered by the
	 * authentication-manager
	 */
	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	/**
	 * Hold on HttpSecurity to authenticate different endpoint or to authenticated based on their Roll
	 * e.g /admin for the loggedin used with Role as admin etc.
	 * Authenticate all request end points except the "/authenticate" end point
	 * enable session policy (JWT foloows stateless authentication  mechenism) for JWT and register the JWT filter
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
		.antMatchers("/authenticate").permitAll().anyRequest().authenticated()
		.and().exceptionHandling().and().sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS); //enable session creation policy which is stateless
	http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); //registed filter
	// Role based or endpoint 
	// match all endpoint in a order from most restricted to least restricted
	/*http.authorizeRequests()  
		.antMatchers("/admin").hasRole("ADMIN") ///admin endpoint for loggedin user with role  'ADMIN'
		.antMatchers("/users").hasRole("USER") ///users endpoing for logged in user with 'User" role 
		.antMatchers("/*").permitAll() // for all end points it needs to access login form. Note:never put this first
		.and().formLogin();*/
	
	}
} 
