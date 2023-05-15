import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class randomG {
	
	public static void main(String[] args) {
        /*
	    try {
	    	
	    	PrintStream myconsole = new PrintStream(new File("outputconsole.txt"));
	    	System.setOut(myconsole);
	    	myconsole.print("");
	      */
		// TODO Auto-generated method stub
		int num_node = 10;
		int num_bus =3;
		int bus_cap=3;
		int[] array_noshow=new int[] {};
		int[] array_cancel=new int[] {};
		long startTime = System.nanoTime();
		 network exa = new network();
         exa.generateRandomNetwork(num_node,bus_cap,num_bus);
        // model exa1 = new model();        
        // exa1.creatModel(exa);
         //exa1.solve();
         exa.creatnew(array_noshow, array_cancel);
         //exa.setnetwork();
         //System.out.println(exa.P);
         //exa.outprtnw();
         /*
         NewModel exa2 = new NewModel(); 
         exa2.creatModel(exa);
         
         if(exa2.solve()==true) {*/
         //System.out.println("==========end of directly solve=============");
         ColumnGeneration colexam = new ColumnGeneration(exa);
         colexam.readexcel("20120602.xlsx");
         colexam.testDP(num_bus);
         //System.out.println("=====direct solve value:"+exa2.objdirec+"============");
         //}
         
         for(int i=0;i<300;i++) {
        	 double j=i*200+(Math.random()*50);
        	 System.out.println(j);
         }
         /*
	    }
	    catch(FileNotFoundException fx) {
	    	System.out.println(fx);
	    }
         //exa.outprtnw();
         /*
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		double seconds = totalTime/1000000000;
        System.out.println(+num_node+"=="+num_bus+"=="+seconds);     
        */
	    
	}
}
