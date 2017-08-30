package de.tub.ise.cryptoproxy.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class IdentService {

    public String getKeyFor(String identy,String server){
        return "password1234funf";
    }
}
