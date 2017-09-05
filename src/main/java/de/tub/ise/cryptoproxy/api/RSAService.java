package de.tub.ise.cryptoproxy.api;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAService {

	public KeyPair create() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(512);
		return keyGen.genKeyPair();
	}

	public byte[] encryptWithPublicKey(byte[] input, long modulo, long exponent)
			throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException,
			InvalidKeyException, NoSuchPaddingException {
		BigInteger mod = BigInteger.valueOf(modulo);
		BigInteger exp = BigInteger.valueOf(exponent);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Key pub;
		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(mod, exp);
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

		Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return cipher.doFinal(input);
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		RSAService test = new RSAService();
		KeyPair pair = test.create();
		Key publi= pair.getPublic();
		Key privat =pair.getPrivate();
		String pub =publi.toString();
		
		
	}
}

//Sun RSA public key, 512 bits
//modulus: 7614996636649999770342084767844287530852371043040840404974894320078027316087125197663925392844410441250821309535727298468554486195498903755669110093006139
//public exponent: 65537
