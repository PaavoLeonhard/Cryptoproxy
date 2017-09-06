package de.tub.ise.cryptoproxy.data;

public class HandshakeResponse {


    private final String sid;
    private final byte[] secret;

    public HandshakeResponse(String sid, byte[] secret) {
        this.sid = sid;
        this.secret = secret;
    }

    public String getSid() {
        return sid;
    }

    public byte[] getSecret() {
        return secret;
    }
}
