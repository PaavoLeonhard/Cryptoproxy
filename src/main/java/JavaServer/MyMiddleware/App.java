package JavaServer.MyMiddleware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.mashape.unirest.http.Unirest;

/**
 *Start Spring-Boot server at 8080
 */
@SpringBootApplication
public class App {
	public static void main(String[] args) {
        SpringApplication.run(App.class, args);
	}
}
