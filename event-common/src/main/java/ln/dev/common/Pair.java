package ln.dev.common;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pair<T, U> {
    T first;
    U second;
}
