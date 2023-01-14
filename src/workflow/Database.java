package workflow;

import input.ActionInput;
import input.Const;
import input.Input;
import input.MovieInput;
import input.user.Notifications;
import input.user.UserInput;

import java.util.ArrayList;

public final class Database {

    private Database() {

    }

    public static void notifyDelete(final ActionInput action, final ArrayList<UserInput> users) {
        for (UserInput crtUser: users) {
            for (MovieInput movie : crtUser.getPurchasedMovies()) {
                if (movie.getName().equals(action.getDeletedMovie())) {
                    if (crtUser.getCredentials().getAccountType().equals("premium")) {
                        crtUser.setNumFreePremiumMovies(crtUser.getNumFreePremiumMovies() + 1);
                    } else {
                        crtUser.setTokenCount(crtUser.getTokenCount() + Const.NO_TOKENS_MOVIE);
                    }
                    Notifications notifications = new Notifications();
                    notifications.setMessage("DELETE");
                    notifications.setMovieName(action.getDeletedMovie());
                    Notifications.enqueue(crtUser.getNotifications(), notifications);
                    break;
                }
            }
        }
    }

    public static void notifyAdd() {

    }
}
