package ln.dev.proximity.geohash;

import static org.junit.jupiter.api.Assertions.*;

import ln.dev.geohash.LatLonCoordinate;
import org.junit.jupiter.api.Test;

class GeoHashTreeTest {

    @Test
    void invalidTreeIni() {
        assertThrows(IllegalArgumentException.class, () -> new GeoHashTree<String>(-1, 10));
        assertThrows(
                IllegalArgumentException.class, () -> new GeoHashTree<String>(GeoHashTree.MAX_TREE_HEIGHT + 1, 10));
    }

    @Test
    void findLCAByGeoHash_Invalid() {
        GeoHashTree<Integer> tree = new GeoHashTree<>(3, 1);
        assertThrows(IllegalArgumentException.class, () -> tree.findLCAByGeoHash("a"));
    }

    @Test
    void findLCAByGeoHash() {
        GeoHashTree<Integer> tree = new GeoHashTree<>(3, 1);
        tree.add(1, "01");
        tree.add(1, "1b");
        tree.add(1, "01b");
        assertEquals('1', tree.findLCAByGeoHash("01").getValue());
        assertEquals('b', tree.findLCAByGeoHash("01bb").getValue());
        assertEquals('*', tree.findLCAByGeoHash("c").getValue());
    }

    @Test
    void findNodeByGeoHash_MoreCapacity() {
        GeoHashTree<Integer> tree = new GeoHashTree<>(3, 100);
        tree.add(1, "01");
        tree.add(2, "1b");
        tree.add(3, "01b");
        assertTrue(tree.findNodeByGeoHash("01b").isEmpty());
        assertTrue(tree.findNodeByGeoHash("01w").isEmpty());
    }

    @Test
    void findNodeByGeoHash_LessCapacity() {
        GeoHashTree<Integer> tree = new GeoHashTree<>(3, 1);
        tree.add(1, "0");
        tree.add(2, "1bbb");
        tree.add(3, "01");
        tree.add(4, "01b");
        tree.add(5, "01b");
        assertTrue(tree.findNodeByGeoHash("01b").isPresent());
        assertTrue(tree.findNodeByGeoHash("1").isPresent());
        assertTrue(tree.findNodeByGeoHash("1b").isEmpty());
        assertTrue(tree.findNodeByGeoHash("01w").isEmpty());
    }

    @Test
    void add() {
        GeoHashTree<Integer> tree = new GeoHashTree<>(3, 1);
        tree.add(1, LatLonCoordinate.builder().latitude(1).longitude(1).build());
        assertFalse(tree.findLCAByGeoHash("00000").getDescendantElements().isEmpty());
    }

    @Test
    void remove() {
        GeoHashTree<Integer> tree = new GeoHashTree<>(3, 1);
        LatLonCoordinate coordinate =
                LatLonCoordinate.builder().latitude(1).longitude(1).build();
        tree.add(1, coordinate);
        tree.remove(2, coordinate);
        assertFalse(tree.findLCAByGeoHash("00000").getDescendantElements().isEmpty());
        tree.remove(1, coordinate);
        assertTrue(tree.findLCAByGeoHash("00000").getDescendantElements().isEmpty());
    }
}
