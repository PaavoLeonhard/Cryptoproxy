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
