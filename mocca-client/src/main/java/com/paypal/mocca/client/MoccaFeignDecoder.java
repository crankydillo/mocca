package com.paypal.mocca.client;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Mocca Feign decoder, responsible for deserializing the response payload
 *
 * @author fabiocarvalho777@gmail.com
 */
class MoccaFeignDecoder implements Decoder {

    private final MoccaDeserializer moccaDeserializer = new MoccaDeserializer();

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (response.status() != 200) {
            throw new MoccaException("Unexpected HTTP response status code: " + response.status());
        }

        Optional<?> result;
        try (InputStream inputStream = response.body().asInputStream()) {
            if (inputStream == null) {
                throw new MoccaException("Response does not contain a payload");
            }

            final String operationName = MoccaFeignEncoder.getOperationName(response);
            result = moccaDeserializer.deserialize(inputStream, type, operationName);
        }

        return result.orElse(null);
    }

}