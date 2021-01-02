package parserOnJavaV1;

import java.io.*;
import java.sql.*;

public class TextFileExport {
	private int id, exportCategory;
	public void export(Statement statement, String tableName, int idd, int exportCategory) throws Exception {
		String title, description;
		int price, artid;
		this.id = idd;
		this.exportCategory = exportCategory;
		
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File("C://Eclipse/parserOnJavaV1/export/oc_export_to_db.sql")), "UTF-8");
		
		
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
			
			
			osw.write("INSERT INTO `oc_product` (`product_id`, `model`, `sku`, `upc`, `ean`, `jan`, `isbn`, `mpn`, `location`, `quantity`, `stock_status_id`, `image`, `manufacturer_id`, `shipping`, `price`, `points`, `tax_class_id`, `date_available`, `weight`, `weight_class_id`, `length`, `width`, `height`, `length_class_id`, `subtract`, `minimum`, `sort_order`, `status`, `viewed`, `date_added`, `date_modified`, `noindex`) VALUES\r\n" + 
					"("+id+", '"+artid+"', '', '', '', '', '', '', '', 100, 7, 'catalog/"+tableName+"/"+artid+".jpg', 0, 1, '"+Math.addExact(price, price)+"', 0, 0, '0000-00-00', '0.00000000', 1, '0.00000000', '0.00000000', '0.00000000', 1, 1, 1, 1, 1, 1, '0000-00-00 00:00:00', '0000-00-00 00:00:00', 1);\n");
			
			//osw.write("INSERT INTO `oc_product_description` (`product_id`, `language_id`, `name`, `description`, `tag`, `meta_title`, `meta_description`, `meta_keyword`, `meta_h1`) VALUES("+id+", 1, '"+title+"', '"+description+"', '', '', '', '', '');\n");
			
			//osw.write("INSERT INTO `oc_product_to_category` (`product_id`, `category_id`, `main_category`) VALUES("+id+", "+exportCategory+", 1);");
			
			//osw.write("INSERT INTO `oc_product_to_store` (`product_id`, `store_id`) VALUES("+id+", 0);\n");
			
			
			
			id++;
		}
		osw.flush();
		osw.close();
	
		System.out.println("SQL exported");
		
	}
	public void exportId() throws Exception{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("C://Eclipse/parserOnJavaV1/export/exportId.txt")));
		bw.write(id+"");
		bw.write("\n");
		bw.write((exportCategory+1)+"");
		bw.flush();
		bw.close();
	}
}
