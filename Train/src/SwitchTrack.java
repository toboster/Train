/**
 * Switch train track
 */
public class SwitchTrack extends Track{
    private Track branch;
    private Direction branchDirection;
    boolean active;

    public SwitchTrack(int x, int y){
        super(x,y);
        active = true;
    }

    /**
     * Add branch reference
     * @param branch
     * @param branchDirection
     */
    public void addBranch(Track branch, Direction branchDirection){
        this.branch = branch;
        this.branchDirection = branchDirection;
    }

    public Track getBranch() {
        return branch;
    }

    public int getBranchID(){
        return branch.trackID;
    }

    public boolean isActive(){
        return active;
    }

    /**
     * Handles a message based on context
     * @throws InterruptedException
     */
    @Override
    public synchronized void handleMessage() throws InterruptedException {
        Message message = messageQueue.take();
        System.out.println("SwitchTrack: " + trackID + "handeling" + message.target);
        if(message instanceof RootFinder){
            //find next ID in route
            if(((RootFinder) message).returning)
            {
                if(message.hasNextID()){
                    int nextID = message.getNextID();
                    if(left != null && nextID == left.trackID) {
                        left.messageQueue.put(message);
                    }else if(right != null && nextID == right.trackID){
                        right.messageQueue.put(message);
                    }else if(branch != null && nextID == branch.trackID){
                        branch.messageQueue.put(message);
                    } else{
                        System.out.println("Could not find path back");
                    }
                }else{
                    if(train != null){ train.messageQueue.put(message); }
                }
            }

            //Lock track and pass message to next in route
        }else if(message instanceof LockRoot){
            lock = true;
             if(message.hasNextID()){
                int nextID = message.getNextID();
                if(left != null && nextID == left.trackID){
                    left.messageQueue.put(message);
                }else if(right != null && nextID == right.trackID){
                    right.messageQueue.put(message);
                }else if(branch != null && nextID == branch.trackID) {
                    branch.messageQueue.put(message);
                    active = true;
                }else{
                    System.out.println("Could not find lock path");
                }
            }else if(message instanceof SignalLight){
                if(right != null && message.sendTo == Direction.RIGHT){
                    right.messageQueue.put(message);
                }else if(left != null && message.sendTo == Direction.LEFT){
                    left.messageQueue.put(message);
                }
            }
        }else {
            //return failed message if this track is locked
            if (lock) {
                message.returnToSender(false);
            } else {
                Direction dir = message.getSend();
                message.addTrackID(trackID);
                switch (dir) {
                    case RIGHT:
                        if (right != null){ right.messageQueue.put(message);}
                        if(branchDirection == Direction.RIGHT){
                            Message messageCopy = ((RootFinder)message).getCopy();
                            branch.messageQueue.put(messageCopy);
                        }
                        break;
                    case LEFT:
                        if (left != null) {left.messageQueue.put(message);}
                        if(branchDirection == Direction.LEFT){
                            Message messageCopy = ((RootFinder)message).getCopy();
                            branch.messageQueue.put(messageCopy);
                        }
                        break;
                    case NONE:
                    default:
                        System.out.println("No message direction specified");
                        break;
                }
            }
        }
    }
}

