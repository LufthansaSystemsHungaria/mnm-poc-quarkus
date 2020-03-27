package com.lhsystems.rd.mm.resources;

import com.google.zxing.NotFoundException;
import com.google.zxing.*;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import io.vertx.core.json.Json;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Path("/api/mm/barcode")
public class BarcodeResource {

    private static Logger log = LoggerFactory.getLogger(BarcodeResource.class);

    @Inject
    @Named("notificationQueue")
    Processor<String, String> notificationQueue;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public BarcodeNotification hello(MultipartFormDataInput files)
            throws Exception {
        log.info("request arrived");

        BarcodeNotification notif = null;
        InputStream file = files.getFormDataPart("file", InputStream.class, null);

        byte[] barcode = IOUtils.toByteArray(file);
        log.info("image size=" + barcode.length);

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(barcode));
            if (image == null) {
                throw new IllegalStateException("Cannot read image.");
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            MultiFormatReader reader = new MultiFormatReader();

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(DecodeHintType.PURE_BARCODE, false);
            hints.put(DecodeHintType.TRY_HARDER, true);

            Result result = reader.decode(bitmap, hints);

            notif = BarcodeNotification.builder().
                    format(result.getBarcodeFormat().name()).
                    mediaType("image/png").
                    barcodeData(reencodeAztec(result.getText())).
                    info(result.getText()).build();
            notificationQueue.onNext(Json.mapper.writeValueAsString(notif));

            return notif;

        } catch (NotFoundException na) {
            notif = BarcodeNotification.builder().
                    info("Data not found").
                    build();
        }

        notificationQueue.onNext(Json.mapper.writeValueAsString(notif));

        return notif;
    }

    private String reencodeAztec(String text) throws WriterException, IOException {
        int size = 50;

        Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.MARGIN, 1);

        AztecWriter writer = new AztecWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.AZTEC, size, size, hintMap);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);

        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("/streaming")
    public Publisher<String> streaming() {
        return notificationQueue;
    }
}
