import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Encapsulates a track and thread.
 */
public class TrackView extends VBox {

    private Track track;
    private Thread thread;
    private ImageView imgView;
    private int boxHeight = 70;
    private int boxWidth = 0;
    private int VIEW_WIDTH = 100;
    private int VIEW_HEIGHT = 70;
    // whether current view is a padded view in GUI
    private boolean empty = false;
    private boolean mouseEvent = false;

    public TrackView(Track track) {
        this.track = track;
        setPrefHeight(boxHeight);
        setPrefWidth(boxWidth);
        setAlignment(Pos.BOTTOM_CENTER);
        if (track != null) {
            thread = new Thread(track);
        }
    }

    public void startTrack() {
        thread.start();
    }

    /**
     * reassigns imageView according to properties of tracks.
     */
    public void update() {
        getChildren().clear();
        if (empty == true && track != null) {
            setEmptyView();
        } else if (track != null) {
            String trackString = "trainTrack";
            // check whether a variable for switch on
            if (track instanceof SwitchTrack && ((SwitchTrack) track).isActive()) {
                trackString = "switchTrack";
            } else if (track.isLock()) {
                trackString = "lockTrack";
            } else if (track.isHandlingMessage()) {
                trackString = "findTrack";
            }
            imgView = FileMap.getInstance().getView(trackString);
            getChildren().add(imgView);
        }
        if (imgView != null) {
            setViewFit();
        }
    }

    /**
     * sets the image if a track is empty.
     */
    public void setEmptyView() {
        if (track instanceof Station) {
            imgView = FileMap.getInstance().getView("station");

            if (!mouseEvent) {
                mouseEvent = true;
                addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        TrainBuilder.getInstance().addBuildList(track.getID());
                    }
                });
            }
            getChildren().add(imgView);

        }
        if (track instanceof LightTrack) {
            if (((LightTrack) track).isLightActive()) {
                imgView = FileMap.getInstance().getView("redLight");
            } else {
                imgView = FileMap.getInstance().getView("greenLight");
            }
            getChildren().add(imgView);
        }
    }

    /**
     * sets ideal size for the View.
     */
    public void setViewFit() {
        imgView.setFitHeight(VIEW_HEIGHT);
        imgView.setFitWidth(VIEW_WIDTH);
    }

    public int getX() {
        return track.getX();
    }

    public int getY() {
        return track.getY();
    }

    public Track getTrack() {
        return track;
    }

    public void setEmpty(boolean bool) {
        empty = bool;
    }

    public int getTrackID() {
        int result = 0;
        if (track != null) {
            result = track.getID();
        } else {
            System.out.println("error getting track from TrackView");
        }
        return result;
    }

    public void printConnected() {
        track.printConnected();
    }
}
