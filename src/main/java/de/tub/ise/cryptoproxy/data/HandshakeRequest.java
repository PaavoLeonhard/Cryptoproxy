package de.tub.ise.cryptoproxy.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class HandshakeRequest {
	private BigInteger exp;
	private BigInteger mod;
	private String identity;
	private PublicKey key;

	public HandshakeRequest(String identity, RSAPublicKey key) {
		this.identity = identity;
		this.exp = key.getPublicExponent();
		this.mod = key.getModulus();
	}

	public HandshakeRequest(){

	}


	public String getIdentity() {
		return identity;
	}

	public boolean validate() {
		return true;
	}

	@JsonIgnore
	public RSAPublicKey getKey() {
		if(key == null){
			try {
				KeyFactory factory = KeyFactory.getInstance("RSA");
				key = factory.generatePublic(new RSAPublicKeySpec(mod,exp));
			} catch (Exception e) {
				throw new IllegalStateException("cannot generate key",e);
			}
		}
		return (RSAPublicKey) key;
	}

	public BigInteger getExp() {
		return exp;
	}

	public BigInteger getMod() {
		return mod;
	}

	public void setExp(BigInteger exp) {
		this.exp = exp;
	}

	public void setMod(BigInteger mod) {
		this.mod = mod;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
}
