package parserOnJavaV1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Statement;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParserMultipleCategories {
	private List<String> badIdLinks, excludeLinks, parsedLinks, invalidPropertyLinks = new ArrayList<>();
	private int exportCategory;
	private String startLink;
	private Statement statement;
	private String categoryName;
	private HashMap<String, String> categoriesLinks = new HashMap<>();
	private TextFileExport2 tfe = new TextFileExport2();
	private HashMap<Integer, String> pagesLinks = new HashMap<>();

	ParserMultipleCategories(String startLink, Statement statement, int exportId, int exportCategory) {
		this.startLink = startLink;
		this.statement = statement;
		this.exportCategory = exportCategory;
		tfe.setIdAndCategory(exportId);
	}

	public void startParsingMultipleCategories() throws Exception {
		clearExportSQLFile();
		parseCategoriesLinksAndNames(startLink);

		for (Map.Entry<String, String> entry : categoriesLinks.entrySet()) {
			// parsedLinks = parseSecondLevelCategories(entry.getValue());
			pagesLinks.clear();
			parseProducts(entry.getKey(), parseProductsLinks(parsePagesLinks(entry.getValue())), exportCategory);
			exportCategory++;
			log("LOG " + entry.getKey());
		}
		tfe.exportId();
		tfe.closeStreams();
		log("Ivalid property links\n"+ invalidPropertyLinks);
	}

	private void parseProducts(String categoryName, ArrayList<String> list, int exportCategory) throws Exception {
		log("Starting parsing elements");
		String title = null, priceStr = null, artidStr = null, image = null, description = null;
		int artid = 0, price = 0;
		for (String link : list) {
			Document doc = Jsoup.connect("https://izida.biz" + link).get();
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
				price = Math.round(Float.parseFloat(priceStr.split(" ")[0]));
				price++;
				/*invalidPropertyLinks.add(link);
				log("BAD PRICE");
				continue;*/
			}
			// log(price);

			// Поиск артикула
			Elements elID = doc.getElementsByClass("dop");
			// log(elID.html());
			artidStr = elID.html();
			try {
				artid = Integer.parseInt(artidStr.split(" ")[1]);
			} catch (NumberFormatException e) {
				invalidPropertyLinks.add(link);
				log("BAD ID");
				continue;
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
			/////////////
			new ImageSaver().saveImage(image, artid + "", categoryName + "_2nd");
			log("Saved " + artid);
			tfe.export(categoryName + "_2nd", title, description, artid, price, exportCategory);
		}

		log("Finished parsing elements");
	}

	private void parseCategoriesLinksAndNames(String startLink) throws Exception {
		Document doc = Jsoup.connect(startLink).get();
		log("Starting scrap categories links and names");
		Elements elUl = doc.getElementsByTag("ul");
		Elements elDivs = doc.getElementsByTag("div");

		String categoriesLink, categoriesName;

		for (Element inputElements : elUl) {
			String resultClass = inputElements.attr("class");

			/*
			 * if(resultClass.equals("black black-in")) { log("product page");
			 * 
			 * for(Element inputDivs : elDivs) { String resultDivs =
			 * inputDivs.attr("class");
			 * 
			 * if(resultDivs.equals("img-h-2")) { //print(inputDivs.toString());
			 * log(inputDivs.getElementsByTag("a").attr("href")); } } }
			 */
			if (resultClass.equals("white white-in")) {
				log("catalog page with categories");

				for (Element inputDivs : elDivs) {
					String resultDivs = inputDivs.attr("class");
					if (resultDivs.equals("img-h")) {
						categoriesName = inputDivs.getElementsByTag("img").attr("alt");
						categoriesLink = "https://izida.biz" + inputDivs.getElementsByTag("a").attr("href");
						categoriesLinks.put(Cyr2lat.cyr2lat(categoriesName), categoriesLink);
						log(categoriesName);
						log(categoriesLink);

					}
				}
			}
		}
		log("Finished scrapping categories links and names");
	}
	/*
	 * private ArrayList<String> parseSecondLevelCategories(String link) throws
	 * Exception{ Document doc = Jsoup.connect(link).get();
	 * 
	 * ArrayList<String> links = new ArrayList<>(); Elements elUl =
	 * doc.getElementsByTag("ul"); Elements elDivs = doc.getElementsByTag("div");
	 * 
	 * for (Element inputElements : elUl) { String resultClass =
	 * inputElements.attr("class");
	 * 
	 * if (resultClass.equals("black black-in")) { log("product page");
	 * 
	 * for (Element inputDivs : elDivs) { String resultDivs =
	 * inputDivs.attr("class");
	 * 
	 * if (resultDivs.equals("img-h-2")) { //
	 * log(inputDivs.getElementsByTag("a").attr("href")); if
	 * (!excludeLinks.contains(inputDivs.getElementsByTag("a").attr("href"))) {
	 * links.add(inputDivs.getElementsByTag("a").attr("href")); } } } } if
	 * (resultClass.equals("white white-in")) { log("catalog page");
	 * 
	 * for (Element inputDivs : elDivs) { String resultDivs =
	 * inputDivs.attr("class"); if (resultDivs.equals("img-h")) { //
	 * log(inputDivs.getElementsByTag("a").attr("href"));
	 * parseSecondLevelCategories("https://izida.biz" +
	 * inputDivs.getElementsByTag("a").attr("href")); } } } } return links; }
	 */

	private HashMap<Integer, String> parsePagesLinks(String str) throws Exception {
		ArrayList<Integer> pagesIndex = new ArrayList<>();
		
		Document doc = Jsoup.connect(str).get();

		for (Element inputElements : doc.getElementsByTag("div")) {
			// log(inputElements.attr("class"));
			if (inputElements.attr("class").equals("pages")) {
				for (Element element : inputElements.getElementsByTag("a")) {
					if (!pagesLinks.containsValue(element.attr("href")) && element.attr("href").indexOf("=") != -1) {
						pagesLinks.put(
								Integer.parseInt(element.attr("href").substring(element.attr("href").indexOf("=") + 1)),
								element.attr("href"));
						log("index "+ Integer.parseInt(element.attr("href").substring(element.attr("href").indexOf("=") + 1))
								+ " link " + element.attr("href") + " added");
					}
				}
			}
		}

		for (Map.Entry<Integer, String> entry : pagesLinks.entrySet()) {
			pagesIndex.add(entry.getKey());
		}
		Collections.sort(pagesIndex);
		// log(pagesIndex);
		try {
			log(pagesIndex.size() +"pagesIndex"+"\n"+pagesLinks+"pagesLinks");
			if (pagesIndex.size() > 2) {
				if ((pagesIndex.get(pagesIndex.size() - 1) - pagesIndex.get(pagesIndex.size() - 2)) != 1) {
					for (Map.Entry<Integer, String> entry : pagesLinks.entrySet()) {
						if (entry.getKey() == pagesIndex.get(pagesIndex.size() - 2)) {
							pagesIndex.clear();
							parsePagesLinks("https://izida.biz" + entry.getValue());

						}
					}
				}
			}else {
				pagesLinks.put(0, str.substring(17));
			}
		} catch (Exception e) {
			log("EXCEPTION");
		}
		return pagesLinks;
	}

	private ArrayList<String> parseProductsLinks(HashMap<Integer, String> pagesLinks) throws Exception {
		ArrayList<String> links = new ArrayList<>();
		for (Map.Entry<Integer, String> pagesLink : pagesLinks.entrySet()) {
			Document doc = Jsoup.connect("https://izida.biz" + pagesLink.getValue()).get();

			for (Element inputElements : doc.getElementsByTag("ul")) {
				String resultClass = inputElements.attr("class");

				if (resultClass.equals("black black-in")) {
					log("product page");

					for (Element inputDivs : doc.getElementsByTag("div")) {
						String resultDivs = inputDivs.attr("class");

						if (resultDivs.equals("img-h-2")) {
							// log(inputDivs.getElementsByTag("a").attr("href"));
							if (!links.contains(inputDivs.getElementsByTag("a").attr("href"))) {
								links.add(inputDivs.getElementsByTag("a").attr("href"));
							}
						}
					}
				}
			} // for
		}
		return links;
	}

	private void clearExportSQLFile() {
		try (OutputStreamWriter clearExportFile = new OutputStreamWriter(
				new FileOutputStream(new File("C://Eclipse/parserOnJavaV1/export/oc_export_to_db.sql")), "UTF-8");) {
			clearExportFile.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void log(Object s) {
		System.out.println(s.toString());
	}

}
