import java.util.Iterator;
import java.util.LinkedList;

public class LockRoot extends Message{
    private  Iterator<Integer> backRoute;
    boolean locked;

    public LockRoot(Direction dir, LinkedList<Integer> route, int target){
        setSend(dir);
        this.target = target;
        backRoute = route.descendingIterator();
        routeIter = route.iterator();
        locked = false;
    }

    @Override
    public void returnToSender(boolean valid) {
        locked = valid;
        routeIter = backRoute;
        routeIter.next();
    }
}
