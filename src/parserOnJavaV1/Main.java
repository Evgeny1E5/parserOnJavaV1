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


public class Main {
	    private static Connection connection;
	    private static ArrayList<String> links = new ArrayList<>();
	    private static Connection con;
	    private static Statement statement = null;
	    private static ImageSaver imageSaver;
	    private static String tableName;
	    private static int insertCount;
	    private static ArrayList<String> excludeLink;
	    private static ArrayList<String> badIdLinks = new ArrayList<>();
	public static void main(String[] args) throws Exception{ 
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int exportId, exportCategory;
		String inputCommand, excludeLinkReadLine;
		
		BufferedReader br2 = new BufferedReader(new FileReader(new File("C://Eclipse/parserOnJavaV1/export/exportId.txt")));
		
		BufferedReader br3 = new BufferedReader(new FileReader(new File("C://Eclipse/parserOnJavaV1/export/excludeLinks.txt")));
		
		exportId = Integer.parseInt(br2.readLine());
		exportCategory = Integer.parseInt(br2.readLine());
		
		//1. Set table name
		tableName = "pod_kapelnoye_serebro"; 
	
		// Import exclude links
		excludeLink = new ArrayList<>();
		while((excludeLinkReadLine = br3.readLine()) != null) {
	
			excludeLink.add(excludeLinkReadLine);
		}
		/*excludeLink.add("/ru/item/35654");
		excludeLink.add("/ru/item/26317");
		excludeLink.add("/ru/item/56389");
		excludeLink.add("/ru/item/56557");
		excludeLink.add("/ru/item/56386");
		*/
		inputCommand = br.readLine();
		
		if(inputCommand.equals("1")) {
			con = connectDB();
			TextFileExport tfe = new TextFileExport();
			tfe.export(statement, tableName, exportId, exportCategory);
			tfe.exportId();
			return;
		}else {
		
		//connection to db
		con = connectDB();

		
	
		
		int i=0;
		statement.execute("DELETE FROM "+tableName);
		
		//parser start position
		parseStartingPosition(inputCommand);
		
		//parser loop
		while(i<links.size()) {
			parseElements(links.get(i));
			i++;
		}
		
		TextFileExport tfe = new TextFileExport();
		tfe.export(statement, tableName, exportId, exportCategory);
		tfe.exportId();
		//export exclude links, uncomment if needed
		/*
		BufferedWriter bw = new BufferedWriter(new FileWriter( new File("C://Eclipse/parserOnJavaV1/export/excludeLinks.txt")));
	    log(insertCount+" products added to DB");
	    for(String str: links) {
	    	bw.write(str);
	    	bw.write("\n");
	    }
	    bw.flush();
	    bw.close();
	    */
		if(badIdLinks.size() > 0) {
			log("List of errors IDs VVV");
		for(String logStr: badIdLinks) {
			log(logStr);
		}
		}
	    br3.close();
	    br2.close();
	    con.close();
	}
}
	
	private static void log(String inStr) {
		System.out.println(inStr);
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
	
	
	private static void parseStartingPosition(String startLink) throws Exception{
		
		Document doc = Jsoup.connect(startLink).get();
		
		Elements elUl = doc.getElementsByTag("ul");
		Elements elDivs = doc.getElementsByTag("div");
		
		for(Element inputElements : elUl) {
			String resultClass = inputElements.attr("class");
			
			if(resultClass.equals("black black-in")) {
				log("product page");
				
				for(Element inputDivs : elDivs) {
					String resultDivs = inputDivs.attr("class");
					
					if(resultDivs.equals("img-h-2")) {
						//log(inputDivs.getElementsByTag("a").attr("href"));
						if(!excludeLink.contains(inputDivs.getElementsByTag("a").attr("href"))) {
						links.add(inputDivs.getElementsByTag("a").attr("href"));
						}
					}
				}
			}
			if(resultClass.equals("white white-in")) {
				log("catalog page");
				
				for(Element inputDivs: elDivs) {
					String resultDivs = inputDivs.attr("class");
					if(resultDivs.equals("img-h")) {
						//log(inputDivs.getElementsByTag("a").attr("href"));
						parseStartingPosition("https://izida.biz" + inputDivs.getElementsByTag("a").attr("href"));
					}
				}
				return;
			}
		}
	}
	
	
	
	public static void parseElements(String str) throws Exception{
		ResultSet rs;
		
		Document doc = Jsoup.connect("https://izida.biz"+str).get();
		log(str);
		String title, content, priceStr, artidStr,image= null, description = null;
		int artid, price;
		//Поиск заголовка
	     title = doc.title();  
	    //log("Заголовок : " + title);
	    //Поиск цены
	    Elements elPrice = doc.getElementsByClass("price-2");
	    priceStr = elPrice.html();
	    //log("Цена: " + priceStr);
	    try {
	    price = Integer.parseInt(priceStr.split(" ")[0]);
	    }catch(NumberFormatException e) {
	    	badIdLinks.add(str);
	    	log("BAD PRICE");
	    	return;
	    }
	    //log(price);
	    
	    //Поиск артикула
	    Elements elID = doc.getElementsByClass("dop");
	    //log(elID.html());
	    artidStr = elID.html();
	    try {
	    artid = Integer.parseInt(artidStr.split(" ")[1]);
	    }catch(NumberFormatException e) {
	    	badIdLinks.add(str);
	    	log("BAD ID");
	    	return;
	    }
	    //log(artid+"");
	   
	    Elements elDescription = doc.getElementsByTag("meta");
	    for(Element inputElement : elDescription) {
	    	
	    	String propertyLoop = inputElement.attr("property");
	    	String contentLoop = inputElement.attr("content");

	    	if(propertyLoop.equals("og:image")) {
	    		//log("Фото: "+contentLoop);
	    		image = contentLoop;
	    	}
	    	if(inputElement.attr("name").equals("description")) {
	    		//log("Описание: "+contentLoop);
	    		description = contentLoop;
	    	}
	    }
	    //log(title+"     "+description);
	    
	   
	   
	    Elements elHref = doc.getElementsByTag("a");
	    
	    for(Element input: elHref) {
	    	String href = input.attr("href");
	    	
	    	if(href.contains("/ru/item/") && !links.contains(href) && !href.contains("izida.biz")) {
	    		//exception link(mirrors)
	    		if(!excludeLink.contains(href)) {
	    			
	    			links.add(href);
	    		}
	    	}
	    }
	    
	    	
	   
	    		statement.executeUpdate("INSERT INTO "+tableName+"(title, price, artid, description, image)"
	    				+ "VALUES ('"+title.toString()+"','"+price+"','"+artid+"','"+description+"','"+image+"')");
	    		
	    		imageSaver = new ImageSaver();
	    	    imageSaver.saveImage(image, artid+"", tableName);
	    	    
	    	    log(insertCount+"");
	    	    insertCount++;
	    		}
	}



