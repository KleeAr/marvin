package ar.com.klee.marvin.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@SpringBootApplication
@ComponentScan({ "ar.com.klee.marvin.*" })
@EnableJpaRepositories({ "ar.com.klee.marvin.repository" })
@EntityScan({ "ar.com.klee.marvin.model" })
@EnableResourceServer
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
			http.authorizeRequests().antMatchers("/users/register").permitAll()
					.antMatchers("/oauth*").permitAll().anyRequest().authenticated();
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

	@Configuration
	@EnableAuthorizationServer
	public static class OAuthConfiguration extends
			AuthorizationServerConfigurerAdapter {

		@Autowired
		private DataSource dataSource;

		@Bean
		public TokenStore tokenStore() {
			return new JdbcTokenStore(dataSource);
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.tokenStore(tokenStore());
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients)
				throws Exception {
			clients.inMemory().withClient("marvin").authorities("ROLE_BASIC")
					.resourceIds("marvin").scopes("read", "write")
					.authorizedGrantTypes("client_credentials")
					.secret("password");
		}
	}

}
