package ru.homedepot.Common;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.homedepot.Entity.Link;

import java.util.ArrayList;
import java.util.List;

public class SiteLoader {

    private final String FIRST_PAGE = "http://www.homedepot.com/";
    private final String PREFIX_PAGE = "http://www.homedepot.com";

    public void Start() {
        List<Link> firstLevelMenu = buildFirstLevelLinks();

        for (Link link : firstLevelMenu) {
            List<Link> secondLevel = loadSecondLevelItems(link);

            for (Link secondLink : secondLevel) {
                System.out.println("Link: " + secondLink.getLinkName());

                if( !secondLink.getLinkName().equals("Appliance Savings") ) {
                    // пропускаем первую линку

                    String body = PageLoader.Loader(secondLink.getLinkUrl());

                }



            }

        }
    }

    private List<Link> loadSecondLevelItems(Link link) {
        String body = PageLoader.Loader(link.getLinkUrl());

        Document doc = Jsoup.parse(body);
        Elements sections = doc.select("div.col__2-12.col__2-12--xs.col__2-12--sm.col__2-12--md.col__2-12--lg.col__2-12--xl.col__rail--lg")
                .first().select("div.col__12-12.col__12-12--xs.col__12-12--sm.col__12-12--md.col__12-12--lg.col__12-12--xl.pad");

        List<Link> secondLevel = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            String linkUrl = PREFIX_PAGE + sections.get(i).select("a").attr("href");
            String linkName = sections.get(i).select("a").html();

            Link lnk = new Link(linkUrl, linkName);
            secondLevel.add(lnk);
        }

        return secondLevel;
    }

    private List<Link> buildFirstLevelLinks() {
        Document doc = Jsoup.parse(loadFirstPage());

        Elements sections = doc.select("div.MainFlyout").select("section");
        Elements menuItem = sections.get(0).select("div.MainFlyout__level1Wrapper").select("ul").select("li");

        List<Link> firstLevelMenu = new ArrayList<>();
        for (int i = 0; i < menuItem.size(); i++) {
            Element el = menuItem.get(i);
            String linkUrl = el.select("a").attr("href");
            String linkName = el.select("a").html();
            Link linkItem = new Link(linkUrl, linkName);
            firstLevelMenu.add(linkItem);
        }
        return firstLevelMenu;
    }

    private String loadFirstPage() {
        return PageLoader.Loader(FIRST_PAGE);
    }
}
