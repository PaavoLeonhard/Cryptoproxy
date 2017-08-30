package de.tub.ise.cryptoproxy.util;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class RestRequestService {

    public final OkHttpClient client;

    public RestRequestService(){
        client = new OkHttpClient();
    }


}
