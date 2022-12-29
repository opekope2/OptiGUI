package opekope2.linker;

/**
 * Interface holding Java primitives
 */
public interface JvmPrimitive {
    Class<Byte> BYTE = byte.class;
    Class<Character> CHAR = char.class;
    Class<Double> DOUBLE = double.class;
    Class<Float> FLOAT = float.class;
    Class<Integer> INT = int.class;
    Class<Long> LONG = long.class;
    Class<Short> SHORT = short.class;
    Class<Boolean> BOOLEAN = boolean.class;
    Class<Void> VOID = void.class;
}
