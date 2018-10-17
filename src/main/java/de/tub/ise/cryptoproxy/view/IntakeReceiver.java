/*
 * Copyright 2018 Information Systems Engineering, TU Berlin, Germany
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.This is being developed for the DITAS Project: https://www.ditas-project.eu/
 */

package de.tub.ise.cryptoproxy.view;

import de.tub.ise.cryptoproxy.api.ICryptoService;
import de.tub.ise.cryptoproxy.api.ISecureStorageService;
import de.tub.ise.cryptoproxy.util.ApacheCryptoBuilder;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import de.tub.ise.cryptoproxy.util.RestRequestService;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Paavo.Camps
 *
 */
@RestController
public class IntakeReceiver {

	@Autowired
	RestRequestService requestService;

	@Autowired
	ISecureStorageService secureStorageService;

	RestTemplate restTemplate = new RestTemplate();

	/**
	 * Default constructor required by Spring
	 */
	public IntakeReceiver() {
	}

	/**
	 * Receives Http request and forwards it to the data source thereafter it
	 * encrypts and sends the data with the http response
	 * 
	 * @return
	 */
	@RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.OPTIONS,RequestMethod.HEAD })
	public ResponseEntity<String> show(HttpServletRequest request, @RequestHeader("X-Server") String server,
			@RequestHeader("X-Identity") String identity) {

		Response res = null;
		try {
			res = performProxyCall(request, server);
		} catch (Exception e1) {
			return ResponseEntity.status(500).build();
		}

		String encrypt = null;
		try {
			encrypt = encrypt(identity, res);
		} catch (IOException e) {
			return ResponseEntity.status(500).build();
		}

		return ResponseEntity.status(res.code()).headers(transfrom(res.headers())).body(encrypt);
	}

	private HttpHeaders transfrom(Headers headers) {
		HttpHeaders theader = new HttpHeaders();
		
		for(String name : headers.names()){
			theader.add(name, headers.get(name));
		}
		
		return theader;
	}

	private String encrypt(String identity, Response res) throws IOException {
		byte[] raw = res.body().bytes();

		ICryptoService cryptoService = new ApacheCryptoBuilder()
				.withSecret(secureStorageService.get(identity))
				.build();

		return cryptoService.applyAsString(raw);
	}

	private Response performProxyCall(HttpServletRequest initialRequest, String server) throws Exception {
		System.out.println(initialRequest.getMethod());
		
		Request.Builder builder = new Request.Builder();
		
		//X-Server url + request URI
		builder.url(server + initialRequest.getRequestURI());
		
		switch (initialRequest.getMethod()) {
			case "GET":
				builder.get();
				break;
			case "POST":
				builder.post(generateRequestBody(initialRequest));
				break;
			case "PUT":
				builder.put(generateRequestBody(initialRequest));
				break;
			case "DELETE":
				builder.delete();
				break;
			case "OPTIONS":
				builder.method("OPTIONS", null);
				break;
			case "HEAD":
				builder.head();
				break;
		}

		return buildAndSendRequest(builder);
	}

	private Response buildAndSendRequest(Request.Builder builder) {
		try {
			Request request = builder.build();
			System.out.println(request);
			Response response = requestService.client.newCall(request).execute();
			return response;
		} catch (IOException e) {
			return new Response.Builder().code(500).build();
		}
	}

	private RequestBody generateRequestBody(HttpServletRequest initialRequest) throws IOException {
		return RequestBody.create(extractMediaType(initialRequest),  responseToByteArray(initialRequest));
	}

	private byte[] responseToByteArray(HttpServletRequest initialRequest) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = initialRequest.getInputStream();
		int read = -1;
		byte[] buffer = new byte[1024];
		while ((read = is.read(buffer)) > 0) {
			bos.write(buffer, 0, read);
		}
		is.close();
		bos.flush();
		
		return bos.toByteArray();
	}

	private MediaType extractMediaType(HttpServletRequest initialRequest) {
		MediaType media;
		if(initialRequest.getContentType() == null){
			media = MediaType.parse("text/plain; charset=utf-8");
		} else {
			media = MediaType.parse(initialRequest.getContentType());
		}
		return media;
	}
}
