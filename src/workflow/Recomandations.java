package workflow;

import input.MovieInput;
import input.user.UserInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Recomandations {

    private Recomandations() {

    }

    public static List<String> sortGenres(final UserInput crtUser) {
        List<String> genres = crtUser.getLikedMovies().stream().map(MovieInput::getGenres)
                .flatMap(Collection::stream).toList();

        genres.stream().sorted((o1, o2) -> {
            if (nrLikes(o1, crtUser) != nrLikes(o2, crtUser)) {
                return nrLikes(o1, crtUser) - nrLikes(o2, crtUser);
            }
            return o2.compareTo(o1);
        });
        return null;
    }

    public static int nrLikes(final String genre, final UserInput crtUser) {
        return crtUser.getLikedMovies().stream().filter(movie -> movie.getGenres().contains(genre))
                .toList().size();
    }

}
