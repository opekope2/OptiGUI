package opekope2.optigui.extra;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Util {
    private Util() {
    }

    public static <TKey, TValue> Map<TKey, TValue> buildMap(Consumer<Map<TKey, TValue>> mapFiller) {
        Map<TKey, TValue> map = new HashMap<>();
        mapFiller.accept(map);
        return map;
    }
}
