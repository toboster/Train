import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Track implements Runnable {
    protected Track right;
    protected Track left;
    protected Train train;

    public BlockingQueue<Message> messageQueue;

    protected static int ID = 0;
    protected int trackID;

    protected boolean handleingMessage = false;

    protected int x;
    protected int y;


    protected boolean lock = false;

    public Track(int x, int y){
        trackID = ID;
        ID++;
        this.x = x;
        this.y = y;
        messageQueue = new LinkedBlockingQueue<>();
    }

    public void addTrain(Train train){ this.train = train; }

    public int getLeftID(){return left.trackID;}

    public int getRightID(){return right.trackID;}

    public abstract void handleMessage() throws InterruptedException;

    public void addRight(Track right){
        this.right = right;
    }

    public void addLeft(Track left){
        this.left = left;
    }

    public boolean isLock() {
        return lock;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getID(){
        return trackID;
    }

    public boolean isHandlingMessage(){
        return handleingMessage;
    }

    public boolean containsMessage(Message findRoute){
        return messageQueue.contains(findRoute);
    }

    public void printConnected(){
        boolean leftConnect = false;
        boolean rightConnect = false;

        if(right != null ){
           rightConnect = true;
        }
        if(left != null){
           leftConnect = true;
        }
        System.out.println(trackID + ": left=" + leftConnect + ", right=" + rightConnect);
        if(left != null){
            System.out.println("left = " + left.getID());
        }
        if(right != null){
            System.out.println("right = " + right.getID());
        }
    }

    /**
     * Handles a message for normal and signal tracks
     * @param m
     * @throws InterruptedException
     */
    protected void sendRootFinder(Message m) throws InterruptedException{
        if(((RootFinder) m).returning)
        {
            if(m.hasNextID()){
                int nextID = m.getNextID();
                if(nextID == left.trackID) {
                    left.messageQueue.put(m);
                }
                else if(nextID == right.trackID){
                    right.messageQueue.put(m);
                }
                else{
                    System.out.println("Could not find path back");
                }
            }else{
                if(train != null){ train.messageQueue.put(m); }
            }
        }else {
            if (lock) {
                m.returnToSender(false);
            } else {
                Direction dir = m.getSend();
                m.addTrackID(trackID);
                switch (dir) {
                    case RIGHT:
                        if (right != null)
                            right.messageQueue.put(m);
                        break;
                    case LEFT:
                        if (left != null)
                            left.messageQueue.put(m);
                        break;
                    case NONE:
                    default:
                        System.out.println("No message direction specified");
                        break;
                }
            }
        }
    }

    /**
     * Handles a message for normal and signal tracks
     * @param m
     * @throws InterruptedException
     */
    protected void sendLockMessage(Message m) throws InterruptedException{
        lock = true;
        if(m.hasNextID()) {
            int nextID = m.getNextID();
            if (nextID == left.trackID) {
                left.messageQueue.put(m);
            } else if (nextID == right.trackID) {
                right.messageQueue.put(m);
            } else {
                System.out.println("Could not find lock path");
            }
        }
    }

    /**
     * Handles a message for normal and signal tracks
     * @param m
     * @throws InterruptedException
     */
    protected void sendSignalMessage(Message m) throws InterruptedException{
        if(right != null && m.sendTo == Direction.RIGHT){
            right.messageQueue.put(m);
        }else if(left != null && m.sendTo == Direction.LEFT){
            left.messageQueue.put(m);
        }
    }


    @Override
    public String toString() {
        return trackID + "";
    }

    @Override
    public void run() {
        System.out.println(trackID + " starting");
        while (!Thread.interrupted()) {
            try {
                handleMessage();
                handleingMessage = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}



