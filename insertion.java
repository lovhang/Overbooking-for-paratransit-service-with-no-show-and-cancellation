import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import ilog.concert.IloException;
import ilog.cplex.IloCplexModeler.Exception;



public class insertion {

	  private TreeMap<Integer,Integer> Pa;
	  private TreeMap<Integer,Integer> Pb;
	  public TreeMap<Integer,Integer> Pn;//no-show request
	  public TreeMap<Integer,Integer> Pc;//late cancel request
	  public TreeMap<Integer,Integer> Pt;//union of Pa, Pc and Pn
	  private TreeMap<Integer,Integer> P;
	  private TreeMap<Integer,Integer> N;
	  private int n;
	  private int V;
	  private double[] d;//load
	  private double[][] t;//travel time
	  private double[] a;//arrival time window
	  private double[] b ;//leave time window
	  private double[] A;//actual arrive time
	  private double Q;
	  private double[] s;//service duration
	  private ArrayList<Integer> req;//request list with increasing pickup time
	  private HashMap<Integer,ArrayList<Integer>> reql;//request list
	  private ArrayList<Double> cancost;//candidate cost
	  private HashMap<Integer, int[]> bestroute;// currently bestroute
	  private HashMap<Integer,HashMap<Integer,Double>> bestAT;// actual time for bestroute
	  private HashMap<Integer,HashMap<Integer,Integer>> bestLOAD;//load for bestroute
	  private HashMap<Integer,Double> bestcost;
	  private TreeMap<Integer,Double> timewindow;// arrival time for each nodes
	  private HashMap<Integer,Integer> ori;//origin for each bus at current time
	  private HashMap<Integer,Integer> des;//destination for each bus at current time
	  private HashMap<Integer,Integer> ran;//the rank of route array for each bus that network update
	  private int ns;//the bus that encounter noshow or late cancellation
	  private network nw1;
	public insertion(network nw) {
		 nw1=nw;
		 this.Pa = nw.Pa;
	     this.Pb = nw.Pb;
	     this.Pt = nw.Pt;
	     this.Pn = nw.Pn;
	     this.Pc = nw.Pc;
	     this.P = nw.P;
	     this.N = nw.N;
	     this.d = nw.d;
	     this.t = nw.t;
	     this.a = nw.a;
	     this.b = nw.b;
	     this.s = nw.s;     
	     this.n = nw.n;
	     this.V = nw.V;
	     this.Q = nw.Q;
	     //initiate
	     req = new ArrayList<Integer>();
	     bestroute = new HashMap<Integer,int[]>();
	     bestAT = new HashMap<Integer,HashMap<Integer,Double>>();
	     bestLOAD = new HashMap<Integer,HashMap<Integer,Integer>>();
	     bestcost = new HashMap<Integer,Double>();
	     timewindow = new TreeMap<Integer,Double>();
	     //bestroute.add( new int[0]);
	}
	
	void print(){
		for(int i=0;i<=2*n+1;i++) {
			System.out.println(i+"  "+a[i]+"  "+b[i]);
		}
	}
	
