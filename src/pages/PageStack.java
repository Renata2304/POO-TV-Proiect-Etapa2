package pages;

import java.util.ArrayList;
import java.util.Stack;

public class PageStack extends Page {

    private PageStack() {

    }

    /**
     * TODO
     * @param stack
     * @param elem
     */
    public static void push(ArrayList<String> stack, final String elem) {
        stack.add(0, elem);
    }

    /**
     * TODO
     * @param stack
     * @return
     */
    public static String pop(final ArrayList<String> stack) {
        String first = null;
        if (!stack.isEmpty()) {
            first = stack.get(0);
            stack.remove(0);
        }
        return first;
    }
}
