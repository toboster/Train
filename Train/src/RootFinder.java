import java.util.Iterator;
import java.util.LinkedList;

public class RootFinder extends Message{
    boolean liveRoute;
    boolean returning;

    public RootFinder(Direction dir, int target, int trainID){
        sendTo = dir;
        this.target = target;
        originID = trainID;
        liveRoute = false;
        returning = false;
    }

    public void returnToSender(boolean correctRoot){
        sendTo = Direction.NONE;
        routeIter = route.descendingIterator();
        routeIter.next();
        liveRoute = correctRoot;
        returning = true;
    }

    /**
     * Gets a copy of message for switch track
     * @return
     */
    public Message getCopy(){
        Message copy = new RootFinder(sendTo, target, originID);
        copy.setRoute((LinkedList)route.clone());
        return copy;
    }
}
