package lang.jmx;
/**
    JMX: java management extensions，是java平台为程序植入管理功能的框架，

    为了标准化对程序的监控，java使用JMX作为管理和监控的标准接口，任何程序，安好JMX规范访问该接口，可以获取所有管理与监控信息
    JMX主要用于配置和监控资源的状态，使用JMX还可以监视和管理Java虚拟机。因为JMX可以远程管理应用程序，所以很容易在分布式环境中使用

    Zabbix，Ngios等工具对JVM的监控都是通过JMX

        ┌─────────┐  ┌─────────┐
        │jconsole │  │   Web   │
        └─────────┘  └─────────┘
             │            │
    ┌ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ┼ ─ ─ ─ ─
     JVM     ▼            ▼        │
    │   ┌─────────┐  ┌─────────┐
      ┌─┤Connector├──┤ Adaptor ├─┐ │
    │ │ └─────────┘  └─────────┘ │
      │       MBeanServer        │ │
    │ │ ┌──────┐┌──────┐┌──────┐ │
      └─┤MBean1├┤MBean2├┤MBean3├─┘ │
    │   └──────┘└──────┘└──────┘
     ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘

     JMX把所有的资源称为 MBean(Managed bean)，由 {@link javax.management.MBeanServer}管理，访问MBean，通过 {@link javax.management.MBeanServer}访问即可
     使用JMX不需要任何额外组件，可通过RMI连接到 {@link javax.management.MBeanServer}

     JVM会将自身的各种资源以MBean注册到JMX中，我们自己的配置，监控信息也可以作为MBean注册到JMX


 */