	void sort() {
		req = new ArrayList<Integer>();
		//System.out.println("Pt Size "+Pt.size());
		for(int i:Pt.keySet()) {
			req.add(Pt.get(i));
		}
		for(int k=0;k<req.size()-1;k++) {
		  for(int i=0;i<req.size()-1;i++) {
			if(a[req.get(i)]>a[req.get(i+1)]) {
				Collections.swap(req, i, i+1);
			}
		  }
		}  		
		//System.out.println(req);
	}
	public ArrayList<Integer>  sortns(ArrayList<Integer> a) {
		ArrayList<Integer> temp = (ArrayList<Integer>) a.clone();
		//if(a.isEmpty()==false) {
		for(int i=0;i<temp.size()-1;i++) {
			for(int j=0;j<temp.size()-1;j++) {
				if(timewindow.get(temp.get(j))>timewindow.get(temp.get(j+1))) {
					Collections.swap(temp, j, j+1);
				}
			}
		}
		//}
		return temp;
	}
	boolean insert(int request) {
		int ne=request;//new request
		int nn=ne+n;
		HashMap<Integer,Double> cancost =new HashMap<Integer,Double>();
		HashMap<Integer,Double> canAT = new HashMap<Integer,Double>();
		HashMap<Integer,Integer> canLoad = new HashMap<Integer,Integer>();
		HashMap<Integer,int[]> canroute = new HashMap<Integer,int[]>() ;
	    double bc=Double.MAX_VALUE; //bestcost
	    HashMap<Integer,Double> bCOST = new HashMap<Integer,Double>();
	    HashMap<Integer,HashMap<Integer,Double>> bAT = new HashMap<Integer,HashMap<Integer,Double>>();
	    HashMap<Integer,HashMap<Integer,Integer>> bLOAD = new HashMap<Integer,HashMap<Integer,Integer>>();
	    HashMap<Integer,int[]> broute = new HashMap<Integer,int[]>(); 
	   for(int k=0;k<=bestroute.size();k++) {
		   //System.out.println("K "+k);
		   if(k==bestroute.size()) {
			   if(bestroute.size()<V) {
				   //System.out.println("add vehicle");
			      int[] temproute = new int[4];
			      temproute[0]=0;
 	    	      temproute[1]=ne;
 	    	      temproute[2]=ne+n;
 	    	      temproute[3]=2*n+1;
 	              HashMap<Integer,HashMap<Integer,Double>> tempAT = cloneDOUBLE(bestAT);
 	              HashMap<Integer,Double> tempat = new HashMap<Integer,Double>();
 	              tempat.put(0,0.0);
 	              tempat.put(ne, a[ne]);
 	              double a2=Math.max(a[ne]+t[ne][ne+n], a[ne+n]);
 	              tempat.put(ne+n, a2);
 	              tempat.put(2*n+1,a2+t[ne+n][2*n+1]);
 	              tempAT.put(k, tempat);
 	              HashMap<Integer,HashMap<Integer, Integer>> tempLOAD = cloneINT(bestLOAD);
 	              HashMap<Integer,Integer> tempload = new HashMap<Integer,Integer>();
 	              tempload.put(0, 0);
 	              tempload.put(ne, 1);
 	              tempload.put(ne+n, 0);
 	              tempload.put(2*n+1, 0);
 	              tempLOAD.put(k, tempload);
 	              HashMap<Integer,Double> tempCOST = clonecost(bestcost);
 	              double tempcost=tempat.get(2*n+1);
 	              tempCOST.put(k, tempcost);
 	              //cancost.add(tempcost);
 	              //canAT.add(tempat);
 	              //canLoad.add(tempload);
 	              HashMap<Integer,int[]> tempRoute = clonearray(bestroute);
 	              tempRoute.put(k,temproute);
 	              double ttcost =0;
 	              for(int i:tempCOST.keySet()) {
 	            	  ttcost = ttcost+tempCOST.get(i);
 	              }
 	              /*
 	             for(int l:tempRoute.keySet()) {
                	   int[] temp3 = tempRoute.get(l);
                	   //System.out.println("vehicle  " +l);
                	   for(int s=0;s<temp3.length;s++) {
                		   System.out.print(temp3[s]+"|");
                	   }
                	   //System.out.println("");
                   }*/
	            	 //System.out.println("AT "+tempAT);
	            	 //System.out.println("LOAD "+tempLOAD);
	            	 //System.out.println("COST "+tempCOST);
 	              //System.out.println("ttcost"+ttcost);
 	              
 	              if(ttcost<bc) {
 	            	  bc=tempcost;
 	            	  bAT=tempAT;
 	            	  bLOAD=tempLOAD;
 	            	  bCOST=tempCOST;
 	            	  broute=tempRoute;
 	              }
 	            	 
			   }
		   }else {
		      int[] route = bestroute.get(k);   
   	       //System.out.println("");
   	       //System.out.println("route size "+route.length);
   	          HashMap<Integer,int[]> tempRoute = clonearray(bestroute);
   	          HashMap<Integer,HashMap<Integer,Double>> tempAT = cloneDOUBLE(bestAT); 
   	          HashMap<Integer,HashMap<Integer,Integer>> tempLOAD = cloneINT(bestLOAD);
 		      HashMap<Integer,Double> bestat=clonedouble(bestAT.get(k));
		      HashMap<Integer,Integer> bestload = cloneint(bestLOAD.get(k));
		      //checkroute(route);
               for(int i=0;i<route.length-1;i++) {
            	   //System.out.println(" i :"+i);
       	          HashMap<Integer,Double> tempat = clonedouble(bestat);
       	          HashMap<Integer,Integer> tempload = cloneint(bestload);
        	       //calculate ADOWN
            	   //System.out.println("");
            	   //System.out.println("===== i "+i+"=============");
        	       double ADOWN=b[2*n+1]-a[2*n+1];
        	       for(int j=i+1;j<route.length;j++) {
        		       double tempadwn=b[route[j]]-bestat.get(route[j]);
        		       if(tempadwn<=ADOWN){
        			    ADOWN=tempadwn;	
        		       }
        	       }
        	       //System.out.println("ADOWN"+ADOWN);
        	       double a1 = Math.max(bestat.get(route[i])+t[route[i]][ne], a[ne]);
        	       double d1 = a1-bestat.get(route[i]);
        	       double a2 = Math.max(a1+t[ne][route[i+1]], bestat.get(route[i]));
        	       double d2 = a2-a1;
        	       double DETOUR = d1+d2-(bestat.get(route[i+1])-bestat.get(route[i]));
        	       int l1 = bestload.get(route[i])+1;
        	       
        	       
        	       //System.out.println(" DETOUR "+DETOUR+" ADOWN "+ADOWN+" a1 "+a1+" b[ne] "+b[ne] + " l1 " +l1 );
        	      // System.out.println(DETOUR<ADOWN && a1<b[ne] && l1<=Q);
        	          if(DETOUR<ADOWN && a1<b[ne] && l1<=Q ) {
        	        	//System.out.println("DETOUR"+DETOUR);
                          tempat.put(ne, a1);
                          tempload.put(ne, l1);
               	       for(int j=i+1;j<route.length;j++) {
               	    	   int t = route[j];        	    	  
               	    	   double temp2=bestat.get(t);
               	    	   tempat.put(t,temp2+DETOUR);
               	    	   int temp3 = bestload.get(t);
               	    	   tempload.put(t, temp3+1);
               	       }
                   	   int[] temproute = add(route,i,ne);
                   	   /*
                   	   for(int j=0;j<temproute.length;j++) {
                   		System.out.print(temproute[j]+"|");
                   	   }
                   	   System.out.println(" ");*/
                   	   //System.out.println("");
                   	   for(int j=i+1;j<temproute.length-1;j++) {
                   		   //System.out.println("");
                   		   //System.out.println("===== j "+j+"=============");
                   		   //HashMap<Integer,Double> bestat1=clonedouble(tempAT.get(k));
                		       //HashMap<Integer,Integer> bestload1 = cloneint(tempLOAD.get(k));
                   		   double ADOWN2=b[2*n+1]-a[2*n+1];
                   		   for(int l=j+1;l<temproute.length;l++) {
                   			   //System.out.println("l "+l);
                   			   int t=temproute[l];
                   			   //System.out.println("t "+t);
                   			   double tempadwn2=b[t]-tempat.get(t);
                   			   if(tempadwn2<=ADOWN2) {
                   				   ADOWN2=tempadwn2;
                   				   
                   			   }
                   		   }          
                   		  // System.out.println(ADOWN2);
                   		   for(int l=0;l<temproute.length;l++) {
                   			   //System.out.print(temproute[l]+"|");
                   		   }
                   		   //System.out.println("");
                   		   //System.out.println("tempat "+tempat);
                   		   //System.out.println("predecessor "+temproute[j]+" travel time "+t[temproute[j]][nn]+" a "+nn +" : "+a[nn]);
                   		   double a3 = Math.max(tempat.get(temproute[j])+t[temproute[j]][nn], a[nn]);
                   		   double d3 = a3-tempat.get(temproute[j]);
                   		   //System.out.println("a3 "+a3);
                   		   double a4 = Math.max(a3+t[nn][temproute[j+1]], tempat.get(temproute[j+1]));
                   		   double d4 = a4-a3;  
                   		   //System.out.println("a4 "+a4);
                   		   double DETOUR2 = d4+d3-(tempat.get(temproute[j+1])-tempat.get(temproute[j]));
                   		   //System.out.println("DETOUR"+DETOUR2);
                   		   int l2 = tempload.get(temproute[j])-1;
                   		   
                 	          //System.out.println(" DETOUR "+DETOUR+" ADOWN "+ADOWN+" a1 "+a1+" b[ne] "+b[ne] + " l1 " +l1 );
                 	          
                   		   if(DETOUR2 < ADOWN2 && a3 <b[nn] ) {
                   			 HashMap<Integer,Double> tempat2 = clonedouble(tempat);
                     		   tempat2.put(nn, a3);
                     		   HashMap<Integer,Integer> tempload2 = cloneint(tempload);
                     		   tempload2.put(nn,l2);
                     		   //HashMap<Integer, int[]> tempRoute2 = clonearray(tempRoute);
                                //System.out.println("at2"+tempat2);
                     		   for(int l=j+1;l<temproute.length;l++) {
                     			   int t = temproute[l];
                     			   double temp2 = tempat.get(t);
                     			   //System.out.println("t "+t);
                     			  //System.out.println(temp2);
                     			   tempat2.put(t, temp2+DETOUR2);
                     			   int temp3 = tempload.get(t);
                     			   tempload2.put(t, temp3-1);
                     		   }
                     		   //System.out.println("at2 after "+tempat2);
                     		   int[] temproute2 = add(temproute,j,nn);
                     		   /*
                     		  for(int s=0;s<temproute2.length;s++) {
                             		System.out.print(temproute2[s]+"|");
                             	   }
                     		  System.out.println(" ");*/
                     		   /*
                     		   for(int r=0;r<temproute2.length;r++) {
                         		   System.out.print(temproute2[r]+"|");
                         	   }
                     		   System.out.println("");*/
                     		   tempAT.put(k, tempat2);
                     		   tempLOAD.put(k, tempload2);;
                     		   tempRoute.put(k,temproute2);
                     		   HashMap<Integer,Double> tempCOST = clonecost(bestcost);
                     		   tempCOST.put(k, tempat2.get(2*n+1));
                     		   double ttcost=0;
                     		   
                     		   for(int l:tempCOST.keySet()) {
                     			   ttcost=ttcost+ tempCOST.get(l);
                     		   }
                     		   /*
                     		   for(int l:tempRoute.keySet()) {
                             	   int[] temp3 = tempRoute.get(l);
                             	   //System.out.println("vehicle  " +l);
                             	   
                             	   for(int s=0;s<temp3.length;s++) {
                             		   System.out.print(temp3[s]+"|");
                             	   }
                             	   System.out.println("");
                                }*/
             	            	 //System.out.println("AT "+tempAT);
             	            	//System.out.println("LOAD "+tempLOAD);
             	            	 //System.out.println("COST "+tempCOST);
                     		   //System.out.println("ttcost "+ttcost);
                     		   //System.out.println("bc "+bc);
                                if(ttcost<bc) {
                             	   bc=ttcost;
                             	   bCOST= tempCOST;
                             	   broute = tempRoute;
                             	   bAT = tempAT;
                             	   bLOAD = tempLOAD;
                                }
                   		   }       
                   	   }
        	          }
               }
		   }
	   }    
	   //System.out.println("BLOAD "+bLOAD);\
	   int num1=0;//total number of requests inserted;
	   for(int i:broute.keySet()) {
		   num1=num1+broute.get(i).length;
	   }
	   int num2=0;//total number of requests inserted before 
	   for(int i:bestroute.keySet()) {
		   num2 = num2+bestroute.get(i).length; 
	   }
	   
	if(bLOAD.size()!=0 && num1>num2) {
		//System.out.println("feasible");
		   bestcost=bCOST;
		   bestroute=broute;
		   bestAT= bAT;
		   bestLOAD=bLOAD;
		   return true;
	}else {
		//System.out.println("infeasible");	
		return false;
	}
	 }
	
