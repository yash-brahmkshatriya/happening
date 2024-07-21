package ln.dev.proximity.geohash;


import ln.dev.geohash.Base32;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class GeoHashNode<D> {

    private char value;

    private List<D> elements;

    @Setter(AccessLevel.NONE)
    private List<GeoHashNode<D>> children;

    private int level;

    public GeoHashNode(char value, int level, boolean willHaveChildren) {
        this.value = value;
        this.elements = new ArrayList<>();
        this.level = level;
        this.initializeChildren(willHaveChildren);
    }

    private void initializeChildren(boolean willHaveChildren) {
        if(willHaveChildren) {
            this.children = new ArrayList<>();
        } else {
            this.children = Collections.emptyList();
        }
    }

    public void produceChildren(boolean willHaveChildren) {
        Base32.getBase32Characters()
                .forEach(character -> {
                    this.children.add(new GeoHashNode<>(character, this.level + 1, willHaveChildren));
                });
    }

    public List<D> getDescendantElements() {
        List<D> elements = new ArrayList<>(this.elements);
        this.getChildren()
                .stream()
                .flatMap(child -> child.getDescendantElements().stream())
                .forEach(elements::add);
        return elements;
    }
}
