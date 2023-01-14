package input.user;

import java.util.ArrayList;

public final class Notifications {

    private String movieName;
    private String message;

    private Notifications() {

    }

    /**
     *
     * @param arrayList
     * @return
     */
    public static Notifications dequeue(final ArrayList<Notifications> arrayList){
        Notifications elem = arrayList.get(0);
        arrayList.remove(0);
        return elem;
    }

    /**
     *
     * @param arrayList
     * @param elem
     */
    public static void dequeue(final ArrayList<Notifications> arrayList, final Notifications elem){
        arrayList.add(elem);
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
