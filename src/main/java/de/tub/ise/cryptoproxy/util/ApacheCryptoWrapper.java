package de.tub.ise.cryptoproxy.util;

import java.io.InputStream;
import java.util.Base64;

import de.tub.ise.cryptoproxy.api.ICryptoService;

public class ApacheCryptoWrapper implements ICryptoService{
	private ApacheCrypto service;
	private byte[] secret;
	
	

	public ApacheCryptoWrapper(byte[] secret, ApacheCrypto instance) {
		this.secret = secret;
		this.service = instance;
	}

	@Override
	public byte[] apply(byte[] input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] apply(InputStream input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String applyAsString(byte[] input) {
		// TODO Auto-generated method stub
		return this.service.apply(input, Base64.getEncoder().encodeToString(secret));
	}

}
