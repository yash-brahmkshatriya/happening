package ln.dev.proximity.geohash;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.IntStream;
import ln.dev.geohash.Base32;
import ln.dev.geohash.GeoHash;
import ln.dev.geohash.LatLonCoordinate;

public class GeoHashTree<D> {

    public static final int MAX_TREE_HEIGHT = 6;

    private final GeoHashNode<D> root;

    private final int precision;

    public GeoHashTree(int requiredLevels) {
        if (requiredLevels < 0 || requiredLevels > MAX_TREE_HEIGHT)
            throw new IllegalArgumentException("GeoHash Tree levels should be between 0 and " + MAX_TREE_HEIGHT);

        this.precision = requiredLevels;
        this.root = new GeoHashNode<>('*', -1);
    }

    public GeoHashNode<D> findLCAByGeoHash(String geoHash) {
        if (!Base32.isValidBase32String(geoHash)) throw new IllegalArgumentException("Geohash invalid");
        // TODO: Log warning that it may loose precision if len(hash) > this.precision
        Queue<Character> hashQueue = new LinkedList<>();

        IntStream.range(0, geoHash.length()).mapToObj(geoHash::charAt).forEach(hashQueue::add);

        GeoHashNode<D> node = root;

        while (!hashQueue.isEmpty()) {
            char hashBlockValue = hashQueue.poll();
            Optional<GeoHashNode<D>> nextChild = node.getChildren().stream()
                    .filter(child -> child.getValue() == hashBlockValue)
                    .findFirst();

            if (nextChild.isEmpty()) return node;
            node = nextChild.get();
        }

        return node;
    }

    public Optional<GeoHashNode<D>> findNodeByGeoHash(String geoHash) {
        GeoHashNode<D> lcaOfNode = findLCAByGeoHash(geoHash);
        if (lcaOfNode.getLevel() == geoHash.length()) return Optional.of(lcaOfNode);
        else return Optional.empty();
    }

    public void add(D element, LatLonCoordinate coordinate) {
        String geoHash = GeoHash.encode(coordinate, this.precision);
        findLCAByGeoHash(geoHash).add(element, geoHash);
    }

    public void remove(D element, LatLonCoordinate coordinate) {
        String geoHash = GeoHash.encode(coordinate, this.precision);
        findLCAByGeoHash(geoHash).remove(element);
    }
}
