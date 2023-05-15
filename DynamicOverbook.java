import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DynamicOverbook {

	
	public DynamicOverbook() {
		
	}
	public void run() {
		int scenarionum=10;
		HashMap<Integer,String> status = new HashMap<Integer,String>();
		network nw = new network();
		nw.readnw("20100113uc10.txt");
		//nw.changeV(30);
		//System.out.println(nw.P);
		ArrayList<Integer> nsl = new ArrayList<Integer>();//no show list
		nsl =	nw.generatenoshow();
		//System.out.println(nsl);
		insertion ins = new insertion(nw);
		System.out.println("===========================initiate================================");
		
		if(ins.runinsertion()==true) {
			
		
		System.out.println("after insertion");
		System.out.println("nsl "+nsl);
		nsl = ins.sortns(nsl);
		//System.out.println(nsl);
		ins.outputbestroute();
		
		/*
		for(int i=0;i<nsl.size();i++) {
			System.out.println("=======================no show "+nsl.get(i)+" ======================");
			ins.update(nsl.get(i));
			ins.reruninsertion();
			ins.outputbestroute();
		}
		*/
		while(nsl.isEmpty()==false) {
			System.out.println("=======================no show "+nsl.get(0)+" ======================");
			System.out.println("===========no show size "+nsl.size()+"==============");
			ins.update(nsl.get(0));
			if(ins.reruninsertion()==false) {
				System.out.println("fail to insert");
				break;
			}
			ins.outputbestroute();
			nsl.remove(0);
			nsl=ins.sortns(nsl);
		}
		}else {
			System.out.println("Fail to generate initial solotuion");
		}
	}
	//////////////////test for specific scenario///////////////////
	public void scenario() {
		int num=200;
		HashMap<Integer,String> status = new HashMap<Integer,String>();
		network nw = new network();
		nw.readnw("20100113u2.txt");
		//nw.changeV(30);
		//System.out.println(nw.P);
		nw.generatesnr();
		ArrayList<Integer> nsl = new ArrayList<Integer>();//no show list
		nsl =	nw.generatenoshow();
		insertion ins = new insertion(nw);
		
		nsl = nw.generatenoshow();
		System.out.println(nsl);
		ins.runinserttillwithoutcancel(num);
		ins.runinserttill(num,nsl);
		//ins.outputbestroute();
		
		
		
	}
	public void scenario2() {
		    int num=200;
			HashMap<Integer,String> status = new HashMap<Integer,String>();
			network nw = new network();
			nw.readnw("20100113u2.txt");
			//nw.changeV(30);
			//System.out.println(nw.P);
			ArrayList<Integer> nsl = new ArrayList<Integer>();//no show list
			nsl =	nw.generatenoshow();
			insertion ins = new insertion(nw);
			ins.runinserttill(num,nsl);
			//ins.outputbestroute();
			for(int i=0;i<nsl.size();i++) {
				if(i>num) {
					nsl.remove(i);
				}
			}
			while(nsl.isEmpty()==false) {
				System.out.println("=======================no show "+nsl.get(0)+" ======================");
				System.out.println("===========no show size "+nsl.size()+"==============");
				ins.update(nsl.get(0));
				if(ins.reruninsertion()==false) {
					System.out.println("fail to insert");
					break;
				}
				ins.outputbestroute();
				nsl.remove(0);
				nsl=ins.sortns(nsl);
			}
	}
	public void test() {
		int scenarionum=10;
		HashMap<Integer,String> status = new HashMap<Integer,String>();
		network nw = new network();
		nw.readnw("toy1.txt");
		//System.out.println(nw.P);
		ArrayList<Integer> nsl = new ArrayList<Integer>();//no show list
		//nsl =	nw.generatenoshow();
		nsl.add(1);
		nsl.add(3);
		nsl.add(4);
		//System.out.println(nsl);
		insertion ins = new insertion(nw);
		System.out.println("===========================initiate================================");
		if(ins.runinsertion()==true) {		
		
		nsl = ins.sortns(nsl);
		//System.out.println(nsl);
		ins.outputbestroute();
		ins.outputcost();
		/*
		for(int i=0;i<nsl.size();i++) {
			System.out.println("=======================no show "+nsl.get(i)+" ======================");
			ins.update(nsl.get(i));
			ins.reruninsertion();
			ins.outputbestroute();
		}
		*/
		while(nsl.isEmpty()==false) {
			System.out.println("=======================no show "+nsl.get(0)+" ======================");
			System.out.println("===========no show size "+nsl.size()+"==============");
			ins.update(nsl.get(0));
			if(ins.reruninsertion()==false) {
				System.out.println("fail to insert");
				break;
			}
			ins.outputbestroute();
			nsl.remove(0);
			nsl=ins.sortns(nsl);
		}
		ins.outputcost();
		}else {
			System.out.println("Initialization fail");
		}
	}
	public void test2() {
		network nw = new network();
		nw.readnw("20100113u2.txt");
		//nw.recalculate(3, 5, 0.2);
	    insertion ins = new insertion(nw);
		TreeMap<Integer,String> temp = ins.runinsertionDARP();
		try {
		    
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("status");
			for(int i:temp.keySet()) {
			Row row = sheet.createRow(i);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(i);
			Cell cell2 = row.createCell(1);
			cell2.setCellValue(temp.get(i));
			}
			FileOutputStream fileOut = new FileOutputStream("DARP.xlsx");
	        workbook.write(fileOut);
	        fileOut.close();
	  } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		//ins.outputbestroute();
 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
	public static void main(String[] args) {
		try {
			PrintStream o = new PrintStream(new File("blank.txt"));
	   		//PrintStream console = System.out;
	   		//System.setOut(o);
		
		DynamicOverbook exam = new DynamicOverbook();
		//exam.test2();
		//exam.run();
		//exam.scenario();
		exam.test();
		} catch (FileNotFoundException e) {
	   		// TODO Auto-generated catch block
	   		e.printStackTrace();
	   	}
	}
}
