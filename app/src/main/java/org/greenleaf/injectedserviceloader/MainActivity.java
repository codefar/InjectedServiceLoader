package org.greenleaf.injectedserviceloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.greenleaf.api.InjectedServiceLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectedServiceLoader.serviceListMap.putAll(org.greenleaf.injectedserviceloader.InjectedServiceRegistry.serviceListMap);
        setContentView(R.layout.activity_main);
        BuinessInterface buinessInterface = InjectedServiceLoader.getService(BuinessInterface.class);
        if (buinessInterface != null) {
            buinessInterface.business("AppCompatActivity");
        } else {
            System.err.println("AppCompatActivity error");
        }
    }
}