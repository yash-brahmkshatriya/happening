package ln.dev.util;


import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IdGenerator {

    public static String generate() {
        return NanoIdUtils.randomNanoId();
    }

}
