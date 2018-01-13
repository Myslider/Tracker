package com.example.monisia.tracker;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Monisia on 1/6/2018.
 */

public class UserDto {

    @JsonIgnore
    public Long id;
    public String username;
    public String password;
    public Boolean isParent;
    public String firstName;
    public String lastName;
    @JsonIgnore
    public Long personId;
}
