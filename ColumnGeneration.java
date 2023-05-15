
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import ilog.concert.IloColumn;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;
import ilog.cplex.IloCplexModeler.Exception;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ColumnGeneration {
  public double[] c;//cost for route
  public double[][] cn;//cost for arc not changed
  private double new_c;//cost for new column
  private ArrayList<Double> newcset;//add multiple column
  public double[][] ar; // if request i cover by route r;
  public int V; // number of bus;
  //private IloCplex cplex;
  private  IloLinearNumExpr obj;
  private IloLinearNumExpr expr;
  private IloNumVar[] x;
  private IloNumVar[] Y;
  private IloNumVar[] Tb;
  private IloNumVar[] Tl;
  private IloNumVar[] Tr;
 // private ArrayList<Integer> rc;//route collection
  //private int rn;////get the number of route
  private int n;//number of request
  private ArrayList<Integer> r;// number of current column
  public HashMap<Integer, IloRange> cons;
  public double[][] ca;//cost for changed arc
  public IloIntVar[][] xs;//subproblem route choose for single vehicle
  private TreeMap<Integer,Integer> Pa;
  private TreeMap<Integer,Integer> Pb;
  public TreeMap<Integer,Integer> Pn;//no-show request
  public TreeMap<Integer,Integer> Pc;//late cancel request
  public TreeMap<Integer,Integer> Pt;//union of Pa, Pc and Pn
  private TreeMap<Integer,Integer> P;
  private TreeMap<Integer,Integer> N;
  private double[] w;
  private double M;
  private double[] d;//load
  private double[][] t;//travel time
  private double[] a;//arrival time window
  private double[] b ;//leave time window
  private double Q;
  private double[] s;//service duration
  private HashMap<Integer,Double> pi;// dual value
  private HashMap<Integer,Double> del;//weight for each node
  private HashMap<Integer,Double> new_column;
  private ArrayList<HashMap<Integer,Double>> newcolumnset;//add multiple column
  private double margi_cost;
  private IloCplex cplex_master;
  private IloCplex cplex_sub;
  private IloCplex cplex_final;
  //private IloRange[] testconstraints;
  public  masterproblem mas_prob;
  public subproblem sub_prob;
  private NewModel model1;
  private TreeMap<Integer,ArrayList<Integer>> S;//state
  private HashMap<Integer, Double> T;//time
  private HashMap<Integer, Double> Z;//cost
  private HashMap<Integer, Double> Yk;//load
  private HashMap<Integer, ArrayList<Integer>> R;//
  //private ArrayList<Integer> LabelList;
  private HashMap<Integer,Integer> e;//end of state
  private HashMap<Integer,ArrayList<Integer>> adroute;//list of admissibe route
  private ArrayList<HashMap<Integer,Double>> finalcolumn;//matrix of final column
  private ArrayList<Double> finalcost;//list of cost of final matrix
  private ArrayList<ArrayList<Integer>> finalroute;
  private ArrayList<Integer> numberofroute;
  private double[] A;//earlist time visit node j
  private int elimination1;
  private int elimination2;
  private int elimination3;
  private int elimination4;
  private double[] xcoord;
  private double[] ycoord;
  public double scenariocost;//obj for the final model
  private network nw;
  private int iteration;

  public ColumnGeneration(network nw) {
	  finalcolumn = new ArrayList<HashMap<Integer,Double>>();
	  finalcost = new ArrayList<Double>();
	  finalroute = new ArrayList<ArrayList<Integer>>();
	  numberofroute = new ArrayList<Integer>();
     //rn=rc.size();
     //a = new double[n][rn];
	  
     M = 10000;     
     r = new ArrayList<Integer>();
     //pi = new HashMap<Integer,Double>();
     del = new HashMap<Integer,Double>();
     cons = new HashMap<Integer, IloRange>();
     this.nw=nw;
     //network nw = new network();
     iteration=0;
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
     this.w = nw.w;
     this.ca = new double[nw.c.length][nw.c.length];    
     xcoord = nw.x;
     ycoord = nw.y;
     //System.out.println("Ptkeyset0:"+Pt.keySet());
     //System.out.println("b(2n+1)"+b[2*n+1]);
     //System.out.println("Pt"+Pt);
     //System.out.println("Pb:"+Pb);
    // System.out.println("length=="+nw.c.length);
     //System.out.println("Pt"+Pt);
     for(int i=0;i<nw.c.length;i++) {
    	 for(int j=0;j<nw.c.length;j++) {
    		 ca[i][j]=nw.c[i][j];
    	 }
     }
     this.cn = new double[nw.c.length][nw.c.length];     
     for(int i=0;i<nw.c.length;i++) {
    	 for(int j=0;j<nw.c.length;j++) {
    		 cn[i][j]=nw.c[i][j];
    	 }
     }
    
  }
  public class masterproblem {
	  private IloObjective objective;
	 
      public masterproblem() {
      }
      public void creatmodel() {
	  try {
		  cplex_master = new IloCplex();
		  obj = cplex_master.linearNumExpr();
		  x = new IloNumVar[n+1];
		  for(int i:Pt.keySet()) {
			  x[i-1]=cplex_master.numVar(0, 1);
			  x[i-1].setName("x."+i);
			  }
// objective function		
		  
		  for(int i:Pt.keySet()) {
			  obj.addTerm(c[i-1], x[i-1]);
		  }
		  objective = cplex_master.addMinimize(obj);
//constraint 1		  
		  for(int i:Pt.keySet()) {			 
			  expr=cplex_master.linearNumExpr();
			  int j=i-1;
				  expr.addTerm(ar[i][j], x[j]);
			  
			 cons.put(i, cplex_master.addEq(expr, 1)) ;
			  //testconstraints[i] = cplex_master.addEq(expr, 1);
		  }
//constraint 2		  
		  expr=cplex_master.linearNumExpr();
		  for(int i:Pt.keySet()) {
			 expr.addTerm(1.0, x[i-1]);			  
		  }
		  //testconstraints[0] = cplex_master.addLe(expr, V);
		  cons.put(0, cplex_master.addLe(expr, Pt.size()));		  
		  //testconstraints[0]=cons.get(0);		
		  //cplex_master.exportModel("Initial-masterprob.lp");
	  }catch (IloException e) {
			System.err.println("Concert exception caught: " + e);		
		}
    }
      //initialization with identity matrix
      
  public void initialization() {

	  
    	  for(int i:Pt.keySet()) {
    		  r.add(i);
    	  }
    	  //System.out.println("size:"+r.size());
    	  ar= new double [n+1][n+1];// n is changed by the number of variables
    	  c = new double[n+1];
    	  //System.out.println("n:"+n);
    	  //System.out.println("Ptkeyset0:"+Pt.keySet());
    	  for(int i:Pt.keySet()) {
    		  //System.out.println(i);
    		  int j=i-1;
    		     ar[i][j]=1;		  
    		     c[j]=M; 
    			  
    		  
    	  }    	  
    	  
      }
      //initialization with feasible solution
      public void initializationF() {    	
    	  model1 = new NewModel();
    	  model1.creatModel(nw);
    	  model1.getfeassol();
          model1.solves();
          //model1.output();
          ar=new double[n+1][V+1];
          c = new double[V+1];
          double[][] temp1 = model1.outputmatrix();
          
          double[] temp2 = model1.outputcost();
          for(int k=0;k<V;k++) {
        	  //for each vehicle k generate column
        	  new_column = new HashMap<Integer,Double>();
        	  ArrayList<Integer> routetemp = new ArrayList<Integer>();
              for(int i:Pt.keySet()) {
         		 new_column.put(i, 0.0);
         	 } 
               for(int i:Pt.keySet()) {        	  
        		  ar[i][k]=temp1[i][k];
        		  if(temp1[i][k]==1) {
        			  new_column.put(i, 1.0);
        			  routetemp.add(i);
        		  }
        	  }
               finalcolumn.add(new_column);
               finalroute.add(routetemp);
          } 
          for(int k=0;k<V;k++) {
        		for(int i:Pa.keySet()) {
              		//System.out.println("k."+i+"."+k+"==="+ar[i][k]);
              	}
              	}
          for(int k=0;k<V;k++) {
        	  c[k] = temp2[k];
        	  finalcost.add(c[k]);
          }
      }
      public void addColumn() {
    	  try {
    		  //System.out.println("new_column"+new_column);
       for(int k=0;k<newcolumnset.size();k++) {
    	  IloColumn col1 = cplex_master.column(objective,newcset.get(k));
    	  //IloColumn col2 = cplex_master.column(cons.get(0),1);
    	  IloColumn col2 = cplex_master.column(cons.get(0),1);
          //test for newcolumn
    	  /*new_column = new HashMap<Integer,Double>();
    	  new_column.put(1, 0.0);
    	  new_column.put(2, 1.0);
    	  new_column.put(3, 0.0);
    	  new_column.put(4, 1.0);*/
    	  //System.out.println("cons size="+cons.size());
    	  //System.out.println("new column size="+new_column.size());
    	  for(int i:Pt.keySet()) {  
    		  col2 = col2.and(cplex_master.column(cons.get(i),newcolumnset.get(k).get(i))); 
    	  }
    	  cplex_master.numVar(col1.and(col2), 0,1);
    	  //cplex_master.exportModel("masterprob-multi_"+iteration+".lp");
      }
    	  }catch(IloException e) {    		  
    	  }
      }
      //stop here
      public void solve() {           
    	  try {        	
    		  //cplex_master.exportModel("masterproblemm.lp");
    		  //cplex_master.setParam(IloCplex.Param.Preprocessing.Presolve, false);
    		  //System.out.println(cplex_master.getAlgorithm());
    		    //cplex_master.setParam(IloCplex.Param.MIP.Display, 0);
    		   cplex_master.setOut(null);
    		   cplex_master.solve();
    		   //System.out.println("x"+(iteration+6+" "+cplex_master.getValue(x[iteration+3])));
		    	//System.out.println("Solved");
		    	//System.out.println("list objvalue");
		    	//System.out.println("obj value=="+cplex_master.getObjValue());
		    	//double a  = cplex_master.getDual(cons.get(4));
		    	pi = new HashMap<Integer,Double>();
		    	for(int i:Pt.keySet()) {
		    		pi.put(i, cplex_master.getDual(cons.get(i)));
		    		//System.out.println("request."+i+"=="+pi.get(i));
		    	}
		    	pi.put(0, cplex_master.getDual(cons.get(0)));
		    	/*
		    	for(int i:pi.keySet()) {
		    		System.out.println("pi."+i+"=="+pi.get(i));
		    	}*/
		    	//cplex_master.end();
        }catch(IloException e) {        	
        	System.out.println("Exception catched"+e);
		 }   
      }     
    }  
  
  public void changeweight() {
	  //System.out.println("start changeweight");
	  del = new HashMap<Integer,Double>();
	  //System.out.println("Pt:"+Pt);
	  //System.out.println("pi:"+pi);
	  for(int i:Pt.keySet()) {
		  del.put(i, pi.get(i));
	  }
	  for(int i:Pb.keySet()) {
		  del.put(i, 0.0);
	  }
	  //System.out.println("Pb"+Pb);
	  //System.out.println("pi.0=="+pi.get(1));
	  del.put(0, pi.get(0));
	  del.put(2*n+1, 0.0);
	  //System.out.println("delta:"+del);
	  //System.out.println("cn.0.1=="+cn[0][1]);
	  //System.out.println("del.0=="+del.get(0));
	  for(int i:N.keySet()) {
		  for(int j:N.keySet()) {
			  if(i !=j) {
			
			  ca[i][j]=cn[i][j]-del.get(i);
			  //System.out.println("c."+i+"."+j+"=="+ca[i][j]);
		  }
		  }
	  }
  }
  public class subproblem {
	  
	 public subproblem() {		 
	 }
	 void creatmodel() {
	  try {
	  //new_column = new HashMap<Integer,Double>();
	  cplex_sub = new IloCplex();
	  obj=cplex_sub.linearNumExpr();
	  xs = new IloIntVar[2*n+2][2*n+2];
	  Y = new IloNumVar[2*n+2];
	  Tb = new IloNumVar[2*n+2];
	  Tl = new IloNumVar[1];
	  Tr = new IloNumVar[1];
	  
	  for(int i:N.keySet()) {
		  for(int j:N.keySet()) {
			  xs[i][j]=cplex_sub.boolVar();
			  xs[i][j].setName("xs."+i+"."+j);
			  }
	  }
	  for(int i:P.keySet()) {
			Tb[i]=cplex_sub.numVar(0, Double.MAX_VALUE);
			Tb[i].setName("Tb."+i);					
		}
	  Tl[0] = cplex_sub.numVar(0, Double.MAX_VALUE);
	  Tr[0] = cplex_sub.numVar(0, Double.MAX_VALUE);
     for(int i:N.keySet()) {
    	 Y[i]=cplex_sub.numVar(0, Double.MAX_VALUE);
    	 Y[i].setName("Y."+i);
     } 
       
	  for(int i:N.keySet()) {
		  for(int j:N.keySet()) {
			 obj.addTerm(ca[i][j], xs[i][j]);
	   	  }
	  }    
	  cplex_sub.addMinimize(obj);
	       
//7		    
		   for(int i:P.keySet()) {
		      expr=cplex_sub.linearNumExpr();
		       for(int j:N.keySet()) {
			   expr.addTerm(1.0, xs[i][j]);
		       }		   		   
		       for(int j:N.keySet()) {
			   expr.addTerm(-1.0,xs[j][i] );
		       }
		   cplex_sub.addEq(expr, 0);
		   }
//8		   
		   expr=cplex_sub.linearNumExpr();
		   for(int j:P.keySet()){
			   expr.addTerm(1.0, xs[0][j]);
		   }
		   cplex_sub.addEq(expr, 1);
//9
		   expr=cplex_sub.linearNumExpr();
		   for(int i:P.keySet()) {
			   expr.addTerm(1.0, xs[i][2*n+1]);
		   }
		   cplex_sub.addEq(expr, 1);
//10
		   for(int i: Pa.keySet()) {			
				   expr=cplex_sub.linearNumExpr();
				   for(int j:N.keySet()) {
					   expr.addTerm(1.0, xs[i][j]);
				   }
				   for(int j:N.keySet()) {
					   expr.addTerm(-1.0, xs[j][n+i]);
				   }
				   cplex_sub.addEq(expr, 0);			   
		   }
//11
		  for(int i:Pa.keySet()) {
			  	expr=cplex_sub.linearNumExpr();
			  	expr.addTerm(1.0, Tb[i]);
			  	expr.addTerm(-1.0, Tb[i+n]);
			  	cplex_sub.addLe(expr, -t[i][i+n]-s[i]);			  	
		  }
//12
		  for(int i:P.keySet()) {
			  for(int j:P.keySet()) {
				  expr = cplex_sub.linearNumExpr();
				  expr.addTerm(1.0, Tb[i]);
				  expr.addTerm(-1.0, Tb[j]);
				  expr.addTerm(M, xs[i][j]);
				  cplex_sub.addLe(expr, M-s[i]-t[i][j]);
			  }
		  }	  
//13
		  for(int j:Pa.keySet()) {
			  expr=cplex_sub.linearNumExpr();
			  expr.addTerm(1.0, Tl[0]);
			  expr.addTerm(M, xs[0][j]);
			  expr.addTerm(-1.0,Tb[j]);
			  cplex_sub.addLe(expr, M-t[0][j]);
		  }
//14
		  for(int i:Pb.keySet()) {
			  expr = cplex_sub.linearNumExpr();
			  expr.addTerm(M, xs[i][2*n+1]);
			  expr.addTerm(1.0, Tb[i]);
			  expr.addTerm(-1.0, Tr[0]);
			  cplex_sub.addLe(expr, M-s[i]-t[i][2*n+1]);
		  }
//15 
		  for(int i:P.keySet()) {
			   expr=cplex_sub.linearNumExpr();
			   expr.addTerm(1.0, Tb[i]);
			   cplex_sub.addGe(expr, a[i]);
			   cplex_sub.addLe(expr, b[i]);
		   }
//16
		  expr = cplex_sub.linearNumExpr();
		   expr.addTerm(1.0, Tl[0]);
		   cplex_sub.addGe(expr, a[0]);
		   cplex_sub.addLe(expr, b[0]);
//17
		  
			   expr = cplex_sub.linearNumExpr();
			   expr.addTerm(1.0, Tr[0]);
			   cplex_sub.addGe(expr, a[2*n+1]);
			   cplex_sub.addLe(expr, b[2*n+1]);
		   
//18

			   for(int i:P.keySet()) {
				   for(int j:Pa.keySet()) {
						   expr=cplex_sub.linearNumExpr();
						   expr.addTerm(M, xs[i][j]);
						   expr.addTerm(1.0, Y[j]);
						   expr.addTerm(-1.0, Y[i]);
						   cplex_sub.addLe(expr, M+d[j]);					   
				   }
			   }
			   
			   for(int i:P.keySet()) {
				   for(int j:Pa.keySet()) {
						   expr=cplex_sub.linearNumExpr();
						   expr.addTerm(M, xs[i][j]);
						   expr.addTerm(-1.0, Y[j]);
						   expr.addTerm(1.0, Y[i]);
						   cplex_sub.addLe(expr, M-d[j]);					   
				   }
			   }
//19
			   for(int i:P.keySet()) {
				   for(int j:Pb.keySet()) {
						   expr=cplex_sub.linearNumExpr();
						   expr.addTerm(M, xs[i][j]);
						   expr.addTerm(1.0, Y[j]);
						   expr.addTerm(-1.0, Y[i]);
						   cplex_sub.addLe(expr, M-d[j-n]);								   
				   }
			   }
			   
			   for(int i:P.keySet()) {
				   for(int j:Pb.keySet()) {
						   expr=cplex_sub.linearNumExpr();
						   expr.addTerm(M, xs[i][j]);
						   expr.addTerm(-1.0, Y[j]);
						   expr.addTerm(1.0, Y[i]);
						   cplex_sub.addLe(expr, M+d[j-n]);	
				   }
			   }
//20
			   for(int j:Pa.keySet()) {
					   expr = cplex_sub.linearNumExpr();
					   expr.addTerm(M, xs[0][j]);
					   expr.addTerm(1.0, Y[j]);
					   expr.addTerm(-1.0, Y[0]);
					   cplex_sub.addLe(expr, M+d[j]);				   
			   }
			   
			   for(int j:Pa.keySet()) {
					   expr = cplex_sub.linearNumExpr();
					   expr.addTerm(M, xs[0][j]);
					   expr.addTerm(-1.0, Y[j]);
					   expr.addTerm(1.0, Y[0]);
					   cplex_sub.addLe(expr, M-d[j]);				   
			   }
//21
			   expr=cplex_sub.linearNumExpr();
			   expr.addTerm(1.0, Y[0]);;
			   cplex_sub.addEq(expr,0);
			    
			   for(int i:Pa.keySet()) {
				   expr = cplex_sub.linearNumExpr();
				   expr.addTerm(1.0, Y[i]);
				   cplex_sub.addLe(expr, d[i]);
				   cplex_sub.addGe(expr, Q);
			   }
//(22)added constraints	   			   			   
				   expr=cplex_sub.linearNumExpr();
				   for(int i:P.keySet()) {
					   expr.addTerm(1.0, xs[i][0]);
				   }
				   cplex_sub.addEq(expr, 0);
				   for(int j:P.keySet()) {
					   expr.addTerm(1.0, xs[2*n+1][j]);
				   }
				   cplex_sub.addEq(expr, 0);			   
			   
			   cplex_sub.exportModel("subproblem.lp");
	  }catch (IloException e) {		  
			System.err.println("Concert exception caught: " + e);			
		}	  
   }	 
	 public void solve() {
		try {
			//System.out.println("run sub_problem");
			 if(cplex_sub.solve()==true) {
				 System.out.println("problem solved with obj ="+cplex_sub.getObjValue());
				   margi_cost = cplex_sub.getObjValue();
				   for(int i:N.keySet()) {
						 for(int j:N.keySet()) {
							 if(cplex_sub.getValue(xs[i][j])>0.99 && cplex_sub.getValue(xs[i][j])<1.01) {						 			
								 System.out.println("x."+i+"."+j+"=="+cplex_sub.getValue(xs[i][j]));
							 }
						 }
					 }
			   }else {
				   System.out.println("sub problem not solved");
			   }
		}catch(IloException e) {
			System.out.print("subproblem solve exception catched"+e);
		}
	 }
	 public void catchpath() throws Exception, IloException {
		 new_column = new HashMap<Integer,Double>();
		 new_c=0;
		 /*
		 for(int i:N.keySet()) {
			 for(int j:N.keySet()) {
				 if(cplex_sub.getValue(xs[i][j])>0.99 && cplex_sub.getValue(xs[i][j])<1.01) {	
					// System.out.println("x."+i+"."+j+"="+cplex_sub.getValue(xs[i][j]));
				 }
			 }
		 }*/
		 for(int i:Pa.keySet()) {
			 new_column.put(i, 0.0);
		 } 
		 
		 for(int i:Pa.keySet()) {
			 for(int j:N.keySet()) {
				 if(cplex_sub.getValue(xs[i][j])>0.99 && cplex_sub.getValue(xs[i][j])<1.01) {	
					 //System.out.println("x."+i+"."+j+"="+cplex_sub.getValue(xs[i][j]));
					 new_column.put(i, 1.0);
					
				 }
			 }
		 }
		 /*
		 for(int i:new_column.keySet()) {
			 System.out.println( new_column.get(i));
		 }*/
		 
		 for(int i:N.keySet()) {
			 for(int j:N.keySet()) {
				 if(cplex_sub.getValue(xs[i][j])>0.99 && cplex_sub.getValue(xs[i][j])<1.01) {						 
					 new_c = new_c + cn[i][j];					
					// System.out.println("arc."+i+"."+j+"=="+cn[i][j]);
				 }
			 }
		 }
	
		 
	 }
  }
  
  public void shortestpath() {
	  //setup
	  elimination1=0;
	  elimination2=0;
	  elimination3=0;
	  elimination4=0;
	  S = new TreeMap<Integer,ArrayList<Integer>>() ;
	  T = new HashMap<Integer, Double>() ;
	  Z = new  HashMap<Integer, Double>();
	  Yk = new HashMap<Integer, Double>();
	  R = new HashMap<Integer, ArrayList<Integer>>();
	  e = new HashMap<Integer,Integer>();//last node for label
	  adroute = new HashMap<Integer,ArrayList<Integer>>();
	  
	  //LabelList = new ArrayList<Integer>();
	 // LabelList.add(1);
	  S.put(1, new ArrayList<Integer>(Arrays.asList(0)));
	  T.put(1, 0.0);
	  Z.put(1, 0.0);
	  Yk.put(1, 0.0);
	  R.put(1, new ArrayList<Integer>());
	
	  e.put(1, 0);
	  HashMap<Integer,ArrayList<Integer>> H = new HashMap<Integer,ArrayList<Integer>>(); 
	  int indexa=0;
	  int indexb=0;
	  int label=1;
	  /*
	  for(int i=0;i<a.length;i++) {
		  System.out.println("a."+i+":"+a[i]);
	  }*/
	 for(int k=1;k<=N.size();k++) {
		//System.out.println("=========iteration"+k+"=============");
		 //ArrayList<Integer> templabellist = new ArrayList<Integer>(); 
		 TreeMap<Integer,ArrayList<Integer>> tempS = new TreeMap<Integer,ArrayList<Integer>>();
		 HashMap<Integer,ArrayList<Integer>> tempRlist = new HashMap<Integer,ArrayList<Integer>>();
		 //System.out.println("labellist"+LabelList);
		 indexa=label+1;
		 //System.out.println("S:"+S);
		 //System.out.println("S keyset:"+S.keySet());
		 for(int i:S.keySet()) {
			 
			//System.out.println("------------"+i+"-----------");
			 //System.out.println(S.get(i));
			 //System.out.println("last node:"+e.get(i));
			 int ltemp=e.get(i);
			 //System.out.println("Nkeyset:"+N.keySet());
			 for(int j:N.keySet()) {
				 //System.out.println("==============="+j+"==============");
				 if(S.get(i).contains(j)==false ) {
				 //System.out.println(a[e.get(i)]+"///"+s[e.get(i)]+t[e.get(i)][j]);
					 //System.out.println("==============="+j+"==============");
				 //System.out.println(Ytemp+"???"+Q);
				 //System.out.println(R);
					 //System.out.println("label"+label);
					 if( Pa.containsKey(j)) {
						 //System.out.println("contains");
					 double Ytemp = 0;
					 double Ttemp = Math.max(a[j], T.get(i)+t[ltemp][j]+s[ltemp]); 
					 
					 //System.out.println(a[j]+"///"+(T.get(i)+s[ltemp]+t[ltemp][j]));
					 //System.out.println(Ttemp +"///"+b[j]);	
				   if( Ttemp<=b[j] && Ttemp<=b[2*n+1]) {
			             //System.out.println("start time  "+T.get(i)+"service and travel time    "+(s[ltemp])+" == "+(T.get(i)+s[ltemp]+t[ltemp][j]));
						 //System.out.println("time window  "+a[j]+ " : "+b[j]);
						 //System.out.println("Ttemp  "+Ttemp );
					   //System.out.println("start time  "+T.get(i)+"service  time   "+s[ltemp]+ "  travel time  "+t[ltemp][j] +"  =  "+(T.get(i)+s[ltemp]+t[ltemp][j]));
					   //System.out.println("time window  "+a[j]+ " : "+b[j]);
					 //System.out.println("S contains status:"+S.get(i).contains(j));					 
						// System.out.println("R:"+R);
						 Ytemp =Yk.get(i)+d[j];
						 //System.out.println(Ytemp+"///"+Q);
						 if(Ytemp <=Q) {
						
						// System.out.println("follow 1st");
					 label++;
					 //System.out.println("LABEL "+label);
					 //templabellist.add(label);
					 ArrayList<Integer> temp = new ArrayList<Integer>();
						 for(int v:S.get(i)) {
							 temp.add(v);
						 }	
					 temp.add(j);//add this node
					 //S.put(label,temp );//and creat new label
					 tempS.put(label, temp );
				
					 ArrayList<Integer> tempR = new ArrayList<Integer>();
					 for(int v:R.get(i)) {
						 tempR.add(v);
					 }
		             tempR.add(j);
		             tempRlist.put(label, tempR);
					 T.put(label, Ttemp);
					 //System.out.println(T);
					 Z.put(label, Z.get(i)+ca[e.get(i)][j]);
					 Yk.put(label, Ytemp);
					 e.put(label, j);	
					 //System.out.println("tempS"+tempS);
						 }
					 }else {
						 //System.out.println("time violation");
						 //System.out.println("start time  "+T.get(i)+"service and travel time    "+(s[ltemp]+t[ltemp][j])+" == "+T.get(i)+s[ltemp]+t[ltemp][j]);
						 //System.out.println("time window  "+a[j]+ " : "+b[j]);
						 //System.out.println("Ttemp  "+Ttemp );
					 }
				   
				 }
					 else if(Pb.containsKey(j) ) {
						 //System.out.println("enter 54");
						 double Ytemp = 0;
						 double Ttemp = Math.max(a[j], T.get(i)+t[ltemp][j]+s[ltemp]); 
						
						 //System.out.println(Ttemp+ "???"+b[j]);
						 //System.out.println(Ttemp+"???"+b[2*n+1]);
						 //System.out.println("2*n:"+2*n);
						 //System.out.println("2*n+1:"+(2*n+1));
						 if( Ttemp<=b[j] && Ttemp<=b[2*n+1] ) {
							 //System.out.println("start time  "+T.get(i)+"service and travel time    "+(s[ltemp]+t[ltemp][j])+" == "+(T.get(i)+s[ltemp]+t[ltemp][j]));
							 //System.out.println("time window  "+a[j]+ " : "+b[j]);
							 //System.out.println("Ttemp  "+Ttemp );
							//System.out.println("start time  "+T.get(i)+"service  time   "+s[ltemp]+ "  travel time  "+t[ltemp][j] +"  =  "+(T.get(i)+s[ltemp]+t[ltemp][j]));
							//System.out.println("time window  "+a[j]+ " : "+b[j]);
							 //System.out.println("enter time ");
						 if(R.get(i).contains(j-n)==true ) {
						 //System.out.println(Yk.get(i)+" + "+d[j]);
						 Ytemp =Yk.get(i)+d[j];
						 //System.out.println(d[j]);
						 //System.out.println(Ytemp+"///"+Q);
						 
						
						 //System.out.println("follow 2nd");
						// System.out.println(j+"==");
						 label++;
						 //System.out.println("LABEL "+label);
						 //templabellist.add(label);
						 ArrayList<Integer> temp = new ArrayList<Integer>();						 
							 for(int v:S.get(i)) {
								 temp.add(v);
							 }	
							 
						 temp.add(j);//add this node
						 //S.put(label,temp );//and creat new label
						 tempS.put(label, temp);
						 ArrayList<Integer> tempR = new ArrayList<Integer>();
						 for(int v:R.get(i)) {
							 tempR.add(v);
						 }
						 //System.out.println(j-n);
						 
			             tempR.remove(tempR.indexOf(j-n));
			             tempRlist.put(label, tempR);
						 T.put(label, Ttemp);
						 Z.put(label, Z.get(i)+ca[e.get(i)][j]);
						 Yk.put(label, Ytemp);
						 e.put(label, j);
						 //System.out.println("tempS"+tempS);
						 }else {
							 //System.out.println("tempS"+tempS);
						 }
						 }else {
							 //System.out.println("time violation");
							 //System.out.println("start time  "+T.get(i)+"service and travel time    "+(s[ltemp]+t[ltemp][j])+" == "+T.get(i)+s[ltemp]+t[ltemp][j]);
							 //System.out.println("time window  "+a[j]+ " : "+b[j]);
							 //System.out.println("Ttemp  "+Ttemp );
						 }
					 }else if(j==2*n+1) {
						 //if the last node is 2n+1 add to "adroute"
						 //System.out.println("follow 3rd");
						 double Ytemp = 0;
						 double Ttemp = Math.max(a[j], T.get(i)+s[ltemp]+t[ltemp][j]); 
						 //System.out.println(Ttemp+"???"+b[j]);
						 if( Ttemp<=b[j] ) {
							 //System.out.println("pass 3");
							 //System.out.println("R:"+R.get(i));
							 //System.out.println(S.size());
						 if(R.get(i).isEmpty()==true && S.get(i).size()>1) {
						 label++;
						 //templabellist.add(label);
						 ArrayList<Integer> temp = new ArrayList<Integer>();						
							 for(int v:S.get(i)) {
								 temp.add(v);
							 }								 
						 temp.add(j);//add this node
						 //System.out.println("new route:"+temp+"with reduce cost:"+(Z.get(i)+ca[e.get(i)][j]));
						 adroute.put(label, temp);
						 
						 //S.put(label,temp );//and creat new label
						 //tempS.put(label, temp);
						 T.put(label, Ttemp);
						 Z.put(label, Z.get(i)+ca[e.get(i)][j]);
						 Yk.put(label, Ytemp);
						 e.put(label, j);
						 /*
						 for(int ad:adroute.keySet()) {
							 System.out.println(adroute.get(ad)+":"+Z.get(ad));
						 }*/
						 }
						 }
					 }			 
				 //System.out.println("tempS"+tempS);
				//System.out.println(label);
			 else if(Pn.containsKey(j)) {
				 //System.out.println(i+"--"+j);
				 //System.out.println("i:"+i);
				 //for(int v=0;v<w.length;v++) {
				//	  System.out.println("w."+v+":"+w[v]);
				 //System.out.println(a[e.get(i)]);
				// System.out.println(T.get(i));
				 //System.out.println(s[e.get(i)]);
				 //System.out.println(t[e.get(i)][j]);
				 //System.out.println(w[j]);
				 //System.out.println("Ttemp:"+Ttemp);
				 //System.out.println("b."+j+":"+b[j]);
				// System.out.println("b."+2*n+":"+b[2*n]);
					 double Ttemp = Math.max(a[j], T.get(i)+s[ltemp]+t[ltemp][j]); 
					 double Ytemp = Yk.get(i);
					 //System.out.println(a[j]+"///"+(T.get(i)+s[ltemp]+t[ltemp][j]));//do not add w[i] here, add it later
					 //System.out.println(Ttemp +"///"+b[j]);	
				     if( Ttemp<=b[j] && Ttemp<b[2*n] ) {
					 
						 label++;
						 //System.out.println(label);
						 //templabellist.add(label);
						 ArrayList<Integer> temp = new ArrayList<Integer>();
							 for(int v:S.get(i)) {
								 temp.add(v);
							 }	
						 temp.add(j);//add this node
						// S.put(label,temp );//and creat new label
						 tempS.put(label, temp );
						 ArrayList<Integer> tempR = new ArrayList<Integer>();
						 for(int v:R.get(i)) {
							 tempR.add(v);
						 }
			             //tempR.add(j);
			             tempRlist.put(label, tempR);
						 //System.out.println("S"+S);
			             Ttemp=Ttemp+w[j];
						 T.put(label, Ttemp);
						 //System.out.println(T);
						 Z.put(label, Z.get(i)+ca[e.get(i)][j]);
						 Yk.put(label, Ytemp);
						 e.put(label, j);	
						 //System.out.println("tempS"+tempS);
					 }	 
				 
			 }else if(Pc.containsKey(j)) {
					 double Ttemp = Math.max(a[j], T.get(i)+s[ltemp]+t[ltemp][j]); 
					 double Ytemp = Yk.get(i);
					 //System.out.println(a[j]+"///"+(T.get(i)+s[ltemp]+t[ltemp][j]));
					 //System.out.println(Ttemp +"///"+b[j]);	
				      if( Ttemp<=b[j] && Ttemp<b[2*n] ) {					 
						 label++;
						 //System.out.println(label);
						 //templabellist.add(label);
						 ArrayList<Integer> temp = new ArrayList<Integer>();
							 for(int v:S.get(i)) {
								 temp.add(v);
							 }	
						 temp.add(j);//add this node
						// S.put(label,temp );//and creat new label
						 tempS.put(label, temp );
						 ArrayList<Integer> tempR = new ArrayList<Integer>();
						 for(int v:R.get(i)) {
							 tempR.add(v);
						 }
			             //tempR.add(j);
			             tempRlist.put(label, tempR);
						 //System.out.println("S"+S);
						 T.put(label, Ttemp);
						 //System.out.println(T);
						 Z.put(label, Z.get(i)+ca[e.get(i)][j]);
						 Yk.put(label, Ytemp);
						 e.put(label, j);	
						 //System.out.println("tempS"+tempS);
					 }	 
				 
			 }
		}
			 //System.out.println("R"+R);
			 //System.out.println("S"+S);
			 //S.remove(i);//??????????????????
			 }
			 Z.remove(i);
			 T.remove(i);
			 Yk.remove(i);
			 e.remove(i);
			 //System.out.println(S);
			 //System.out.println(S.keySet());
		 }
		 indexb=label;
		//System.out.println("tempS"+tempS);
		 S=tempS;//assign new sets to S
		 //System.out.println(S);
		// System.out.println("e"+e);
		 R=tempRlist;//assign new sets of R
		 //System.out.println("R"+R);
		// System.out.println("print label list");
		 //LabelList=templabellist;
		 //System.out.println("admissible route"+adroute);
		 //System.out.println("labellist"+LabelList);
		 //System.out.println(LabelList.size());

// elimination 1
		 
		ArrayList<Integer> tempkeyset = new ArrayList<Integer>();
		 for(int i:S.keySet()) {
			 tempkeyset.add(i);
		 }
		 for(int i:tempkeyset) {// i is the label
			 //System.out.println(i);
			 //System.out.println(R);			 
			 if(R.get(i).isEmpty()==false) {
				 //System.out.println(i);
				 for(int j:R.get(i)) {//j belong to R
					 //System.out.println("S"+S);
					 //System.out.println("R"+R);
					 //System.out.println("e"+e);
					 if(e.get(i) != n+j) {
					 if(T.get(i)+t[e.get(i)][j+n]>b[n+j] || T.get(i)+t[e.get(i)][j+n]+t[j+n][2*n+1]>b[2*n+1] ) {
						 eliminate(i);
						 elimination1=elimination1+1;
						 //System.out.println("eliminate1--"+i);
						 //System.out.println(S);
						 break;
					 }
					 }
				 }
				 
			 }
			 
		 }
		 
// elimination 2
		 ArrayList<Integer> tempkeyset1 = new ArrayList<Integer>();
		 for(int i:S.keySet()) {
			 tempkeyset1.add(i);
		 }
		 for(int l:tempkeyset1) {
			 //System.out.println(l);
			 if(R.get(l).isEmpty()==false) {
				 for(int i :R.get(l) ) {
					 for(int j:R.get(l)) {
						 if(i != j) {
							 if( (T.get(l)+t[e.get(l)][i+n]>b[i+n] || T.get(l)+t[e.get(l)][i+n]+t[i+n][j+n]>b[j+n] || t[e.get(l)][i+n]+t[i+n][j+n]+t[j+n][2*n+1]>b[2*n+1])
							&&    T.get(l)+t[e.get(l)][j+n]>b[j+n] || T.get(l)+t[e.get(l)][j+n]+t[j+n][i+n]>b[i+n] || t[e.get(l)][j+n]+t[j+n][i+n]+t[i+n][2*n+1]>b[2*n+1]	 ) {
								 eliminate(l);
								 elimination2=elimination2+1;
								 //System.out.println("eliminate2 "+l);
								 //System.out.println("R"+R);
								 break;								 
							 }							
						 }						 
					 }
					 break;
				 }
			 }			 
		 }
		 		 
//get H set		 
		 H=getHset(S);
		 //System.out.println("H"+H);
		 
//eliminate 3		 
		 for(int label1:H.keySet()) {
			 ArrayList<Integer> tempkeyset2 = new ArrayList<Integer>();
			 for(int i:H.get(label1)) {
				 tempkeyset2.add(i);
			 }
			 for(int i:tempkeyset2) {
				 for(int j:tempkeyset2) {
					 if( i < j) {
						 if(R.get(i) != null && R.get(j) != null) {							 
						 if(isEqual(R.get(i),R.get(j)) ) {
						 if(Z.get(i)<Z.get(j) && T.get(i)<T.get(j)) {
							 //System.out.println(i+"--"+j);
							 eliminate(j);
							 elimination3=elimination3+1;
							 //System.out.println("eliminate3--"+j);
							 //H.get(label1).remove(H.get(label1).indexOf(j));
							 //System.out.println(H); 
						 }
						 }
						 }
					 }
				 }
			 }
		 }	
//eliminate 4
		 
		 for(int label1:H.keySet()) {
			 ArrayList<Integer> tempkeyset3 = new ArrayList<Integer>();
			 for(int i:H.get(label1)) {
				 tempkeyset3.add(i);
			 }
			 for(int i:tempkeyset3) {
				 for(int j:tempkeyset3) {
					 if(i < j) {
						 if(R.get(i)!=null && R.get(j)!=null && R.get(i).size()==1) {
							 if(R.get(j).contains(R.get(i).get(0))) {
								 if(Z.get(i)<Z.get(j) && T.get(i)<T.get(j)) {
									 eliminate(j);
									 elimination4=elimination4+1;
									 //System.out.println("eliminate4--"+j);
								 }
							 }
						 }
					 }
				 }
			 }
		 }
		 //System.out.println("admissible route"+adroute.size());
	 }
	 // delete dominant state

	 //System.out.println("#admissible route"+adroute.size());
	 //System.out.println("adroute:"+adroute);
	 ArrayList<Integer> routelist = new ArrayList<Integer>();
	 for(int i:adroute.keySet()) {
		 routelist.add(i);
	 }
	 //System.out.println("Z  "+Z+"  "+Z.size());
	 //System.out.println("R  "+routelist);
	 
	 for(int i=0;i <routelist.size();i++) {
	    for(int j=0;j<routelist.size()-2;j++) {
		 if(Z.get(routelist.get(j))>Z.get(routelist.get(j+1))){
			 Collections.swap(routelist, j, j+1);
		   }
	 }
	 }
	 /*
	 for(int i=0;i<routelist.size();i++) {
		 System.out.println(routelist.get(i)+"  "+Z.get(routelist.get(i)));
	 }*/
	 ArrayList<Integer> routelisttemp = new ArrayList<Integer>();
	 int top =1;
	 int tempm = Math.min(top,(routelist.size()));//select top # of route
	 //System.out.println(tempm);
	 //System.out.println(" Z "+Z);
	 //System.out.println(" R "+routelist);
	 for(int i=0;i<=tempm-1;i++) {
		 //System.out.println(i);
	    	if(Z.get(routelist.get(i))<-0.01) {
	    		routelisttemp.add(routelist.get(i));
	    	}
	    }
	
	 routelist = routelisttemp;
	 //System.out.println("after "+routelist);
	 /* 
     for(int i:adroute.keySet()) {
    	 if(Z.get(i)<temp) {
    		 temp = Z.get(i);
    		 a=i;
    	 }
     }*/
	 int tempr=routelist.size();
	 //System.out.println("Z "+Z);
	 //System.out.println("routelist "+routelist);
	 if(routelist.isEmpty()==true) {
		 margi_cost=0;
	 }else {
	 margi_cost=Z.get(routelist.get(0));
	 }
	 //System.out.println(margi_cost);
	 /*
	 for(int i:adroute.keySet()) {
    	 System.out.println(adroute.get(i));
     }*/
     
     //System.out.println("Z:"+Z);
     //System.out.println("a"+a);
     //System.out.println("Z"+Z);
     //System.out.println("0--2"+ca[0][2]);
	 //System.out.println("best route:"+adroute.get(a)+":reducecost:"+temp);
	 //System.out.println("routelist size "+routelist.size());
	 
	 /*
	    for(int i:adroute.keySet()) {
	    	System.out.println(Z.get(i) +"  "+adroute.get(i));
	    }*/
    for(int i=0;i<routelist.size();i++) {    
    	    int tempi = routelist.get(i);
    	    ArrayList<Integer> temp = adroute.get(tempi);
    	    System.out.println(temp);
    		finalroute.add(temp);    	
    		//System.out.println("best route"+Z.get(tempi) +"  "+temp);
    }

    //System.out.println("end");
	// finalroute.add(adroute.get(a));
	// System.out.println("=====================fianl route========================");
	 /*
	 Collection<ArrayList<Integer>> values = adroute.values();
	 
	 while (values.remove(null)) {}
	 
	 Iterator iterator = values.iterator();
	 while(iterator.hasNext()) {
		 System.out.println(iterator.next());
	 }*/
	 numberofroute.add(adroute.size());
	 newcolumnset = new ArrayList<HashMap<Integer,Double>>();
	 newcset= new ArrayList<Double>();
	  /*
	 for(int i=0;i<adroute.get(a).size()-1;i++) {
		 routecost = routecost +t[adroute.get(a).get(i)][adroute.get(a).get(i+1)];
	 }*/
	 
	 //System.out.println("routesize  "+routelist.size());
	 for(int i=0;i<routelist.size();i++) {
		 //System.out.println(i+"==================");
		 double routecost=0;	
		 int routnum = routelist.get(i);
		 
		 //System.out.println("route "+routnum);
		 ArrayList<Integer> temproute = adroute.get(routnum);
		 for(int j=0;j<temproute.size()-1;j++) {
			 //System.out.println(j+"=================");
			 routecost = routecost+t[temproute.get(j)][temproute.get(j+1)];
		 }
		 newcset.add(routecost);
		 finalcost.add(routecost);
	 }
	 //double[] route_cost[] = 
	 //System.out.println("routecost:"+routecost);
	 
	 //new_column = new HashMap<Integer,Double>();
	 //new_c=0;
	 
	 for(int i=0;i<routelist.size();i++) {
		 HashMap<Integer,Double> coltemp = new HashMap<Integer,Double>();
		 for(int j:Pt.keySet()) {
			 coltemp.put(j, 0.0);
		 }
		 newcolumnset.add(coltemp);
		 
	 }
	 /*
	 for(int i:Pt.keySet()) {
		 new_column.put(i, 0.0);
	 } 
*/
	 //System.out.println("a:"+a);
	 for(int i=0;i<newcolumnset.size();i++) {		 
	    int routenum=routelist.get(i);
	    ArrayList<Integer> temproute = adroute.get(routenum);
	    for(int j=1;j<temproute.size()-1;j++) {// only assign p+ and p-, not include 0 and 2n+1, so size-1
		            newcolumnset.get(i).put(temproute.get(j), 1.0);
		    
	    }
	 }
	 for(int i=0;i<newcolumnset.size();i++) {
	 finalcolumn.add(newcolumnset.get(i));
	 }
	 
  }
  HashMap<Integer,ArrayList<Integer>> getHset( TreeMap<Integer,ArrayList<Integer>> a) {
	  HashMap<Integer,ArrayList<Integer>> H = new HashMap<Integer,ArrayList<Integer>>(); //H set <destination node j, label set>
	  for(int i:a.keySet()){
		  int j = a.get(i).get(a.get(i).size()-1);
		  if(H.keySet().contains(j)==false) {
			  H.put(j, new ArrayList<Integer>(Arrays.asList(i)));
		  }else {
			  H.get(j).add(i);
		  }
	  }
	  return H;
  }
// eliminate path
  void eliminate(int a) {
	     S.remove(a);
	     R.remove(a);
	     Z.remove(a);
		 T.remove(a);
		 Yk.remove(a);
		 e.remove(a);
  }
  boolean isEqual(ArrayList<Integer> aa,ArrayList<Integer> ab) {
	  ArrayList<Integer>  atemp = new ArrayList<Integer>();
	  ArrayList<Integer> btemp = new ArrayList<Integer>();
	  atemp=aa;
	  btemp=ab;
	  for(int i: atemp) {
		  if(btemp.contains(i)==false) {
			  return false;
		  }
		  
	  }
	  return true;
  }
  ArrayList<Integer> sortlist(ArrayList<Integer> a){
	  //sorting label by time
	  //sort list by time visit node j
	  for(int i=0;i<a.size();i++) {
		  for(int j=0;j<a.size()-i-1;j++) {
			  if(T.get(a.get(j))>T.get(a.get(j))) {
				  Collections.swap(a, j, j+1);
			  }
		  }
	  }
	  return a;
  }
  public class fnmodel {
	  private int nn;
	  private IloNumVar[] x;
	  public fnmodel() {
		   nn=finalcolumn.size();
	  }
	  public void creatmodel() {
		  try {
			 
			
			  cplex_final = new IloCplex();
			  obj = cplex_final.linearNumExpr();
			  //System.out.println(finalcolumn);
			  x = new IloNumVar[nn];
			  //System.out.println("size:"+nn);
			  for(int i=0;i<nn;i++) {
				  x[i]=cplex_final.numVar(0, 1);
				  x[i].setName("x."+i);
				  }
	// objective function		  
			  for(int i=0;i<nn;i++) {
				  obj.addTerm(finalcost.get(i), x[i]);
			  }
			   cplex_final.addMinimize(obj);
	//constraint 1		 
			   /*
			   for(int i=0;i<nn;i++) {
				   System.out.println(finalcolumn.get(i));
			   }*/
			  for(int i:Pt.keySet()) {			 
				  expr=cplex_final.linearNumExpr();
				  for(int j=0;j<nn;j++) {
					  expr.addTerm(finalcolumn.get(j).get(i), x[j]);
				  }
				  cplex_final.addEq(expr, 1) ;
				  //testconstraints[i] = cplex_final.addEq(expr, 1);
			  }
	//constraint 2		  
			  expr=cplex_final.linearNumExpr();
			  for(int i=0;i<nn;i++) {
				 expr.addTerm(1.0, x[i]);			  
			  }
			  //testconstraints[0] = cplex_final.addLe(expr, V);
			  cplex_final.addLe(expr, V);		  
			  //testconstraints[0]=cons.get(0);		
			  //cplex_final.exportModel("finalmodel.lp");
		  }catch (IloException e) {
				System.err.println("Concert exception caught: " + e);		
			}
	    }
	  boolean solve() {
		  try {
			 cplex_final.setOut(null);
			if(cplex_final.solve()) {
				//cplex_final.exportModel("finalmodel.lp");
				//double objvalue=0;
				//System.out.println("array_finalcost"+finalcost);
				/*
				for(int i=1;i<nn;i++) {
					System.out.println(finalroute.get(i));
				}*/
				int j=1;
				double numbus=0;
				  for(int i=0;i<nn;i++) {
					  if(cplex_final.getValue(x[i])>0.0) {
						  //objvalue =  objvalue+finalcost.get(i)*cplex_final.getValue(x[i]);
						  //System.out.println(i+"-selected");
						  //System.out.println(numberofroute.get(i)+":route created");
						  System.out.println(cplex_final.getValue(x[i])+":route"+j+":"+finalroute.get(i));
						  numbus = numbus+cplex_final.getValue(x[i]);
						 // System.out.println(T.get(i));
						  //System.out.println(finalcost.get(i));
						  /*
						  ArrayList<Integer> temp = finalroute.get(i);
						  ArrayList<Integer> temp1 = new ArrayList<Integer>();//sets for node go directly to deliver node
						  ArrayList<Integer> temp2 = new ArrayList<Integer>();//sets for node share route
						  for(int k=1;k<temp.size()-1;k++) {
							  if(temp.get(k)<n && temp.get(k)+n == temp.get(k+1)) {
								  temp1.add(temp.get(k));
							  }else if(temp.get(k)<n) {
								  temp2.add(temp.get(k));
							  }
						  }
						  System.out.println("node that go directly to deliver node"+temp1);
						  System.out.println("node that share route" +temp2);*/
						  j++;
					  }
				  }
				  //System.out.println("final_obj:"+cplex_final.getObjValue());
				  //scenariocost=cplex_final.getObjValue();
				  //System.out.println("final_obj byhand"+objvalue);
				  System.out.println("bus used "+numbus );
				  
				  cplex_final.end();				 
				  return true;
			  }else {
				  System.out.println("final_problem not solved");
				  /*
				  for(int i:Pt.keySet()) {
					  scenariocost=scenariocost+2*t[i][i+n];
				  }*/
				  return false;
			  }
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	  }
  }
  public class testforCG {
	  public IloCplex cplex;
        public testforCG() {
        	
        }
      public void creatmodel() { 
	  try {		  
		  System.out.println("start");
		  cplex = new IloCplex();
		  IloRange[] c1 = new IloRange[2];
		  IloNumVar x1 = cplex.numVar(0,10);
		  IloNumVar x2 = cplex.numVar(0, 10);
		  obj = cplex.linearNumExpr();
		  obj.addTerm(2, x1);
		  obj.addTerm(3, x2);
		  IloObjective objective =  cplex.addMinimize(obj);
		  expr = cplex.linearNumExpr();
		  expr.addTerm(1, x1);
		  expr.addTerm(1, x2);
		  c1[0]=cplex.addGe(expr, 1);
		  expr = cplex.linearNumExpr();
		  expr.addTerm(2, x1);
		  expr.addTerm(1, x2);
		  c1[1]=cplex.addGe(expr, 3);		  
		  IloColumn col1 = cplex.column(objective,1);
		  IloColumn col2 = cplex.column(c1[0],2);
		  //double[] index = new double[] {1,2};
		  col2 = col2.and(cplex.column(c1[1],1));
		  cplex.numVar(col1.and(col2), 0,10);
		  cplex.exportModel("test.lp");
		  cplex.solve();
		  double tempdual = cplex.getDual(c1[0]);
		  System.out.println("dual"+tempdual);		 
		  }catch (IloException e) {		 
			  System.out.println("");
	  }
   }
  }
  public void runtest() {
	  testforCG exa = new testforCG();
	  exa.creatmodel();
  }
  public void ColumnGenRun() throws Exception, IloException {
	  mas_prob = new masterproblem(); 
	  //mas_prob.initializationF(); //initial with feasible solution
	  mas_prob.initialization();//initial with indentity matrix
	  mas_prob.creatmodel();
	  mas_prob.solve();
	  changeweight();
	  sub_prob = new subproblem();
	  sub_prob.creatmodel();
	  sub_prob.solve();
	  double iteration=0;
	  while(margi_cost<-0.01 ) {
		  System.out.println("============");
		  System.out.println("============");
		  System.out.println("============");
		  System.out.println("iteration"+iteration);
		  sub_prob.catchpath();
		  mas_prob.addColumn();
		  mas_prob.solve();
		  changeweight();
		  sub_prob.creatmodel();
		  sub_prob.solve();
		  iteration++;
		  System.out.println("marginal cost"+margi_cost);
	  }	  
  }
  void test() {
	  System.out.println("begin test");
	  masterproblem exa = new masterproblem();
	  exa.initialization();
	  exa.creatmodel();
	  exa.solve();
  }
  public void testformasP() throws IloException, IloException {
	  mas_prob = new masterproblem();
	  mas_prob.initialization();
	  mas_prob.creatmodel();
	  mas_prob.addColumn();
	  mas_prob.solve();
  }
  boolean testDP(int busnum) {//input bus number
	  //test for dynamic programming
	  //readexcel();
	  V=busnum;
	  mas_prob = new masterproblem(); 
	 
	 mas_prob.initialization();//initial with indentity matrix
	  //mas_prob.initializationF();
	
	  mas_prob.creatmodel();
	  mas_prob.solve();
	  
	  iteration=0;
	  margi_cost=-1000;
	  //System.out.println("marginal cost"+margi_cost);
	  while(margi_cost<-800 ) {
		  //System.out.println("iteration "+iteration);
		  //System.out.println("========================================");
		  //System.out.println("========================================");
		  //System.out.println("========================================");
		  //System.out.println("==============iteration"+iteration+"===============");
		  changeweight();
		  shortestpath();
		  mas_prob.addColumn();
		  mas_prob.solve();
		  System.out.println("marginal cost"+margi_cost);
		  iteration++;
		  
	  }	
	  /*
	  for(int i=0;i<finalroute.size();i++) {
		  System.out.println("route."+i+":"+finalroute.get(i));
	  }*/
	  cplex_master.end();
	  fnmodel finalmodel = new fnmodel();
	  finalmodel.creatmodel();
	  if(finalmodel.solve()==true) {
		return true;  
	  }else {
		  return false;
	  }
	  //System.out.println("elimination1:"+elimination1);
	  //System.out.println("elimination2:"+elimination2);
	  //System.out.println("elimination3:"+elimination3);
	 // System.out.println("elimination4:"+elimination4);
	  
  }
  void readexcel(String input) {
	  try {
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
        this.a=new double[2*n+2];
        this.b=new double[2*n+2];
        this.Pt = new TreeMap<Integer,Integer>();
        this.Pa = new TreeMap<Integer,Integer>();
        this.Pb = new TreeMap<Integer,Integer>();
        this.N = new TreeMap<Integer,Integer>();
        this.P = new TreeMap<Integer,Integer>();
        this.t= new double[2*n+2][2*n+2];
        this.cn = new double[2*n+2][2*n+2];
        this.ca = new double[2*n+2][2*n+2];
        this.s = new double[2*n+2];
        this.d = new double[2*n+2];
        for(int i=1;i<2*n+1;i++) {
        	for(int j=1;j<2*n+1;j++) {
        		if(i != j) {
        		t[i][j]=(Math.random()*(15)+5);//time vary from a : b
        		cn[i][j]=t[i][j];
        		}
        	}
        }    
        for(int i=1;i<2*n+2;i++) {
        	t[0][i]=(Math.random()*(15)+5);
        }
        for(int i=0;i<2*n+1;i++) {
        	t[i][2*n+1]= (Math.random()*(15)+5);
        }
        for(int i=1;i<=n;i++) {
        	a[i]=hour[i]*60+min[i]-10;
        	//System.out.println(a[i]);
        	b[i]=a[i]+10;
        }
        for(int i=n+1;i<=2*n;i++) {
        	a[i]=a[i-n]+60;
        	b[i]=a[i-n]+2*60;       	
        }
        a[0]=a[1]-t[0][1];
        b[0]=b[2*n]+60;
        a[2*n+1]=a[1];
        b[2*n+1]=b[2*n]+60;   
        for(int i=1;i<=n;i++) {
        	s[i]=10;
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
  

  public static void main (String[] args) throws Exception, IloException {
	  network exa = new network();
      exa.generateRandomNetwork(5,3,30);//(n,Q,V)
	  ColumnGeneration exam = new ColumnGeneration(exa);
	  //exam.runtest(); //test for columngeneration algorithm
	  //exam.testformasP();	
	  //exam.ColumnGenRun();
	  //exam.test();
	  //exam.testDP();
	  //exam.readexcel();
     }
   }
  
