package com.precisionhawk.latas.client.util;

import com.precisionhawk.latas.client.model.HasLocation;
import com.precisionhawk.latas.client.model.Location;
import com.precisionhawk.latas.client.model.ViewPort;

public class ViewPortExtent {

    private double minX, maxX, minY, maxY;
    private boolean hasAny;

    public void add(HasLocation hasLocation) {
        Location loc = hasLocation.getLocation();
        double lon = loc.getLon();
        double lat = loc.getLat();
        add(lat, lon);
    }

    public void add(double lat, double lon) {
        if (!hasAny) {
            minX = maxX = lon;
            minY = maxY = lat;
            hasAny = true;
        } else {
            if (lon < minX) {
                minX = lon;
            } else if (lon > maxX) {
                maxX = lon;
            }
            if (lat < minY) {
                minY = lat;
            } else if (lat > maxY) {
                maxY = lat;
            }
        }
    }

    public ViewPort getViewPortWithPadding(double padding) {
        Location bottomLeft = new Location(minY - padding, minX - padding);
        Location topRight = new Location(maxY + padding, maxX + padding);
        ViewPort viewPort = new ViewPort(bottomLeft, topRight);
        System.out.println("viewPort = " + viewPort);
        return viewPort;
    }
}
