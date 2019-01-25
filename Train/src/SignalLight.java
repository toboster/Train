public class SignalLight extends Message{

    public SignalLight(Direction direction){
        sendTo = direction;
    }
    public void returnToSender(boolean valid){

    }
}
