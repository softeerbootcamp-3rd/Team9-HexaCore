package com.hexacore.tayo.car.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Getter
@AllArgsConstructor
public class CreatePositionRequestDto {

    private Double lat;
    private Double lng;

    public Point toEntity() {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(this.lng, this.lat));
    }
}
