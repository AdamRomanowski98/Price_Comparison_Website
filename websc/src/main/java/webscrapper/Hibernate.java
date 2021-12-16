package webscrapper;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;



 /**
 * Hibernate class that uses annotation to map between a Monitor object and monitors in database
 * 
 */
public class Hibernate {


    //Creates new Sessions when we need to interact with the database
    private SessionFactory sessionFactory;


    /**
     * Empty constructor
     */
    Hibernate() {
    }

    
    public ArrayList<Integer> addMonitor(String manufacturer, String model, String description, float price, String imageUrl, String urlHref) {

        //Get a new Session instance from the session factory
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        if (!checkMonitorDuplicates("model", model, "manufacturer", manufacturer)) {

            MonitorAnnotation monitor = new MonitorAnnotation();
            ComparisonAnnotation comparison = new ComparisonAnnotation();

            //Set monitors
            monitor.setModel(model);
            monitor.setManufacturer(manufacturer);
            monitor.setDescription(description);
            monitor.setImageUrl(imageUrl);

            //Add monitors to database
            session.save(monitor);

            comparison.setMonitorId(monitor.getId());
            comparison.setPrice(price);
            comparison.setUrl(urlHref);
            session.save(comparison);

            
            session.getTransaction().commit();

            ArrayList<Integer> monitors = new ArrayList<>();

            monitors.add(monitor.getId());
            monitors.add(comparison.getId());

            //Close the session and release database connection
            session.close();
            System.out.println("Monitor added to database with ID: " + monitor.getId());
            return monitors;
        } else if (checkMonitorDuplicates("description", description, "image_url", imageUrl)) {
            session.close();
            System.out.println("Monitor is already in DB");
            return new ArrayList<>();
        } else if (checkComparisonDuplicates("url", urlHref)) {
            session.close();
            System.out.println("Comparison is already in DB");
            return new ArrayList<>();
        } else {
            //Get a new Session instance from the session factory
            MonitorAnnotation existingMonitor = matchMonitor("model", model);

            ComparisonAnnotation comparison = new ComparisonAnnotation();

            //Add Comparison to database
            comparison.setMonitorId(existingMonitor.getId());
            comparison.setPrice(price);
            comparison.setUrl(urlHref);
            session.save(comparison);

          
            session.getTransaction().commit();

            session.close();
            System.out.println("New Comparison added to database with ID: " + comparison.getId());
            return new ArrayList<>();
        }

    }

    public boolean checkMonitorDuplicates(String column1, String data1, String column2, String data2) {

        Session session = sessionFactory.getCurrentSession();

        List<MonitorAnnotation> monitorList = session.createQuery("from MonitorAnnotation where " + column1 + " = '" + data1 + "' AND " + column2 + "= '" + data2 + "'").getResultList();

        return monitorList.size() > 0;
    }

    public boolean checkComparisonDuplicates(String column1, String data1) {

        Session session = sessionFactory.getCurrentSession();

        List<ComparisonAnnotation> comparisonList = session.createQuery("from ComparisonAnnotation where " + column1 + " = '" + data1 + "'").getResultList();

        return comparisonList.size() > 0;
    }

    public MonitorAnnotation matchMonitor(String column1, String data1) {

        Session session = sessionFactory.getCurrentSession();

        List<MonitorAnnotation> monitorList = session.createQuery("from MonitorAnnotation where " + column1 + " = '" + data1 + "'").getResultList();

        return monitorList.get(0);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void shutDown() {
        sessionFactory.close();
    }
}



