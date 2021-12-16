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
public class LaptopsDirectScrapper extends Thread {

    private final int CRAWL_DELAY = 1;

    volatile private boolean runThread = false;


    /**
    * Scrape data from laptopsdirect.co.uk
    */
    @Override
    public void run(){

        runThread = true;
        while(runThread){

        for (int pages = 1; pages < 15; pages++) {
            try {
                Document doc = Jsoup.connect("https://www.laptopsdirect.co.uk/ct/tvs-projectors-and-monitors/monitors?pageNumber="+pages).userAgent("Mozilla/5.0 (X11; Linux x86_64)").get();

                Elements monitors = doc.select(".OfferBox");

                for (int i = 0; i < monitors.size(); i++) {


                    //Scrape model
                    Elements model = monitors.get(i).select("a.offerboxtitle");
                    String brand = model.text();
                    String[] brandArray = brand.split("\\s+");
                    brand = brandArray[0].toLowerCase();

                    if (brand.contains("refurbished"))
                        brand = brandArray[1].toLowerCase();


                    //Scrape description
                    Elements description = monitors.get(i).select("div.productInfo");


                    //Scrape price
                    Elements priceText = monitors.get(i).select(".offerprice");



                    String priceString = priceText.text().substring(1).replace(",", "");
                    String[] priceArray = priceString.split("\\s+");
                    String priceArrayString = priceArray[0];
                    Float price = Float.parseFloat(priceArrayString);


                    //Scrape image
                    Elements image = monitors.get(i).select("div.sr_image a img");
                    String imageURL = "https://www.laptopsdirect.co.uk" + image.attr("src");

                    //Scrape URl

                    Elements url = monitors.get(i).select("div.sr_image a");
                    String urlHref = url.attr("href");


                    System.out.println("Brand: " + brand + " " + "Model: " + model+ " " + "Price: " + price + " " + "Description: " + description.text() + " " + "Url: " + "https://laptopsdirect.co.uk/" + urlHref + " img url: " + imageURL);


                    //Add monitors to database
                    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                    Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                    hibernate.addMonitor(brand, model.text(), description.text(), price, imageURL, "https://laptopsdirect.co.uk/"+urlHref );
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







