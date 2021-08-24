package org.greenleaf.injectedserviceloader;

import org.greenleaf.annotation.Service;

@Service(key = "MyBusinessService11", serviceClass = BuinessInterface.class)
public class MyBusinessService11 implements BuinessInterface {
    @Override
    public void business(String string) {
        System.out.println("MyBusinessService11" + string);
    }
}