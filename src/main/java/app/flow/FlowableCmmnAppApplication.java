package app.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class FlowableCmmnAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowableCmmnAppApplication.class, args);
	}
	
	@Bean
	public ObjectMapper objectMapper() {
	    return new ObjectMapper();
	}

}