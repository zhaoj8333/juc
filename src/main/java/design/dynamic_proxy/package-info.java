package design.dynamic_proxy;

/**
 * 静态代理：
 *     由程序员创建或特定工具自动生成源码，在编译时就将接口，被代理类，代理类等确定下来。在程序运行之前，代理类的class文件就已经生成
 *
 *  public interface Person {
 *      void giveMoney();
 *  }
 *
 *  public class Student implements Person {
 *      private String name;
 *      private Student(String name) {
 *          this.name = name;
 *      }
 *
 *      public void giveMoney() {
 *          System.out.println(name + "give money");
 *      }
 *  }
 *
 *  // 通过代理对象来执行被代理的对象
 *  // 代理模式就是在实际对象引入一定的间接性，这种间接性可以附加多种用途
 *  public class StudentProxy implements Person {
 *      Student stu;
 *      public StudentProxy(Person stu) {
 *          this.stu = stu;
 *      }
 *      public void giveMoney() {
 *          // extra logic, 这就是Spring AOP面向切面编程
 *          stu.giveMoney();
 *      }
 *  }
 */

/**
 * 动态代理：
 *     在运行时创建代理的方式成为动态代理，代理类并非在Java代码中定义，而是在运行时根据在java代码中的指示动态生成。
 *     动态代理可以对代理类的函数进行统一处理，而不用修改每个代理类的方法
 */


/**
 * 动态字节码技术：
 *     ASM: cglib底层， {@link jdk.internal.org.objectweb.asm.ClassReader}
 *                     {@link jdk.internal.org.objectweb.asm.ClassWriter}
 *
 */