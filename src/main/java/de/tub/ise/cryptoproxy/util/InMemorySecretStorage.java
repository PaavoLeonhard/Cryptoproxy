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
