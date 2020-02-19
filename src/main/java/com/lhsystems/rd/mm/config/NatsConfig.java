/*
 *  Copyright Lufthansa Systems.
 */
package com.lhsystems.rd.mm.config;

import io.reactivex.processors.MulticastProcessor;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.reactivestreams.Processor;

@ApplicationScoped
public class NatsConfig {

    @Produces
    @Named("notificationQueue")
    public Processor<String, String> notficiationQueue() {
        MulticastProcessor proc= MulticastProcessor.create();
        
        proc.subscribe();
        proc.start();
        
        return proc;
    }
    
    @Inject
    @ConfigProperty(name = "nats.servers", defaultValue = "localhost:4442")
    private String servers;

    
    
}
