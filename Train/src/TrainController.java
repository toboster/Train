import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The entry point of the program, builds system of tracks and observes the
 * state of the tracks, stations, trains. This is displayed using javaFX.
 *
 * @author Tony Nguyen
 * @author Lucas Jackson.
 */
public class TrainController extends Application {

    private ImageView bgImgView = new ImageView(
            new Image(getClass().getClassLoader().getResourceAsStream("res/Sky.jpg"))
    );
    private final int WIDTH = 700;
    private final int HEIGHT = 700;
    private List<TrackView> viewList = TrainBuilder.getInstance().getTrackViews();
    // padded TrackViews on grid.
    private List<TrackView> emptyViewList = new LinkedList<>();
    private GridPane gridPane = new GridPane();
    private List<TrainView> trainViewList = new LinkedList<>();
    private Pane root;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(createContent());

        primaryStage.setTitle("Train");
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer a = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkBuildList();
                update();
            }
        };
        a.start();
    }

    /**
     * modifies the root pane.
     *
     * @return
     */
    private Parent createContent() {
        //root.setStyle("-fx-background-color: black");
        this.root = new Pane();
        setup();
        // ROOT
        root.setPrefSize(WIDTH, HEIGHT);
        root.getChildren().addAll(bgImgView, gridPane);
        addTrainViews(root);

        return root;
    }

    /**
     * invokes methods to properly setup network and preferences.
     */
    private void setup() {
        addTrackViews();
        startTrackViews();
        initTrains();
        startTrains();
        setBgSize();
        update();
        printTrackViews();
    }

    /**
     * checks if a buildList is full, elements are added to buildList by
     * listeners from GUI.
     */
    private void checkBuildList() {
        if (TrainBuilder.getInstance().getBuildList().size() == 2) {
            buildTrain();
            TrainBuilder.getInstance().clearBuildList();
        }
    }

    /**
     * assumes that there are exactly two elements in buildList, creates
     * a train and sets intended direction.
     */
    private void buildTrain() {
        List<Integer> buildList = TrainBuilder.getInstance().getBuildList();
        int originID = buildList.get(0).intValue();
        Track track = TrainBuilder.getInstance().getTrack(originID);
        Direction trainDir = getTrainDir(track.getX());
        TrainView trainView = new TrainView(new Train(track));
        trainView.findRoute(trainDir, buildList.get(1).intValue());
        trainViewList.add(trainView);
        trainView.startTrain();
        root.getChildren().add(trainView.getImgView());

    }

    /**
     * fills the grid with TrackViews, as well as padded TrackViews.
     */
    private void addTrackViews() {
        for (TrackView t : viewList) {
            int xPos = t.getX();
            int yPos = t.getY();
            TrackView tempView = new TrackView(t.getTrack());
            tempView.setEmpty(true);
            emptyViewList.add(tempView);
            // pads grid with blanks
            gridPane.add(tempView, xPos, 2 * yPos);
            gridPane.add(t, xPos, 2 * yPos + 1);
        }
    }

    /**
     * start tracks.
     */
    private void startTrackViews() {
        for (TrackView t : viewList) {
            t.startTrack();
        }
    }

    /**
     * used to create trains, if there were any specified in config file.
     */
    private void initTrains() {
        List<Integer> trainIDstations = TrainBuilder.getInstance().getTrainsIDstationList();
        for (Integer i : trainIDstations) {
            int trackID = i.intValue();
            makeTrain(trackID);
        }
    }

    /**
     * creates a train based on trackID, used by initTrains()
     * @param trackID
     */
    private void makeTrain(int trackID) {
        for (TrackView trackV : viewList) {
            if (trackID == trackV.getTrackID()) {
                System.out.println("make train track ID: " + trackV.getTrackID());
                trainViewList.add(new TrainView(new Train(trackV.getTrack())));
            }
        }
    }

    /**
     * sets route for trains mentioned in config file as well as starts the
     * thread for the train.
     */
    private void startTrains() {
        for (TrainView trainV : trainViewList) {
            int trainXPos = trainV.getX();
            Direction trainDir = getTrainDir(trainXPos);
            // wanted to start trains to random station.
            trainV.startTrain();

            trainV.findRoute(trainDir, TrainBuilder.getInstance().getRandStation(trainXPos));
            System.out.println("target");
            System.out.println(TrainBuilder.getInstance().getRandStation(trainXPos));
            System.out.println("train direction");
            System.out.println(trainDir);
        }
    }

    /**
     * adds all trainViews contained in the list to root.
     *
     * @param root
     */
    private void addTrainViews(Pane root) {
        for (TrainView trainView : trainViewList) {
            root.getChildren().add(trainView.getImgView());
        }
    }

    /**
     * used to create trains.
     *
     * @param xPos xPosition
     * @return
     */
    private Direction getTrainDir(int xPos) {
        return xPos == 0 ? Direction.RIGHT : Direction.LEFT;
    }

    /**
     * used for debugging.
     */
    private void printTrackViews() {
        for (TrackView t : viewList) {
            t.printConnected();
        }
    }

    /**
     * updates images in TrackViews, TrainViews, emptyTrackViews.
     */
    private void update() {
        for (TrackView tv : viewList) {
            tv.update();
        }
        for (TrackView emptyTv : emptyViewList) {
            emptyTv.update();
        }
        updateTrains();
    }

    /**
     * used in update(), to update trainViews
     */
    private void updateTrains() {
        for (TrainView trainView : trainViewList) {
            trainView.update();
        }
    }

    /**
     * sets desired background image size.
     */
    private void setBgSize() {
        bgImgView.setFitWidth(WIDTH);
        bgImgView.setFitHeight(HEIGHT);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