	boolean insert1(int v,int request) {
		//System.out.println("insert rqeust "+request+" to bus "+v+"===============");
		int ne=request;//new request
		//int nn=ne+n;
		HashMap<Integer,Double> cancost =new HashMap<Integer,Double>();
		HashMap<Integer,Double> canAT = new HashMap<Integer,Double>();
		HashMap<Integer,Integer> canLoad = new HashMap<Integer,Integer>();
		HashMap<Integer,int[]> canroute = new HashMap<Integer,int[]>() ;
	    double bc=Double.MAX_VALUE; //bestcost
	    HashMap<Integer,Double> bCOST = new HashMap<Integer,Double>();
	    HashMap<Integer,HashMap<Integer,Double>> bAT = new HashMap<Integer,HashMap<Integer,Double>>();
	    HashMap<Integer,HashMap<Integer,Integer>> bLOAD = new HashMap<Integer,HashMap<Integer,Integer>>();
	    HashMap<Integer,int[]> broute = new HashMap<Integer,int[]>();
	    int k=v;
		   //System.out.println("K "+k);		  
		      int[] route = bestroute.get(k);  		     
   	          HashMap<Integer,int[]> tempRoute = clonearray(bestroute);
   	          HashMap<Integer,HashMap<Integer,Double>> tempAT = cloneDOUBLE(bestAT); 
   	          HashMap<Integer,HashMap<Integer,Integer>> tempLOAD = cloneINT(bestLOAD);
 		      HashMap<Integer,Double> bestat=clonedouble(bestAT.get(k));
		      HashMap<Integer,Integer> bestload = cloneint(bestLOAD.get(k));
		      //checkroute(route);
               for(int i=0;i<route.length-1;i++) {
            	   //System.out.println("trying to insert at " +i);
       	          HashMap<Integer,Double> tempat = clonedouble(bestat);
       	          HashMap<Integer,Integer> tempload = cloneint(bestload);
        	       //calculate ADOWN
            	   //System.out.println("");
            	   //System.out.println("===== i "+i+"=============");
        	       double ADOWN=b[2*n+1]-a[2*n+1];
        	       
        	       for(int j=i+1;j<route.length;j++) {
        	    	   //System.out.println("route j "+route[j]);
        		       double tempadwn=b[route[j]]-bestat.get(route[j]);
        		       if(tempadwn<=ADOWN){
        			    ADOWN=tempadwn;	
        		       }
        	       }
        	       //System.out.println("ADOWN"+ADOWN);
        	       //System.out.println("bestat "+bestat+" ne " +ne);
        	       //System.out.println("t "+t[route[i]][ne]);
        	       //System.out.println("a "+a[ne]);
        	       //System.out.println(" routei "+route[i]);
        	       double a1 = Math.max(bestat.get(route[i])+t[route[i]][ne], a[ne]);
        	       double d1 = a1-bestat.get(route[i]);
        	       double a2 = Math.max(a1+t[ne][route[i+1]], bestat.get(route[i]));
        	       double d2 = a2-a1;
        	       double DETOUR = d1+d2-(bestat.get(route[i+1])-bestat.get(route[i]));
        	       int l1 = bestload.get(route[i])-1;
        	       
        	       
        	       //System.out.println(" DETOUR "+DETOUR+" ADOWN "+ADOWN+" a1 "+a1+" b[ne] "+b[ne] + " l1 " +l1 );
        	       //System.out.println(DETOUR<ADOWN && a1<b[ne] && l1<=Q);
        	          if(DETOUR<ADOWN && a1<b[ne] && l1<=Q ) {
        	        	//System.out.println("DETOUR"+DETOUR);
                          tempat.put(ne, a1);
                          tempload.put(ne, l1);
               	       for(int j=i+1;j<route.length;j++) {
               	    	   int t = route[j];        	    	  
               	    	   double temp2=bestat.get(t);
               	    	   tempat.put(t,temp2+DETOUR);
               	    	   int temp3 = bestload.get(t);
               	    	   tempload.put(t, temp3-1);
               	       }
                   	   int[] temproute = add(route,i,ne);
                   	   
                   
                   	   //System.out.println("");
                   		   //System.out.println("");
                   		   //System.out.println("===== j "+j+"=============");
                   		   //HashMap<Integer,Double> bestat1=clonedouble(tempAT.get(k));
                		       //HashMap<Integer,Integer> bestload1 = cloneint(tempLOAD.get(k));                  		   
                     		   tempAT.put(k, tempat);
                     		   tempLOAD.put(k, tempload);;
                     		   tempRoute.put(k,temproute);
                     		   HashMap<Integer,Double> tempCOST = clonecost(bestcost);
                     		   tempCOST.put(k, tempat.get(2*n+1));
                     		   
                     		  double ttcost=0;
                    		   
                    		   for(int l:tempCOST.keySet()) {
                    			   ttcost=ttcost+ tempCOST.get(l);
                    		   }
                    		   //System.out.println(" ttcost "+ttcost);
                    		   //System.out.println(" bc " +bc);
                    		   if(ttcost<bc) {
                             	   bc=ttcost;
                             	   bCOST= tempCOST;
                             	   broute = tempRoute;
                             	   bAT = tempAT;
                             	   bLOAD = tempLOAD;
                                } 	   
                    		   
        	          }
	          }
               int num1=0;//total number of requests inserted;
        	   for(int i:broute.keySet()) {
        		   num1=num1+broute.get(i).length;
        	   }
        	   int num2=0;//total number of requests inserted before 
        	   for(int i:bestroute.keySet()) {
        		   num2 = num2+bestroute.get(i).length; 
        	   }
        	   
        	if(bLOAD.size()!=0 && num1>num2) {
        		//System.out.println("feasible");
        		   bestcost=bCOST;
        		   bestroute=broute;
        		   bestAT= bAT;
        		   bestLOAD=bLOAD;
        		   return true;
        	}else {
        		//System.out.println("infeasible");	
        		return false;
        	}
	}
     void initiate() {
    	 int m=req.get(0);
    	 int[] temproute = new int[4];
    	 temproute[0]=0;
    	  temproute[1]=m;
    	  temproute[2]=m+n;
    	  temproute[3]=2*n+1;
          HashMap<Integer,Double> tempat = new HashMap<Integer,Double>();
          tempat.put(0, 0.0);
          double a1 = Math.max(a[m], t[0][m]);
          tempat.put(m, a1);
          double a2=Math.max(a1+t[m][m+n], a[m+n]);
          tempat.put(m+n, a2);
          tempat.put(2*n+1,a2+t[m+n][2*n+1]);
          HashMap<Integer, Integer> tempload = new HashMap<Integer,Integer>();
          tempload.put(0, 0);
          tempload.put(m, 1);
          tempload.put(m+n, 0);
          tempload.put(2*n+1, 0);
          double tempcost=tempat.get(2*n+1);
          HashMap<Integer,HashMap<Integer,Double>> tempAT = new HashMap<Integer,HashMap<Integer,Double>>();
          HashMap<Integer,HashMap<Integer,Integer>> tempLoad = new HashMap<Integer,HashMap<Integer,Integer>>();
          HashMap<Integer, int[]> tempRoute = new HashMap<Integer,int[]>();
          tempAT.put(0, tempat);
          tempLoad.put(0, tempload);
          tempRoute.put(0, temproute);
          bestcost.put(0, tempcost);
          //System.out.println(bestcost);
          bestAT=tempAT;
          //System.out.println(bestAT);
          bestLOAD=tempLoad;
          bestroute=tempRoute;
          /*
          for(int i=0;i<bestroute.get(0).length;i++) {
        	  System.out.print(bestroute.get(0)[i]+"|");
          }
          System.out.println(bestLOAD);*/
    	 
     }
     int[] add(int[] array, int position, int element) {
    	 if(position<array.length) {
    	 int[] temp = new int[array.length+1];
    	 for(int i=0;i<=position;i++) {
    		 temp[i]=array[i];
    	 }
    	 temp[position+1]=element;
    	 for(int i=position+1;i<array.length;i++) {
    		 temp[i+1]=array[i];
    	 }
    	 return temp;
    	 }else {
    		 System.out.println("position "+position+"  greater than array length   "+array.length);
    		 return new int[1];
    	 }
     }
     HashMap<Integer,Double> clonecost(HashMap<Integer,Double> input){
    	 HashMap<Integer,Double> temp = new HashMap<Integer,Double>();
    	 //System.out.println(input.keySet());
    	 for(int i:input.keySet()) {
    		 temp.put(i, input.get(i));
    		 //System.out.println(i+" -- "+input.get(i));
    	 }
    	 return temp;
     }
     HashMap<Integer,HashMap<Integer, Double>> cloneDOUBLE(HashMap<Integer,HashMap<Integer,Double>> input){
    	 HashMap<Integer,HashMap<Integer, Double>> temp = new HashMap<Integer,HashMap<Integer, Double>>();
    	 for(int i:input.keySet()) {
    		 HashMap<Integer, Double> temp1 = new HashMap<Integer, Double>();
    		 HashMap<Integer, Double> temp2 = input.get(i);
    		 for(int j:temp2.keySet()) {
    			 temp1.put(j, temp2.get(j));
    		 }
    		temp.put(i, temp1);
    	 }
    	 return temp;
     }
     HashMap<Integer, Double> clonedouble(HashMap<Integer, Double> input){
    	 HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
    	 for(int i:input.keySet()) {
    		 temp.put(i, input.get(i));
    	 }
    	 return temp;
     }
     HashMap<Integer,HashMap<Integer, Integer>> cloneINT(HashMap<Integer,HashMap<Integer,Integer>> input){
    	 HashMap<Integer,HashMap<Integer, Integer>> temp = new HashMap<Integer,HashMap<Integer, Integer>>();
    	 for(int i:input.keySet()) {
    		 HashMap<Integer, Integer> temp1 = new HashMap<Integer, Integer>();
    		 HashMap<Integer, Integer> temp2 = input.get(i);
    		 for(int j:temp2.keySet()) {
    			 temp1.put(j, temp2.get(j));
    		 }
    		temp.put(i, temp1);
    	 }
    	 return temp;
     }
     HashMap<Integer, Integer> cloneint(HashMap<Integer, Integer> input){
    	 HashMap<Integer,Integer> temp = new HashMap<Integer, Integer>();
    	 for(int i:input.keySet()) {
    		 temp.put(i, input.get(i));
    	 }
    	 return temp;
     }
     HashMap<Integer, int[]> clonearray(HashMap<Integer,int[]> input){
    	 HashMap<Integer, int[]> temp = new HashMap<Integer, int[]>();
    	 for(int i:input.keySet()) {
    		 temp.put(i,input.get(i));
    	 }
    	 return temp;
     }
     TreeMap<Integer,Integer> clonetreemap(TreeMap<Integer,Integer> input){
    	 TreeMap<Integer,Integer> temp = new TreeMap<Integer,Integer>();
    	 for(int i:input.keySet()) {
    		 temp.put(i, input.get(i));
    	 }
    	 return temp;
     }
     int[] cloneary (int[] input) {
    	 int[] tempn = new int[input.length];
    	 for(int i=0;i<input.length;i++) {
    		 tempn[i]=input[i];
    	 }
    	 return tempn;
     }
     void checkroute(int[] input) {
    	 for(int i=0;i<input.length;i++) {
    		 System.out.print(input[i]+"|");
    	 }
     }

