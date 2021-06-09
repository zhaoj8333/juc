package internal;

import clazz.Teacher;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class MiscUnsafe {

    public static void main(String[] args) {
        changeObjectFields();
    }

    private static void changeObjectFields() {
        final Unsafe unsafe = getUnsafe();
        try {
            assert unsafe != null;
            final long id = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
            System.out.println("offset of id   : " + id);
            final long name = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));
            System.out.println("offset of name : " + name);
            final Teacher t = new Teacher(1, "啊");
            System.out.println(t);
            unsafe.compareAndSwapInt(t, id, 1, 10);
            unsafe.compareAndSwapObject(t, name, t.getName(), "Mr. D");

            System.out.println(t);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private static Unsafe getUnsafe() {
        try {
            final Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
            unsafe.setAccessible(true);
            final Unsafe theUnsafe = (Unsafe) unsafe.get(null);
            return theUnsafe;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
