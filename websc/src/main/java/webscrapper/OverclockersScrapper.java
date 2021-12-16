package webscrapper;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Web scraping with JSoup
 */
public class OverclockersScrapper extends Thread{

    private final int CRAWL_DELAY = 1;

    volatile private boolean runThread = false;


    /**
     * Scrape data from the www.overclockers.co.uk 
     */
    @Override
    public void run() {
        runThread = true;

        while (runThread) {



        for(int pages = 1; pages < 2; pages++) {

            try {

            Document doc = Jsoup.connect("https://www.overclockers.co.uk/monitors?p=" + pages).userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();

            Elements monitors = doc.select("div.artbox");

            for(int i =0; i<monitors.size(); i++){

                Elements model = monitors.get(i).select(".ProductTitle");
                String brand = model.text();
                String[] brandArray = brand.split("\\s");
                brand = brandArray[0].toLowerCase();

                Elements description = monitors.get(i).select("p.desc a");
                Elements priceText = monitors.get(i).select("span.price");
                String priceString = priceText.text().substring(1).replace(",", "");
                String[] priceArray = priceString.split("\\s+");
                String priceArrayString = priceArray[0];
                Float price = Float.parseFloat(priceArrayString);

                Elements imageUrl = monitors.get(i).select("a.product_img");
                String imageURL = imageUrl.attr("src");


                Elements url = monitors.get(i).select("a.producttitles");
                String urlHref = url.attr("href");


                System.out.println("Brand: " + brand + " " + "Model: " + model.text() + " " + "Price: " +price + " " +"Description: " + description.text() + " " +"Url: " +urlHref +"img url: " +imageURL);


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
