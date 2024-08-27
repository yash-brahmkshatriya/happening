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

    public final int MAX_NODE_CAPACITY;

    private final GeoHashNode<D> root;

    private final int precision;

    public GeoHashTree(int requiredLevels, int maxNodeCapacity) {
        if (requiredLevels < 0 || requiredLevels > MAX_TREE_HEIGHT)
            throw new IllegalArgumentException("GeoHash Tree levels should be between 0 and " + MAX_TREE_HEIGHT);

        this.precision = requiredLevels;
        this.MAX_NODE_CAPACITY = maxNodeCapacity;
        this.root = new GeoHashNode<>('*', -1, MAX_NODE_CAPACITY);
    }

    /**
     * Return the closest ancestor of the given hash present in tree. <br />
     * Example: Ancestor of path '01b' can be '01b', '01', '0', or ROOT based on the node capacity.
     * @param geoHash - hash of which ancestor is supposed to be found
     * @return Closest ancestor node
     */
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

    /**
     * Returns the exact node present in tree (if any). <br />
     * Example: It will return node for '01b' only if there is a node with path '01b'.
     * @param geoHash - hash of which node is supposed to be found
     * @return Exact node by hash
     */
    public Optional<GeoHashNode<D>> findNodeByGeoHash(String geoHash) {
        GeoHashNode<D> lcaOfNode = findLCAByGeoHash(geoHash);
        if (lcaOfNode.getLevel() + 1 == geoHash.length()) return Optional.of(lcaOfNode);
        else return Optional.empty();
    }

    /**
     * Adds the element to the tree
     * @param element Element to add
     * @param coordinate coordinate of element
     */
    public void add(D element, LatLonCoordinate coordinate) {
        String geoHash = GeoHash.encode(coordinate, this.precision);
        add(element, geoHash);
    }

    /**
     * Adds the element to the tree
     * @param element Element to add
     * @param geoHash geohash of element
     */
    public void add(D element, String geoHash) {
        findLCAByGeoHash(geoHash).add(element, geoHash);
    }

    /**
     * Removes the element from tree
     * @param element Element to remove
     * @param coordinate coordinate of element
     */
    public void remove(D element, LatLonCoordinate coordinate) {
        String geoHash = GeoHash.encode(coordinate, this.precision);
        findLCAByGeoHash(geoHash).remove(element);
    }
}
