import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class overbooking {

	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		int tn=0;//total number of request
		double prebenefit =0; //benefit for each request accepted
	    double benefit=60; //benefit for each complete request
	    double penalty=200; // penalty for outsource
	    int scenarionum=10;
	    ArrayList<ArrayList<Integer>> cluster = new ArrayList<ArrayList<Integer>>();
       //network nw = new network();
       //nw.readexcel("20100113.xlsx",3,30);
       //nw.outprtnw();
       //nw.savenw("20100113uc10.txt");
       //nw.readnw("20100113uc10.txt");
       //nw.cluster();
//******************************* solve network with cluster *******************************        
       /*
       nw.generatesnr();
       System.out.println("whole size "+nw.Pt.size());
       ArrayList<network> cluster1 = nw.clusternw();
 	  double[] benelist = new double[cluster1.size()];
       for(int k=0;k<cluster1.size();k++) {
    	   System.out.println("solving cluster "+k+" size "+cluster1.get(k).Pt.size());
           ColumnGeneration columnexam = new ColumnGeneration(cluster1.get(k));  
           columnexam.testDP(50);
  	    }	
  	    /*
//******************************* solve network with cluster *******************************      
       
//******************************* column generation toyproblem ******************************* 
       /*
       nw.readnw("toymodel.txt");
       NewModel nm = new NewModel();
       nm.creatModel(nw);
       nm.solves();
       
       try {  	   
   		PrintStream o = new PrintStream(new File("toy1.txt"));
   		//PrintStream console = System.out;
   		//System.setOut(o);
        //nw.readnw("toymodel.txt");
        ColumnGeneration columnexam = new ColumnGeneration(nw); 
        columnexam.testDP(2);
       //System.setOut(console);
       } catch (FileNotFoundException e) {
      		// TODO Auto-generated catch block
      		e.printStackTrace();
      	}*/
//******************************* column generation toyproblem *******************************        
       //iteration=2;
       
       HashMap<Integer,String> status = new HashMap<Integer,String>();
       HashMap<Integer,Double> benefitlist = new HashMap<Integer,Double>();
       HashMap<Integer,Double> snrcost = new HashMap<Integer,Double>();//scenario cost list(route cost)
       HashMap<Integer,Double> earns = new HashMap<Integer,Double>();//scenario cost list(route cost)
       //nw.readnw("20100113d20u.txt");
       //nw.readnw("20100113d20c.txt");
       //nw.changenw();
       //nw.readnw("10requestexam.txt");
      // nw.outprtnw();
       //nw.savenw("20100113d20c.txt");
       //nw.readnw("20100113d.txt");
       //nw.tempsavenw("20100113q.txt");
       //nw.divide(19);
       //nw.savenw("20100113d20.txt");
       /*
       
       nw.generatesnr();
       cluster=nw.cluster();
       System.out.println(cluster.size());
       ArrayList<network> clusternw = nw.clusternw();
       ColumnGeneration tempcolumn = new ColumnGeneration(clusternw.get(1));
       tempcolumn.testDP();
       */
       /*
       cluster=nw.cluster();
       for(int i=0;i<cluster.size();i++) {
    	   System.out.println("=========="+i+"==================");
    	   System.out.println("size"+cluster.get(i).size());
    	   System.out.println(cluster.get(i));
       }
       */
       /*
       ArrayList<network> examnw = nw.clusternw();
       TreeMap<Integer,Integer> Nq = examnw.get(0).N;
       System.out.println(Nq);
       */
       //examnw.get(1).savenw("cluster1-nw");
       /*
       nw.initiate();//test singe randomized case with fixed number of request
       for(int i=1;i<=25;i++) {
    	   nw.add(i);
       }
       ColumnGeneration tempcolumn = new ColumnGeneration(nw);
       tempcolumn.testDP();
       //nw.generatesnr();
       /*
       int tempt =nw.Pt.size();
       System.out.println("randomize request #"+tempt);
       ColumnGeneration tempcolumn = new ColumnGeneration(nw);
       tempcolumn.testDP();
       */
       /*
       double tempd=3.4;
       int tempi=(int) Math.round(tempd);
       System.out.println(tempi);
       */
       ///////////////////////////////////overbooking ////////////////////////////////////////////
       
       /*
       try {
		PrintStream o = new PrintStream(new File("overbookingoutput.txt"));
		PrintStream console = System.out;
		//System.setOut(o);
		int iteration = nw.n;
	       System.out.println("request #:"+iteration);
	       int Vbus=nw.V;
	       nw.initiate();       
	       for(int i=1;i<iteration;i++) {
	    	   System.out.println("*************************************request"+i+"****************************************");  
	    	   System.out.println("# of request accepted: "+nw.Pt.size());
	    	   nw.add(i);	    	   
	    	   double[] scenariobenefit=new double[scenarionum];
	    	   double avebenefit=0;    	   
	    	  int feasi=0; //if there are any infeasible case
	    	  int nn=nw.Pt.size();
	    	   for(int j=0;j<scenarionum;j++) {
	    		   double feasible=0.0;
	    		   
	    		   //double scenariobenefittemp=0.0;
	    	      if(nw.generatesnr()==true) { 
	    	    	  ArrayList<network> cluster1 = nw.clusternw();
	    	    	  double[] benelist = new double[cluster1.size()];
	                  
	                 
	    	    	 for(int k=0;k<cluster1.size();k++) {
	    	             ColumnGeneration columnexam = new ColumnGeneration(cluster1.get(k));  
	    	             //System.out.println(cluster1.get(k).Pt.size());
	    	             //System.out.println(nw.Pt.size());
	    	             double portion=(double)cluster1.get(k).Pt.size()/(double)nw.Pt.size();
	    	             //System.out.println(portion);
	    	             int tempd=(int) Math.ceil(portion*Vbus); //bus number for each cluster
	    	             //System.out.println("cluster "+cluster1.get(k).Pt.size()+"   "+tempd);
	    	                if(columnexam.testDP(tempd)==true) {
	    	        	        //scenariobenefit = scenariobenefit-columnexam.scenariocost; //calculate cost
	    	        	        //snrcost.put(i, columnexam.scenariocost);
	    	        	 
	    	                }else {
	    	        	        //scenariobenefit[j] =  scenariobenefit[j]-penalty;//calculate penalty, each fail clusters get penalty(but not penalty without cost)
	    	        	        //benelist[k]=-penalty;
	    	        	        //snrcost.put(i, -penalty);
	    	        	        //scenariobenefit=scenariobenefit-columnexam.scenariocost;
	    	        	        feasible=1.0;
	    	                      } 
	    	    	    }
	    	    	 //scenariobenefit=scenariobenefit-penalty*feasible;
	    	       }else {
	    	    	   scenariobenefit[j]=scenariobenefit[j]+0;
	    	    	   //nw.restore();
	    	    	   //System.out.println("noshow");
	    	      }
	    	      //System.out.println("show up: "+nw.Pt.size());
	    	      if(feasible==1.0) {
	    	    	  System.out.println("infeasible");
	    	      }else {
	    	    	  System.out.println("feasible");
	    	      }
	    	      // restore network first and calculate the number of accepted request
	    	      scenariobenefit[j] = scenariobenefit[j]+benefit*nn-feasible*penalty;//add the benefit
	    	      //System.out.println(scenariobenefit[j]);
	    	      nw.restore();
	    	      
	           }
	    	   for( int j=0;j<scenarionum;j++) {
	    		   avebenefit=avebenefit+scenariobenefit[j];
	    	   }
	    	   
	    	   if(feasi==0) {
	    		   System.out.println("all feasible");
	    	   }else {
	    		   System.out.println("infeasible case#:"+feasi);
	    	   }

	    		   avebenefit=avebenefit/scenarionum;
	    	   benefitlist.put(i, avebenefit);
	    	   System.out.println("average benefit"+avebenefit);
	    	   prebenefit=(nn-1)*benefit;
	    	   if(prebenefit<avebenefit) {
	    		   status.put(i, "accept");
	    		   System.out.println("accept");
	    		   //prebenefit=avebenefit;
	    	   }else {
	    		   status.put(i, "reject");
	    		   System.out.println("reject");
	    		   nw.delete(i);
	    	   }
	       }
	       //System.setOut(console);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

       //System.out.println(benefitlist);
       //System.out.println(snrcost);
       //System.out.println(status);
       for(int i:status.keySet()) {
    	   System.out.println(i+"  "+ benefitlist.get(i)+"   "+status.get(i));
       }
      */
       /////////////////checking lagrest number of request it can received////////////////////
   /*
       int iteration = nw.n;
       nw.initiate();
       int Vbus=nw.V;
       
       for(int i=2;i<=iteration;i++) {
    	   System.out.println("**********************request "+i+"******************************");
    	   nw.add(i);
    	   ArrayList<network> cluster1 = nw.clusternw();
           double feasible=0.0;
	          for(int k=0;k<cluster1.size();k++) {
	             ColumnGeneration columnexam = new ColumnGeneration(cluster1.get(k));  
	             //System.out.println(cluster1.get(k).Pt.size());
	             //System.out.println(nw.Pt.size());
	             double portion=(double)cluster1.get(k).Pt.size()/(double)nw.Pt.size();
	             //System.out.println(portion);
	             int tempd=(int) Math.ceil(portion*Vbus); //bus number for each cluster
	             System.out.println("cluster "+cluster1.get(k).Pt.size()+"   "+tempd);
	                if(columnexam.testDP(tempd)==true) {
	        	        //scenariobenefit = scenariobenefit-columnexam.scenariocost; //calculate cost
	        	        //snrcost.put(i, columnexam.scenariocost);
	        	 
	                }else {
	        	        //scenariobenefit[j] =  scenariobenefit[j]-penalty;//calculate penalty, each fail clusters get penalty(but not penalty without cost)
	        	        //benelist[k]=-penalty;
	        	        //snrcost.put(i, -penalty);
	        	        //scenariobenefit=scenariobenefit-columnexam.scenariocost;
	        	        feasible=1.0;
	                      } 
	    	    }
	          if(feasible==1.0) {
	        	  System.out.println("infeasible");
	        	  break;
	          }
       }
       
     
       nw.initiate();
       int Vbus=nw.V;
       
       for(int i=2;i<30;i++) {
    	   nw.add(i);
       }
       ColumnGeneration columnexam = new ColumnGeneration(nw);  
       columnexam.testDP(1);
       */
       
       try {
    	   network nw = new network();
   		PrintStream o = new PrintStream(new File("blank.txt"));
   		//PrintStream console = System.out;
   		//System.setOut(o);
       nw.readnw("20100113u2.txt");
       scenarionum=5;
       double alpha=0.80;
       int iteration = nw.n;
       System.out.println("request #:"+iteration);
       nw.initiate();       
       int num=0;
       for(int i=1;i<iteration;i++) {
    	   //System.out.println("*************************************request"+i+"****************************************");  
    	   //System.out.println("# of request accepted: "+nw.Pt.size());
    	   //System.out.println(nw.Pt);
    	   nw.add(i);	   
    	   int feasitime=0;
    	   for(int j=0;j<scenarionum;j++) {
    		   //double feasible=0.0;
    		   //double scenariobenefittemp=0.0;
    	      if(nw.generatesnr()==true) { 
    	    	  //System.out.println("# of request showup" + nw.Pt.size());
    	    	  insertion inex = new insertion(nw);
    	    	  if(inex.runinsertion()==true) {
    	    		  //System.out.println("feasible");
    	    		  feasitime=feasitime+1;
    	    	  }else {
    	    		 //System.out.println("infeasible");
    	    	  }
    	      }else {
    	    	  feasitime=feasitime+1;
   	    	   //nw.restore();
   	    	   //System.out.println("noshow");
   	          }
    	      nw.restore();
    	   }    
    	   if((feasitime/scenarionum)>=alpha) {
    		   num++;
    		   status.put(i, "accept");
    		   //System.out.println("accept");
    	   }else {
    		   status.put(i, "reject");
    		   //System.out.println("reject");
    		   nw.delete(i);
    	   }
       }
       for(int i:status.keySet()) {
    	   System.out.println(i+"   "+status.get(i));
       }
       System.out.println("num "+num);
     //System.setOut(console);
       
       } catch (FileNotFoundException e) {
   		// TODO Auto-generated catch block
   		e.printStackTrace();
   	}
	}
}
