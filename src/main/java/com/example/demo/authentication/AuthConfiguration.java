package com.example.demo.authentication;



import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.demo.model.Credentials.ADMIN_ROLE;
@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {
@Autowired
DataSource datasource;
 @Override 
 protected void configure (HttpSecurity http)throws Exception {
	 http
	 		//authorization paragraph : here we define Who can access which pages
	 		.authorizeRequests()
	 		.antMatchers(HttpMethod.GET,"/","/index","/login","/users/register","/users/update","/js/**","/css/**").permitAll()
	 		.antMatchers(HttpMethod.POST,"/login","/users/register","/users/update").permitAll()
	 		.antMatchers(HttpMethod.GET,"/admin").hasAnyAuthority(ADMIN_ROLE)
	 		.anyRequest().authenticated()
	 		.and().formLogin()
	 		.defaultSuccessUrl("/logged")
	 		.and().logout().logoutUrl("/logout")
	 		.logoutSuccessUrl("/index");
	 		
 }
 @Override 
 public void configure(AuthenticationManagerBuilder auth)throws Exception{
	 auth.jdbcAuthentication()
	 .dataSource(this.datasource)
	 .authoritiesByUsernameQuery("SELECT user_name,role FROM credentials WHERE user_name=?")
	 .usersByUsernameQuery("SELECT user_name,password,1 as enabled FROM credentials WHERE user_name=?");
 }
 /*Con bean noi annotiamo un oggetto la cui  instanza non verrà inizializzata ogni volta , ma l'applicazione riprenderà questo oggetto precedentemente salvato*/
 /*Quindi rimane nel contesto dell' applicazione e lo possiamo ottenere tramite @Autowired  NOTA:@Component è un tipo particolare di @Bean*/
 @Bean
 PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}
}
