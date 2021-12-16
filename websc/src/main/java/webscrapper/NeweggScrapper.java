package webscrapper;

import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Web scraping with JSoup
 */
public class NeweggScrapper extends Thread {

    private final int CRAWL_DELAY = 1;

    volatile private boolean runThread = false;

     /**
     * Scrape data from the www.newegg.com
     */
    @Override
    public void run() {
        runThread = true;

        while (runThread) {

        for(int pages = 1; pages <12; pages++){


            try {

            Document doc = Jsoup.connect("https://www.newegg.com/global/uk-en/p/pl?d=monitors&page="+pages).get();
            Elements monitors = doc.select("div.item-container");


            for(int i=0; i<monitors.size(); i++){

                Elements model = monitors.get(i).select("a.item-title");
                String brand = model.text();
                String[] brandArray = brand.split("\\s");
                brand = brandArray[0].toLowerCase();

                Elements description = monitors.get(i).select("a.item-title");

                Elements priceText = monitors.get(i).select("li.price-current");

                String priceString = priceText.text().substring(1).replace(",", "");
                String[] priceArray = priceString.split("\\s+");
                String priceArrayString = priceArray[0];
                Float price = Float.parseFloat(priceArrayString);

                Elements imageUrl = monitors.get(i).select("img");
                String imageURL = imageUrl.attr("src");

                Elements url = monitors.get(i).select("div.item-container a");
                String urlHref = url.attr("href");


                System.out.println("Brand: " + brand + " " + "Model: " + model+ " " + "Price: " +price + " " +"Description: " + description.text() + " " +"Url: " +urlHref +"img url: " +imageURL);
                ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                hibernate.addMonitor(brand, model.text(), description.text(), price, imageURL, urlHref );
                hibernate.shutDown();
                stopThread();

                try {
                    sleep(1000 * CRAWL_DELAY);
                } catch (InterruptedException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        }
    }

    //Other classes can use this method to terminate the thread.
    public void stopThread() {
        runThread = false;
    }
}