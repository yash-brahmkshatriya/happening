package ln.dev.proximity;

import java.util.List;

/**
 * Proximity Interface with coordinate system U
 */
public interface Proximity<U, V> {

    List<V> findAllInProximity(U u, double delta);

}
