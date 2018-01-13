package com.example.monisia.tracker;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Monisia on 1/1/2018.
 */

public class ChildDto {

    public long id;

    @JsonProperty("firstName")
    public String FirstName;

    @JsonProperty("lastName")
    public String LastName;
}
