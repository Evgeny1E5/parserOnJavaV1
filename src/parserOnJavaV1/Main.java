package parserOnJavaV1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.io.*;
//import parserOnJavaV1.Cyr2lat;

public class Main {
	private static Connection connection;
	private static List<String> links, excludeLinks, badIdLinks;
	private static Connection con;
	private static Statement statement = null;
//	private static ImageSaver imageSaver;
	private static String tableName;
	private static int exportId, exportCategory;

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
		String inputCommand1, inputCommand2, excludeLinkReadLine;
		ArrayList<ParserElements> parserElementsList = new ArrayList<>();
		links = Collections.synchronizedList(new ArrayList<String>());
		excludeLinks = Collections.synchronizedList(new ArrayList<String>());
		badIdLinks = Collections.synchronizedList(new ArrayList<String>());
		//HashMap<String, String> categoriesLinks = new HashMap<>();

		BufferedReader br3 = new BufferedReader(
				new FileReader(new File("C://Eclipse/parserOnJavaV1/export/excludeLinks.txt")));

		// 1. Set table name
		tableName = "ukrasheniya_noviy_god";

		// Import exclude links
		while ((excludeLinkReadLine = br3.readLine()) != null) {

			excludeLinks.add(excludeLinkReadLine);
		}
		/*
		 * excludeLink.add("/ru/item/35654"); excludeLink.add("/ru/item/26317");
		 * excludeLink.add("/ru/item/56389"); excludeLink.add("/ru/item/56557");
		 * excludeLink.add("/ru/item/56386");
		 */
		inputCommand1 = br.readLine();
		inputCommand2 = br.readLine();

		if (inputCommand1.equals("1")) {
			if (inputCommand2.equals("1")) {
				parseExportIdAndCategory();
				con = connectDB();
				TextFileExport tfe = new TextFileExport();
				tfe.export(statement, tableName, exportId, exportCategory);
				tfe.exportId();
			return;
		} else {
			parseExportIdAndCategory();
			// connection to db
			con = connectDB();
			statement.execute("DELETE FROM " + tableName);

			// parser start position
			parseStartingPosition(inputCommand2);

			// parser loop
			int i = 0;
			while (i < links.size()) {
				parserElementsList
						.add(new ParserElements(links.get(i), links, excludeLinks, badIdLinks, statement, tableName));
				if (i == 11) {
					Thread.sleep(1000);
				}
				Thread.sleep(300);
				i++;
				log(i);
			}
			for (ParserElements ps : parserElementsList) {
				if (ps.isAlive())
					ps.join();
			}

			TextFileExport tfe = new TextFileExport();
			tfe.export(statement, tableName, exportId, exportCategory);
			tfe.exportId();
			// export exclude links, uncomment if needed
			/*
			 * BufferedWriter bw = new BufferedWriter(new FileWriter( new
			 * File("C://Eclipse/parserOnJavaV1/export/excludeLinks.txt")));
			 * log(insertCount+" products added to DB"); for(String str: links) {
			 * bw.write(str); bw.write("\n"); } bw.flush(); bw.close();
			 */
			if (badIdLinks.size() > 0) {
				log("List of errors IDs VVV");
				for (String logStr : badIdLinks) {
					log(logStr);
				}
			}
			br3.close();
			con.close();
		}
		}else if(inputCommand1.equals("2")) {
			parseExportIdAndCategory();
			ParserMultipleCategories parserMultipleCategories = new ParserMultipleCategories(inputCommand2, statement, exportId, exportCategory);
			parserMultipleCategories.startParsingMultipleCategories();
			}
		}

	private static Connection connectDB() {
		try {

			Class.forName("org.postgresql.Driver");

			String url = "jdbc:postgresql://192.168.172.129:5432/postgres";

			connection = DriverManager.getConnection(url, "evgen", "root");
			statement = connection.createStatement();

			if (connection == null)
				System.err.println("Нет соединения с БД!");
			else
				System.out.println("Cоединения с БД установлено!");

		} catch (SQLException e) {
			System.err.println("SQLException : " + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return connection;
	}

	private static void parseStartingPosition(String startLink) throws Exception {

		Document doc = Jsoup.connect(startLink).get();

		Elements elUl = doc.getElementsByTag("ul");
		Elements elDivs = doc.getElementsByTag("div");

		for (Element inputElements : elUl) {
			String resultClass = inputElements.attr("class");

			if (resultClass.equals("black black-in")) {
				log("product page");

				for (Element inputDivs : elDivs) {
					String resultDivs = inputDivs.attr("class");

					if (resultDivs.equals("img-h-2")) {
						// log(inputDivs.getElementsByTag("a").attr("href"));
						if (!excludeLinks.contains(inputDivs.getElementsByTag("a").attr("href"))) {
							links.add(inputDivs.getElementsByTag("a").attr("href"));
						}
					}
				}
			}
			if (resultClass.equals("white white-in")) {
				log("catalog page");

				for (Element inputDivs : elDivs) {
					String resultDivs = inputDivs.attr("class");
					if (resultDivs.equals("img-h")) {
						// log(inputDivs.getElementsByTag("a").attr("href"));
						parseStartingPosition("https://izida.biz" + inputDivs.getElementsByTag("a").attr("href"));
					}
				}
				return;
			}
		}
	}

	private static void parseExportIdAndCategory() {
		try (BufferedReader br2 = new BufferedReader(
				new FileReader(new File("C://Eclipse/parserOnJavaV1/export/exportId.txt")));) {
			exportId = Integer.parseInt(br2.readLine());
			exportCategory = Integer.parseInt(br2.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void log(Object inStr) {
		System.out.println(inStr.toString());
	}
}
