package de.tub.ise.cryptoproxy.api;

public interface ISecureStorageService {
	
	public String put(byte[] aesKey, long ttl);
	
    public byte [] get(String sID);
}
