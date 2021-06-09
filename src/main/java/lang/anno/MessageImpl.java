package lang.anno;

@SuppressWarnings("serial")
public class MessageImpl implements IMessage {
    @Override
    public void send() {
        System.out.println("消息发送....");
    }
}
