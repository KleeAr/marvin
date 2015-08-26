package ar.com.klee.marvin.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootApplication
@ComponentScan({ "ar.com.klee.marvin.*" })
@EnableJpaRepositories({ "ar.com.klee.marvin.repository" })
@EntityScan({ "ar.com.klee.marvin.model" })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@EnableWebSecurity
	public static class SecurityConfiguration extends
			WebSecurityConfigurerAdapter {

		@Autowired
		private UserDetailsService detailsService;

		@Override
		protected void configure(AuthenticationManagerBuilder auth)
				throws Exception {
			auth.userDetailsService(detailsService);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/users/register").permitAll().anyRequest().authenticated();
			http.httpBasic();
			http.csrf().disable();
		}

		public UserDetailsService getDetailsService() {
			return detailsService;
		}

		public void setDetailsService(UserDetailsService detailsService) {
			this.detailsService = detailsService;
		}

	}

}
