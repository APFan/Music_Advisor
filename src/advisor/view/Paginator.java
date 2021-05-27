package advisor.view;

import java.util.ArrayList;
import java.util.List;

public class Paginator {
    private List<List<String>> content = new ArrayList<>();
    private final int pageEntries;
    private int curIndex = 0;

    public Paginator(int pageEntries) {
        this.pageEntries = pageEntries;
    }

    public void setContent(List<List<String>> content) {
        this.content = content;
        curIndex = 0;
    }

    public void showContent() {
        if (content.size() == 0) {
            System.out.println("no content");
            return;
        }
        content.subList(curIndex, Math.min(curIndex + pageEntries, content.size())).forEach(list -> {
            list.forEach(System.out::println);
            System.out.println();
        });

        System.out.println("---PAGE " + (curIndex / pageEntries + 1) + " OF " + (content.size() + pageEntries - 1) / pageEntries + "---");
    }

    public void prev() {
        if (content.size() == 0) {
            System.out.println("no content");
        } else if (curIndex < pageEntries) {
            System.out.println("No more pages.");
        } else {
            curIndex -= pageEntries;
            showContent();
        }
    }

    public void next() {
        if (content.size() == 0) {
            System.out.println("no content");
        } else if (curIndex > content.size() - pageEntries - 1) {
            System.out.println("No more pages.");
        } else {
            curIndex += pageEntries;
            showContent();
        }
    }
}