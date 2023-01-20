package opekope2.linker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
class DescriptorTest {
    @Test
    void ofMethodTest() {
        assertEquals("()I", Descriptor.ofMethod(int.class)); // int Object.hashCode()
        assertEquals("()Ljava/lang/String;", Descriptor.ofMethod(String.class)); // String Object.toString()
        assertEquals("(Ljava/lang/Object;)Z", Descriptor.ofMethod(boolean.class, Object.class)); // bool Object.equals(Object)
        assertEquals("()Ljava/lang/Class;", Descriptor.ofMethod(Class.class)); // Class Object.getClass()
        assertEquals("(JI)V", Descriptor.ofMethod(void.class, long.class, int.class)); // void Object.wait(long, int)
        assertEquals("([C)Ljava/lang/String;", Descriptor.ofMethod(String.class, char[].class)); // String String.valueOf(char[])
    }


    @Test
    void ofClassTestPrimitives() {
        assertEquals("B", Descriptor.ofClass(byte.class));
        assertEquals("C", Descriptor.ofClass(char.class));
        assertEquals("D", Descriptor.ofClass(double.class));
        assertEquals("F", Descriptor.ofClass(float.class));
        assertEquals("I", Descriptor.ofClass(int.class));
        assertEquals("J", Descriptor.ofClass(long.class));
        assertEquals("S", Descriptor.ofClass(short.class));
        assertEquals("Z", Descriptor.ofClass(boolean.class));
    }

    @Test
    void ofClassTestPrimitiveArrays1() {
        assertEquals("[B", Descriptor.ofClass(byte[].class));
        assertEquals("[C", Descriptor.ofClass(char[].class));
        assertEquals("[D", Descriptor.ofClass(double[].class));
        assertEquals("[F", Descriptor.ofClass(float[].class));
        assertEquals("[I", Descriptor.ofClass(int[].class));
        assertEquals("[J", Descriptor.ofClass(long[].class));
        assertEquals("[S", Descriptor.ofClass(short[].class));
        assertEquals("[Z", Descriptor.ofClass(boolean[].class));
    }

    @Test
    void ofClassTestPrimitiveArrays2() {
        assertEquals("[[B", Descriptor.ofClass(byte[][].class));
        assertEquals("[[C", Descriptor.ofClass(char[][].class));
        assertEquals("[[D", Descriptor.ofClass(double[][].class));
        assertEquals("[[F", Descriptor.ofClass(float[][].class));
        assertEquals("[[I", Descriptor.ofClass(int[][].class));
        assertEquals("[[J", Descriptor.ofClass(long[][].class));
        assertEquals("[[S", Descriptor.ofClass(short[][].class));
        assertEquals("[[Z", Descriptor.ofClass(boolean[][].class));
    }

    @Test
    void ofClassTestPrimitiveWrappers() {
        assertEquals("Ljava/lang/Byte;", Descriptor.ofClass(Byte.class));
        assertEquals("Ljava/lang/Character;", Descriptor.ofClass(Character.class));
        assertEquals("Ljava/lang/Double;", Descriptor.ofClass(Double.class));
        assertEquals("Ljava/lang/Float;", Descriptor.ofClass(Float.class));
        assertEquals("Ljava/lang/Integer;", Descriptor.ofClass(Integer.class));
        assertEquals("Ljava/lang/Long;", Descriptor.ofClass(Long.class));
        assertEquals("Ljava/lang/Short;", Descriptor.ofClass(Short.class));
        assertEquals("Ljava/lang/Boolean;", Descriptor.ofClass(Boolean.class));
    }

    @Test
    void ofClassTestPrimitiveWrapperArrays1() {
        assertEquals("[Ljava/lang/Byte;", Descriptor.ofClass(Byte[].class));
        assertEquals("[Ljava/lang/Character;", Descriptor.ofClass(Character[].class));
        assertEquals("[Ljava/lang/Double;", Descriptor.ofClass(Double[].class));
        assertEquals("[Ljava/lang/Float;", Descriptor.ofClass(Float[].class));
        assertEquals("[Ljava/lang/Integer;", Descriptor.ofClass(Integer[].class));
        assertEquals("[Ljava/lang/Long;", Descriptor.ofClass(Long[].class));
        assertEquals("[Ljava/lang/Short;", Descriptor.ofClass(Short[].class));
        assertEquals("[Ljava/lang/Boolean;", Descriptor.ofClass(Boolean[].class));
    }

    @Test
    void ofClassTestPrimitiveWrapperArrays2() {
        assertEquals("[[Ljava/lang/Byte;", Descriptor.ofClass(Byte[][].class));
        assertEquals("[[Ljava/lang/Character;", Descriptor.ofClass(Character[][].class));
        assertEquals("[[Ljava/lang/Double;", Descriptor.ofClass(Double[][].class));
        assertEquals("[[Ljava/lang/Float;", Descriptor.ofClass(Float[][].class));
        assertEquals("[[Ljava/lang/Integer;", Descriptor.ofClass(Integer[][].class));
        assertEquals("[[Ljava/lang/Long;", Descriptor.ofClass(Long[][].class));
        assertEquals("[[Ljava/lang/Short;", Descriptor.ofClass(Short[][].class));
        assertEquals("[[Ljava/lang/Boolean;", Descriptor.ofClass(Boolean[][].class));
    }

    @Test
    void ofClassTestGeneral() {
        assertEquals("Ljava/lang/Object;", Descriptor.ofClass(Object.class));
        assertEquals("Ljava/lang/String;", Descriptor.ofClass(String.class));
    }
}
