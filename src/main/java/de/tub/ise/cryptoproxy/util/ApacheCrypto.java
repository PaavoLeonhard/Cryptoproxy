package de.tub.ise.cryptoproxy.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.stream.CryptoInputStream;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.apache.commons.crypto.utils.Utils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.tub.ise.cryptoproxy.data.Data;


/**
 * Uses the Apache Commons Crypto library to encrypt and decrypt streams and
 * object
 * 
 * @author Paavo.Camps
 *
 */
@Service
@Scope("prototype")
public class ApacheCrypto {
	private IvParameterSpec iv;
	private Properties properties;
	private String transform;

	public ApacheCrypto() {
		this.iv = new IvParameterSpec(getUTF8Bytes("1234567890123456"));
		this.transform = "AES/CBC/PKCS5PADDING";
	}

	public void setTransform(String transform) {
		this.transform = transform;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setIv(IvParameterSpec iv) {
		this.iv = iv;
	}

	public String getSimpleName() {
		return "ApacheCrypto";
	}


	public String apply(byte[] input, String... args) {
		SecretKeySpec key = new SecretKeySpec(getUTF8Bytes(args[0]), "AES");
		if (args.length == 2) {
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		} else if (args.length > 3) {
			this.transform = args[2];
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		}

		try {
			Cipher encipher = Cipher.getInstance(transform);
			encipher.init(Cipher.ENCRYPT_MODE, key, iv);
			return Base64.encodeBase64URLSafeString(encipher.doFinal(input));
		} catch (Exception e) {
			return null;
		}
	}

	public Data revert(Data encData, int length, String... args) {
		SecretKeySpec key = new SecretKeySpec(getUTF8Bytes(args[0]), "AES");
		if (args.length == 2) {
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		} else if (args.length > 3) {
			this.transform = args[2];
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		}
		CryptoCipher decipher;
		try {
			Cipher cipher = Cipher.getInstance(transform);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);


			byte[] original = cipher.doFinal(Base64.decodeBase64(encData.getData()));

			return new Data(new String(original));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Takes input and output Path as parameters as well as a varying number of
	 * Strings, the first String is the password and must be of length 16 for
	 * AES, the second String can be the algorithm, the third can be the
	 * padding.
	 */
	public void revert(Path inputFile, Path outputFile, String... args) {
		SecretKeySpec key = new SecretKeySpec(getUTF8Bytes(args[0]), "AES");
		if (args.length == 2) {
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		} else if (args.length > 3) {
			this.transform = args[2];
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		}
		try {
			InputStream inputStreamE = new FileInputStream(inputFile.toFile());
			CryptoInputStream cis = new CryptoInputStream(transform, properties, inputStreamE, key, iv);
			OutputStream outputStream = new FileOutputStream(outputFile.toFile());
			byte[] decryptedData = new byte[1024];
			int length = 0;
			while ((length = cis.read(decryptedData, 0, decryptedData.length)) > -1) {
				outputStream.write(decryptedData, 0, length);
			}
			cis.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 73 * Converts String to UTF8 bytes 74 * 75 * @param input the input
	 * string 76 * @return UTF8 bytes 77
	 */
	public static byte[] getUTF8Bytes(String input) {
		return input.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Takes input and output Path as parameters as well as a varying number of
	 * Strings, the first String is the password and must be of length 16 for
	 * AES, the second String can be the algorithm, the third can be the
	 * padding.
	 */
	public void apply(Path inputFile, Path outputFile, String... args) {
		SecretKeySpec key = new SecretKeySpec(getUTF8Bytes(args[0]), "AES");
		if (args.length == 2) {
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		} else if (args.length > 3) {
			this.transform = args[2];
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		}
		try {
			File encryptedFile = outputFile.toFile();
			OutputStream outputStream;
			outputStream = new FileOutputStream(encryptedFile);
			CryptoOutputStream cos = new CryptoOutputStream(transform, properties, outputStream, key, iv);
			File toEncryptFile = inputFile.toFile();
			InputStream toEncryptStr = new FileInputStream(toEncryptFile);
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = toEncryptStr.read(buffer, 0, buffer.length)) != -1) {
				cos.write(buffer, 0, length);
			}
			toEncryptStr.close();
			cos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}