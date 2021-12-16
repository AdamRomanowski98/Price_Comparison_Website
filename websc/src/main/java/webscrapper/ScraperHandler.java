package webscrapper;


import java.util.List;

public class ScraperHandler {

    private static List<Thread> scraperList;

    //empty constructor
    public ScraperHandler(){
    }

    //Getter
    public static List<Thread> getScraperList(){
        return scraperList;
    }

    //Setter
    public static void setScraperList(List<Thread> list){
        scraperList = list;
    }

    //Method to start scraping
    public void startScraping(){
        for(Thread scraper : scraperList){
            scraper.start();
        }
    }

    


}
