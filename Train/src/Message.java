import java.util.Iterator;
import java.util.LinkedList;

/**
 * Abract class to define messages
 */
public abstract class Message {
    protected Direction sendTo = Direction.NONE;
    protected Iterator<Integer> routeIter;
    protected LinkedList<Integer> route = new LinkedList<>();
    protected int target;
    protected int originID;

    /**
     * Set direction of the message
     * @param dir direction
     */
    public void setSend(Direction dir){
        this.sendTo = dir;
    }

    /**
     * Get direction of message
     * @return message direction
     */
    public Direction getSend(){
        return sendTo;
    }

    /**
     * Get a reverse order route of the messages path
     * @return route
     */
    public Iterator<Integer> getRoute() {
        return route.descendingIterator();
    }

    /**
     * Id of track to next go to
     * @return id of track
     */
    public int getNextID(){ return routeIter.next(); }

    /**
     * See if there is a next track to go to
     * @return true for yes
     */
    public boolean hasNextID(){ return routeIter.hasNext(); }

    /**
     * Add a track ID to the list of ids in the route
     * @param id
     */
    public void addTrackID(int id){ route.add(id); }

    /**
     * Set a specific route for the message
     * @param route
     */
    public void setRoute(LinkedList<Integer> route){ this.route = route; }

    /**
     * Get track id that the message is trying to reach
     * @return
     */
    public int getTargetStation(){
        return target;
    }

    /**
     * Get Id of what created the message
     * @return
     */
    public int getOriginID(){
        return originID;
    }

    /**
     * Absract method to define what happens after the message reaches an end
     * @param valid
     */
    public abstract void returnToSender(boolean valid);

    @Override
    public String toString() {
        return "Message{" +
                "route=" + route +
                '}' + "Target=" + target;
    }
}
