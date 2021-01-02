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
	public static void main(String[] args) throws Exception{ 
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int exportId, exportCategory;
		String inputCommand;
		
		BufferedReader br2 = new BufferedReader(new FileReader(new File("C://Eclipse/parserOnJavaV1/export/exportId.txt")));
		exportId = Integer.parseInt(br2.readLine());
		exportCategory = Integer.parseInt(br2.readLine());
		
		tableName = "granat"; 
		
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
		//name of table in DB
		
	
		
		//starting position URL
		links.add("/ru/item/77349");
		links.add("/ru/item/77348");
		links.add("/ru/item/74707");
		/*links.add("/ru/item/73895");
		links.add("/ru/item/5988");
		links.add("/ru/item/72339");
		 	*/
		int i=0;
		
		
		statement.execute("DELETE FROM "+tableName);
		
		
		
		while(i<links.size()) {
			parseElements("https://izida.biz"+links.get(i));

		
			i++;
		}
		
		TextFileExport tfe = new TextFileExport();
		tfe.export(statement, tableName, exportId, exportCategory);
		tfe.exportId();
		
	    log(insertCount+" products added");
	
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

            String url = "jdbc:postgresql://192.168.3.130:5432/postgres";

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
	
	
	
	public static void parseElements(String str) throws Exception{
		ResultSet rs;
		Document doc = Jsoup.connect(str).get();
		String title, content, priceStr, artidStr,image= null, description = null;
		int artid, price;
		//Поиск заголовка
	     title = doc.title();  
	    //log("Заголовок : " + title);
	    //Поиск цены
	    Elements elPrice = doc.getElementsByClass("price-2");
	    priceStr = elPrice.html();
	    //log("Цена: " + priceStr);
	    price = Integer.parseInt(priceStr.split(" ")[0]);
	    //log(price);
	    
	    //Поиск артикула
	    Elements elID = doc.getElementsByClass("dop");
	    //log(elID.html());
	    artidStr = elID.html();
	    artid = Integer.parseInt(artidStr.split(" ")[1]);
	   // log(artid+"");
	   
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
	    	links.add(href);
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



