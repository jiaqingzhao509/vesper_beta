package com.example.auth.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tycoding
 * @since 2024/5/31
 */
@Data
public class OpencageData implements Serializable {
    private List<OpencageItem> results;

    @Data
    public static class OpencageItem {
        private String formatted;
        private Geometry geometry;
    }

    @Data
    public static class Geometry {
        private Double lat;
        private Double lng;
    }

}
