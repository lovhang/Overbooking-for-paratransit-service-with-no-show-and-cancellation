
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ilog.concert.IloNumVar;

public class network {
	public TreeMap<Integer,Integer> Pa;
	public TreeMap<Integer,Integer> Pn;//no-show request
	public TreeMap<Integer,Integer> Pc;//late cancel request
	public TreeMap<Integer,Integer> Pb;//set of destination node
	public TreeMap<Integer,Integer> Pt;//union of Pa, Pn,and Pb
	public TreeMap<Integer,Integer> P;
	public TreeMap<Integer,Integer> N;
	public int n;//number of request
	public double[] d;
	public double[] s;
	//public double[] q;
	public double[][] t;
	public double[][] c;
	public double[] a;
	public double[] b;
	public double[] w;
	public double[] x;
	public double[] y;
	public double Q;//capacity of bus
	public int V;
	public double[] prob;
	public TreeMap<Integer,Integer> temphash;
	private String[] col;
	double max;
	double speed;
	double probn;//probability of no show
	public int numshow;//number of showup request
	private int updaten;//network update time, so that the network increase with this number
       public network() {
    	   speed=0.5;
    	   probn=0.1;
    	   updaten=2*n+2;//initiate with one more scale of initial network
       }
       public void generateRandomNetwork(int n,double Q,int V) {
    	   
    	   Pa = new TreeMap<Integer,Integer>();
    	   Pb = new TreeMap<Integer,Integer>();
    	   P = new TreeMap<Integer,Integer>();
    	   N = new TreeMap<Integer,Integer>();
    	   d = new double[2*n+2];
    	   s = new double[2*n+2];
    	   t = new double[2*n+2][2*n+2];
    	   c = new double[2*n+2][2*n+2];
    	   a = new double[2*n+2];
    	   b = new double[2*n+2];
    	   this.Q=Q;
    	   this.n=n;
    	   this.V=V;
    	   for(int i=1;i<=n;i++) {
    		   Pa.put(i, i);
    	   }

    	   for(int i=n+1;i<=2*n;i++) {
    		   Pb.put(i, i);

    	   }


    	   for(int i=1;i<=2*n;i++) {
    		   P.put(i, i);
    	   }

    	   for(int i=0;i<2*n+2;i++) {
    		   N.put(i, i);
    	   }
    	   x= new double[2*n+2];
   		   y= new double[2*n+2];
   		    max =100;//define the max distance in network
   		   for(int i=1;i<=2*n;i++){
			x[i]=(Math.random()*(max)+1);
		   }
		   for(int i=1;i<2*n;i++){
		 	y[i]=(Math.random()*(max)+1);
		   }
		   x[0]=max/2;
		   y[0]=max/2;
		   x[2*n+1]=x[0];
		   y[2*n+1]=y[0];
		   
		   for(int i:N.keySet()) {
				for(int j:N.keySet()) {
					//t[i][j]=(Math.abs(x[i]-x[j])+Math.abs(y[i]-y[j]))/10;
					int a = N.get(i);
					int b = N.get(j);
					c[a][b]=Math.sqrt((x[a]-x[b])*(x[a]-x[b])+(y[a]-y[b])*(y[a]-y[b]));			
					t[a][b]=c[a][b]/100;
					//System.out.println(t[a][b]);
				}
			}
		   for(int i=1;i<=n;i++) {
			   a[i]=(Math.random()*(max/2));
			   //System.out.println(i+":"+a[i]);
		   }
		   for(int i=n+1;i<=2*n;i++) {
			   a[i]=a[i-n]+50;
			   //System.out.println(i+":"+a[i]);
		   }
		   a[0]=0;
		   a[2*n+1]=0;
		   for(int i=0;i<=2*n;i++) {
			   b[i]=a[i]+20;
		   }
		 
		   b[0]=max;
		   b[2*n+1]=max;
		   for(int i=1;i<=2*n;i++) {		   
			  s[i]=(Math.random()*(max/100));
		   }
		   s[0]=0;
		   s[2*n+1]=0;
		   for(int i=1;i<=2*n;i++) {
			   d[i]=1;//set demand 1
		   }

		   d[0]=0;
		   d[2*n+1]=0;
		   
       }
       public void setnetwork() {
    	   

    	   for(int i=0;i<=2*n+1;i++) {
				for(int j=i+1;j<=2*n+1;j++) {
					//c[i][j]=10+Math.random()*10;
					//c[j][i]=c[i][j];
					//System.out.println("c."+i+"."+j+":"+c[i][j]);
					t[i][j]=10;
				}
    	   }
    	   
    	   for(int i=1;i<=2*n;i++) {
    		   s[i]=2;
    	   }
  //SETTING SPECIFIC NETOWORK
    	   
    	   a[0]=0;a[1]=26.89;a[2]=3.69;a[3]=18.3;a[4]=76.89;a[5]=53.69;a[6]=68.31;a[7]=0;
    	   b[0]=100;b[1]=46.89;b[2]=23.70;b[3]=38.31;b[4]=96.89;b[5]=73.70;b[6]=88.3;b[7]=100;
           c[0][1]=15.58;c[0][2]=12.8;c[0][3]=19.7;c[0][4]=11.8;c[0][5]=18.4;c[0][6]=11.6;c[0][7]=17.13;
    	   c[1][2]=16.24;c[1][3]=13.14;c[1][4]=16.9;c[1][5]=16.0;c[1][6]=10.86;c[1][7]=14.5;
    	   c[2][3]=18.35;c[2][4]=12.6;c[2][5]=10.46;c[2][6]=17.76;c[2][7]=18.14;
    	   c[3][4]=11.92;c[3][5]=12.78;c[3][6]=12.53;c[3][7]=18.78;
    	   c[4][5]=10.04;c[4][6]=11.70;c[4][7]=13.55;
    	   c[5][6]=14.67;c[5][7]=14.87;
    	   c[6][7]=13.29;
    	   
    	   for(int i=0;i<=2*n+1;i++) {
    		   for(int j=i+1;j<=2*n+1;j++) {
    			   if(i != j) {
    				   c[j][i]=c[i][j];
    			   }
    		   }
    	   }
       }
       public void changeP(TreeMap<Integer,Integer> P) {
    	   this.P=P;
       }
       public void changePa(TreeMap<Integer,Integer> Pa) {
    	   this.Pa=Pa;
       }
       public void changePb(TreeMap<Integer,Integer> Pb) {
    	   this.Pb=Pb;
       }
       public void changeN(TreeMap<Integer,Integer> N) {
    	   this.N=N;
       }
       public void changeV(int v) {
    	   this.V=v;
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
       public void scenarioInput(int[] scenario) {
    	   TreeMap<Integer,Integer> Patemp = new TreeMap<Integer,Integer>();
    	   TreeMap<Integer,Integer> Pbtemp = new TreeMap<Integer,Integer>();
    	   TreeMap<Integer,Integer> Ptemp = new TreeMap<Integer,Integer>();
    	   TreeMap<Integer,Integer> Ntemp = new TreeMap<Integer,Integer>();
    	   for(int i=0;i<n;i++) {
    		   if(scenario[i]>0) {
    			   //System.out.println("enter"+scenario[i]);    			   
    			   Patemp.put(i+1, i+1);    			     			   
    			   Pbtemp.put(i+n+1, i+n+1);    			       			   
    			   Ptemp.put(i+1, i+1);
    			   Ptemp.put(i+n+1, i+n+1);    			       			   
    			   Ntemp.put(i+1, i+1);
    			   Ntemp.put(i+n+1, i+n+1);
    			   Ntemp.put(0, 0);
    			   Ntemp.put(2*n+1, 2*n+1);
    			   
    		   }
    	   }
    	   Pa=Patemp;
    	   Pb=Pbtemp;
    	   P=Ptemp;
    	   N = Ntemp;
    	   
       }
       public void creatnew(int[] tempa,int[] tempb ) {
    	   Pt = new TreeMap<Integer,Integer>();
    	   Pn =new TreeMap<Integer,Integer>();
    	   Pc = new TreeMap<Integer,Integer>();
    	   for(int i:Pa.keySet()) {
   			System.out.println(i);
   			Pt.put(i, i);
   		}
    	   //System.out.println(Pt);
    	   Pn = new TreeMap<Integer,Integer>();
			Pc = new TreeMap<Integer,Integer>();
			for(int i=0;i<tempa.length;i++) {
				Pn.put(tempa[i],tempa[i]);
			}
			for(int i=0;i<tempb.length;i++) {
				Pc.put(tempb[i], tempb[i]);
			}
			w= new double [2*n+2];
			for(int i=0;i<2*n+2;i++) {
				w[i]=0;
			}
			//assign waiting time for no_show request
			for(int i:Pn.keySet()) {
				w[i]=10;
			}
			for(int i:Pn.keySet()) {
					Pa.remove(i);
					Pb.remove(i+n);
					P.remove(i+n);
					N.remove(i+n);
					d[i]=0;
			}
			for(int i:Pc.keySet()) {
					Pa.remove(i);
					Pb.remove(i+n);
					P.remove(i+n);
					N.remove(i+n);
					d[i]=0;
			}
			//System.out.println("Pt:"+Pt);
			//System.out.println("Pa:"+Pa);
			//System.out.println("Pn:"+Pn);
			//System.out.println("Pc:"+Pc);
			//System.out.println("Pb:"+Pb);
			//System.out.println("P:"+P);
			//System.out.println("N:"+N);
       }
       public void outprtnw() {
    	   
    	   for(int i=0;i<2*n+1;i++) {
    		   System.out.println(x[i]+"     "+y[i]);
    	   }
    	   for(int i=0;i<=2*n+1;i++) {
    		   System.out.println("a."+i+":"+a[i]+"    b."+i+":"+b[i]);
    	   }
    	   
    	   /*
    	   for(int i=0;i<=2*n+1;i++) {
    		   for(int j=0;j<=2*n+1;j++) {
    			   if(i != j) {
    				   System.out.println("t."+i+"."+j+"  "+t[i][j]);
    			   }
    		   }
    	   }*/
           System.out.println("busnum:"+V);
       }
       public void savenw(String nwname) {
    	   String fileName = nwname;
    	   try {
			PrintWriter outputStream = new PrintWriter(fileName);
			outputStream.println(n);
			outputStream.println(Q);
			outputStream.println(V);
			for(int i:N.keySet()) {
				outputStream.println(i+"  "+x[i]+"  "+y[i]+"  "+a[i] +"  "+b[i]+"  "+s[i]+"  "+prob[i]);
			}
			//System.out.println(N);
			//System.out.println("Pt:"+Pt);
			 //System.out.println("Ptkeyset00:"+Pt.keySet());
			outputStream.println();			
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	   
       }

       public void readnw(String input) {//intput file name and the node it read
    	   File file = new File(input);
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				   
				   String st ="";
				   st = br.readLine();
				   n=Integer.parseInt(st);
				   //updaten=2*n+2;//initiate with one more scale of initial network
				   //System.out.println(n);
				   st = br.readLine();
				   Q=Double.parseDouble(st);
				   //System.out.println(Q);
				   st = br.readLine();				   
				   V=Integer.parseInt(st);
				   //System.out.println(V);
				   Pt = new TreeMap<Integer,Integer>();
					Pa = new TreeMap<Integer,Integer>();
			    	   Pb = new TreeMap<Integer,Integer>();
			    	   P = new TreeMap<Integer,Integer>();
			    	   N = new TreeMap<Integer,Integer>();
			    	   d = new double[2*n+2];
			    	   s = new double[2*n+2];
			    	   t = new double[2*n+2][2*n+2];
			    	   c = new double[2*n+2][2*n+2];
			    	   x = new double[2*n+2];
			    	   y = new double[2*n+2];
			    	   a = new double[2*n+2];
			    	   b = new double[2*n+2];
			    	   prob = new double[2*n+2];
						//col=st.split("\\s+ ");
						//K=Integer.parseInt(col[1]);	
						
						for(int i=0;i<=2*n+1;i++) {
							//System.out.println("======"+i+"=========");
							st = br.readLine();
							//System.out.println(st);
							col=st.split("\\s+ ");
							//System.out.println(i);
						       x[i]=Double.parseDouble(col[1]);
						       //System.out.println(x[i]);
						       y[i]=Double.parseDouble(col[2]);
						       //System.out.println(y[i]);
						       a[i]=Double.parseDouble(col[3]);
							    //System.out.println(a[i]);
							    b[i]=Double.parseDouble(col[4]);
							    //System.out.println(b[i]);
							    s[i]=Double.parseDouble(col[5]);
							    //System.out.println(s[i]);
							  prob[i]=Double.parseDouble(col[6]);
						    }
						//br.readLine();
					    
				    	   for(int i=1;i<=n;i++) {
				    		   Pt.put(i, i);
				    		   Pa.put(i, i);
				    	   }

				    	   for(int i=n+1;i<=2*n;i++) {
				    		   Pb.put(i, i);
				    	   }


				    	   for(int i=1;i<=2*n;i++) {
				    		   P.put(i, i);
				    	   }

				    	   for(int i=0;i<2*n+2;i++) {
				    		   N.put(i, i);
				    	   }
				    	   for(int a:N.keySet()) {
								for(int b:N.keySet()) {
									//t[i][j]=(Math.abs(x[i]-x[j])+Math.abs(y[i]-y[j]))/10;
									if(a != b) {
									c[a][b]=Math.sqrt((x[a]-x[b])*(x[a]-x[b])+(y[a]-y[b])*(y[a]-y[b]));	
									t[a][b]=c[a][b]/speed;
									//System.out.println("t."+i+"."+j+":"+ t[a][b]);
									}else {
										c[a][b]=0.0;
										t[a][b]=0.0;
									}
								}
							}
                           //System.out.println("t: "+t[1][546]+" "+t[546][1]);
                           //System.out.println("t: "+t[200][745]);
				    	   /*
				    	   for(int i=n+1;i<=2*n;i++) {
			    	        	a[i]=a[i-n]+t[i][i-n];
			    	        	b[i]=b[i-n]+2*t[i][i-n];       	
			    	        }
				    	   
			    	        a[0]=a[1]-t[0][1];
			    	        b[0]=b[2*n]+t[0][1];
			    	        a[2*n+1]=a[1];
			    	        b[2*n+1]=b[2*n]+60; 
			    	        */
				    	   for(int i=1;i<=n;i++) {
							   d[i]=1;//set demand 1
							   d[i+n]=-1;
						   }
						   d[0]=0;
						   d[2*n+1]=0;
						   //System.out.println("2n+1 num:"+(2*n+1));
						   //System.out.println("b2n+1 read"+b[2*n+1]);
						  //System.out.println(Pt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }
       
       public double[][] recalculate (int a, int b, double p) {
    	   

    	   double cx=0;
    	   double cy=0;
    	   if(x[a]>x[b]) {
    		   cx=x[a]-(x[a]-x[b])*p;
    	   }else {
    		   cx=(x[b]-x[a])*p+x[a];
    	   }
    	   if(y[a]>y[b]) {
    		   cy=y[a]-(y[a]-y[b])*p;
    	   }else {
    		   cy=(y[b]-y[a])*p+y[a];
    	   }
    	   //System.out.println("cx "+cx);
    	   //System.out.println("cy "+cy);
    	   int length = x.length;
    	   //System.out.println("length "+length);
    	   double[] xnew = new double[length+1];
    	   double[] ynew = new double[length+1];
    	   double[][] cnew = new double[length+1][length+1];
    	   double[][] tnew = new double[length+1][length+1];
    	   
    	   for(int i=0;i<x.length;i++) {
    		   xnew[i] = x[i];
    	   }
    	   xnew[length]=cx;
    	   for(int i=0;i<y.length;i++) {
    		   ynew[i] = y[i];
    	   }
    	   
    	   xnew[length] =cx;
    	   ynew[length] = cy;
    	   x=xnew;
    	   y=ynew;
    			   
    	   for(int i=0;i<length;i++) {
    		   for(int j=0;j<length;j++) {
    			   cnew[i][j] = c[i][j];
    			   tnew[i][j] = t[i][j];
    		   }
    	   }
    	   
    	   for(int i=0;i<length;i++) {
    		   cnew[i][length] = Math.sqrt((x[i]-x[length])*(x[i]-x[length])+(y[i]-y[length])*(y[i]-y[length]));
    		   cnew[length][i] = cnew[i][length];
    		   tnew[i][length]=cnew[i][length]/speed;
    		   tnew[length][i]=tnew[i][length];
    	   }
    	   cnew[length][length]=0.0;
    	   tnew[length][length]=0.0;
    	   c=cnew;
    	   t=tnew;
    	  return tnew;
       }
       public void changeclusternw(ArrayList<Integer> input,Integer busnum) {
    	   V=busnum;
    	   System.out.println(input);
    	   TreeMap<Integer,Integer>	  Pttemp = new TreeMap<Integer,Integer>();
    	   TreeMap<Integer,Integer> Patemp = new TreeMap<Integer,Integer>();
    	   TreeMap<Integer,Integer> Pctemp = new TreeMap<Integer,Integer>();
    	   TreeMap<Integer,Integer> Pntemp = new TreeMap<Integer,Integer>();
    	   TreeMap<Integer,Integer> Pbtemp = new TreeMap<Integer,Integer>();
    	   TreeMap<Integer,Integer>  Ptemp = new TreeMap<Integer,Integer>();
    	   TreeMap<Integer,Integer>  Ntemp = new TreeMap<Integer,Integer>();
    	  	  for(int i:input) {
    	  		  //if(Pt.containsKey(i)) {
    	  		  Pttemp.put(i, i);
    	  		  //}
    	  	  }
    	  	  Pt=Pttemp;
    	  	for(int i:input) {
  	  		  //if(Pa.containsKey(i)) {
  	  		  Patemp.put(i, i);
  	  		  //}
  	  	    }
    	  	Pa=Patemp;
    	  	/*
    	  	for(int i:input) {
    	  		  if(Pc.containsKey(i)) {
    	  		  Pctemp.put(i, i);
    	  		  }
    	  	    }
    	  	Pc=Pctemp;
    	  	for(int i:input) {
    	  		  if(Pn.containsKey(i)) {
    	  		  Pntemp.put(i, i);
    	  		  }
    	  	    }
    	  	Pn=Pntemp;*/
    	  	  for(int i:input) {
    	  		  Pbtemp.put(n+i, n+i);
    	  	  }
    	  	Pb=Pbtemp;
    	  	  for(int i:input) {
    	  		  Ptemp.put(i, i);
    	  		  Ptemp.put(n+i, n+i);
    	  	  }
    	  	P=Ptemp;
    	  	  for(int i:input) {
    	  		  Ntemp.put(i, i);
    	  		  Ntemp.put(n+i, n+i);
    	  	  }
    	  	  Ntemp.put(0, 0);
    	  	  Ntemp.put(2*n+1,2*n+1 );
    	  	  N = Ntemp;
    	    }
       public ArrayList<network> clusternw() {
    	   ArrayList<ArrayList<Integer>> cluster = cluster();
    	   ArrayList<network> tempclusternw = new ArrayList<network>();
    	   
    	   //System.out.println("clusterset"+cluster);
    	   for(int i=0;i<cluster.size();i++) {
    		   //System.out.println("cluster "+i+" "+cluster.get(i));
    	   network temp = new network();
    	   temp.Q=this.Q;
    	   temp.n=this.n;
    	   temp.V=this.V;
    	   temp.d=this.d;
           temp.s=this.s;
           temp.t=this.t;
           temp.c=this.c;
           temp.a=this.a;
           temp.b=this.b;
           temp.prob=this.prob;
           temp.Pt = new  TreeMap<Integer,Integer>();
           temp.Pa = new TreeMap<Integer,Integer>();
           temp.Pb = new TreeMap<Integer,Integer>();
           temp.Pc = new TreeMap<Integer,Integer>();
           temp.Pn = new TreeMap<Integer,Integer>();
           temp.P = new TreeMap<Integer,Integer>();
           temp.N = new TreeMap<Integer,Integer>();
    	   for(int j:cluster.get(i)) {
    		   temp.Pt.put(j, j);
    		   temp.Pa.put(j, j);
    		   temp.Pb.put(j+n, j+n);
    		   temp.P.put(j, j);
    		   temp.P.put(j+n, j+n);
    		   temp.N.put(j, j);
    		   temp.N.put(j+n, j+n);
    	   }
    	   temp.N.put(0, 0);
    	   temp.N.put(2*n+1, 2*n+1);
           tempclusternw.add(temp);
    	  }
    	   return tempclusternw;
    	   
       }
       public ArrayList<ArrayList<Integer>> cluster() {
    		  //sorting
    		  ArrayList<Double> durationlist = new ArrayList<Double>();
    		  ArrayList<Integer> requestlist = new ArrayList<Integer>();
    		  for(int i:Pt.keySet()) {
    			  //durationlist.add(t[i][n+i]);
    			  requestlist.add(i);
    		  }
    		  /*
    		  for(int j=1;j<=n;j++) {
    		    for(int i=0;i<n-1;i++) {
    		    	//System.out.println(i);
    			  if(durationlist.get(i)<durationlist.get(i+1)) {
    				  Collections.swap(durationlist, i, i+1);
    				  Collections.swap(requestlist , i, i+1);
    			  }
    		    }
    		  }*/
    		  //System.out.println(durationlist);
    		  //System.out.println(requestlist);
    		  ArrayList<ArrayList<Integer>> cluster = new ArrayList<ArrayList<Integer>>();
    		  cluster.add(new ArrayList<Integer>(Arrays.asList(requestlist.get(0))));
    		  boolean ad; 
    		  int clustersize=1;
    		  for(int i=1;i<requestlist.size();i++) {
    			  //System.out.println(requestlist.get(i));
    			  int tempi = requestlist.get(i);
    			  //System.out.println(i+"--"+tempi);
    			  ad=false;
    			  //System.out.println("cluster size"+clustersize);
    			  for(int k=0;k<clustersize;k++) {
    				  //System.out.println("cluster:"+k);
    				  //System.out.println(cluster.get(k));
    				  for(int j:cluster.get(k) ) {
    					  //int j=cluster.get(k).get(0);
    					  if(isneighbour(tempi,j)==true) {
    						  //cluster.get(k).add(tempi);					  
    						  ad = true;
    						  
    					  }
    					  //System.out.println("ad:"+ad);
    				  }
    				  if(ad ==true) {
    					  cluster.get(k).add(tempi);
    					  break;
    				  }
    				  
    			  }
    			  if(ad==false) {
    				  cluster.add(new ArrayList<Integer>(Arrays.asList(tempi)));
    				  clustersize=clustersize+1;
    				  
    			  }
    			  //System.out.println(cluster);
    		  }
    		  for(int j=0;j<cluster.size();j++) {
    			  System.out.println(" size "+cluster.get(j).size()+"  "+cluster.get(j));
    		  }
    		  return cluster;
    	  }
    	  public boolean isneighbour(Integer i, Integer j) {
    		  int temp=0;
    		  double alpha=4.0;
    		  double beta=90;
    		  /*
    		  boolean temp1 =false;boolean temp2=false;boolean temp3=false;
    		  if(a[i]<=a[j] && a[j]<=b[n+i]) {
    			  temp1=true;
    		  }
    		  if(a[i]<=b[n+j] && b[n+j]<=b[n+i]) {
    			  temp2=true;
    		  }
    		  if(a[j]<=a[i] && a[i]<=b[n+i] && b[n+i] <= b[n+j]) {
    			  temp3=true;
    		  }
    		  if(temp1 || temp2 || temp3 == true) {
    			  temp=temp+1;
    		  }else {
    			  //System.out.println("violate condition 1");
    		  }
    		  */
    		  
    		  boolean temp4 = false; boolean temp5 = false;

    		  if(t[i][j]+t[j][n+i]<=alpha*t[i][n+i]) {
    			  temp4=true;
    		  }

    		  if(t[i][j]+t[i][n+j]<=alpha*t[j][n+j]) {
    			  temp5=true;
    		  }
    		  if(temp4||temp5==true) {
    			  temp=temp+1;
    		  }else {
    			  //System.out.println("violate condition 2");
    		  }
    		  /*
    		  if(t[i][j]<3) {
    			  temp=1;
    		  }
    		  /*
    		  double theta1 = getangle((y[n+i]-y[i]),(x[n+i]-x[i]));
    		  double theta2 = getangle((y[n+j]-y[j]),(x[n+j]-x[j]));

    		  double angle = Math.abs(theta1-theta2);
    		  //System.out.println("degree"+degs);
    		  if(angle<=beta) {
    			  temp=temp+1;
    		  }else {
    			  //System.out.println("violate condition 3");
    		  }
    		  */
    		  if(temp==1) {
    			  return true;
    		  }else {
    			  return false;
    		  }
    	  }
    	  double getangle(double a, double b) {
    		  if(a>0 && b>0) {
    			  return Math.toDegrees(Math.atan(a/b));
    		  }else if(a<0 && b>0) {
    			  return Math.toDegrees(Math.atan(a/b))+180;
    		  }else if(a<0 && b<0) {
    			  return Math.toDegrees(Math.atan(a/b))+180;
    		  }else if(a>0 && b<0) {
    			  return Math.toDegrees(Math.atan(a/b))+360;
    		  }else {
    			  return 0;
    		  }
    	  }
    void readexcel(String input,int capacity, int busnumber) {
    		  try {
    			  Q=capacity;
    			  V=busnumber;
    			FileInputStream file = new FileInputStream(new File(input));
    			Workbook workbook = new XSSFWorkbook(file);
    			Sheet firstsheet = workbook.getSheetAt(0);
    			Iterator<Row> iterator = firstsheet.iterator();
    			String cellvalue ="";
    			ArrayList<String> time = new ArrayList<String>();
    			int n=0;
    			
    			while(iterator.hasNext()){
    				Row nextRow = iterator.next();
    			    Iterator<Cell> cellIterator	= nextRow.cellIterator();		    
    			    while(cellIterator.hasNext()) {
    			    	Cell cell = cellIterator.next();
    			    	
    			    	switch(cell.getCellType()) {
    			    	case Cell.CELL_TYPE_NUMERIC:		    	
    			    			cellvalue =new DataFormatter().formatCellValue(cell);
    			    			time.add(cellvalue);
    			    			n=n+1;
    			    	//System.out.println("-");		    		
    			    		break;
    			    	case Cell.CELL_TYPE_STRING:
    			    		time.add(cell.getStringCellValue());
    			    		
    			    	}
    			    	
    			    }
    			    //System.out.println();
    			}
    			workbook.close();
    			file.close();
    			System.out.println(time);
    			//System.out.println(time.size());
    			System.out.println(time.size());
    			n=time.size()-1;
    			double[] hour = new double[time.size()];
    			double[] min = new double[time.size()];
    			for(int i=1;i<time.size();i++) {
    			    String[] temp = time.get(i).split(":");
    			    //System.out.println(time.get(i));
    			    hour[i]=Double.parseDouble(temp[0]);
    			    min[i]=Double.parseDouble(temp[1]);
    			}
    	        this.n=n;
    	        //System.out.println("============this n============:"+n);
    	        this.a=new double[2*n+2];
    	        this.b=new double[2*n+2];
    	        this.Pt = new TreeMap<Integer,Integer>();
    	        this.Pa = new TreeMap<Integer,Integer>();
    	        this.Pb = new TreeMap<Integer,Integer>();
    	        this.N = new TreeMap<Integer,Integer>();
    	        this.P = new TreeMap<Integer,Integer>();
    	        this.t= new double[2*n+2][2*n+2];
    	        this.s = new double[2*n+2];
    	        this.d = new double[2*n+2];
    	        this.c = new double[2*n+2][2*n+2];
    	        this.x = new double[2*n+2];
    	        this.y = new double[2*n+2];
    	        this.prob = new double[2*n+2];
    	         max =100;//define the max distance in network
    	         
    	   		   for(int i=1;i<=2*n;i++){
    				x[i]=(Math.random()*(max)+1);
    			   }
    			   for(int i=1;i<2*n;i++){
    			 	y[i]=(Math.random()*(max)+1);
    			   }
    			   x[0]=max/2;
    			   y[0]=max/2;
    			   x[2*n+1]=x[0];
    			   y[2*n+1]=y[0];
//**************************************   cluster to 5 clusters *****************************	        
    	         c5();//cluster 
//**************************************   cluster to 5 clusters *****************************	
    	        for(int i=0;i<=2*n+1;i++) {
    	        	for(int j=0;j<=2*n+1;j++) {
    	        		if(i != j) {
    	        		c[i][j]=Math.sqrt((x[i]-x[j])*(x[i]-x[j])+(y[i]-y[j])*(y[i]-y[j]));//time vary from a : b
    	        		t[i][j]=c[i][j]*2;
    	        		}
    	        	}
    	        }    
    	        for(int i=1;i<=n;i++) {
    	        	prob[i]=Math.random();
    	        	prob[i+n]=prob[i];
    	        }
    	        prob[0]=1;
    	        prob[2*n+1]=1;
    	        for(int i=1;i<=n;i++) {
    	        	a[i]=hour[i]*60+min[i]-10;
    	        	//System.out.println(a[i]);
    	        	b[i]=a[i]+10;
    	        }
    	        for(int i=n+1;i<=2*n;i++) {
    	        	a[i]=a[i-n]+t[i][i-n];
    	        	b[i]=b[i-n]+2*t[i][i-n];       	
    	        }
    	       

    	        a[0]=a[1]-t[0][1];
    	        b[0]=b[2*n]+t[0][1];
    	        a[2*n+1]=a[1];
    	        int tt=0;
    	        double td=0.0;
    	        for(int i=0;i<=2*n;i++) {
    	        	if(b[i]>td) {
    	        		tt=i;
    	        	}
    	        	td=b[i];
    	        }
    	        System.out.println(tt+"  "+b[tt]);
    	        b[2*n+1]=b[tt]+2*t[tt][2*n+1];   
    	        for(int i=1;i<=n;i++) {
    	        	s[i]=2;
    	        	d[i]=1;
    	        	Pa.put(i, i);
    	        	Pt.put(i, i);
    	        	P.put(i,i);
    	        }
    	        for(int i=n+1;i<=2*n;i++) {
    	        	s[i]=0;
    	        	d[i]=-1;
    	        	Pb.put(i, i);
    	        	P.put(i, i);
    	        }
    	        s[0]=0;
    	        d[0]=0;
    	        s[2*n+1]=0;
    	        d[2*n+1]=0;
    	        for(int i=0;i<=2*n+1;i++) {
    	        	N.put(i, i);
    	        }        
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		  
    	  }
    //generate location that divide network into p separate cluster
    public void c5() { //cluster 5 clusters location
    	double b=10;
    	
    	for(int i=0;i<109;i++){
    		int a1=5*i;
			x[a1]=(Math.random()*(b)+10);
			y[a1]=(Math.random()*(b)+10);
			x[a1+n]=(Math.random()*(b)+10);
			y[a1+n]=(Math.random()*(b)+10);
			
			x[a1+1]=(Math.random()*(b)+20);
			y[a1+1]=(Math.random()*(b)+80);
			x[a1+n+1]=(Math.random()*(b)+20);
			y[a1+n+1]=(Math.random()*(b)+80);
			
			x[a1+2]=(Math.random()*(b)+40);
			y[a1+2]=(Math.random()*(b)+50);
			x[a1+n+2]=(Math.random()*(b)+40);
			y[a1+n+2]=(Math.random()*(b)+50);
			
			x[a1+3]=(Math.random()*(b)+70);
			y[a1+3]=(Math.random()*(b)+20);
			x[a1+n+3]=(Math.random()*(b)+70);
			y[a1+n+3]=(Math.random()*(b)+20);
			
			x[a1+4]=(Math.random()*(b)+90);
			y[a1+4]=(Math.random()*(b)+70);
			x[a1+n+4]=(Math.random()*(b)+90);
			y[a1+n+4]=(Math.random()*(b)+70);
		   }
          x[0]=50;
          y[0]=50;
          x[2*n+1]=50;
          y[2*n+1]=50;
    	  x[545]=(Math.random()*(b)+10);
    	  y[545]=(Math.random()*(b)+10);
    	  x[1090]=(Math.random()*(b)+10);
    	  y[1090]=(Math.random()*(b)+10);
		   
    }
    public void divide(int p) {
    	int time=p;
    	int q=n/time;
    	int dist = 100/time;
    	max=100;
    	System.out.println(q);
    	for(int i=1;i<=time;i++) {
    		System.out.println("===============================cluster "+i+"============================");    
    		double xcore = (i-1)*dist;    		
        	double ycore = (i-1)*dist;        	 
        	System.out.println("xcore "+xcore);
        	System.out.println("ycore "+ycore);
            for(int j=(i-1)*q+1;j<=i*q;j++) {
            	x[j]=Math.min(max, xcore+Math.random()*4) ;
            	x[j+n]=Math.min(max, xcore+Math.random()*4) ;
            	y[j]=Math.min(max, ycore+Math.random()*4);
            	y[j+n]=Math.min(max, ycore+Math.random()*4);
            	System.out.println(j+"  "+x[j]+"  "+y[j]);
            	//System.out.println(x[j+n]+"  "+y[j+n]);
            	
            }
    	}
    	System.out.println("===============================cluster "+(time+1)+"============================");
    	double xcore = time*dist;
    	double ycore = time*dist;
    	for(int i=(time*q)+1;i<=n;i++) {   		
    		x[i]=Math.min(max, xcore+Math.random()*4) ;
        	x[i+n]=Math.min(max, xcore+Math.random()*4) ;
        	y[i]=Math.min(max, ycore+Math.random()*4);
        	y[i+n]=Math.min(max, ycore+Math.random()*4);
        	System.out.println(i+"  "+x[i]+"  "+y[i]);
        	//System.out.println(x[i+n]+"  "+y[i+n]);
    	}
    }
    public boolean generatesnr() {//generate scenario based on probability, if all no show return false, otherwise return true
    	
    	//System.out.println("gePt:"+Pt);
    	temphash = new TreeMap<Integer,Integer>();
    	int dec=0;//decision v for all no show 
    	//System.out.println(Pt);
    	for(int i:Pt.keySet()) {
    		double ptemp = Math.random();
    		//System.out.println(i+" "+prob[i]);
    		if (ptemp > prob[i]) {
    			temphash.put(i, 0);
    		}else {
    			temphash.put(i, 1);
    			dec=dec+1;
    		}
    	}
    	numshow=dec;
    	for(int i:temphash.keySet()) {
    		int index = temphash.get(i);
    		if(index==0) {    			
    			delete(i);
    		}
    	}
    	//tempnw.delete(1);
    	//System.out.println("temphash:"+temphash);
    	//System.out.println("Pt:"+Pt);
    	//System.out.println("n"+Pt.size());
    	if(dec==0) {
    		System.out.println("no request showup");
    		return false;
    	}else {
    		return true;
    	}
    
    }
    
    public ArrayList<Integer> generatenoshow() {
    	//System.out.println(Pt);
    	ArrayList<Integer> noshowlist = new ArrayList<Integer>();
    	for(int i:Pt.keySet()) {
    		double prob = Math.random();
    		//System.out.println(i);
    		//System.out.print(prob);
    		if(prob < probn) {
    			//System.out.println("add " +i);
    			noshowlist.add(i);
    		}
    	}
    	return noshowlist;
    }
    
    public void restore() {//restore nw after generate random scenario
    	for(int i:temphash.keySet()) {
    		int index = temphash.get(i);
    		if(index==0) {
    		     this.add(i);
    			
    		}
    	}
    	//System.out.println("afterstorePt:"+Pt);
    }
    
    public void initiate() {
    	
    	Pt = new TreeMap<Integer,Integer>();
    	Pt.put(1, 1);
    	Pa = new TreeMap<Integer,Integer>();
    	Pa.put(1, 1);
    	Pb = new TreeMap<Integer,Integer>();
    	Pb.put(1+n, 1+n);
    	N = new TreeMap<Integer,Integer>();
    	N.put(1, 1);
    	N.put(1+n, 1+n);
    	N.put(0, 0);
    	N.put(2*n+1, 2*n+1);
    	P = new TreeMap<Integer,Integer>();
    	P.put(1, 1);
    	P.put(1+n, 1+n);
    	//System.out.println("======n======:"+n);
    	//tempnw.n=Pt.size();
    	//System.out.println("b2n+1 pre"+b[2*n+1]);
    	
    }
    
    public void add(int q) {
    	//System.out.println("addq:"+q);
    	//System.out.println("n:"+n);
    	//System.out.println("q+n:"+(q+n));
    	Pt.put(q, q);
    	Pa.put(q, q);
    	Pb.put(q+n, q+n);
    	N.put(q, q);
    	N.put(q+n, q+n);
    	P.put(q, q);
    	P.put(q+n, q+n);
    	//System.out.println("======n======:"+n);
    	//tempnw.n=Pt.size();    	
    }
    
    public void delete(int q) {
    	Pt.remove(q);
    	Pa.remove(q);
    	Pb.remove(q+n);
    	N.remove(q);
    	N.remove(q+n);
    	P.remove(q);
    	P.remove(q+n);
    }
    
    public void bookingmatrix() {
    	System.out.println(temphash);
    }

}
