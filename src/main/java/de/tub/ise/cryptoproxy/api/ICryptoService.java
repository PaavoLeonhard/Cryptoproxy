package de.tub.ise.cryptoproxy.api;

import java.io.InputStream;

public interface ICryptoService {
	
	public interface ICryptoBuilder {
		public ICryptoBuilder withSecret(byte[] secret);
		
		public ICryptoService build();
	}
	
	public byte[] apply(byte[] input);
	
	public byte[] apply(InputStream input);
	
	public String applyAsString(byte[] input);

}
