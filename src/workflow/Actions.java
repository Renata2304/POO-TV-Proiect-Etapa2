package workflow;

import input.ActionInput;
import input.Const;
import input.Input;
import input.MovieInput;
import input.user.Notifications;
import input.user.UserInput;
import pages.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import pages.PageStack;
import pages.Upgrades;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

public final class Actions {

    private Actions() {

    }

    public static void action(final ObjectMapper objectMapper, Input inputData,
                              final ArrayNode output) {
        Page crtPage = new Page();
        crtPage.setPageType("homepage neautentificat");
        ArrayList<MovieInput> crtMovies = new ArrayList<>();
        UserInput crtUser = new UserInput();
        ArrayList<String> stack = new ArrayList<>();
        for (ActionInput action: inputData.getActions()) {
            switch (action.getType()) {
                case "on page" -> {
                    // testing to see if there are any errors
                    boolean verif = Errors.checkErrorFeatureOnPage(crtPage.getPageType(), action);
                    if (!verif) {
                        // printing the error
                        OutPrint.printError(output);
                        break;
                    }
                    switch (action.getFeature()) {
                        case "register" -> {
                            crtUser = pages.Register.getRegister().initialOnPage(inputData, action,
                                    output, objectMapper, crtPage, crtUser, crtMovies);
                        }
                        case "login" -> {
                            crtUser = pages.Login.getLogin().initialOnPage(inputData, action,
                                    output, objectMapper, crtPage, crtUser, crtMovies);
                        }
                        case "search" -> {
                            pages.Movies.getMovies().onPageSearch(objectMapper, output, action,
                                    crtUser, crtMovies);
                        }
                        case "filter" -> {
                            crtMovies = pages.Movies.getMovies().onPageFilter(objectMapper,
                                    inputData, output, action, crtUser);
                        }
                        case "buy tokens" -> {
                            Upgrades.getUpgrades().onPageBuyTokens(action, crtUser);
                        }
                        case "buy premium account" -> {
                            Upgrades.getUpgrades().onPageBuyPremium(output, crtUser);
                        }
                        case "purchase" -> {
                            pages.Movies.getMovies().onPagePurchase(objectMapper, output, crtUser);
                        }
                        case "watch" -> {
                            pages.Movies.getMovies().onPageWatch(objectMapper, output, crtUser);
                        }
                        case "like" -> {
                            pages.Movies.getMovies().onPageLike(objectMapper, output, crtUser);
                        }
                        case "rate" -> {
                            pages.Movies.getMovies().onPageRate(objectMapper, output,
                                    action, crtUser);
                        }
                        case "subscribedGenre" -> {

                        }
                        default -> {
                        }
                    }
                }
                case "change page" -> {
                    boolean ok = Errors.checkErrorChangePage(crtPage.getPageType(),
                            action);
                    if (!ok) {
                        OutPrint.printError(output);
                    } else {
                        switch (action.getPage()) {
                            case "register" -> {
                                pages.Register.getRegister().
                                        changePage(output, action, crtPage);
                            }
                            case "login" -> {
                                pages.Login.getLogin().changePage(output, action, crtPage);
                            }
                            case "logout" -> {
                                crtMovies.removeAll(crtMovies);
                                crtUser = new UserInput();
                                crtPage.setPageType("homepage neautentificat");
                                stack.clear();
                            }
                            case "movies" -> {
                                crtMovies = pages.Movies.getMovies().changePageMovie(objectMapper,
                                        output, crtPage, crtUser, inputData, stack);
                            }
                            case "see details" -> {
                                pages.SeeDetails.getSeeDetails().changePageSeeDetails(output,
                                        objectMapper, crtUser, crtMovies, action, crtPage, stack);
                            }
                            case "upgrades" -> {
                                crtPage.setPageType("upgrades");
                                PageStack.push(stack, "upgrades");
                            }
                            default -> {
                            }
                        }
                    }
                }
                case "back" -> {
                    if (stack.isEmpty()
                            || crtPage.getPageType().equals("homepage neautentificat")) {
                        OutPrint.printError(output);
                    } else {
                        if (crtPage.getPageType().equals("see details")) {
                            crtMovies = pages.Movies.getMovies().changePageMovie(objectMapper,
                                output, crtPage, crtUser, inputData, stack);
                        }
                        crtPage.setPageType(PageStack.pop(stack));
                    }
                }
                case "database" -> {
                    switch (action.getFeature()) {
                        case "add" -> {
                            if (inputData.getMovies().contains(action.getAddedMovie())) {
                                OutPrint.printError(output);
                            } else {
                                inputData.getMovies().add(action.getAddedMovie());
//                                Database.notifyAdd(action, inputData.getUsers());
                            }
                        }
                        case "delete" -> {
//                            boolean ok = false;
//                            int poz = 0;
//                            for (MovieInput movie : inputData.getMovies()) {
//                                if (movie.getName().equals(action.getDeletedMovie())) {
//                                    ok = true;
//                                    break;
//                                }
//                                poz = poz + 1;
//                            }
//                            if (!ok) {
                            if (inputData.getMovies().stream().map(MovieInput::getName).
                                    noneMatch(name-> name.equals(action.getDeletedMovie()))) {
                                OutPrint.printError(output);
                            } else {
                                inputData.getMovies().removeIf(movie -> movie.getName()
                                                .equals(action.getDeletedMovie()));
//                                crtUser.getPurchasedMovies().removeIf(movie -> movie.getName()
//                                        .equals(action.getDeletedMovie()));
//                                crtUser.getLikedMovies().removeIf(movie -> movie.getName()
//                                        .equals(action.getDeletedMovie()));
//                                crtUser.getWatchedMovies().removeIf(movie -> movie.getName()
//                                        .equals(action.getDeletedMovie()));
//                                crtUser.getRatedMovies().removeIf(movie -> movie.getName()
//                                        .equals(action.getDeletedMovie()));
                                crtMovies.removeIf(movie -> movie.getName()
                                        .equals(action.getDeletedMovie()));
                                Database.deleteMovieFromUser(inputData, crtUser, action);
                                Database.notifyDelete(action, inputData.getUsers());
                            }
                        }
                        default -> {
                        }
                    }
                }
                default -> {
                }
            }
        }
        if (crtUser.getCredentials().getAccountType().equals("premium")) {
            if (crtUser.getLikedMovies().isEmpty()) {
                Notifications notifications = new Notifications();
                notifications.setMessage("Recommendation");
                notifications.setMovieName("No recommendation");
                Notifications.enqueue(crtUser.getNotifications(), notifications);

                OutPrint.printNoErrorNotif(objectMapper, output, crtUser);
            }
        }
    }
}
