package com.hexacore.tayo.car.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Getter
@AllArgsConstructor
public class PositionDto {

    private Double lat;
    private Double lng;

    public Point toEntity() {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(this.lng, this.lat));
    }
}
