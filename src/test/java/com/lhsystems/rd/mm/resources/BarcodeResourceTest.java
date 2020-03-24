package com.lhsystems.rd.mm.resources;

import io.quarkus.test.junit.QuarkusTest;
import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.restdriver.serverdriver.RestServerDriver.post;
import static io.quarkus.test.common.http.TestHTTPResourceManager.getUri;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;

@QuarkusTest
public class BarcodeResourceTest {

    @ParameterizedTest
    @ValueSource(strings = {"qr","aztec"})
    public void test(String input) throws Exception {
        MultipartFormBody output = new MultipartFormBody();
        output.addFormField("source", "test");
        output.addFileField("file", "filename.png", IOUtils.toByteArray(new FileInputStream(
                "src/test/resources/" + input + ".png")));

        assertJsonEquals(post(getUri() + "/api/mm/barcode", output).asText(),
                FileUtils.readFileToString(new File("src/test/resources/" + input + ".json"), "utf-8"));
    }
}
