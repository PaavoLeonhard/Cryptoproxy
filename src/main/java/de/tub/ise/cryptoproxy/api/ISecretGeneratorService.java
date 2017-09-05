package de.tub.ise.cryptoproxy.api;

import java.security.NoSuchAlgorithmException;

public interface ISecretGeneratorService {
	
	public byte[] generate() throws NoSuchAlgorithmException;

}
