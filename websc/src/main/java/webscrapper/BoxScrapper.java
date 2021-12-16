package webscrapper;

import org.jsoup.Jsoup;

import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;



public class BoxScrapper extends Thread{


//Specifies the interval between HTTP requests to the server in seconds.
private final int CRAWL_DELAY = 1;

//Allows us to shut down our application cleanly
volatile private boolean runThread = false;

    @Override
    public void run() {
        runThread = true;

        while (runThread) {

            for (int pages = 1; pages < 30; pages++) {

                try {
                    //Get all of the products on the page
                    Document doc = Jsoup.connect("https://www.box.co.uk/monitors/page/" + pages).get();

                    Elements monitors = doc.select("div.product-list-item");

                    for (int i = 0; i < monitors.size(); ++i) {

                        //Get model and manufacturer
                        Elements model = monitors.get(i).select("div.p-list-section.p-list-section-middle a");
                        String brand = model.text();
                        String[] brandArray = brand.split("\\s");
                        brand = brandArray[0].toLowerCase();

                        //Get description
                        Elements description = monitors.get(i).select(".p-list-points ");

                        //Get price
                        Elements priceText = monitors.get(i).select(".p-list-sell");


                        String priceString = priceText.text().substring(1).replace(",", "");
                        String[] priceArray = priceString.split("\\s+");
                        String priceArrayString = priceArray[0];
                        Float price = Float.parseFloat(priceArrayString);

                        //Get img
                        Elements imageUrlDiv = monitors.get(i).select("table.p-list-image a img");
                        String imageURL = "https://www.box.co.uk" + imageUrlDiv.attr("data-src");

                        //Get url
                        Elements productUrlTable = monitors.get(i).select("table.p-list-image a");
                        String urlHref = productUrlTable.attr("href");


                        System.out.println("Brand: " + brand + " " + "Model: " + model + " " + "Price: " + price + " " + "Description: " + description.text() + " " + "Url: " + "https://www.ebuyer.com" + urlHref + "img url: " + imageURL);

                        //Add monitors to database 
                        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                        Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                        hibernate.addMonitor(brand, model.text(), description.text(), price, imageURL, urlHref);
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

    //Stop thread
    public void stopThread() {
        runThread = false;
    }
}



