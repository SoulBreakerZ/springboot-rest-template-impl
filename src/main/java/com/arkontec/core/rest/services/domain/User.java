package com.arkontec.core.rest.services.domain;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -736559163130677022L;

    private Long id;
    private String firstname;
    private String lastname;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
