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
	static Data res = null;

	/**
	 * Default constructor required by Spring
	 */
	public Receiver() {
	}

	/**
	 * Receives Http request and forwards it to the data source 
	 * thereafter it encrypts and sends the data with the http response 
	 * @param path
	 * @return
	 */
	@RequestMapping(value = "{path}", method = RequestMethod.GET)
	public Data show(HttpServletResponse responseHead,@PathVariable String path) {
		//Assuming the data is located at localhost:1880
		ResponseEntity<String> result = restTemplate.getForEntity("http://127.0.0.1:1880" + path, String.class);
		String res = result.getBody();
		byte [] raw = ApacheCrypto.getUTF8Bytes(res);
		int length = raw.length;
		String encrypt = crypt.apply( raw , "password1234funf");
		responseHead.addIntHeader("Content-Length", length);
//		System.out.println(res.getData());
		return new Data(encrypt);
	}
}
