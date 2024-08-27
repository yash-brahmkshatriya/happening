package ln.dev.proximity.geohash;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

// TODO: update class to use test properties
class GeoHashNodeTest {

    @Test
    void add() {
        GeoHashNode<Integer> geoHashNode = new GeoHashNode<>('$', 0, 1);
        geoHashNode.add(1, "0");
        assertEquals(1, geoHashNode.getElements().size());

        geoHashNode.add(2, "02");
        assertEquals(1, geoHashNode.getElements().size());
        assertEquals(1, geoHashNode.getChildren().size());
    }

    @Test
    void remove() {
        GeoHashNode<Integer> geoHashNode = new GeoHashNode<>('$', 0, 3);
        geoHashNode.add(5, "01");
        geoHashNode.add(10, "01");
        geoHashNode.add(5, "01b");
        geoHashNode.remove(5);
        assertEquals(1, geoHashNode.getElements().size());
    }

    @Test
    void split() {
        GeoHashNode<Integer> geoHashNode = new GeoHashNode<>('$', 0, 5);
        geoHashNode.add(5, "0");
        geoHashNode.add(10, "01");
        geoHashNode.add(10, "02");
        geoHashNode.add(10, "01");
        geoHashNode.add(5, "0b");
        geoHashNode.add(5, "03");

        assertEquals(1, geoHashNode.getElements().size());
        assertEquals(4, geoHashNode.getChildren().size());
        assertEquals(6, geoHashNode.getDescendantElements().size());
    }
}
