package dev.snbv2.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CatalogApplication {

	private static final Log LOG = LogFactory.getLog(CatalogApplication.class);

	@Autowired
	CatalogRepository catalogRepository;

	public static void main(String[] args) {
		SpringApplication.run(CatalogApplication.class, args);
	}

	@EventListener(ApplicationStartedEvent.class)
	public void loadTestData() {

		LOG.info("Loading test data.");

		CatalogItem catalogItem = new CatalogItem();
		catalogItem.setId(100);
		catalogItem.setName("Turtle Beach Recon 200 Headset");
		catalogItem.setImageSource("https://images-na.ssl-images-amazon.com/images/I/41SzNJfEQwL._AC_SR400,600_.jpg");
		catalogItem.setDescription(
				"Powerful amplified audio: Immerse yourself in your games with rechargeable, battery powered amplified sound from your Xbox and PlayStation");
		catalogItem.setAmount(49.95d);
		catalogRepository.save(catalogItem);

		catalogItem = new CatalogItem();
		catalogItem.setId(200);
		catalogItem.setName("Rustic Town Buffalo Leather Toiletry Bag");
		catalogItem.setImageSource("https://m.media-amazon.com/images/I/51ZYJsc6BhL._AC_SY161_.jpg");
		catalogItem.setDescription(
				"Multiple Compartments for Essentials: With a roomy interior and numerous compartments, this womens and mens toiletry bag keeps your shaving, personal hygiene & cosmetic products easily accessible.");
		catalogItem.setAmount(31.99d);
		catalogRepository.save(catalogItem);

		catalogItem = new CatalogItem();
		catalogItem.setId(300);
		catalogItem.setName("Engraved Mini Oak Aging Cask");
		catalogItem.setImageSource("https://m.media-amazon.com/images/I/5175N5yWpyL._AC_SY161_.jpg");
		catalogItem.setDescription(
				"Medium Charred interior | 10 Liters American Oak Aging Barrel | Barrel Capacity is an approximate");
		catalogItem.setAmount(79.49d);
		catalogRepository.save(catalogItem);

		catalogItem = new CatalogItem();
		catalogItem.setId(400);
		catalogItem.setName("Deluxe Hot Sauce Kit");
		catalogItem.setImageSource("https://m.media-amazon.com/images/I/516tKLkVRzL._AC_SY161_.jpg");
		catalogItem.setDescription(
				"All the ingredients you need in one place: make great tasting hot ones hot sauce dabs, with a gourmet spice blend and ancho pasillas, chipotle, habanero and 5x ghost peppers");
		catalogItem.setAmount(49.95d);
		catalogRepository.save(catalogItem);

		catalogItem = new CatalogItem();
		catalogItem.setId(500);
		catalogItem.setName("This Worx Car Vacuum Cleaner ");
		catalogItem.setImageSource("https://m.media-amazon.com/images/I/51cTT8TEgQS._AC_SY161_.jpg ");
		catalogItem.setDescription(
				"Portable, High Power, Handheld Vacuums w/ 3 Attachments, 16 Ft Cord & Bag - 12v, Auto Accessories Kit for Interior Detailing");
		catalogItem.setAmount(34.99d);
		catalogRepository.save(catalogItem);

		catalogItem = new CatalogItem();
		catalogItem.setId(600);
		catalogItem.setName("TRX All-in-One Suspension Trainer");
		catalogItem.setImageSource("https://m.media-amazon.com/images/I/81WHuOpgHyL._AC_SX466_.jpg");
		catalogItem.setDescription(
				"The TRX All-in-One System is the prime suspension trainer for full-body workouts. This system comes with a suspension trainer, a suspension anchor, a door anchor, a getting-started guide and access to the TRX Training Club app.");
		catalogItem.setAmount(99.99d);
		catalogRepository.save(catalogItem);

		catalogItem = new CatalogItem();
		catalogItem.setId(700);
		catalogItem.setName("UGREEN 100W USB C Wall Charger");
		catalogItem.setImageSource("https://m.media-amazon.com/images/I/61kMoKJEcXL._AC_SX679_.jpg");
		catalogItem.setDescription(
				"4-in-1 Multiport Charger for More Devices: The Ugreen Nexode USB C Charger has 3 USB-C ports and a USB-A port supplying up to 100W of power. Charge a Phone, tablet, laptop, and more at the same time.");
		catalogItem.setAmount(67.99d);
		catalogRepository.save(catalogItem);

		catalogItem = new CatalogItem();
		catalogItem.setId(800);
		catalogItem.setName("Anker PowerConf C300 Smart Full HD Webcam");
		catalogItem.setImageSource("https://m.media-amazon.com/images/I/61DeUZhKs2L._AC_SX679_.jpg");
		catalogItem.setDescription(
				"Make a great first impression with clients and impress your boss with PowerConf C300s crisp HD webcam 1080p/60FPS camera with true-to-life colors.");
		catalogItem.setAmount(99.99d);
		catalogRepository.save(catalogItem);

		catalogItem = new CatalogItem();
		catalogItem.setId(900);
		catalogItem.setName("SAMSUNG 23.5in Curved Computer Monitor");
		catalogItem.setImageSource("https://m.media-amazon.com/images/I/91ubktnbNVL._AC_SX679_.jpg");
		catalogItem.setDescription(
				"The 1800R curvature of the screen lets you enjoy big, bold and stunning panoramic views while you work or play at home or the office");
		catalogItem.setAmount(139.99d);
		catalogRepository.save(catalogItem);

		LOG.info("Finished loading test data.");
	}

}
