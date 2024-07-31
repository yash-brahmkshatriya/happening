package ln.dev.proximity.geohash;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.IntStream;
import ln.dev.geohash.Base32;
import ln.dev.geohash.GeoHash;
import ln.dev.geohash.LatLonCoordinate;

public class GeoHashTree<D> {

    private final GeoHashNode<D> root;

    private final int precision;

    public GeoHashTree(int requiredLevels) {
        if (requiredLevels < 0) throw new IllegalArgumentException("GeoHash Tree cannot have negative levels");

        this.precision = requiredLevels;
        this.root = new GeoHashNode<>('*', 0, true);

        Queue<GeoHashNode<D>> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);

        while (!nodeQueue.isEmpty()) {
            GeoHashNode<D> node = nodeQueue.poll();
            boolean willNodeHaveChildren = node.getLevel() + 1 <= this.precision;
            node.produceChildren(willNodeHaveChildren);
            nodeQueue.addAll(node.getChildren());
        }
    }

    public GeoHashNode<D> findNodeByGeoHash(String geoHash) {
        Queue<Character> hashQueue = new LinkedList<>();

        IntStream.range(0, geoHash.length()).mapToObj(geoHash::charAt).forEach(hashQueue::add);

        GeoHashNode<D> node = root;

        while (!hashQueue.isEmpty()) {
            char hashBlockValue = hashQueue.poll();
            Optional<GeoHashNode<D>> nextChild = node.getChildren().stream()
                    .filter(child -> child.getValue() == hashBlockValue)
                    .findFirst();

            if (nextChild.isEmpty()) throw new RuntimeException("Child not found");
            node = nextChild.get();
        }

        return node;
    }

    public void add(D element, LatLonCoordinate coordinate) {
        String geoHash = GeoHash.encode(coordinate, this.precision);
        findNodeByGeoHash(geoHash).getElements().add(element);
    }

    public void remove(D element, LatLonCoordinate coordinate) {
        String geoHash = GeoHash.encode(coordinate, this.precision);
        findNodeByGeoHash(geoHash).getElements().remove(element);
    }

    public List<D> findElementsByGeoHash(String geoHash) {
        if (!Base32.isValidBase32String(geoHash)) throw new IllegalArgumentException("Geohash invalid");
        // TODO: Log warning that it may loose precision if len(hash) > this.precision

        char[] hashChars = geoHash.substring(0, this.precision).toLowerCase().toCharArray();

        Queue<Character> hashQueue = new LinkedList<>();

        IntStream.range(0, hashChars.length).mapToObj(i -> hashChars[i]).forEach(hashQueue::add);

        GeoHashNode<D> node = root;

        while (!hashQueue.isEmpty()) {
            char hashBlockValue = hashQueue.poll();
            Optional<GeoHashNode<D>> nextChild = node.getChildren().stream()
                    .filter(child -> child.getValue() == hashBlockValue)
                    .findFirst();

            if (nextChild.isEmpty()) throw new RuntimeException("Child not found");
            node = nextChild.get();
        }

        return node.getDescendantElements();
    }
}
