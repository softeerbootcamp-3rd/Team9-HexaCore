package com.hexacore.tayo.car.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Getter
@AllArgsConstructor
public class PositionDto {

    final private int SRID = 4326;

    private Double lat;
    private Double lng;

    public Point toEntity() {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point position = geometryFactory.createPoint(new Coordinate(lat, lng));
        position.setSRID(SRID);
        return position;
    }
}
