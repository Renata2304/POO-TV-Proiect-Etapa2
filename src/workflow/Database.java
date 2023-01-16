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

    public static void deleteMovieFromUser(final Input inputData, final UserInput currentUser,
                                           final ActionInput action) {
        inputData.getMovies().removeIf(movie -> movie.getName()
                .equals(action.getDeletedMovie()));
        currentUser.getPurchasedMovies().removeIf(movie -> movie.getName()
                .equals(action.getDeletedMovie()));
        currentUser.getLikedMovies().removeIf(movie -> movie.getName()
                .equals(action.getDeletedMovie()));
        currentUser.getWatchedMovies().removeIf(movie -> movie.getName()
                .equals(action.getDeletedMovie()));
        currentUser.getRatedMovies().removeIf(movie -> movie.getName()
                .equals(action.getDeletedMovie()));
    }

    public static void notifyAdd(final ActionInput action, final ArrayList<UserInput> users) {
        for (UserInput crtUser : users) {
            for (int i = 0; i < action.getAddedMovie().getGenres().size(); i++ ) {
                if (crtUser.getSubscribedGenres()
                        .contains(action.getAddedMovie().getGenres().get(i))
                    && !action.getAddedMovie().getCountriesBanned()
                        .contains(crtUser.getCredentials().getCountry())) {
                    Notifications notifications = new Notifications();
                    notifications.setMessage("ADD");
                    notifications.setMovieName(action.getAddedMovie().getName());
                    Notifications.enqueue(crtUser.getNotifications(), notifications);
                    break;
                }
            }
        }
    }
}
