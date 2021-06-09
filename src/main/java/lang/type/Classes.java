package lang.type;

public class Classes {
    public static void main(String[] args) {
        extracted();
    }

    /**
     * Integer: Object, 有成员变量和方法, reference to an int primitive
     * int:     int,    无成员变量和方法, a literal numerical value
     */
    private static void extracted() {
        Class<Integer> a = int.class;
        Class<Integer> b = Integer.TYPE;
        Class<Integer> c = Integer.class;
        System.out.println(System.identityHashCode(a));
        System.out.println(System.identityHashCode(b));
        System.out.println(System.identityHashCode(c));
    }
}
