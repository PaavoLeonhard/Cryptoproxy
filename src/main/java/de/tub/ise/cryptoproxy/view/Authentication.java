package de.tub.ise.cryptoproxy.view;

import de.tub.ise.cryptoproxy.api.ISecretGeneratorService;
import de.tub.ise.cryptoproxy.api.ISecureStorageService;
import de.tub.ise.cryptoproxy.data.HandshakeRequest;
import de.tub.ise.cryptoproxy.data.HandshakeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

@RestController
public class Authentication {

    private static final long DEFAULT_TTL = 3600;

    @Autowired
    ISecureStorageService secureStorageService;

    @Autowired
    ISecretGeneratorService secretGeneratorService;


    @RequestMapping(value = "/debug", method = RequestMethod.GET)
    public ResponseEntity<HandshakeRequest> debug(){
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(512);
            KeyPair keyPair = keyGen.genKeyPair();
            return ResponseEntity.ok(new HandshakeRequest("debugmin", (RSAPublicKey) keyPair.getPublic()));
        } catch (NoSuchAlgorithmException e) {
            return ResponseEntity.noContent().build();
        }



    }

    @RequestMapping(value = "/link", method = { RequestMethod.POST},
            consumes = {"*"})
    public ResponseEntity<HandshakeResponse> link(@RequestBody HandshakeRequest request){
        if(request.validate()){
            String identiy = request.getIdentity();

                try {
                    byte[] sessionKey = secretGeneratorService.generate();
                    String SID = secureStorageService.put(sessionKey, DEFAULT_TTL);

                    ResponseEntity<HandshakeResponse> response = createResponse(SID, sessionKey, request);
                    if (response != null){
                        return response;
                    }
                    secureStorageService.invalidate(SID);
                } catch (Exception e){
                    e.printStackTrace();
                    //TODO log
                }
        }

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }

    private ResponseEntity<HandshakeResponse> createResponse(String sid, byte[] sessionKey, HandshakeRequest request)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, request.getKey());

        byte[] secret = cipher.doFinal(sessionKey);

        return ResponseEntity.ok(new HandshakeResponse(sid,secret));
    }

}
