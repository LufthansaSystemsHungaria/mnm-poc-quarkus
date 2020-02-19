/*
 *  Copyright Lufthansa Systems.
 */
package com.lhsystems.rd.mm.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class NatsConfig {

    @Inject
    @ConfigProperty(name = "nats.servers", defaultValue = "localhost:4442")
    private String servers;

    
    
}
