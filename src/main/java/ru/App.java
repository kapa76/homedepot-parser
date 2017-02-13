package ru;

import ru.homedepot.Common.SiteLoader;

import java.io.IOException;

public class App {

    private SiteLoader siteLoader = new SiteLoader();

    public App() {
    }

    public void load(){
        siteLoader.Start();
        siteLoader.saveToFile("items.csv");
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Start loader...");
        App app = new App();
        app.load();
    }


}
