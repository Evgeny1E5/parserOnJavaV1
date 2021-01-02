package parserOnJavaV1;

import java.io.*;
import java.io.OutputStreamWriter;
import java.sql.*;

public class TextFileExport2 {
	public void export(Statement statement, String tableName, int id, int exportCategory) throws Exception {
		String title, description;
		int price, artid;
		
		
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("C://Eclipse/parserOnJavaV1/export/oc_product.csv")));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File("C://Eclipse/parserOnJavaV1/export/oc_product_to_category.csv")));
		OutputStreamWriter bw3 = new OutputStreamWriter(new FileOutputStream(new File("C://Eclipse/parserOnJavaV1/export/oc_product_description.csv")),  "UTF-8");
		BufferedWriter bw4 = new BufferedWriter(new FileWriter(new File("C://Eclipse/parserOnJavaV1/export/oc_product_to_store.csv")));
		
		ResultSet rs = statement.executeQuery("SELECT title, price, artid, description FROM "+tableName);
		
		while(rs.next()) {
			title = rs.getString("title");
			price = rs.getInt("price");
			artid = rs.getInt("artid");
			description = rs.getString("description");
			if(description.toLowerCase().contains("izida") || description.toLowerCase().contains("изида")) {
				description = description.replace("изида", "");
				description = description.replace("izida", "");
				description = description.replace("Изида", "");
				description = description.replace("Izida", "");
			}
			while(description.contains("\"")) {
				description = description.replace("\"", "");
			}
			
			while(title.contains("\"")) {
				title = title.replace("\"", "");
			}
			
			
			bw1.write("\""+id+"\",\""+artid+"\",,,,,,,,\"100\",\"7\",\"catalog/"+tableName+"/"+artid+".jpg\",\"0\",\"1\",\""+Math.addExact(price, price)+"\",\"0\",\"0\",\"0000-00-00\",\"0.00000000\",\"1\",\"0.00000000\",\"0.00000000\",\"0.00000000\",\"1\",\"1\",\"1\",\"1\",\"1\",\"1\",\"0000-00-00 00:00:00\",\"0000-00-00 00:00:00\",\"1\"");
			bw1.write("\n");
			
			bw2.write("\""+id+"\",\""+exportCategory+"\",\"1\"");
			bw2.write("\n");
			
			bw3.write("\""+id+"\",\"1\",\""+title+"\",\""+description+"\",,,,,");
			bw3.write("\n");
			
			bw4.write("\""+id+"\",\"0\"");
			bw4.write("\n");
			
			
			
			
			id++;
		}
		bw1.flush();
		bw1.close();
		bw2.flush();
		bw2.close();
		bw3.flush();
		bw3.close();
		bw4.flush();
		bw4.close();
		System.out.println("CSV exported");
		
	}
}
