package com.example.monisia.tracker;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by Monisia on 1/6/2018.
 */

public class ParentDto {

    @JsonIgnore
    private Long id;
    private String firstName;
    private String lastName;
    private List<ChildDto> childs;
    private LoginDto user;
}
