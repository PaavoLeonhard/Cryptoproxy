package de.tub.ise.cryptoproxy.util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import de.tub.ise.cryptoproxy.api.ISecretGeneratorService;

public class KeyGenerator implements ISecretGeneratorService {

	@Override
	public byte[] generate() throws NoSuchAlgorithmException {
		javax.crypto.KeyGenerator keyGen;
		keyGen = javax.crypto.KeyGenerator.getInstance("AES");
		keyGen.init(256); // for example
		SecretKey secretKey = keyGen.generateKey();
		return secretKey.getEncoded();

	}

}
