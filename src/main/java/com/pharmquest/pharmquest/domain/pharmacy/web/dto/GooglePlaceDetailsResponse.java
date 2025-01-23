package com.pharmquest.pharmquest.domain.pharmacy.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GooglePlaceDetailsResponse {

    private String status;
    private Result result;

    public String getStatus() {
        return status;
    }

    public Result getResult() {
        return result == null ? new Result() : result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        private List<String> types;
        private String name;
        private String formattedAddress;
        private OpeningHours openingHours;
        private Location geometry;
        private List<AddressComponent> addressComponents;

        public List<String> getTypes() {
            return types;
        }

        @JsonProperty("name")
        public String getName() {
            return name == null ? "Unknown" : name;
        }

        @JsonProperty("formatted_address")
        public String getFormattedAddress() {
            return formattedAddress == null ? "Unknown" : formattedAddress;
        }

        @JsonProperty("opening_hours")
        public OpeningHours getOpeningHours() {
            return openingHours == null ? new OpeningHours() : openingHours;
        }

        @JsonProperty("geometry")
        public Location getGeometry() {
            return geometry == null ? new Location() : geometry;
        }

        @JsonProperty("address_components")
        public List<AddressComponent> getAddressComponents() {
            return addressComponents == null ? List.of() : addressComponents;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressComponent {

        private String longName;
        private String shortName;
        private List<String> types;

        @JsonProperty("long_name")
        public String getLongName() {
            return longName == null ? "" : longName;
        }

        @JsonProperty("short_name")
        public String getShortName() {
            return shortName == null ? "" : shortName;
        }

        @JsonProperty("types")
        public List<String> getTypes() {
            return types == null ? List.of() : types;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpeningHours {

        private Boolean openNow;
        private List<Period> periods;

        @JsonProperty("open_now")
        public Boolean getOpenNow() {
            return openNow;
        }

        @JsonProperty("periods")
        public List<Period> getPeriods() {
            return periods;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Period {

        private Time open;
        private Time close;

        @JsonProperty("open")
        public Time getOpen() {
            return open == null ? new Time() : open;
        }

        @JsonProperty("close")
        public Time getClose() {
            return close == null ? new Time() : close;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Time {

        private Integer day;
        private String time;

        @JsonProperty("day")
        public Integer getDay() {
            return day == null ? 0 : day;
        }

        @JsonProperty("time")
        public String getTime() {
            return time == null ? "" : time;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class Location {

        private Coordinates location;

        public Coordinates getLocation() {
            return location == null ? new Coordinates() : location;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coordinates {

        private Double lat;
        private Double lng;

        @JsonProperty("lat")
        public Double getLat() {
            return lat == null ? 0 : lat;
        }

        @JsonProperty("lng")
        public Double getLng() {
            return lng == null ? 0 : lng;
        }
    }
}
