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
public class EBuyerScrapper extends Thread{

    private final int CRAWL_DELAY = 1;

    volatile private boolean runThread = false;


    /**
    * Scrape data from the ebuyer.com 
    */
   @Override
    public void run(){

       runThread = true;
       while(runThread){

        for(int pages = 1; pages < 17; pages++){

            try{
            Document doc = Jsoup.connect("https://www.ebuyer.com/store/Computer/cat/Monitors?page=" +pages).get();

            //Get all products on website
            Elements monitors = doc.select("div.grid-item");

            for (int i = 0; i < monitors.size(); i++){


                //Scrape model
                Elements model = monitors.get(i).select("h3.grid-item__title");
                String brand = model.text();
                String[] brandArray = brand.split("\\s+");
                brand = brandArray[0].toLowerCase();

                if(brand.contains("refurbished"))
                    brand = brandArray[1].toLowerCase();


                //Scrape description
                Elements description = monitors.get(i).select(".grid-item__ksp");


                //Scrape price
                Elements priceText = monitors.get(i).select("div.grid-item__price");




                String priceString = priceText.text().substring(1).replace(",","");
                String[] priceArray = priceString.split("\\s+");
                String priceArrayString = priceArray[1];
                Float price = Float.parseFloat(priceArrayString);


                //Scrape image
                Elements image = monitors.get(i).select("div.grid-item__img a img");
                String imageURL = image.attr("src");

                //Scrape URl

                Elements url = monitors.get(i).select("div.grid-item__img a");
                String urlHref = url.attr("href");


                System.out.println("Brand: " + brand + " " + "Model: " + model + " " + "Price: " +price + " " +"Description: " + description.text() + " " +"Url: " +"https://www.ebuyer.com"+urlHref +"img url: " +imageURL);

                //Add monitors to database
                ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                hibernate.addMonitor(brand, model.text(), description.text(), price, imageURL, "https://www.ebuyer.com/"+urlHref );
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
