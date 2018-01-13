package com.example.monisia.tracker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Monisia on 12/28/2017.
 */

public class CoordinateDto {
    @JsonIgnore
    public String id;

    @JsonProperty("length")
    public String longitude;

    @JsonProperty("width")
    public String latitude;

    public String date;

    public String time;

    public String childFirstName;

    public String childLastName;

    public String childId;
}
