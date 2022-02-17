package com.paypal.mocca.client;

import org.testng.annotations.Test;

import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithRequestTimeouts {

    @Override
    protected Class<?> expectedTimeoutExceptionCause() {
        return HttpTimeoutException.class;
    }

    @Override
    MoccaHttpClient.WithRequestTimeouts create() {
        return new MoccaHttp2Client(HttpClient.newBuilder().build());
    }
}
