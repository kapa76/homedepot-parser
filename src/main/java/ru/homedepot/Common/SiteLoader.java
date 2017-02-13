package ru.homedepot.Common;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.homedepot.Entity.Item;
import ru.homedepot.Entity.Link;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SiteLoader {

    private final String FIRST_PAGE = "http://www.homedepot.com/";
    private final String PREFIX_PAGE = "http://www.homedepot.com";

    private Map<String, Item> allShopItems = new HashMap<>();

    private Set<String> queueCancelled = new HashSet<>();

    public void Start() {
        List<Link> firstLevelMenu = buildFirstLevelLinks();
        addQueueCancelled(firstLevelMenu);

        for (Link link : firstLevelMenu) {
            List<Link> secondLevel = loadSecondLevelItems(link);
            for (Link secondLink : secondLevel) {
                if (!secondLink.getLinkName().equals("Appliance Savings")) {
                    // пропускаем первую линку
                    Map<String, Item> parsedItems = loadDataBySecondLink(secondLink);
                    mergeItems(parsedItems);
                }
            }
            addQueueCancelled(secondLevel);
        }
        printItems();
    }

    private void printItems() {
        for (String key : allShopItems.keySet()) {
            Item item = allShopItems.get(key);
            System.out.println(item.toString());
        }
    }

    private void addQueueCancelled(List<Link> links) {
        for (Link link : links) {
            queueCancelled.add(link.getLinkUrl());
        }
    }

    private Map<String, Item> loadDataBySecondLink(Link secondLink) {
        Map<String, Item> parsedItems = new HashMap<>();

        List<Link> ll1 = parseFirstData(secondLink);

        for (Link item : ll1) {
            //если у нас еще не товары а все какие-то группы
            System.out.println("Link parse data: " + item.getLinkUrl());
            List<Link> ll2 = parseItemGroup(item);

            for (Link linkItem : ll2) {
                if (!queueCancelled.contains(linkItem.getLinkUrl())) {
                    System.out.println("Link pars item group: " + linkItem.getLinkUrl());
                    parsedItems.putAll(parseItem(linkItem));
                }
            }
            addQueueCancelled(ll2);
        }
        addQueueCancelled(ll1);

        return parsedItems;
    }

    private void mergeItems(Map<String, Item> parsedItems) {
        allShopItems.putAll(parsedItems);
    }

    private Map<String, Item> parseItem(Link link) {
        String body = PageLoader.Loader(link.getLinkUrl());
        Document doc = Jsoup.parse(body);

        Map<String, Item> mItems = new HashMap<>();
        try {
            if (doc.select("div.col__8-12.col__8-12--xs.col__9-12--sm.col__10-12--md.pod-group__wrapper").size() > 0) {
                Elements items = doc.select("div.col__8-12.col__8-12--xs.col__9-12--sm.col__10-12--md.pod-group__wrapper").first()
                        .select("div.mainContent").first()
                        .select("div.plp-grid-view").first()
                        .select("div.pod-plp__container").first()
                        .select("div.pod-inner");

                mItems.putAll(generateItemFromPage(items));
                while (hasNextPage(doc)) {
                    doc = Jsoup.parse(PageLoader.Loader(getNextPage(doc)));
                    items = doc.select("div.col__8-12.col__8-12--xs.col__9-12--sm.col__10-12--md.pod-group__wrapper").first()
                            .select("div.mainContent").first()
                            .select("div.plp-grid-view").first()
                            .select("div.pod-plp__container").first()
                            .select("div.pod-inner");
                    mItems.putAll(generateItemFromPage(items));
                }

            }
        } catch (Exception e) {
            String a = "";
        }
        if (mItems.size() == 0) {
            List<Link> ll1 = parseItemGroup(link);
            for (Link linkItem : ll1) {
                if (!queueCancelled.contains(linkItem.getLinkUrl())) {
                    System.out.println("Link pars item group: " + linkItem.getLinkUrl());
                    allShopItems.putAll(parseItem(linkItem));
                }
            }
        }

        System.out.println("Loaded Items: " + mItems.size());
        return mItems;
    }

    private String getNextPage(Document doc) {
        Elements el = doc.select("nav.hd-pagination").select("li");
        return PREFIX_PAGE + el.get(el.size() - 1).select("a").attr("href");
    }

    private boolean hasNextPage(Document doc) {
        Elements elems = doc.select("nav.hd-pagination");
        if (elems.size() > 0) {
            Elements el = doc.select("nav.hd-pagination").select("li");
            int count = el.size();
            if (el.get(count - 1).select("a").attr("href").length() > 0)
                return true;
            else return false;
        } else
            return false;
    }

    private Map<? extends String, ? extends Item> generateItemFromPage(Elements items) {
        Map<String, Item> mItems = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            String imageUrl = items.get(i).select("div.plp-pod__image").select("a").select("img").first().attr("src");
            String description = items.get(i).select("div.pod-plp__description").select("a").html();
            String modelName = items.get(i).select("div.pod-plp__model").html();
            String rating = items.get(i).select("div.pod-plp__ratings").select("a").first().select("span").attr("rel");
            String priceSpecial = items.get(i).select("div.price__special").select("span").html();
            String price = items.get(i).select("div.price").text();
            Item item = new Item(imageUrl, description, modelName, rating, priceSpecial, price);
            mItems.put(item.getKey(), item);
        } // если еще есть товары на странице, то пойти по страницам.
        return mItems;
    }

    private List<Link> parseItemGroup(Link item) {
        String body = PageLoader.Loader(item.getLinkUrl());
        Document doc = Jsoup.parse(body);
        Elements elems = doc.select("div.col__4-12.col__4-12--xs.col__4-12--sm.col__4-12--md.col__4-12--lg.col__4-12--xl");
        List<Link> arr = new ArrayList<>();
        for (int i = 0; i < elems.size(); i++) {
            String url = PREFIX_PAGE + elems.get(i).select("div.content_image").select("a").attr("href");
            String name = elems.get(i).select("div.content").select("p").html();
            Link link = new Link(url, name);
            arr.add(link);
        }
        if (arr.size() == 0) {
            if (doc.select("div.col__8-12.col__8-12--xs.col__9-12--sm.col__10-12--md.pod-group__wrapper").size() > 0) {
                allShopItems.putAll(parseItem(item));
            }
        } else {
            System.out.println("Loaded parseItemGroup: " + arr.size());
        }
        return arr;
    }

    private List<Link> parseFirstData(Link link) {
        String body = PageLoader.Loader(link.getLinkUrl());
        Document doc = Jsoup.parse(body);
        Elements elems = doc.select("div.col__4-12.col__4-12--xs.col__4-12--sm.col__4-12--md.col__4-12--lg.col__4-12--xl.pad");
        List<Link> arr = new ArrayList<>();
        for (int i = 0; i < elems.size(); i++) {

            try {
                Element h3 = elems.get(i).select("div.content").select("h3").first();
                Link l = null;
                if (h3 == null) {
                    String name = elems.get(i).select("div.content_image").select("a").attr("title");
                    String url = PREFIX_PAGE + elems.get(i).select("div.content_image").select("a").attr("href");
                    l = new Link(url, name);
                } else {
                    String name = elems.get(i).select("div.content").select("h3").first().select("a").html();
                    String url = PREFIX_PAGE + elems.get(i).select("div.content").select("h3").first().select("a").attr("href");
                    l = new Link(url, name);
                }
                arr.add(l);
            } catch (Exception e) {
                System.out.println("Exception url: " + link.getLinkUrl());
            }
        }
        if (arr.size() == 0) {
            if (doc.select("div.col__8-12.col__8-12--xs.col__9-12--sm.col__10-12--md.pod-group__wrapper").size() > 0) {
                allShopItems.putAll(parseItem(link));
            }
        } else {
            System.out.println("Loaded parseFirstData: " + arr.size());
        }
        return arr;
    }

    private List<Link> loadSecondLevelItems(Link link) {
        String body = PageLoader.Loader(link.getLinkUrl());
        Document doc = Jsoup.parse(body);
        Elements sections = doc.select("div.col__2-12.col__2-12--xs.col__2-12--sm.col__2-12--md.col__2-12--lg.col__2-12--xl.col__rail--lg")
                .first().select("div.col__12-12.col__12-12--xs.col__12-12--sm.col__12-12--md.col__12-12--lg.col__12-12--xl.pad");

        List<Link> secondLevel = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            Elements li = sections.get(i).select("li");
            if (li.size() > 0) {
                for (int j = 0; j < li.size(); j++) {
                    if (li.get(j).select("a").size() > 0) {
                        String linkUrl = PREFIX_PAGE + li.get(j).select("a").attr("href");
                        String linkName = li.get(j).select("a").html();
                        Link lnk = new Link(linkUrl, linkName);
                        secondLevel.add(lnk);
                    }
                }
            } else {
                String linkUrl = PREFIX_PAGE + sections.get(i).select("a").attr("href");
                String linkName = sections.get(i).select("a").html();
                Link lnk = new Link(linkUrl, linkName);
                secondLevel.add(lnk);
            }
        }
        System.out.println("Loaded second level options: " + secondLevel.size());
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
        System.out.println("Loaded first level options: " + firstLevelMenu.size());
        return firstLevelMenu;
    }

    private String loadFirstPage() {
        return PageLoader.Loader(FIRST_PAGE);
    }

    public void saveToFile(String fileName) {
        FileWriter fw = null;
        try {
            File newTextFile = new File(fileName);
            fw = new FileWriter(newTextFile);
            for (String key : allShopItems.keySet()) {
                Item item = allShopItems.get(key);
                fw.write(item.toString());
            }
            fw.close();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }
}
