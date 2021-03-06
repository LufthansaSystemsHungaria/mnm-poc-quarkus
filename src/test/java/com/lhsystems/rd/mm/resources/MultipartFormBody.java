package com.lhsystems.rd.mm.resources;

import com.github.restdriver.serverdriver.http.AnyRequestModifier;
import com.github.restdriver.serverdriver.http.ServerDriverHttpUriRequest;
import java.net.URLConnection;


import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

public class MultipartFormBody implements AnyRequestModifier {
    private MultipartEntityBuilder multipartContent = MultipartEntityBuilder.create();

    public void addFormField(String formField, String value) {
        multipartContent.addPart(FormBodyPartBuilder.create(formField,
                new StringBody(value, ContentType.TEXT_PLAIN)).build());
    }

    public void addFileField(String formField, String fileName, byte[] content) {
        multipartContent.addPart(FormBodyPartBuilder.create(formField,
                new ByteArrayBody(content,
                        ContentType.create(URLConnection.guessContentTypeFromName(fileName)),
                        fileName)).
                build());
    }

    @Override
    public void applyTo(ServerDriverHttpUriRequest request) {
        HttpUriRequest internalRequest = request.getHttpUriRequest();

        if (!(internalRequest instanceof HttpEntityEnclosingRequest)) {
            return;
        }

        HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) internalRequest;
        entityRequest.setEntity(multipartContent.build());
    }
}
