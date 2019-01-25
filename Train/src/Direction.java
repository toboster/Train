public enum Direction {
    RIGHT,
    LEFT,
    NONE;

    public static Direction getOpposite(Direction dir){
        switch(dir){
            case RIGHT:
                return LEFT;
            case LEFT:
                return RIGHT;
            default:
                return NONE;
        }
    }
}
