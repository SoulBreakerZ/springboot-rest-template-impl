package com.arkontec.core.rest.services.helpers;

import com.arkontec.core.rest.application.exception.ErrorCode;
import com.arkontec.core.rest.application.exception.RestCoreException;
import com.arkontec.core.rest.services.domain.ResponseWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestTemplateHelper {

    private static final Logger logger = LogManager.getLogger(RestTemplateHelper.class);

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public RestTemplateHelper() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setRequestFactory(getClientHttpRequestFactory());
        this.restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        this.restTemplate.setInterceptors(getInterceptors());
        this.objectMapper = new ObjectMapper();
    }

    public <T> T getForEntity(Class<T> clazz, String url, Object... uriVariables) throws RestCoreException {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }

    public <T> ResponseWrapper<T> getForResponseWrapper(Class<T> clazz, String url, Object... uriVariables) throws RestCoreException {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseWrapper.class, clazz);
        return readValue(response, type);
    }

    public <T> List<T> getForList(Class<T> clazz, String url, Object... uriVariables) throws RestCoreException {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return readValue(response, collectionType);
    }

    public <T, R> T postForEntity(Class<T> clazz, String url, R requestBody, Object... uriVariables) throws RestCoreException{
        HttpEntity<R> request = new HttpEntity<>(requestBody);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }

    public <T, R> T putForEntity(Class<T> clazz, String url, R requestBody, Object... uriVariables) throws RestCoreException{
        HttpEntity<R> request = new HttpEntity<>(requestBody);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class, uriVariables);
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        return readValue(response, javaType);
    }

    public void delete(String url, Object... uriVariables) throws RestCoreException{
        restTemplate.delete(url, uriVariables);
    }

    private <T> T readValue(ResponseEntity<String> response, JavaType javaType) throws RestCoreException{
        T result = null;
        if (response.getStatusCode() == HttpStatus.OK ||
                response.getStatusCode() == HttpStatus.CREATED) {
            try {
                result = objectMapper.readValue(response.getBody(), javaType);
            } catch (JsonProcessingException e) {
                throw new RestCoreException(ErrorCode.PARSE_EXCEPTION,e.getMessage());
            }
        } else {
            logger.info("No data found {}", response.getStatusCode());
        }
        return result;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }

    private List<ClientHttpRequestInterceptor> getInterceptors(){
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (interceptors.isEmpty()) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new RestTemplateStatusValidatorInterceptor());
        return interceptors;
    }

}