public class TrainTrack extends Track {


    public TrainTrack(int x, int y) {
        super(x, y);
    }

    @Override
    public void handleMessage() throws InterruptedException {
        Message message = messageQueue.take();
        handleingMessage = true;
        Thread.sleep(1000);
        System.out.println("Track: " + trackID + "handeling" + message.target);
        if (message instanceof RootFinder) {
            sendRootFinder(message);
        } else if (message instanceof LockRoot) {
            sendLockMessage(message);
        } else if (message instanceof SignalLight) {
            sendSignalMessage(message);
        }
    }


    @Override
    public String toString() {
        return "-";
    }
}