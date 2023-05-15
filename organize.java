import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class organize {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file = new File("20100113uc10.txt");
		BufferedReader br;
		try {
			String[] col;
			br = new BufferedReader(new FileReader(file));
			   String st ="";
			   ArrayList<String> list = new ArrayList<String>();
			   HashMap<Integer,String> aclist = new HashMap<Integer,String>(); 
			  ArrayList<String> slist = new ArrayList<String>();
			  ArrayList<Double> xlist = new ArrayList<Double>();
			  ArrayList<Double> ylist = new ArrayList<Double>();
			  st=br.readLine();
					  st=br.readLine();
							  st=br.readLine();
									  st=br.readLine();
									  int it = 0;
			  while((st=br.readLine()) !=null){	
				  
			          //st=br.readLine();
				    //System.out.println(it);
			          //System.out.println(st);
			          col=st.split("\\s+ ");
			          if(col.length>2) {
			          //slist.add(col[1]);
			          System.out.println(col[1]);
			          System.out.println(col[2]);
			          xlist.add(Double.parseDouble(col[1]));
			          ylist.add(Double.parseDouble(col[2]));
			         //System.out.println(que);
			          //list.add(st);
			          //System.out.println(st);
			          }
			     }
              for(int i=0;i<slist.size();i++) {
            	  System.out.println((i+1)+"  "+xlist.get(i)+" "+ylist.get(i));
              }
           
  			Workbook workbook = new XSSFWorkbook();
  			Sheet sheet = workbook.createSheet("status");
  			for(int i=0;i<544;i++) {
  			Row row = sheet.createRow(i);
  			Cell cell1 = row.createCell(0);
  			cell1.setCellValue(i+1);
  			Cell cell2 = row.createCell(1);
  			cell2.setCellValue(xlist.get(i));
  			//cell2.setCellValue(slist.get(i));  	
  			Cell cell3 = row.createCell(2);
  			cell3.setCellValue(ylist.get(i));
  			}
  			FileOutputStream fileOut = new FileOutputStream("ucmap.xlsx");
  	        workbook.write(fileOut);
  	        fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
