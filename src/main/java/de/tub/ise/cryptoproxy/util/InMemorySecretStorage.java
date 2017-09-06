package de.tub.ise.cryptoproxy.util;


import de.tub.ise.cryptoproxy.api.ISecureStorageService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

@Service
@Scope("singleton")
public class InMemorySecretStorage implements ISecureStorageService{

    private final HashMap<String,String> storage;
    private final HashMap<String,Long> ttlTable;
    private final MessageDigest hashFunction;

    public InMemorySecretStorage(){
        storage = new HashMap<>();
        ttlTable = new HashMap<>();
        try {
            this.hashFunction = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("could not start Database");
        }
    }


    @Override
    public String put(byte[] aesKey, long ttl) {
        String key = Base64.encodeBase64String(aesKey);
        String sid = Base64.encodeBase64String(hashFunction.digest(aesKey));

        storage.put(sid,key);
        ttlTable.put(sid,System.currentTimeMillis()+ttl);

        return sid;
    }

    @Override
    public byte[] get(String sID) {
        String key = storage.get(sID);
        if(key == null || System.currentTimeMillis() > ttlTable.getOrDefault(key,0L)){
            throw new IllegalArgumentException("SID not availible");
        }
        return Base64.decodeBase64(key);
    }

    @Override
    public void invalidate(String sid) {
        storage.remove(sid);
        ttlTable.put(sid,0L);
    }
}
