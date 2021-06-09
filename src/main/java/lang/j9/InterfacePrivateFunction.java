package lang.j9;

public interface InterfacePrivateFunction {
    private void privateFunc() {
        System.out.println("Private funcitons in interface");
    }

    default void call() {
        this.privateFunc();
    }

    default void protectedFunc() {
        System.out.println("Protected funcitons in interface");
    }
}
