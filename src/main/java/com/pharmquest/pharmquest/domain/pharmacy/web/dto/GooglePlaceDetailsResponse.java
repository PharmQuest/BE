package com.pharmquest.pharmquest.domain.pharmacy.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
        private List<Photo> photos;

        public List<String> getTypes() {
            return types;
        }

        @JsonProperty("name")
        public String getName() {
            return name == null ? "이름 미제공" : name;
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

        @JsonProperty("photos") // Google Place API의 "photos" 필드 매핑
        public List<Photo> getPhotos() {
            return photos == null ? List.of() : photos;
        }

        // 지역 목록( 범위별 명칭 )을 리스트에 담아 반환
        public List<String> getLocationList() {

            if (addressComponents == null || addressComponents.isEmpty()) {
                // 정보 미제공 시 빈 List 반환. 이후에 대체 문자열로 변환
                return new ArrayList<>();
            }

            // 지역 키워드 중 political 가진 country가 아닌 키워드 추출하여 list에 저장
            List<String> locationList = addressComponents.stream()
                    .filter(component -> component.getTypes().contains("political") && !component.getTypes().contains("country"))
                    .map(AddressComponent::getLongName)
                    .map(String::trim)
                    .toList();

            int size = locationList.size();
            if (size == 0) {
                // 마땅한 정보 없을 시 빈 List 반환. 이후에 대체 문자열로 변환
                return new ArrayList<>();
            }

            // 지역 키워드를 규모 큰 순서로 3개 이하가 되도록 설정
            locationList =  locationList.subList(Math.max(0, size - 3), size);
            return locationList;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressComponent {

        private String longName;
        private String shortName;
        private List<String> types;

        @JsonProperty("long_name")
        public String getLongName() {
            return longName == null ? "위치 미제공" : longName;
        }

        @JsonProperty("short_name")
        public String getShortName() {
            return shortName == null ? "위치 미제공" : shortName;
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
            return open;
        }

        @JsonProperty("close")
        public Time getClose() {
            return close;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Time {

        private Integer day;
        private String time;

        @JsonProperty("day")
        public Integer getDay() {
            return day == null ? null : day;
        }

        @JsonProperty("time")
        public String getTime() {
            return time == null ? null : time;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photo {

        private int height;
        private int width;
        private String photoReference;
        private List<String> htmlAttributions;

        @JsonProperty("height")
        public int getHeight() {
            return height;
        }

        @JsonProperty("width")
        public int getWidth() {
            return width;
        }

        @JsonProperty("photo_reference")
        public String getPhotoReference() {
            return photoReference == null ? "" : photoReference;
        }

        @JsonProperty("html_attributions")
        public List<String> getHtmlAttributions() {
            return htmlAttributions == null ? List.of() : htmlAttributions;
        }
    }
}
