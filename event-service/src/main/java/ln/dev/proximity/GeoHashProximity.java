package ln.dev.proximity;

import ln.dev.geohash.Bounds;
import ln.dev.geohash.GeoHash;
import ln.dev.geohash.LatLonCoordinate;
import ln.dev.geohash.Neighbors;
import ln.dev.protos.event.Event;
import ln.dev.proximity.geohash.GeoHashTree;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GeoHashProximity implements Proximity<Event, String> {

    protected GeoHashTree<String> subscriberIdTree;

    private enum Quadrant {
        NORTH_EAST,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST;
    }

    private Quadrant findQuadrant(Bounds hashBounds, LatLonCoordinate point) {
        LatLonCoordinate centerOfBound = new LatLonCoordinate(
                (hashBounds.getSouthEast().getLatitude() + hashBounds.getNorthWest().getLatitude()) / 2,
                (hashBounds.getSouthEast().getLongitude() + hashBounds.getNorthWest().getLongitude()) / 2
        );
        boolean isNorthyPoint = point.getLatitude() >= centerOfBound.getLatitude();
        boolean isEastyPoint = point.getLongitude() >= centerOfBound.getLongitude();

        if(isNorthyPoint && isEastyPoint){
            return Quadrant.NORTH_EAST;
        } else if (isNorthyPoint) {
            return Quadrant.NORTH_WEST;
        } else if (isEastyPoint) {
            return Quadrant.SOUTH_EAST;
        } else return Quadrant.SOUTH_WEST;
    }

    @Override
    public List<String> findAllInProximity(Event publishedEvent, double delta, Metrics metrics) {

        int neededPrecision = GeoHash.precisionRequired(
                delta * metrics.getMultiplier()
        );
        LatLonCoordinate eventLocation =  new LatLonCoordinate(publishedEvent.getLocation());
        String geoHashOfEvent = GeoHash.encode(
                eventLocation,
                neededPrecision
        );
        Quadrant eventQuadrant = findQuadrant(GeoHash.getBounds(geoHashOfEvent), eventLocation);

        Neighbors neighbors = GeoHash.findNeighbors(geoHashOfEvent);

        List<String> neighborsToSearch = new ArrayList<>();


        switch (eventQuadrant) {
            case SOUTH_WEST -> neighborsToSearch.addAll(List.of(neighbors.getSouth(), neighbors.getWest(), neighbors.getSouthWest()));
            case SOUTH_EAST -> neighborsToSearch.addAll(List.of(neighbors.getSouth(), neighbors.getEast(), neighbors.getSouthEast()));
            case NORTH_EAST -> neighborsToSearch.addAll(List.of(neighbors.getNorth(), neighbors.getEast(), neighbors.getNorthEast()));
            case NORTH_WEST -> neighborsToSearch.addAll(List.of(neighbors.getNorth(), neighbors.getWest(), neighbors.getNorthWest()));
        }

        return neighborsToSearch.stream()
                .flatMap(neighborsToSearchHash -> subscriberIdTree.findElementsByGeoHash(neighborsToSearchHash).stream())
                .collect(Collectors.toList());
    }
}
