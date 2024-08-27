package ln.dev.proximity.geohash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.*;

public class GeoHashNode<D> {
    @AllArgsConstructor
    private static class ElementWithHash<D> {
        D element;
        String geoHash;
    }

    @Getter(AccessLevel.PUBLIC)
    private final char value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<ElementWithHash<D>> elements;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.NONE)
    private List<GeoHashNode<D>> children;

    @Getter(AccessLevel.PUBLIC)
    private final int level;

    private final boolean isLeaf;

    private final int capacity;

    public GeoHashNode(char value, int level, int capacity) {
        this.value = value;
        this.elements = new ArrayList<>();
        this.level = level;
        this.capacity = capacity;
        this.isLeaf = this.level == GeoHashTree.MAX_TREE_HEIGHT;
        if (isLeaf) this.children = Collections.emptyList();
        else this.children = new ArrayList<>();
    }

    /**
     * Adds element to node
     * @param element Element
     * @param geoHash GeoHash of element
     */
    public void add(D element, String geoHash) {
        this.elements.add(new ElementWithHash<>(element, geoHash));
        if (this.elements.size() > capacity && !this.isLeaf) {
            this.split();
        }
    }

    /**
     * Removes element from node
     * @param element Element to remove
     */
    public void remove(D element) {
        this.elements.removeIf((ele) -> ele.element == element);
    }

    /**
     * Splits the node into children based on their geohash value
     */
    public void split() {
        this.elements = this.elements.stream()
                .filter(elementWithHash -> {
                    if (this.level == elementWithHash.geoHash.length() - 1) return true;

                    int nextLevel = this.level + 1;
                    char nextLevelChar = elementWithHash.geoHash.charAt(nextLevel);
                    Optional<GeoHashNode<D>> optionalChild = this.children.stream()
                            .filter(child -> child.getValue() == nextLevelChar)
                            .findFirst();
                    GeoHashNode<D> child = optionalChild.orElse(new GeoHashNode<>(nextLevelChar, nextLevel, capacity));
                    if (optionalChild.isEmpty()) this.children.add(child);
                    child.add(elementWithHash.element, elementWithHash.geoHash);
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * @return elements of the node
     */
    public List<D> getElements() {
        return this.elements.stream()
                .map(elementWithHash -> elementWithHash.element)
                .collect(Collectors.toList());
    }

    /**
     * @return elements of the node and all the descendants of it
     */
    public List<D> getDescendantElements() {
        List<D> elements = this.elements.stream()
                .map(elementWithHash -> elementWithHash.element)
                .collect(Collectors.toList());

        this.getChildren().stream()
                .flatMap(child -> child.getDescendantElements().stream())
                .forEach(elements::add);
        return elements;
    }
}
