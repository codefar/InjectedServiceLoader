package org.greenleaf.injectedserviceloader;

import org.greenleaf.annotation.Service;

@Service(key = "MyBusinessService", serviceClass = BuinessInterface.class)
public class MyBusinessService implements BuinessInterface {
    @Override
    public void business(String string) {
        System.out.println("MyBusinessService" + string);
    }
}