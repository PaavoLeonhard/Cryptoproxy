package JavaServer.MyMiddleware;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import encryption.methods.ApacheCrypto;

/**
 * 
 * @author Paavo.Camps
 *
 */
@RestController
public class Receiver {
	RestTemplate restTemplate = new RestTemplate();
	ApacheCrypto crypt = new ApacheCrypto();

	/**
	 * Default constructor required by Spring
	 */
	public Receiver() {
	}

	/**
	 * @param path
	 * @return
	 */
	@RequestMapping(value = "{path}", method = RequestMethod.GET)
	public Data show(HttpServletResponse responseHead,@PathVariable String path) {
		//System.out.println("The path is:" + path);
		ResponseEntity<String> result = restTemplate.getForEntity("http://127.0.0.1:1880" + path, String.class);
		//System.out.println(result.getBody());
		Data res = new Data(result.getBody());
		int length = crypt.apply(res , "password1234funf");
		responseHead.addIntHeader("Content-Length", length);
		System.out.println(res.getData());
		return res;
	}
}
