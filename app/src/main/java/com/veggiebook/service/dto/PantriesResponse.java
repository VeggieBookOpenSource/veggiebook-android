package com.veggiebook.service.dto;


public class PantriesResponse {
    private String name;
    private String id;
    private String address;
    private Double distance;
    private Double lat;
    private Double lon;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public Double getDistance() {
        return distance;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

}
