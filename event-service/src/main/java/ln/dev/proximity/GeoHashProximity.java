package ln.dev.proximity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import ln.dev.geohash.Bounds;
import ln.dev.geohash.GeoHash;
import ln.dev.geohash.LatLonCoordinate;
import ln.dev.geohash.Neighbors;
import ln.dev.protos.event.Event;
import ln.dev.proximity.geohash.GeoHashTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Component;

@Component
public class GeoHashProximity implements Proximity<Event, String> {

    protected GeoHashTree<String> subscriberIdTree;

    public GeoHashProximity(
            @Value("${proximity.geohash.precision:6}") Integer geoHashPrecision,
            @Value("${proximity.geohash.node.capacity:1000}") Integer maxNodeCapacity) {
        this.subscriberIdTree = new GeoHashTree<>(geoHashPrecision, maxNodeCapacity);
    }

    private enum Quadrant {
        NORTH_EAST,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST;
    }

    /**
     * Finds quadrant in which the given point lies when that bounds is divided into 4 quadrants
     * @param hashBounds Bounds of current block
     * @param point location
     * @return Quadrant
     */
    private Quadrant findQuadrant(Bounds hashBounds, LatLonCoordinate point) {
        LatLonCoordinate centerOfBound = new LatLonCoordinate(
                (hashBounds.getSouthEast().getLatitude()
                                + hashBounds.getNorthWest().getLatitude())
                        / 2,
                (hashBounds.getSouthEast().getLongitude()
                                + hashBounds.getNorthWest().getLongitude())
                        / 2);
        boolean isNorthyPoint = point.getLatitude() >= centerOfBound.getLatitude();
        boolean isEastyPoint = point.getLongitude() >= centerOfBound.getLongitude();

        if (isNorthyPoint && isEastyPoint) {
            return Quadrant.NORTH_EAST;
        } else if (isNorthyPoint) {
            return Quadrant.NORTH_WEST;
        } else if (isEastyPoint) {
            return Quadrant.SOUTH_EAST;
        } else return Quadrant.SOUTH_WEST;
    }

    /**
     * Adds to subscriber list
     * @param element Element to add
     * @param coordinate coordinate of element
     */
    public void watch(String element, LatLonCoordinate coordinate) {
        this.subscriberIdTree.add(element, coordinate);
    }

    /**
     * Removes from subscriber list
     * @param element Element to remove
     * @param coordinate coordinate of element
     */
    public void unwatch(String element, LatLonCoordinate coordinate) {
        this.subscriberIdTree.remove(element, coordinate);
    }

    /**
     * Finds all the subscribers in given proximity
     * @param publishedEvent Event in whose proximity, subscribers will be found
     * @param delta Radius to publish
     * @param metrics Metric
     * @return List of subscribers who are in proximity
     */
    @Override
    public List<String> findAllInProximity(Event publishedEvent, double delta, Metrics metrics) {
        int neededPrecision = GeoHash.precisionRequired(delta * metrics.getMultiplier());
        LatLonCoordinate eventLocation = new LatLonCoordinate(publishedEvent.getLocation());
        String geoHashOfEvent = GeoHash.encode(eventLocation, neededPrecision);
        Quadrant eventQuadrant = findQuadrant(GeoHash.getBounds(geoHashOfEvent), eventLocation);

        Neighbors neighbors = GeoHash.findNeighbors(geoHashOfEvent);

        List<String> neighborsToSearch = new ArrayList<>();

        switch (eventQuadrant) {
            case SOUTH_WEST -> neighborsToSearch.addAll(
                    List.of(neighbors.getSouth(), neighbors.getWest(), neighbors.getSouthWest()));
            case SOUTH_EAST -> neighborsToSearch.addAll(
                    List.of(neighbors.getSouth(), neighbors.getEast(), neighbors.getSouthEast()));
            case NORTH_EAST -> neighborsToSearch.addAll(
                    List.of(neighbors.getNorth(), neighbors.getEast(), neighbors.getNorthEast()));
            case NORTH_WEST -> neighborsToSearch.addAll(
                    List.of(neighbors.getNorth(), neighbors.getWest(), neighbors.getNorthWest()));
        }

        List<String> elements =
                subscriberIdTree.findLCAByGeoHash(geoHashOfEvent).getDescendantElements();

        elements.addAll(neighborsToSearch.stream()
                .flatMap(neighborsToSearchHash -> {
                    var node = subscriberIdTree.findNodeByGeoHash(neighborsToSearchHash);
                    if (node.isPresent()) return node.get().getDescendantElements().stream();
                    else return Stream.empty();
                })
                .toList());
        return elements;
    }
}
