package atomic;

import clazz.Teacher;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class FieldUpdator {

    private volatile int test;

    @Override
    public String toString() {
        return "FieldUpdator{" +
                "test=" + test +
                '}';
    }

    /**
     * 字段更新器:针对对象的某个field,进行原子操作,只能配合volatile修饰的字段使用, 否则会出现异常
     * Must be volatile type
     */
    public static void main(String[] args) {
        AtomicIntegerFieldUpdater<FieldUpdator> fieldUpdator = AtomicIntegerFieldUpdater.newUpdater(FieldUpdator.class, "test");
        FieldUpdator test = new FieldUpdator();
        System.out.println(test);

        boolean b = fieldUpdator.compareAndSet(test, 0, 100);
        System.out.println(b);
        System.out.println(test);

        // changePerson();
    }

    static class Person {
        private volatile int age;
        private volatile String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    private static void changePerson() {

        final AtomicIntegerFieldUpdater<Person> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(Person.class, "age");
        final Person person = new Person(0, "啊");
        System.out.println(person);

        final boolean b = fieldUpdater.compareAndSet(person, 0, 10);
        System.out.println(b);
        System.out.println(person);

    }
}
