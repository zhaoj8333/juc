package clazz;

import java.util.concurrent.TimeUnit;

public class Teacher {
    private volatile int id;
    private volatile String name;

    public Teacher(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWithException() {
        try {
            TimeUnit.SECONDS.sleep(1);
            this.id = 1231;
            if (System.currentTimeMillis() > 0) {
                int count = 0;
                int n = 10;
                for (int i = 0; i < n; i++) {
                    count++;
                }
                this.id = count;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
