package com.arkontec.core.rest.services.helpers;

import com.arkontec.core.rest.application.exception.RestCoreException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateStatusValidatorInterceptor
  implements ClientHttpRequestInterceptor {
 
    @Override
    public ClientHttpResponse intercept(
      HttpRequest request,
      byte[] body, 
      ClientHttpRequestExecution execution) throws IOException {
  
        ClientHttpResponse response = execution.execute(request, body);

        if (request.getMethod() == HttpMethod.DELETE && !(response.getStatusCode() == HttpStatus.NO_CONTENT))
            throw new RestCoreException(response.getStatusCode(), "DELETE NOT VALID CODE");

        if (request.getMethod() == HttpMethod.POST && !(response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED))
            throw new RestCoreException(response.getStatusCode(), "CREATED CODE NOT VALID");

        return response;
    }
}