	boolean runinsertion() {
    	 sort();
    	 initiate();
    	// System.out.println(req.size());
    	 //System.out.println(req);
    	 //int num=0;
    	 for(int i=1;i<req.size();i++) {
    		 int tt = req.get(i);
    		 //System.out.println("==============insert to add request "+req.get(i)+" ================");
    		 if(insert(tt)==true) {
    			 //num=num+1;
    			 //System.out.println("feasible");
    			 //outputbestroute();
    		 }else {
    			 //System.out.println("infeasible");    			 
    			 return false;
    		 }
    	 }
      //System.out.println("request accepted "+num);
    	 //System.out.println(bestAT);
    	 for(int i:bestAT.keySet()) {
    		 HashMap<Integer,Double> temp =bestAT.get(i);
    		 //System.out.println(temp);
    		 for(int j:temp.keySet()) {
    			 timewindow.put(j, temp.get(j));
    		 }
    	 }
    	// System.out.println("====================");
    	 //System.out.println(timewindow);
    	// System.out.println("====================");
    	 return true;
     }
	boolean reruninsertion() {
		
		for(int k=0;k<req.size()-1;k++) {
			  for(int i=0;i<req.size()-1;i++) {
				if(a[req.get(i)]>a[req.get(i+1)]) {
					Collections.swap(req, i, i+1);
				}
			  }
			} 
		System.out.println("req "+req);
		for(int i=0;i<req.size();i++) {
   		 int tt = req.get(i);
   		 //System.out.println("==============insert to add request "+i+" ================");
   		 if(insert(tt)==true) {
   			 //num=num+1;
   			 //System.out.println("feasible");
   			 //outputbestroute();
   		 }else {
   			 //System.out.println("infeasible");    			 
   			 return false;
   		 }
   	 }
     //System.out.println("request accepted "+num);
   	 for(int i:bestAT.keySet()) {
   		 HashMap<Integer,Double> temp =bestAT.get(i);
   		 for(int j:temp.keySet()) {
   			 timewindow.put(j, temp.get(j));
   		 }
   	 }
   	 return true;
		
	}
	
