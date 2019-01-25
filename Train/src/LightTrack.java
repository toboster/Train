/**
 * Class for track that has a red/green light.
 */
public class LightTrack extends Track{
    //false - green
    //true - red
    private boolean lightActive;

    /**
     * Calls default track constructor
     * sets light to green initially
     * @param x coordinate on the display
     * @param y coordinate on the display
     */
    public LightTrack(int x, int y){
        super(x,y);
        lightActive = false;
    }

    /**
     * Pulls a message from it's own queue and does what it needs to with it
     * @throws InterruptedException
     */
    @Override
    public void handleMessage() throws InterruptedException {
        Message message = messageQueue.take();
        handleingMessage = true;
        //animation smoothing
        Thread.sleep(1000);
        System.out.println("Track: " + trackID + "handeling" + message.target);
        //findind a root for the train
        if(message instanceof RootFinder){
            sendRootFinder(message);
        //Locking the found root for train
        }else if(message instanceof LockRoot){
            sendLockMessage(message);
        //Turn lights on the path red
        }else if(message instanceof SignalLight){
            lightActive = true;
            sendSignalMessage(message);
        }
    }

    public boolean isLightActive(){
        return lightActive;
    }

    /**
     * String representation according to configuration specs
     * @return 1 char string representation
     */
    @Override
    public String toString(){return "*";}


}
