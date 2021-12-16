npmpackage webscrapper;

import java.io.Serializable;
import javax.persistence.*;


/**
 * Represents comparison table
 */
@Entity
@Table(name = "monitors")

public class MonitorAnnotation implements Serializable{


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private int id;

        @Column(name = "model")
        private String model;

        @Column(name = "manufacturer")
        private String manufacturer;

        @Column(name = "description")
        private String description;

        @Column(name = "image_url")
        private String imageUrl;


        /**
        * Empty constructor
        */
        public MonitorAnnotation() {

        }

        //Getters

        public int getId() {
                return id;
        }

        public String getModel() {
                return model;
        }

        public String getManufacturer() {
                return manufacturer;
        }

        public String getDescription() {
                return description;
        }

        public String getImageURL() {
                return imageUrl;
        }

        //Setters

        public void setId(int id){
                this.id = id;
        }

        public void setModel(String model){
                this.model = model;
        }

        public void setManufacturer(String manufacturer){
                this.manufacturer = manufacturer;
        }

        public void setDescription(String description){
                this.description = description;
        }

        public void setImageUrl(String imageUrl){
                this.imageUrl = imageUrl;
        }

}
