import javafx.scene.image.ImageView;

/**
 * Encapsulates train and its thread.
 */
public class TrainView {

    private Train train;
    private Thread thread;
    private ImageView imgView;

    public TrainView(Train train) {
        this.train = train;
        if (train != null) {
            thread = new Thread(train);
        }
        imgView = FileMap.getInstance().getView("train");
        setSizeView();
    }

    /**
     * starts train.
     */
    public void startTrain() {
        if (thread != null) {
            thread.start();
        } else {
            System.out.println("thread doesnt exists");
        }
        if (train == null) {
            System.out.println("train does not exists");
        }
    }

    /**
     * sets ideal size for train.
     */
    public void setSizeView() {
        imgView.setFitWidth(60);
        imgView.setFitHeight(60);
    }

    public Train getTrain() {
        return train;
    }

    public int getX() {
        return train.getX();
    }

    public ImageView getImgView() {
        return imgView;
    }

    /**
     * @param dir    direction
     * @param target desired station to reach
     */
    public void findRoute(Direction dir, int target) {
        train.findRoute(dir, target);
    }

    /**
     * simulates train movement by using the current track position.
     */
    public void update() {
        imgView.setTranslateX(train.getX() * 100 + 25);
        imgView.setTranslateY(train.getY() * 100 + 25);
    }
}
