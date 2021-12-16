package PriceChecker;

import java.io.IOException;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import webscrapper.*;
import org.jsoup.nodes.Element;





class WebScraperTest {




    @Test
    @DisplayName("Session Factory Test")
    void sessionFactoryTest(){


        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Hibernate hibernate = (Hibernate) context.getBean("hibernate");

        AppConfig cfg = new AppConfig();

        try {
            hibernate.setSessionFactory(cfg.sessionFactory());
        } catch (Exception ex) {
            fail("Error: Can't Set Session Factory: " + ex.getMessage());
        }

        assertNotNull(hibernate.getSessionFactory());
        System.out.println("Test 1 has been completed");

    }

    @Test
    @DisplayName("Test adding monitors")
    void addingMonitorsTest(){

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Hibernate hibernate = (Hibernate) context.getBean("hibernate");

        MonitorAnnotation monitor = new MonitorAnnotation();

        String model1 = "test name";
        String manufacturer = "test manufacturer";
        Float price = 5.5f;
        String description = "test description";
        String imageUrl = "test ImageUrl";

        monitor.setModel(model1);
        monitor.setManufacturer(model1);
        monitor.setDescription(description);
        monitor.setImageUrl(imageUrl);

        ArrayList<Integer> monitors = null;
        try {
            monitors = hibernate.addMonitor(manufacturer, model1, description, price, imageUrl, imageUrl);

        }catch(Exception ex){
            fail("Error: Can't add monitor: " + ex.getMessage());
        }

        assertNotNull(monitors);


    }


    @Test
    @DisplayName("Scraping monitors Test")
    void scrapeMonitorsTest() throws IOException {

        Document doc = Jsoup.connect("https://www.box.co.uk/monitors/page/1").get();

        Elements monitors = doc.select("div.product-list-item");

        for (Element monitor: monitors){
            assertNotNull(monitor);
            System.out.println(monitor);
        }

    }

    @Test
    @DisplayName("Test scraperHandler")
    void scraperHandlerTest(){

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ScraperHandler handler = (ScraperHandler) context.getBean("scraperHandler");

        assertNotNull(handler.getScraperList());

    }


    @Test
    @DisplayName("Threads test")
    void ThreadsTest() {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ScraperHandler handler = (ScraperHandler) context.getBean("scraperHandler");

        try {
            for (Thread thread : ScraperHandler.getScraperList()) {
                thread.start();
            }
        } catch (Exception ex) {
            fail("Failed to start the threads" + ex.getMessage());
        }

        //Test if Threads are running
        for (Thread scraperThread : ScraperHandler.getScraperList()) {
            assertEquals(true, scraperThread.isAlive());
        }

        System.out.println("Test 3 has been completed");
    }




}
