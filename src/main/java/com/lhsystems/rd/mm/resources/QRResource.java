package com.lhsystems.rd.mm.resources;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import io.vertx.core.json.Json;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/mm/qr")
public class QRResource {

    private static Logger log = LoggerFactory.getLogger(QRResource.class);

    @Inject
    @Named("notificationQueue")
    Processor<String, String> notificationQueue;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public QRNotification hello(MultipartFormDataInput files)
            throws Exception {
        log.info("request arrived");

        QRNotification notif = null;
        InputStream file = files.getFormDataPart("file", InputStream.class, null);

        byte[] qr = IOUtils.toByteArray(file);
        log.info("image size=" + qr.length);

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(qr));
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeMultiReader reader = new QRCodeMultiReader();
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            //hints.put(DecodeHintType.POSSIBLE_FORMATS, 
            //         Collections.singletonList(BarcodeFormat.AZTEC));
            hints.put(DecodeHintType.TRY_HARDER,
                    true);

            Result[] results = reader.decodeMultiple(bitmap, hints);

            for (Result result : results) {

                notif = QRNotification.builder().
                        mediaType("image/png").
                        qrData(reencodeAztec(result.getText())).
                        info(result.getText()).build();
                notificationQueue.onNext(Json.mapper.writeValueAsString(notif));
            }

            return notif;

        } catch (NotFoundException na) {
            notif = QRNotification.builder().
                    info("Not found.").
                    build();
        } catch (ReaderException ex) {
            log.error("Error reading", ex);
            notif = QRNotification.builder().
                    info("Error: " + ex.getMessage()).
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
