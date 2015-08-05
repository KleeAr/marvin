package ar.com.klee.marvin.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"ar.com.klee.marvin.*"})
@EnableJpaRepositories({"ar.com.klee.marvin.repository"})
@EntityScan({"ar.com.klee.marvin.model"})
public class Application {

	public static void main(String[] args) {
		 SpringApplication.run(Application.class, args);
	}

}
