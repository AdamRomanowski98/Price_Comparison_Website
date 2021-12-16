package webscrapper;

import java.io.Serializable;
import javax.persistence.*;


/**
 * Represents comparison table
 */
@Entity
@Table(name = "comparison")
public class ComparisonAnnotation implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "price")
    private float price;

    @Column(name = "url")
    private String url;

    @Column(name = "monitor_id")
    private int monitorId;

    /**
     * Empty constructor
     */
    public ComparisonAnnotation(){

    }

    //Getters
    public int getId() {
        return id;
    }

    public Float getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    public int getMonitorId() {
        return monitorId;
    }

   //Setters

    public void setId(int id){
        this.id = id;
    }

    public void setMonitorId(int monitorId){
        this.monitorId = monitorId;
    }

    public void setPrice(Float price){
        this.price = price;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
