package ln.dev.proximity;

import java.util.List;
import org.springframework.data.geo.Metrics;

/**
 * Proximity Interface with coordinate system U
 */
public interface Proximity<U, V> {

    List<V> findAllInProximity(U u, double delta, Metrics metrics);
}
