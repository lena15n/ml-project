package com.machinelearning;

import com.machinelearning.auth.AuthenticationFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

public class MyApplication extends ResourceConfig {
    public MyApplication() {
        packages("com.machinelearning");
        register(LoggingFilter.class);

        register(AuthenticationFilter.class);
    }
}
