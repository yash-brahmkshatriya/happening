package ln.dev.proximity.geohash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Data
@Builder
@AllArgsConstructor
public class GeoHashNode<D> {
    @AllArgsConstructor
    private static class GeoHashElement<D> {
        D element;
        String geoHash;
    }

    @Value("${proximity.geohash.node.capacity:1000}")
    private int MAX_NODE_CAPACITY;

    private char value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<GeoHashElement<D>> elements;

    @Setter(AccessLevel.NONE)
    private List<GeoHashNode<D>> children;

    private final int level;

    public GeoHashNode(char value, int level) {
        this.value = value;
        this.elements = new ArrayList<>();
        this.level = level;
        boolean isLeaf = this.level == GeoHashTree.MAX_TREE_HEIGHT;
        if (isLeaf) this.children = Collections.emptyList();
        else this.children = new ArrayList<>();
    }

    public void add(D element, String geoHash) {
        this.elements.add(new GeoHashElement<>(element, geoHash));
        if (this.elements.size() >= MAX_NODE_CAPACITY) {
            this.split();
        }
    }

    public void remove(D element) {
        this.elements.removeIf((ele) -> ele.element == element);
    }

    public void split() {
        this.elements = this.elements.stream()
                .filter(elementWithHash -> {
                    if (this.level == elementWithHash.geoHash.length()) return true;

                    int nextLevel = level + 1;
                    char nextLevelChar = elementWithHash.geoHash.charAt(nextLevel);
                    Optional<GeoHashNode<D>> optionalChild = this.children.stream()
                            .filter(child -> child.getValue() == nextLevelChar)
                            .findFirst();
                    GeoHashNode<D> child = optionalChild.orElse(new GeoHashNode<>(nextLevelChar, nextLevel));
                    if (optionalChild.isEmpty()) this.children.add(child);
                    child.add(elementWithHash.element, elementWithHash.geoHash);
                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<D> getElements() {
        return this.elements.stream()
                .map(elementWithHash -> elementWithHash.element)
                .collect(Collectors.toList());
    }

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
