package com.lhsystems.rd.mm.resources;

import io.reactivex.processors.MulticastProcessor;
import io.vertx.core.json.Json;
import java.io.InputStream;
import java.util.Base64;
import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/mm/qr")
public class QRResource {

    private static Logger log = LoggerFactory.getLogger(QRResource.class);

    MulticastProcessor<String> notificationQueue = MulticastProcessor.create();

    @PostConstruct
    public void setup() {
        // Create empty subscription to drain queue, if nobody there
        notificationQueue.subscribe();
        notificationQueue.start();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public QRNotification hello(MultipartFormDataInput files)
            throws Exception {
        log.info("request arrived");
        
        InputStream file = files.getFormDataPart("file", InputStream.class, null);

        byte[] qr = IOUtils.toByteArray(file);

        log.info("image size=" + qr.length);
        
        QRNotification notif = QRNotification.builder().
                mediaType("image/gif").
                qrData(Base64.getEncoder().encodeToString(qr)).
                info("Hello!").build();

        notificationQueue.onNext(Json.mapper.writeValueAsString(notif));

        return notif;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("/streaming")
    public Publisher<String> streaming() {
        return notificationQueue;
    }
}