	TreeMap<Integer, String> runinsertionDARP() {
   	 sort();
   	 initiate();
   	// System.out.println(req.size());
   	 System.out.println(req);
   	 int num=0;
   	 TreeMap<Integer,String> temp = new TreeMap<Integer,String>();
   	 for(int i=1;i<req.size();i++) {
   		 int tt = req.get(i);
   		 System.out.print(req.get(i)+" : ");
   		 if(insert(tt)==true) {
   			 num=num+1;
   			 System.out.println("feasible");
   			 temp.put(tt, "accept");
   			 //outputbestroute();
   		 }else {
   			 temp.put(tt, "reject");
   			 System.out.println("infeasible");    			 
   		 }
   	 }
   	 System.out.println("num "+num);
   	 return temp;
     //System.out.println("request accepted "+num);
   	 //System.out.println(bestAT);
   	// System.out.println("====================");
   	 //System.out.println(timewindow);
   	// System.out.println("====================");
   	 
    }
	///////////////////run insertion until "a" request////////////////////////
	boolean runinserttill(int a, ArrayList<Integer> b) {
		sort();
   	 initiate();
   	 
   	 ArrayList<Integer> tempreq = new ArrayList<Integer>();
   	 for(int i=0;i<a;i++) {
   		 tempreq.add(req.get(i));
   	 }
   	 System.out.println("request before remove" +tempreq.size());
   	 for(int i=0;i<b.size();i++) {
   		 int tempb = b.get(i);
   		 if(tempreq.contains(tempb)) {
   			 tempreq.remove(Integer.valueOf(tempb));
   		 }
   	 }
     System.out.println("request size "+tempreq.size());
     System.out.println("with cancellation");
   	 for(int i=1;i<tempreq.size();i++) {
   		 int tt = tempreq.get(i);
   		 if(insert(tt)==true) {
   			 //num=num+1;
   			 //System.out.println("feasible");
   			 //outputbestroute();
   		 }else {
   			//System.out.println("infeasible");    			 
   			 //return false;
   		 }
   	 }
     //System.out.println("request accepted "+num);
   	 //System.out.println(bestAT);
   	 double tempcost =0;
   	 for(int i:bestAT.keySet()) {
   		 tempcost = tempcost+bestAT.get(i).get(2*n+1);
   	 }
   	 System.out.println("cost "+tempcost);
   	 double tr = Math.random()*0.3+1.0;
   	 double cost1 = (tempcost-10000)*tr;
   	 //System.out.println("cost1 "+cost1);
   	 for(int i:bestAT.keySet()) {
   		 HashMap<Integer,Double> temp =bestAT.get(i);
   		 //System.out.println(temp);
   		 for(int j:temp.keySet()) {
   			 timewindow.put(j, temp.get(j));
   		 }
   	 }
   	// System.out.println("====================");
   	 //System.out.println(timewindow);
   	// System.out.println("====================");
   	System.out.println("request size after"+req);
   	 return true;
	}
	boolean runinserttillwithoutcancel(int a) {
		sort();
   	 initiate();
   	 System.out.println("requests size "+req.size());
   	 System.out.println("without cancellation");
   	 //System.out.println(b);
   	 for(int i=1;i<a;i++) {
   		 int tt = req.get(i);
   		 if(insert(tt)==true) {
   			 //num=num+1;
   			 //System.out.println("feasible");
   			 //outputbestroute();
   		 }else {
   			//System.out.println("infeasible");    			 
   			 //return false;
   		 }
   	 }
     //System.out.println("request accepted "+num);
   	 //System.out.println(bestAT);
   	 double tempcost =0;
   	 for(int i:bestAT.keySet()) {
   		 tempcost = tempcost+bestAT.get(i).get(2*n+1);
   	 }
   	 System.out.println("cost "+tempcost);
   	 double tr = Math.random()*0.3+1.0;
   	 double cost1 = (tempcost-10000)*tr;
   	 System.out.println("cost1 "+cost1);
   	 for(int i:bestAT.keySet()) {
   		 HashMap<Integer,Double> temp =bestAT.get(i);
   		 //System.out.println(temp);
   		 for(int j:temp.keySet()) {
   			 timewindow.put(j, temp.get(j));
   		 }
   	 }
   	// System.out.println("====================");
   	 //System.out.println(timewindow);
   	// System.out.println("====================");
   	 return true;
	}
     void maxnum() {
    	 sort();
    	 initiate();
    	 //System.out.println("size"+req.size());
    	 int num=0;
    	 for(int i=1;i<req.size();i++) {
    		 int tt = req.get(i);
    		 System.out.println("==============insert to add request "+i+" ================");
    		 if(insert(tt)==true) {
    			 num=num+1;
    			 System.out.println("feasible");
    			 System.out.println("request accepted "+num);
    			 //outputbestroute();
    		 }else {
    			 System.out.println("infeasible");  
    			 System.out.println("request accepted "+num);
    		 }
    	 }
      
     }
     void outputbestroute() {
    	 for(int i:bestroute.keySet()) {
    		 System.out.println("vehicle "+i);
    		 int[] rtemp = bestroute.get(i);
    		 for(int j=0;j<rtemp.length;j++) {
    			 System.out.print(rtemp[j]+"|");
    		 }
    		 /*
    		 System.out.println("");
    		 System.out.println("--------------------------");
    		 for(int j=0;j<rtemp.length-1;j++) {
    			 System.out.print(rtemp[j] +" -- "+rtemp[j+1]+" : "+t[rtemp[j]][rtemp[j+1]]+"  |||   ");
    			 System.out.println(rtemp[j] +" : "+bestAT.get(i).get(rtemp[j]) +"   "+rtemp[j+1]+" : "+bestAT.get(i).get(rtemp[j+1]));
    		 }*/
    		 System.out.println("");
    		 //System.out.println(" bestcost "+bestcost.get(i));
    		 //System.out.println("");
    		 //System.out.println(bestAT.get(i));
    		 //System.out.println(bestLOAD.get(i));
    		 //System.out.println(bestAT);
    	 }
    	 //System.out.println(timewindow);
     }
     void outputcost() {
    	 double cost=0;
    	 for(int i:bestAT.keySet()) {
    		 cost = bestAT.get(i).get(2*n+1)+cost;
    	 }
    	 System.out.println("total cost "+cost);
     }
     void update(int r) {
    	 try {
    	 reql = new HashMap<Integer,ArrayList<Integer>>();
    	 double tempt = timewindow.get(r);
    	 ori = new HashMap<Integer,Integer>();
    	 des = new HashMap<Integer,Integer>();
    	 ran = new HashMap<Integer,Integer>();
    	 outerloop:

    	 for(int i:bestroute.keySet()) {
    		 int[] tempr1 = bestroute.get(i);
    		 for(int j=1;j<tempr1.length-1;j++) {
    			 if(tempr1[j] == r) {
    				 ns=i;
    				 ran.put(ns, j);
    				 break outerloop;
    			 }
    		 }
    		 
    	 }
    	

    	 
    	 getod(tempt);
    	    ///////////update request/////////////////////
    	 //TreeMap<Integer,Integer> tempPa = new TreeMap<Integer,Integer>();

    	 for(int i:bestroute.keySet()) {
    		    int[] tempr = bestroute.get(i).clone();
    		    ArrayList<Integer> tempar = new ArrayList<Integer>();
    		    if(i==ns) {
    		      for(int j=1;j<ran.get(i);j++) {
    		    	  if(tempr[j]<=n ) {
    				    req.remove(Integer.valueOf(tempr[j]));
    				    //System.out.println("req "+req);
    			         	//System.out.println("tempr "+tempar);
    		    	  }
    		       }
    		     
    		       
    		       for(int j=tempr.length-2;j>=ran.get(i);j--) {
    		    	  if(tempr[j]>n) {
    		    		  tempar.add(tempr[j]);
    		    	  }else {
    		    		  tempar.remove(Integer.valueOf(tempr[j]+n));
    		    	  }
    		       }
    		    }else {
    		    	for(int j=1;j<=ran.get(i);j++) {
    		    		if(tempr[j]<=n ) {
       				    req.remove(Integer.valueOf(tempr[j]));
    		    		}
       		        }     		    
       		      for(int j=tempr.length-2;j>ran.get(i);j--) {
		    	  if(tempr[j]>n) {
		    		  tempar.add(tempr[j]);
		    	  }else {
		    		  tempar.remove(Integer.valueOf(tempr[j]+n));
		    	  }
		       }
    		    }
    		    reql.put(i, tempar);
    	 }
    	 
    	 
    	 
    	 req.remove(Integer.valueOf(r));
    	 System.out.println("request need to be insert "+req);
    	 System.out.println("request list  "+reql);
    	 /*
    	 bestroute = new HashMap<Integer,int[]>();
    	 bestAT = new HashMap<Integer,HashMap<Integer,Double>>();
    	 bestLOAD = new HashMap<Integer,HashMap<Integer,Integer>>();
    	 bestcost = new HashMap<Integer,Double>();
    	 */
    	 ////////////////////initiate route for the rest of buses//////////////////
    	 System.out.println("ns "+ns);
    	 for(int i:bestroute.keySet()) {
    		 //System.out.println("i "+i);	 
    		 if(i == ns) {
    			 int[] tempr = new int[2];
    			 tempr[0]=r; tempr[1]=2*n+1;
    			 HashMap<Integer,Double> tempat = new HashMap<Integer,Double>();
    			 double temptr = tempt;
    			 tempat.put(r, temptr);
    			 
    			 tempat.put(2*n+1, temptr+t[r][2*n+1]);
    			 HashMap<Integer,Integer> templd = new HashMap<Integer,Integer>();
    			 int cl = reql.get(i).size();
    			 templd.put(r, cl);templd.put(2*n+1, cl);
    			 double tempcost = tempat.get(2*n+1);
    			 
    			 bestroute.put(i, tempr);
        		 bestAT.put(i, tempat);
        		 bestLOAD.put(i,templd);
        		 bestcost.put(i, tempcost);
    		 }else {
    			 int addn=t.length;//add dummy node
                 /////////////////////update travel time//////////////
    			 //System.out.println("i "+i);
			     int o1 = ori.get(i);
			     int d1 = des.get(i);
			     double a1 = timewindow.get(o1);
			     double b1 = timewindow.get(d1);
			     double pro = (tempt-a1)/(b1-a1);//proportion of distance based on time
			     double[][] tn = nw1.recalculate(o1, d1, pro);//new t matrix for dummy node to all other nodes
			     double[][] newt = new double[addn+1][addn+1];//new travel time matrix
			
		    	
			    t = tn;
			    
			    int[] tempr = new int[2];
			    tempr[0]=addn; tempr[1]=2*n+1;
			    
			    HashMap<Integer,Double> tempat = new HashMap<Integer,Double>();
			    double nat = tempt;
      		    tempat.put(addn, nat);
      		    changab(nat,b[2*n+1]);
      		    //System.out.println(addn +" timewindow "+a[addn]+" : "+b[addn]);
      		    //System.out.println(2*n+1 +" timewindow "+a[2*n+1]+" : "+b[2*n+1]);
      		   
      		    tempat.put(2*n+1, nat+t[2*n+1][addn]);
      		    HashMap<Integer,Integer> templd = new HashMap<Integer,Integer>();
      		    int cl = reql.get(i).size();
      		    
      		    templd.put(addn, cl);templd.put(2*n+1, cl);
      		    double tempcost = tempat.get(2*n+1);    
      		    
      		  bestroute.put(i, tempr);
     		  bestAT.put(i, tempat);
     		  bestLOAD.put(i,templd);
     		  bestcost.put(i, tempcost);
    		 }
    		 
    	 }
    	
    	 
 ////create new route and add current point   	 
    	 
    	 for(int i:reql.keySet()) {
    		 ArrayList<Integer> tempreql = reql.get(i);
    		 for(int j=0;j<tempreql.size();j++) {
    			 int tempq = tempreql.get(j);
    			 //System.out.println(i+" . "+tempq);
    			 insert1(i,tempq);
    		 }
    	 }
    	 //System.out.println("==================after update initiate ==========================");
    	 //outputbestroute();
    	 //System.out.println("==============");
    	 
    	 /*
    	 Pa=tempPa;
    	 TreeMap<Integer,Integer> tempPb = new TreeMap<Integer,Integer>();
    	 for(int i:tempPa.keySet()) {
    		 tempPb.put(i+n, i+n);
    	 }
    	 TreeMap<Integer,Integer> tempPt = new TreeMap<Integer,Integer>();
    	 for(int i:tempPa.keySet()) {
    		 tempPt.put(i, i);
    	 }
    	 */
    	 }catch(NullPointerException e){
    		 System.out.println();
    	 }
     }
     //get od pair for each bus based on current time
     void getod(double time) {
    	 
    	 System.out.println("ns "+ns);
    	 System.out.println("time "+time);
    	 for(int i:bestroute.keySet()) {
    		 //System.out.println("bus "+i);
    		 if(i != ns) {
    		    int[] tempr = bestroute.get(i);
    		    for(int j=0;j<tempr.length-1;j++) { 
    		    	//System.out.println(tempr[j] +" : " +timewindow.get(tempr[j]) +" -- "+ tempr[j+1]+" : "+timewindow.get(tempr[j+1]));
    			    if(time >= timewindow.get(tempr[j]) && time<=timewindow.get(tempr[j+1]) ) {
    			    	
    				    ori.put(i, tempr[j]);
    				    des.put(i, tempr[j+1]);
    				    ran.put(i, j);
    				    break;
    			    }
    		   }
    		 }
    	 } 
    	 System.out.println("ori  "+ori);
    	 System.out.println("des  "+des);
    	 System.out.println("ran  "+ran);
     }
     
