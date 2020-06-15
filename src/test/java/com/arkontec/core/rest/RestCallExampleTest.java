package com.arkontec.core.rest;

import com.arkontec.core.rest.application.AppConfig;
import com.arkontec.core.rest.application.exception.ErrorCode;
import com.arkontec.core.rest.application.exception.RestCoreException;
import com.arkontec.core.rest.services.helpers.*;
import com.arkontec.core.rest.services.domain.ResponseWrapper;
import com.arkontec.core.rest.services.domain.User;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class })
public class RestCallExampleTest {

    private static final Logger logger = LogManager.getLogger(RestCallExampleTest.class);

    @Autowired
    private RestTemplateHelper restTemplateHelper;

    private String resource = "http://localhost:3000/users";


    @Test
    public void getForEntityWithPathVariable(){
        Long id = 1L;
        String path = "/{id}";
        String url = buildUrl(resource, path);
        User user = restTemplateHelper.getForEntity(User.class, url, id);
        Assertions.assertNotNull(user);
        Assertions.assertSame(User.class,user.getClass());
    }

    @Test
    public void getForEntityWithUrlParametersSimple(){
        Long id = 1L;
        String path = "/search?id={id}";
        String url = buildUrl(resource, path);
        User user = restTemplateHelper.getForEntity(User.class, url, id);
        Assertions.assertNotNull(user);
        Assertions.assertSame(User.class,user.getClass());
    }

    @Test
    @Disabled
    public void getForEntityWithPathVariableAndUrlParameterObject(){
        String json = "{limit:1,offset:2}";
        String path = "/4?includeMetadata={json}";
        String url = buildUrl(resource, path);
        User user = restTemplateHelper.getForEntity(User.class, url, json);
        Assertions.assertSame(User.class,user.getClass());
    }

    @Test
    public void getForEntityResponseWrapperObject(){
        Long id = 5L;
        String path = "/{id}";
        String url = buildUrl(resource, path);
        ResponseWrapper<User> responseWrapper = restTemplateHelper.getForResponseWrapper(User.class, url,id);
        Assertions.assertNotNull(responseWrapper);
        Assertions.assertSame(ResponseWrapper.class,responseWrapper.getClass());
    }

    @Test
    public void getForEntityResponseWrapperList(){
        String path = "/page?limit=10&offset=1";
        String url = buildUrl(resource, path);

        ResponseWrapper<List> responseWrapper = restTemplateHelper.getForResponseWrapper(List.class, url);
        Assertions.assertNotNull(responseWrapper);
        Assertions.assertSame(ResponseWrapper.class,responseWrapper.getClass());
        Assertions.assertNotNull(responseWrapper.getPayload());
        List<User> users = (List<User>) responseWrapper.getPayload();
        Assertions.assertTrue(responseWrapper.getPayload().size() > 0);
    }

    @Test
    public void getForEntityResponse404() {
        RestCoreException exception = assertThrows(RestCoreException.class, () -> {
            Long id = 6L;
            String path = "notfound/{id}";
            String url = buildUrl(resource, path);
            restTemplateHelper.getForEntity(User.class, url, id);
        });
        assertTrue(exception.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void getForEntityWithHostException() {
        ResourceAccessException exception = assertThrows(ResourceAccessException.class, () -> {
            Long id = 2L;
            String hostNotExists = "http://localhost:8000/users";
            restTemplateHelper.getForEntity(User.class, hostNotExists, id);
        });
        assertTrue(exception.getCause() instanceof HttpHostConnectException);
    }

    @Test
    public void getForEntityResponse4xx() {
        RestCoreException exception = assertThrows(RestCoreException.class, () -> {
            Long id = 2L;
            String path = "/{id}";
            String url = buildUrl(resource, path);
            restTemplateHelper.getForEntity(User.class, url, id);
        });
        assertTrue(exception.getStatusCode().is4xxClientError());
    }

    @Test
    public void getForEntityWithParseException() {
        RestCoreException exception = assertThrows(RestCoreException.class, () -> {
            Long id = 4L;
            String path = "/{id}";
            String url = buildUrl(resource, path);
            restTemplateHelper.getForEntity(User.class, url, id);
        });
        assertTrue(exception.getErrorCode() == ErrorCode.PARSE_EXCEPTION);
    }

    @Test
    public void getForEntityResponse5xx() {
        RestCoreException exception = assertThrows(RestCoreException.class, () -> {
            Long id = 3L;
            String path = "/{id}";
            String url = buildUrl(resource, path);
            restTemplateHelper.getForEntity(User.class, url, id);
        });
        assertTrue(exception.getStatusCode().is5xxServerError());
    }

    @Test
    public void getForList(){
        String url = resource;
        List<User> list = restTemplateHelper.getForList(User.class, url);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.size() > 0);
    }

    @Test
    public void getForEmptyList(){
        String path = "/empty";
        String url = buildUrl(resource, path);
        List<User> list = restTemplateHelper.getForList(User.class, url);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.size() == 0);
    }

    @Test
    public void postForEntity(){
        User user = new User();
        user.setFirstname("Mario");
        user.setLastname("Neta");
        user.setStatus("");
        String url = resource;
        User createdUser = restTemplateHelper.postForEntity(User.class,url,user);
        Assertions.assertNotNull(createdUser);
        Assertions.assertSame(User.class,createdUser.getClass());
    }

    @Test
    public void putForEntity(){

        /*String url = "http://test.com/solarSystem/planets/{planet}/moons/{moon}";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("planets", "Mars");
        urlParams.put("moons", "Phobos");

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                // Add query parameter
                .queryParam("firstName", "Mark")
                .queryParam("lastName", "Watney");  */
        User user = new User();
        user.setId(4L);
        user.setFirstname("Mario");
        user.setLastname("Neta");
        user.setStatus("");
        String url = resource;
        User modifiedUser = restTemplateHelper.putForEntity(User.class,url,user);

        Assertions.assertNotNull(modifiedUser);
        Assertions.assertSame(User.class,modifiedUser.getClass());
    }

    @Test
    public void delete(){
        Long id = 1L;
        String path = "/{id}";
        String url = buildUrl(resource,path);
        restTemplateHelper.delete(url, id);
    }

    @Test
    public void deleteWithInvalidResponse(){
        RestCoreException exception = assertThrows(RestCoreException.class, () -> {
            Long id = 2L;
            String path = "/{id}";
            String url = buildUrl(resource, path);
            restTemplateHelper.delete(url, id);
        });
        assertTrue(exception.getStatusCode() != HttpStatus.NO_CONTENT);

    }

    private String buildUrl(String resource, String path) {
        StringBuilder str = new StringBuilder();
        str.append(resource).append(path);
        return str.toString();
    }


}
