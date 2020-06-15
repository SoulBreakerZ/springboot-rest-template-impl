package com.arkontec.core.rest.services.helpers;

import com.arkontec.core.rest.application.exception.RestCoreException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;


@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private static final Logger logger = LogManager.getLogger(RestTemplateResponseErrorHandler.class);

/*    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {

        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError())
            throw new RestCoreException(response.getStatusCode(), response.getStatusText());

    }*/

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        HttpStatus status = clientHttpResponse.getStatusCode();
        return status.is4xxClientError() || status.is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError())
            throw new RestCoreException(response.getStatusCode(), response.getStatusText());
    }
}
