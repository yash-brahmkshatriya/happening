package ln.dev.proximity;

import org.springframework.data.geo.Metrics;

import java.util.List;

/**
 * Proximity Interface with coordinate system U
 */
public interface Proximity<U, V> {

    List<V> findAllInProximity(U u, double delta, Metrics metrics);

}
