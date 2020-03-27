package com.lhsystems.rd.mm.config;

import io.reactivex.processors.MulticastProcessor;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import org.reactivestreams.Processor;

@ApplicationScoped
public class QueueConfig {

    @Produces
    @Named("notificationQueue")
    public Processor<String, String> notficiationQueue() {
        MulticastProcessor proc = MulticastProcessor.create();

        proc.subscribe();
        proc.start();

        return proc;
    }
}
