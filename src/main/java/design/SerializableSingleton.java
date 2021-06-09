package design;

import java.io.*;

public class SerializableSingleton implements Serializable {

    private volatile static SerializableSingleton instance;

    private SerializableSingleton() {}

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Since the method readResolve is here, the unserialized Object from the serialized file instance of
     * SerializableSingleton equals to the Object {@link SerializableSingleton} which produced the serialized file
     */
//    @Serial
    public Object readResolve() {
        return instance;
    }

    public static SerializableSingleton getInstance() {
        if (instance == null) {
            synchronized (SerializableSingleton.class) {
                if (instance == null) {
                    instance = new SerializableSingleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        final SerializableSingleton instance = SerializableSingleton.getInstance();
        instance.setContent("单例序列化");
        System.out.println(instance.getContent());
        try {
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("serializableObject.obj"));
            objectOutputStream.writeObject(instance);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SerializableSingleton o = null;
        try {
            final FileInputStream fis = new FileInputStream("serializableObject.obj");
            final ObjectInputStream ois = new ObjectInputStream(fis);
            o = (SerializableSingleton) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=============================");
        assert o != null;
        System.out.println(o.getContent());
        // false
        System.out.println("equals: " + (o.equals(instance)));
        System.out.println(o.hashCode() + "   " + instance.hashCode());
    }
}
