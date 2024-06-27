package ln.dev.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Location {

    private String type;

    private List<Double> coordinates;
}
