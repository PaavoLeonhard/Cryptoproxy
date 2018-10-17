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

import java.util.Properties;

import javax.crypto.spec.IvParameterSpec;

import de.tub.ise.cryptoproxy.api.ICryptoService;
import de.tub.ise.cryptoproxy.api.ICryptoService.ICryptoBuilder;

	public class ApacheCryptoBuilder implements ICryptoBuilder {

		private byte[] secret;
		private String transform;
		private Properties properties;
		private IvParameterSpec iv;

		public ApacheCryptoBuilder withSecret(byte[] secret) {
			this.secret = secret;
			return this;
		}

		public ApacheCryptoBuilder withTransform(String transform) {
			this.transform = transform;
			return this;
		}

		public ApacheCryptoBuilder withProperties(Properties properties) {
			this.properties = properties;
			return this;
		}

		public ApacheCryptoBuilder withIvParameterSpec(IvParameterSpec iv) {
			this.iv = iv;
			return this;
		}

		@Override
		public ICryptoService build() {
			
			ApacheCrypto instance = new ApacheCrypto();
			if(transform != null){
				instance.setTransform(transform);
			}
			if(properties != null){
				instance.setProperties(properties);
			}
			if(iv!= null){
				instance.setIv(iv);
			}
			return new ApacheCryptoWrapper(secret, instance);
		}

	}

