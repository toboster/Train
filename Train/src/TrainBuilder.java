import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

/**
 * only available through get instance, initially creates track system based
 * on config file.
 */
public class TrainBuilder {

    private static TrainBuilder instance = null;
    private static String configFile = "TrainConfig.txt";
    /**
     * Regular expression for getting individual characters.
     */
    private static final String CHAR_REGEX = "(?<=.)";
    private List<Track> trackList = new LinkedList<>();
    private List<TrackView> trackViews = new LinkedList<>();
    private Map<String, Integer> switchMap = new HashMap<>();
    private Track prevTrack;
    // List that contains which stations are all the trains at.
    // created from config file.
    private List<Integer> trainsIDstationList = new LinkedList<>();
    // list of stations on the left side.
    private List<Integer> rightStationList = new LinkedList<>();
    // list of stations on the right side.
    private List<Integer> leftStationList = new LinkedList<>();
    // used to spawn trains, maximum expected is 2 elements. One origin station,
    // and one target station.
    private List<Integer> buildList = new LinkedList<>();
    private int lineCounter = 0;
    private Random rand = new Random();


    private TrainBuilder() {
        trainSetup();
        buildTrackViews();
    }

    public static TrainBuilder getInstance() {
        if (instance == null) {
            instance = new TrainBuilder();
        }
        return instance;
    }

    /**
     * opens config file, to read symbols in order to create systems of track.
     */
    private void trainSetup() {

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(configFile)))) {
            String line;
            while ((line = in.readLine()) != null) {
                // Make a scanner that uses String as source
                Scanner strScan = new Scanner(line);
                // Use word boundaries to break up input.
                strScan.useDelimiter(CHAR_REGEX);
                createTracks(strScan);
                lineCounter++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * expects a single line from the config file, analyzes symbols to create
     * tracks. Will connect two tracks as long as they're on the same level.
     *
     * @param charIter
     */
    private void createTracks(Iterator<String> charIter) {
        int charCount = 0;
        Track localTrack;
        while (charIter.hasNext()) {
            String symbol = charIter.next();
            // based on symbol creates track.
            localTrack = trackFactory(symbol, charCount);
            trackList.add(localTrack);
            charCount++;
            connectTrack(localTrack);
            // remembers previous Track.
            prevTrack = localTrack;
        }
        // finnish connecting, forget the prevTrack;
        prevTrack = null;
    }

    /**
     * Symbols represent certain tracks,
     * O = Station
     * - = TrainTrack
     * * = Light
     * number = switch , : only connected if there is a matching switch.
     *
     * @param str
     * @param xPosition
     * @return
     */
    private Track trackFactory(String str, int xPosition) {
        Track track = null;
        switch (str) {
            // main track.
            case "-":
                track = new TrainTrack(xPosition, lineCounter);
                break;
            // station.
            case "O":
                track = makeStation(xPosition);
                addStation(track);
                break;
            // train and station.
            case "T":
                track = makeStation(xPosition);
                trainsIDstationList.add(track.getID());
                addStation(track);
                break;
            // lights.
            case "*":
                track = new LightTrack(xPosition, lineCounter);
                //TODO Light
                break;
            default:
                // found similar switch, connect them.
                // there is a possible error where more than two digits are given.
                if (switchMap.containsKey(str)) {
                    track = new SwitchTrack(xPosition, lineCounter);
                    Track oldSwitch = getIDtrack(switchMap.get(str));

                    setSwitchBranch(track, oldSwitch);
                }
                // checks if it's a switch based on a digit.
                else {
                    track = new SwitchTrack(xPosition, lineCounter);
                    if (switchMap.containsKey(str)) {
                        switchMap.put(str, track.getID());
                    }

                }
        }
        return track;
    }

    /**
     * Assumes that given track is a station.
     *
     * @param track
     */
    private void addStation(Track track) {
        if (track instanceof Station) {
            if (track.getX() == 0) {
                leftStationList.add(new Integer(track.getID()));
            } else {
                rightStationList.add(new Integer(track.getID()));
            }
        }
    }

    /**
     * creates station based on position x.
     *
     * @param x
     * @return
     */
    private Track makeStation(int x) {
        Direction side;
        // if station is at column zero we know that the side will be right.
        side = x == 0 ? Direction.LEFT : Direction.RIGHT;
        return new Station(x, lineCounter, side);
    }

    /**
     * returns the branch direction based on two x positions.
     */
    private Direction getBranchDir(int xPos1, int xPos2) {
        return xPos1 <= xPos2 ? Direction.RIGHT : Direction.LEFT;
    }

    /**
     * expects that given tracks are switches, sets the branches of each
     * switches.
     *
     * @param track1
     * @param track2
     */
    private void setSwitchBranch(Track track1, Track track2) {
        Direction dir = getBranchDir(track1.getX(), track2.getX());

        ((SwitchTrack) track1).addBranch(track2, dir);
        ((SwitchTrack) track2).addBranch(track1, Direction.getOpposite(dir));
        System.out.println("(coordinate: (" + track1.getX() + "," + track1.getY()
                + ")" + " added trackID" + track2.getID());

        System.out.println("(coordinate: (" + track2.getX() + "," + track2.getY()
                + ")" + " added trackID" + track1.getID());
    }

    /**
     * @param id
     * @return
     */
    private Track getIDtrack(int id) {
        for (Track t : trackList) {
            if (t.getID() == id) {
                return t;
            }
        }
        System.out.println("Error looking for switch: " + id);
        return null;
    }

    /**
     * @param trackID
     * @return
     */
    public Track getTrack(int trackID) {
        for (Track t : trackList) {
            if (t.getID() == trackID) {
                return t;
            }
        }
        System.out.println("no matching switch Error");
        return null;
    }

    public void addBuildList(int trackID) {
        buildList.add(new Integer(trackID));
    }

    public void clearBuildList() {
        buildList.clear();
    }

    /**
     * connects two tracks by giving giving each track a reference.
     *
     * @param newTrack
     */
    private void connectTrack(Track newTrack) {
        // does not connect whenever a new line of track is being made.
        if (prevTrack != null) {
            prevTrack.addRight(newTrack);
            newTrack.addLeft(prevTrack);
            prevTrack = newTrack;
        }
    }

    public void buildTrackViews() {
        for (Track t :
                trackList) {
            trackViews.add(new TrackView(t));
        }
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public List<TrackView> getTrackViews() {
        return trackViews;
    }

    public List<Integer> getBuildList() {
        return buildList;
    }

    public List<Integer> getTrainsIDstationList() {
        return trainsIDstationList;
    }

    /**
     * given the x position will give an station which is opposite of position.
     *
     * @param xPos
     * @return
     */
    public int getRandStation(int xPos) {
        return xPos == 0 ? getRandRightStation() : getRandLeftStation();
    }

    /**
     * chooses a random station on the "right" side, which are stations that
     * have an x position non-zero.
     *
     * @return
     */
    private int getRandRightStation() {
        return rightStationList.get(rand.nextInt(rightStationList.size()));
    }

    /**
     * chooses a random station on the "left" side, which are stations that
     * have an x position of 0.
     *
     * @return
     */
    private int getRandLeftStation() {
        return leftStationList.get(rand.nextInt(leftStationList.size()));
    }


}
