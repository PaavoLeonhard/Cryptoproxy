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
