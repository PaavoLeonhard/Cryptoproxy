package de.tub.ise.cryptoproxy.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.SecretKey;

import de.tub.ise.cryptoproxy.api.ISecretGeneratorService;
import org.springframework.stereotype.Service;

@Service("SecretGenerator")
public class KeyGenerator implements ISecretGeneratorService {

	public final static SecureRandom rand = new SecureRandom();

	@Override
	public byte[] generate() throws NoSuchAlgorithmException {
		javax.crypto.KeyGenerator keyGen;
		keyGen = javax.crypto.KeyGenerator.getInstance("AES");
		keyGen.init(256,rand);
		SecretKey secretKey = keyGen.generateKey();
		return secretKey.getEncoded();

	}

}