     public void changab(double a, double b ) {
  	   int size = this.a.length;
  	   double[] na = new double[size+1];
  	   double[] nb = new double[size+1];
  	   for(int i=0;i<size;i++) {
  		   na[i] = this.a[i];
  		   nb[i] = this.b[i];
  	   }
  	   na[size]=a;
  	   nb[size]=b;
  	   this.a=na;
  	   this.b=nb;
  	   
     }
     
     void test() {
    	 int[] temp = new int[4];
    	 temp[0]=0;temp[1]=1;temp[2]=6;temp[3]=11;
    	 int[] tempn = add(temp,0,2);
    	 for(int i=0;i<tempn.length;i++) {
    		 System.out.print(tempn[i]+"|");
    	 }
     }
	  public static void main (String[] args) throws Exception, IloException {
	  network nw = new network();
	  
	  //nw.readnw("20100113u2.txt");
	  nw.readnw("toymodel.txt");
	  insertion insert = new insertion(nw);
	  insert.test();
	  //insert.print();
	  //insert.maxnum();
	  
	  /*
	  System.out.println("==============insert to add request 2 ================");
	  insert.insert(2);
	  System.out.println("==============insert to add request 1 ================");
	  insert.insert(1);
	  System.out.println("==============insert to add request 3 ================");
	  insert.insert(3);
	  System.out.println("==============insert to add request 4 ================");
	  insert.insert(4);
	  insert.outputbestroute();
	  System.out.println("==============insert to add request 5 ================");
	  insert.insert(5);
	  System.out.println("V "+insert.V);*/
	  }
	
}	
	     
	     
	     
	     
	     
	     
	     