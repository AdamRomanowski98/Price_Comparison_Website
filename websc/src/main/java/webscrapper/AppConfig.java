package webscrapper;


import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.*;


@Configuration
public class AppConfig {


    SessionFactory sessionFactory;

    /**
     * ScraperHandler Bean
     *
     * @return scraperHandler
     */
    @Bean
    public ScraperHandler scraperHandler() {
        ScraperHandler scraperHandler = new ScraperHandler();

        List<Thread> scraperList = new ArrayList();
        //scraperList.add(boxScraper());
        scraperList.add(eBuyerScraper());
        scraperList.add(laptopsDirectScraper());
        scraperList.add(neweggScraper());
        //scraperList.add(overclockersScraper());
        ScraperHandler.setScraperList(scraperList);

        // Return Scraper Handler object
        return scraperHandler;
    }

     /**
     * BoxScrapper Bean
     *
     * @return boxScraper
     */
    @Bean
    public BoxScrapper boxScraper() {
        BoxScrapper boxScraper = new BoxScrapper();
        return boxScraper;
    }

     /**
     * EBuyerScrapper Bean
     *
     * @return eBuyerScraper
     */
    @Bean
    public EBuyerScrapper eBuyerScraper() {
        EBuyerScrapper eBuyerScraper = new EBuyerScrapper();
        return eBuyerScraper;
    }


     /**
     * LaptopsDirectScrapper Bean
     *
     * @return laptopsDirectScraper
     */
    @Bean
    public LaptopsDirectScrapper laptopsDirectScraper() {
        LaptopsDirectScrapper laptopsDirectScraper = new LaptopsDirectScrapper();
        return laptopsDirectScraper;
    }

     /**
     * NeweggScrapper Bean
     *
     * @return neweggScraper
     */
    @Bean
    public NeweggScrapper neweggScraper() {
        NeweggScrapper neweggScraper = new NeweggScrapper();
        return neweggScraper;
    }


     /**
     * OverclockersScrapper Bean
     *
     * @return overclockersScraper
     */
    @Bean
    public OverclockersScrapper overclockersScraper() {
        OverclockersScrapper overclockersScraper = new OverclockersScrapper();
        return overclockersScraper;
    }




    //Hibernate Bean
    @Bean
    public Hibernate hibernate() {
        Hibernate hibernate = new Hibernate();
        hibernate.setSessionFactory(sessionFactory());
        return hibernate;
    }




    @Bean
    public SessionFactory sessionFactory(){
        if(sessionFactory == null){
            //Build sessionFatory once only
            try {
                //Create a builder for the standard service registry
                StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();

                //Load configuration from hibernate configuration file.
                //Here we are using a configuration file that specifies Java annotations.
                standardServiceRegistryBuilder.configure("hibernate.cfg.xml");

                //Create the registry that will be used to build the session factory
                StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
                try {
                    //Create the session factory - this is the goal of the init method.
                    sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
                }
                catch (Exception e) {
                        /* The registry would be destroyed by the SessionFactory,
                            but we had trouble building the SessionFactory, so destroy it manually */
                    System.err.println("Session Factory build failed.");
                    e.printStackTrace();
                    StandardServiceRegistryBuilder.destroy( registry );
                }
                //Ouput result
                System.out.println("Session factory built.");
            }
            catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("SessionFactory creation failed." + ex);
                ex.printStackTrace();
            }
        }
        return sessionFactory;
    }



}
