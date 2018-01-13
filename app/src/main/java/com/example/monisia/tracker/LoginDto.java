package com.example.monisia.tracker;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Monisia on 12/29/2017.
 */

public class LoginDto {
    @JsonProperty("username")
    public String Username;

    @JsonProperty("password")
    public String Password;

    @JsonProperty("isParent")
    public Boolean IsParent;

    public Long id;
}
