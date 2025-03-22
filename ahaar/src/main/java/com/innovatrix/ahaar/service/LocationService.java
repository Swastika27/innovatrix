package com.innovatrix.ahaar.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public Point createPoint(double latitude, double longitude) {
        // Note: Coordinate constructor takes (x, y) where x=longitude and y=latitude
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}

