package parserOnJavaV1;

import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;

public class ParserElements extends Thread {
	private List<String> badIdLinks, excludeLinks, links;
	private int insertCount;
	private String str;
	private Statement statement;
	private String tableName;

	ParserElements(String str, List<String> links, List<String> excludeLinks, List<String> badIdLinks,
			Statement statement, String tableName) {
		this.str = str;
		this.links = links;
		this.excludeLinks = excludeLinks;
		this.badIdLinks = badIdLinks;
		this.statement = statement;
		this.tableName = tableName;

		log("Thread started" + this);
		start();
	}

	@Override
	public void run() {
		Document doc;
		try {
			 doc = Jsoup.connect("https://izida.biz" + str).get();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		//log(str);
	
		String title, priceStr, artidStr, image = null, description = null;
		int artid, price;
		// Поиск заголовка
		title = doc.title();
		// log("Заголовок : " + title);
		// Поиск цены
		Elements elPrice = doc.getElementsByClass("price-2");
		priceStr = elPrice.html();
		// log("Цена: " + priceStr);
		try {
			price = Integer.parseInt(priceStr.split(" ")[0]);
		} catch (NumberFormatException e) {
			badIdLinks.add(str);
			log("BAD PRICE");
			return;
		}
		// log(price);

		// Поиск артикула
		Elements elID = doc.getElementsByClass("dop");
		// log(elID.html());
		artidStr = elID.html();
		try {
			artid = Integer.parseInt(artidStr.split(" ")[1]);
		} catch (NumberFormatException e) {
			badIdLinks.add(str);
			log("BAD ID");
			return;
		}
		// log(artid+"");

		Elements elDescription = doc.getElementsByTag("meta");
		for (Element inputElement : elDescription) {

			String propertyLoop = inputElement.attr("property");
			String contentLoop = inputElement.attr("content");

			if (propertyLoop.equals("og:image")) {
				// log("Фото: "+contentLoop);
				image = contentLoop;
			}
			if (inputElement.attr("name").equals("description")) {
				// log("Описание: "+contentLoop);
				description = contentLoop;
			}
		}
		// log(title+" "+description);

		Elements elHref = doc.getElementsByTag("a");

		for (Element input : elHref) {
			String href = input.attr("href");

			if (href.contains("/ru/item/") && !links.contains(href) && !href.contains("izida.biz")) {
				// exception link(mirrors)
				if (!excludeLinks.contains(href)) {

					links.add(href);
				}
			}
		}

		try {
			statement.executeUpdate("INSERT INTO " + tableName + "(title, price, artid, description, image)"
					+ "VALUES ('" + title.toString() + "','" + price + "','" + artid + "','" + description + "','"
					+ image + "')");

			// imageSaver = new ImageSaver();
			ImageSaver.saveImage(image, artid + "", tableName);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void log(Object line) {
		System.out.println(line.toString());
	}
}
