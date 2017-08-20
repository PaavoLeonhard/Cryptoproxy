package de.tub.ise.cryptoware.view;

import de.tub.ise.cryptoware.data.Data;
import de.tub.ise.cryptoware.util.ApacheCrypto;
import de.tub.ise.cryptoware.util.IdentService;
import de.tub.ise.cryptoware.util.RestRequestService;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * 
 * @author Paavo.Camps
 *
 */
@RestController
public class Receiver {
	@Autowired
	IdentService identService;

	@Autowired
	ApacheCrypto cryptoService;

	@Autowired
	RestRequestService requestService;

	RestTemplate restTemplate = new RestTemplate();


	/**
	 * Default constructor required by Spring
	 */
	public Receiver() {}

	/**
	 * Receives Http request and forwards it to the data source 
	 * thereafter it encrypts and sends the data with the http response
	 * @return
	 */
	@RequestMapping(value = "/**", method = RequestMethod.GET)
	public ResponseEntity<String> show(
					HttpServletRequest request,
					 @RequestHeader("X-Server") String server,
					 @RequestHeader("X-Identity") String identity,
					 @RequestHeader("X-IdentServer") String identServer) {

		Response res = performProxyCall(request.getRequestURI(), server);

		String encrypt = null;
		try {
			encrypt = encrypt(identity, identServer, res);
		} catch (IOException e) {
			return ResponseEntity.status(500).build();
		}


		return ResponseEntity.status(res.code())
				.body(encrypt);
	}

	private String encrypt(String identity, String identServer, Response res) throws IOException {
		byte [] raw = res.body().bytes();

		String key = identService.getKeyFor(identity,identServer);

		return cryptoService.apply( raw , key);
	}

	private Response performProxyCall(String path,String server) {

		Request request = new Request.Builder().get().url(server+path).build();

		try {
			Response response = requestService.client.newCall(request).execute();
			return response;
		} catch (IOException e) {
			return new Response.Builder().code(500).build();
		}
	}
}
