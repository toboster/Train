import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Train class
 */
public class Train implements Runnable{
    private static int ID = 0;
    private Track currentTrack;
    private int trainID;
    public BlockingQueue<Message> messageQueue;
    private LinkedList<Integer> trainRoute;
    private boolean newMessage;

    /**
     * Starts on a specific track
     * @param currentTrack
     */
    public Train(Track currentTrack){
        this.currentTrack = currentTrack;
        currentTrack.addTrain(this);
        trainID = ID;
        ID++;
        messageQueue  = new LinkedBlockingQueue<>();
        newMessage = false;
    }

    /**
     * Creates a message to find a route to a destination
     * @param dir
     * @param target
     */
    public void findRoute(Direction dir, int target){
        try {
            messageQueue.put(new RootFinder(dir, target, trainID));
            newMessage = true;
        }catch(InterruptedException e){}
    }

    /**
     * Handle messages that it recieves
     * @throws InterruptedException
     */
    public void handleMessage() throws InterruptedException{
        Message m = messageQueue.take();
        //train created message
        if(newMessage){
            currentTrack.messageQueue.put(m);
            newMessage = false;
        }else {
            //Move once root is confirmed to be locked
            if (m instanceof LockRoot) {
                if (((LockRoot) m).locked) {
                    currentTrack.messageQueue.put(new SignalLight(Direction.LEFT));
                    currentTrack.messageQueue.put(new SignalLight(Direction.RIGHT));
                    move();
                } else {
                    System.out.println("Unable to lock route");
                }
            //If route is valid, save it and try to lock it
            } else if (m instanceof RootFinder) {
                if (((RootFinder) m).liveRoute) {
                    LockRoot lockMessage = new LockRoot(m.getSend(), m.route, m.getTargetStation());
                    trainRoute = m.route;

                    currentTrack.messageQueue.put(lockMessage);
                } else {
                    System.out.println("No route available.");
                }
            }
        }
    }

    /**
     * Move along previously specified route
     */
    private void move() {
        for (int x : trainRoute) {
            if (x != currentTrack.getID()) {
                currentTrack.addTrain(null);
                if (currentTrack.left != null && x == currentTrack.getLeftID()) {
                    currentTrack = currentTrack.left;
                } else if (currentTrack.right != null && x == currentTrack.getRightID()) {
                    currentTrack = currentTrack.right;
                } else if (currentTrack instanceof SwitchTrack && ((SwitchTrack) currentTrack).getBranch() != null
                        && ((SwitchTrack) currentTrack).getBranchID() == x) {
                    currentTrack = ((SwitchTrack) currentTrack).getBranch();
                } else {
                    System.out.println("Error in train movement");
                }
                currentTrack.addTrain(this);
                try {
                    //Sleep for animation smoothing
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void run(){
        while(true) {
            try {
                handleMessage();
            }catch (InterruptedException e){
                System.out.println("Error in train message");
            }
        }
    }

    public int getX(){
        return currentTrack.getX();
    }

    public int getY(){
        return currentTrack.getY();
    }
}

