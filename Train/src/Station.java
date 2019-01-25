/**
 *Train station
 */
public class Station extends Track {
    Direction side;

    /**
     * set which side of the map the station is
     * @param x
     * @param y
     * @param side
     */
    public Station(int x, int y, Direction side){
        super(x,y);
        this.side = side;
    }

    /**
     * Handles messages
     * @throws InterruptedException
     */
    @Override
    public synchronized void handleMessage() throws InterruptedException {

        Message message = messageQueue.take();
        handleingMessage = true;
        System.out.println("Station: " + trackID + "handeling" + message.target);
        if(message instanceof RootFinder){
            //root finder coming back, pass it to train
            if(((RootFinder) message).returning){
                if(train != null)
                    train.messageQueue.put(message);
            }else{
                //add it's own id and send it long
                message.addTrackID(trackID);
                if (message.sendTo == side) {
                    if (message.target == trackID) {
                        message.returnToSender(true);
                        message.getNextID();
                    } else {
                        message.returnToSender(false);
                        message.getNextID();
                    }
                } else {
                    message.addTrackID(trackID);
                }
                if (side == Direction.LEFT) {
                    right.messageQueue.put(message);
                } else {
                    left.messageQueue.put(message);
                }
            }
        }else if(message instanceof LockRoot) {
            if (((LockRoot) message).locked) {
                if (train != null) {
                    train.messageQueue.put(message);
                }
            } else {
                lock = true;
                if (message.getTargetStation() == trackID) {
                    message.returnToSender(true);
                }
                message.getNextID();
                if (side == Direction.LEFT) {
                    right.messageQueue.put(message);
                } else {
                    left.messageQueue.put(message);
                }
            }
            //Signal message do nothing
        }else if(message instanceof SignalLight){
            message.returnToSender(true);
        }

    }
}
