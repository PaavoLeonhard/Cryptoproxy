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

