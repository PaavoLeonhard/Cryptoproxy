package encryption.methods;

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
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.cipher.CryptoCipherFactory.CipherProvider;
import org.apache.commons.crypto.stream.CryptoInputStream;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.apache.commons.crypto.utils.Utils;

import JavaServer.MyMiddleware.Data;

/**
 * Uses the Apache Commons Crypto library to encrypt and decrypt streams and
 * object
 * 
 * @author Paavo.Camps
 *
 */
public class ApacheCrypto {
	private IvParameterSpec iv;
	private Properties properties;
	private String transform;

	public ApacheCrypto() {
		this.iv = new IvParameterSpec(getUTF8Bytes("1234567890123456"));
		this.properties = new Properties();
		this.transform = "AES/CBC/PKCS5Padding";
	}

	public String getSimpleName() {
		return "ApacheCrypto";
	}

	/**
	 * Takes an Data class as parameter as well as a varying number of Strings,
	 * the first String is the password and must be of length 16 for AES, the
	 * second String can be the algorithm(default is AES), the third can be the
	 * padding.
	 */
	public int apply(Data rawData, String... args) {
		SecretKeySpec key = new SecretKeySpec(getUTF8Bytes(args[0]), "AES");
		if (args.length == 2) {
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		} else if (args.length > 3) {
			this.transform = args[2];
			key = new SecretKeySpec(getUTF8Bytes(args[0]), args[1]);
		}

		try {
			CryptoCipher encipher = Utils.getCipherInstance(transform, properties);
			byte[] input = getUTF8Bytes(rawData.getData());
			byte[] output = new byte[240];

			encipher.init(Cipher.ENCRYPT_MODE, key, iv);
			int updateBytes = encipher.update(input, 0, input.length, output, 0);
			int finalBytes = encipher.doFinal(input, 0, 0, output, updateBytes);
			encipher.close();
			rawData = new Data(Arrays.toString(Arrays.copyOf(output, updateBytes + finalBytes)));
			return updateBytes + finalBytes;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
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
			decipher = Utils.getCipherInstance(transform, properties);
			decipher.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] input = getUTF8Bytes(encData.getData());
			byte[] decoded = new byte[32];
			System.out.println(input + " und " + length);
			decipher.doFinal(input, 0, 16, decoded, 0);
			System.out.println("output: " + new String(decoded, StandardCharsets.UTF_8));
			return new Data(new String(decoded, StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ShortBufferException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
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
	private static byte[] getUTF8Bytes(String input) {
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
