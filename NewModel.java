import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;
public class NewModel {


public double[][] distance;
public double[] d;
public double[] s;
public double[][] t;
public double[][] c;
public double[] a;
public double[] b;
public double Q;
public double L;
public double T;
public double[] w;
private IloCplex cplex;
private IloLinearNumExpr obj;
private IloLinearNumExpr expr;
private IloIntVar[][][] x;
//private IloNumVar[][] B;
//private IloNumVar[][] Qv;
private IloNumVar[] Y;
//private IloNumVar[][] Lv;
private IloNumVar[] Tb;// service in node i begn
private IloNumVar[] Tl;// time for vehicle leaves depot;
private IloNumVar[] Tr;// time for vehicle retuns depot;
private double max;
//private double maxdemand;
private int K;
private int n;
//private double maxTravelTime;
public TreeMap<Integer,Integer> Pa;//normal request node
public TreeMap<Integer,Integer> Pn;//no-show request
public TreeMap<Integer,Integer> Pc;//late cancel request
public TreeMap<Integer,Integer> Pb;//set of destination node
public TreeMap<Integer,Integer> Pt;//union of Pa, Pc and Pn
public TreeMap<Integer,Integer> P;
public TreeMap<Integer,Integer> N;
private double routecost;
public double profit;
public double outsource;
public double objdirec;
public NewModel( ){
	profit=200;
	outsource=300;

}
	public void InitializationRandomly(){//n pickup location and n delivery location 
		distance=new double[2*n+1][2*n+1];
		double x[]= new double[2*n+1];
		double y[]= new double[2*n+1];
		for(int i=0;i<2*n;i++){
			x[n]=(Math.random()*(max)+1);
		}
		for(int i=0;i<2*n;i++){
			y[n]=(Math.random()*(max)+1);
		}
		for(int i=0;i<2*n+1;i++){
			for(int j=0;j<2*n+1;j++){
				distance[i][j]= (x[i]-x[j])*(x[i]-x[j])+(y[i]-y[j])*(y[i]-y[j]);
				
			}
		}		
	}

	public void creatModel(network nw ){
		//a.generateRandomNetwork(busNum);		
		
		K = nw.V;
		this.Pa=nw.Pa;
		this.Pb=nw.Pb;
		this.N=nw.N;
		this.P=nw.P;
		this.n=nw.n;
		this.a=nw.a;
		this.b=nw.b;
		this.d=nw.d;
		this.s=nw.s;
		this.t=nw.t;
		this.c=nw.c;
		this.Q=nw.Q;
		this.w=nw.w;
		this.Pt=nw.Pt;
		this.Pn=nw.Pn;
		this.Pc=nw.Pc;
		System.out.println("Pt1:"+Pt);
		System.out.println("Pb1:"+Pb);
		System.out.println("Pa1:"+Pa);
		System.out.println("Pc1:"+Pc);
		System.out.println("Pn1:"+Pn);
		System.out.println("P1:"+P);
		System.out.println("N1:"+N);
		routecost=0;
	
		try{			
			double M=200;
			cplex = new IloCplex();
			obj = cplex.linearNumExpr();
			x = new IloIntVar[2*n+2][2*n+2][K];		
			//B = new IloNumVar[2*n+2][K];
			//Qv = new IloNumVar[2*n+2][K];
			//Lv = new IloNumVar[2*n+2][K];
			Tb = new IloNumVar[2*n+2];
			Tl = new IloNumVar[K];
			Tr = new IloNumVar[K];
			Y = new IloNumVar[2*n+2];
			for(int i:N.keySet()) {
				for(int j:N.keySet()) {
					for(int k=0;k<K;k++) {
						if(i != j) {
						//x[i][j][k]=cplex.boolVar();
							x[i][j][k]=cplex.boolVar();
						x[i][j][k].setName("x."+i+"."+j+"."+k);  
						}
					}
				}
			}
			for(int i:P.keySet()) {
				Tb[i]=cplex.numVar(0, Double.MAX_VALUE);
				Tb[i].setName("Tb."+i);					
			}
			for(int i:N.keySet()) {
				Y[i] =cplex.numVar(0, Double.MAX_VALUE);
				Y[i].setName("Y."+i);
			}
			for(int k=0;k<K;k++) {
				Tl[k]=cplex.numVar(0, Double.MAX_VALUE);
				Tl[k].setName("Tl."+k);
				Tr[k]=cplex.numVar(0, Double.MAX_VALUE);
				Tr[k].setName("Tr."+k);				
			}
// change all sets			
//objective function		 
			/*//complicated obj
			for(int i:Pa.keySet()) {
				for(int k=0;k<K;k++) {
					for(int j:N.keySet()) {
						obj.addTerm(profit, x[i][j][k]);
						obj.addTerm(outsource, x[i][j][k]);
					}
				}
			}
			for(int i:N.keySet()) {
		        for(int k=0;k<K;k++) {			  
				   for(int j:N.keySet()) {
					   obj.addTerm( -c[i][j],x[i][j][k] );
				   }
			   }
		   }	   
		   cplex.addMaximize(obj);
		   */
			for(int i:N.keySet()) {
				for(int j:N.keySet()) {
					for(int k=0;k<K;k++) {
					if(i != j) {
						obj.addTerm(c[i][j], x[i][j][k]);
					}
					}
				}
			}
			cplex.addMinimize(obj);
//(6)	
		   for(int i:Pt.keySet()) {
			   expr=cplex.linearNumExpr();
			   for(int k=0;k<K;k++) {
				   for(int j:N.keySet()) {
					   if(i != j) {
					   expr.addTerm(1.0, x[i][j][k]);
					   }
				   }
			   }
			   cplex.addEq(expr, 1);
		   }
		   
//added	not needed since travel to noshow and cancellation destination generate cost.
		  /* 
		   for(int i:Pn.keySet()) {
		   expr=cplex.linearNumExpr();
		   for(int k=0;k<K;k++) {
			   for(int j:N.keySet()) {
				   if(i+n != j) {
				   expr.addTerm(1.0, x[i+n][j][k]);
				   }
			   }
		   }
		   cplex.addEq(expr, 0);
		   }
		   for(int i:Pn.keySet()) {
		   expr=cplex.linearNumExpr();
		   for(int k=0;k<K;k++) {
			   for(int j:N.keySet()) {
				   if(i+n != j) {
				   expr.addTerm(1.0, x[j][i+n][k]);
				   }
			   }
		   }
		   cplex.addEq(expr, 0);
		   }
		   
		   for(int i:Pc.keySet()) {
			   expr=cplex.linearNumExpr();
			   for(int k=0;k<K;k++) {
				   for(int j:N.keySet()) {
					   if(i+n != j) {
					   expr.addTerm(1.0, x[i+n][j][k]);
					   }
				   }
			   }
			   cplex.addEq(expr, 0);
			   }
		    
			for(int i:Pc.keySet()) {
			   expr=cplex.linearNumExpr();
			   for(int k=0;k<K;k++) {
				   for(int j:N.keySet()) {
					   if(i+n != j) {
					   expr.addTerm(1.0, x[j][i+n][k]);
					   }
				   }
			   }
			   cplex.addEq(expr, 0);
			   }*/
			   
//(7)   
		   
		  
		   for(int i:P.keySet()) {
			   for(int k=0;k<K;k++) {
				   expr=cplex.linearNumExpr();
				   for(int j:N.keySet()) {
					   if(i != j) {
					   expr.addTerm(1.0, x[i][j][k]);
					   expr.addTerm(-1.0,x[j][i][k]);
					   }
				   }				   
				   cplex.addEq(expr, 0);
			   }
		   }
		   
		   
		  
//(8)
		   
		   System.out.println("k"+K);
		   for(int k=0;k<K;k++) {
			   expr=cplex.linearNumExpr();
			   for(int j:P.keySet()) {
				   expr.addTerm(1.0, x[0][j][k]);
			   }
			   cplex.addEq(expr, 1);
		   }
//(9)   
		   
		   for(int k=0;k<K;k++) {
			   expr=cplex.linearNumExpr();
			   for(int i:P.keySet()) {
				   expr.addTerm(1.0, x[i][2*n+1][k]);
			   }
			   cplex.addEq(expr, 1);
		   }
//(10)
		   for(int i: Pa.keySet()) {
			   for(int k=0;k<K;k++) {
				   expr=cplex.linearNumExpr();
				   for(int j:N.keySet()) {
					   if(i != j) {
					   expr.addTerm(1.0, x[i][j][k]);
					   }
				   }
				   for(int j:N.keySet()) {
					   if(n+i != j) {
					   expr.addTerm(-1.0, x[j][n+i][k]);
					   }
				   }
				   cplex.addEq(expr, 0);
			   }
		   }
//(11)   
		   for(int i:Pa.keySet()) {
			   expr = cplex.linearNumExpr();
			   expr.addTerm(1.0, Tb[i]);
			   expr.addTerm(-1.0, Tb[i+n]);
			   cplex.addLe(expr, -t[i][i+n]-s[i]);			   
			   }
		   
//(12)
		   
		   for(int i:P.keySet()) {
			   for(int j:P.keySet()) {
				   for(int k=0;k<K;k++) {
					   if(i != j) {
					   expr = cplex.linearNumExpr();
					   expr.addTerm(1.0, Tb[i]);
					   expr.addTerm(-1.0, Tb[j]);
					   expr.addTerm(M,x[i][j][k] );
					   cplex.addLe(expr, M-s[i]-t[i][j]);
					   }
				   }
			   }
		   }
		   
		   
		   //for noshow request		
		   if(Pn != null) {
		     for(int i:Pn.keySet()) {
			   for(int j:P.keySet()) {
				   for(int k=0;k<K;k++) {
					   if(i != j) {
					   expr = cplex.linearNumExpr();
					   expr.addTerm(1.0, Tb[i]);
					   expr.addTerm(-1.0, Tb[j]);
					   expr.addTerm(M,x[i][j][k] );
					   cplex.addLe(expr, M-s[i]-w[i]-t[i][j]);
					   }
				   }
			   }
		    }
		   }
//(13)  }
		   
		   for(int j:Pt.keySet()) {
			   for(int k=0;k<K;k++){
				  expr=cplex.linearNumExpr();
				  expr.addTerm(1.0, Tl[k]);
				  expr.addTerm(M, x[0][j][k]);
				  expr.addTerm(-1.0,Tb[j]);
				  cplex.addLe(expr, M-t[0][j]);
			   }
		   }
//(14)
		   
		   for(int i:Pb.keySet()) {
			   for(int k=0;k<K;k++) {
				   expr=cplex.linearNumExpr();
				   expr.addTerm(M, x[i][2*n+1][k]);
				   expr.addTerm(1.0, Tb[i]);
				   expr.addTerm(-1.0, Tr[k]);
				   cplex.addLe(expr, M-t[i][2*n+1]-s[i]);
			   }
		   }
//(15)
		   for(int i:P.keySet()) {
			   expr=cplex.linearNumExpr();
			   expr.addTerm(1.0, Tb[i]);
			   cplex.addGe(expr, a[i]);
			   cplex.addLe(expr, b[i]);
		   }
//(16)
		   for(int k=0;k<K;k++) {
			   expr = cplex.linearNumExpr();
			   expr.addTerm(1.0, Tl[k]);
			   cplex.addGe(expr, a[0]);
			   cplex.addLe(expr, b[0]);
		   }
//(17)
		   for(int k=0;k<K;k++) {
			   expr = cplex.linearNumExpr();
			   expr.addTerm(1.0, Tr[k]);
			   cplex.addGe(expr, a[2*n+1]);
			   cplex.addLe(expr, b[2*n+1]);
		   }
//(18)
		   for(int i:P.keySet()) {
			   for(int j:Pt.keySet()) {
				   for(int k=0;k<K;k++) {
					   if(i != j) {
					   expr=cplex.linearNumExpr();
					   expr.addTerm(M, x[i][j][k]);
					   expr.addTerm(-1.0, Y[j]);
					   expr.addTerm(1.0, Y[i]);
					   cplex.addLe(expr, M-d[j]);
					   }
				   }
			   }
		   }		   
		   
		   for(int i:P.keySet()) {
			   for(int j:Pt.keySet()) {
				   for(int k=0;k<K;k++) {
					   if(i != j) {
					   expr=cplex.linearNumExpr();
					   expr.addTerm(-M, x[i][j][k]);
					   expr.addTerm(-1.0, Y[j]);
					   expr.addTerm(1.0, Y[i]);
					   cplex.addGe(expr, -M-d[j]);
					   }
				   }
			   }
		   }
		   
//(19)
		 
		   for(int i:P.keySet()) {
			   for(int j:Pb.keySet()) {
				   for(int k=0;k<K;k++) {
					   if(i != j) {
					   expr=cplex.linearNumExpr();
					   expr.addTerm(M, x[i][j][k]);
					   expr.addTerm(-1.0, Y[j]);
					   expr.addTerm(1.0, Y[i]);
					   cplex.addLe(expr, M+d[j-n]);	
					   }
				   }
			   }
		   }		   
		   for(int i:P.keySet()) {
			   for(int j:Pb.keySet()) {
				   for(int k=0;k<K;k++) {
					   if(i != j) {
					   expr=cplex.linearNumExpr();
					   expr.addTerm(-M, x[i][j][k]);
					   expr.addTerm(-1.0, Y[j]);
					   expr.addTerm(1.0, Y[i]);
					   cplex.addGe(expr, -M+d[j-n]);		
					   }
				   }
			   }
		   }
              
//(20)
		   
		   for(int j:Pt.keySet()) {
			   for(int k=0;k<K;k++) {
				   expr = cplex.linearNumExpr();
				   expr.addTerm(M, x[0][j][k]);
				   expr.addTerm(-1.0, Y[j]);
				   expr.addTerm(1.0, Y[0]);
				   cplex.addLe(expr, M-d[j]);				   
			   }
		   }
		   
		   for(int j:Pt.keySet()) {
			   for(int k=0;k<K;k++) {
				   expr = cplex.linearNumExpr();
				   expr.addTerm(-M, x[0][j][k]);
				   expr.addTerm(-1.0, Y[j]);
				   expr.addTerm(1.0, Y[0]);
		   	   cplex.addGe(expr, -M-d[j]);				   
		      }
		   }
		   
//(21)
		   
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, Y[0]);;
		   cplex.addEq(expr,0);
		   
		   for(int i:Pa.keySet()) {
			   expr = cplex.linearNumExpr();
			   expr.addTerm(1.0, Y[i]);
			   cplex.addGe(expr, d[i]);
			   cplex.addLe(expr, Q);
		   }
//(22)added constraints	   
		   
		   for(int k=0;k<K;k++) {
			   expr=cplex.linearNumExpr();
			   for(int i:P.keySet()) {
				   expr.addTerm(1.0, x[i][0][k]);
			   }
			   cplex.addEq(expr, 0);
			   for(int j:P.keySet()) {
				   expr.addTerm(1.0, x[2*n+1][j][k]);
			   }
			   cplex.addEq(expr, 0);			   
		   }		
//test constraint
		   /*
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, x[0][1][0]);
		   cplex.addEq(expr, 1);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, x[1][3][0]);
		   cplex.addEq(expr, 1);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, x[3][4][0]);
		   cplex.addEq(expr, 1);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, x[4][6][0]);
		   cplex.addEq(expr, 1);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, x[6][7][0]);
		   cplex.addEq(expr, 1);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, x[0][2][1]);
		   cplex.addEq(expr, 1);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, x[2][5][1]);
		   cplex.addEq(expr, 1);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, x[5][7][1]);
		   cplex.addEq(expr, 1);
// constraint for time variables		   
		   
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, Tb[1]);
		   cplex.addEq(expr, 27.0);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, Tb[2]);
		   cplex.addEq(expr, 13.0);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, Tb[3]);
		   cplex.addEq(expr, 19.0);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, Tb[4]);
		   cplex.addEq(expr, 77.0);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, Tb[5]);
		   cplex.addEq(expr, 54.0);
		   expr=cplex.linearNumExpr();
		   expr.addTerm(1.0, Tb[6]);
		   cplex.addEq(expr, 69.0);*/
		   System.out.println("end creation");
		}catch(IloException e) {			   
	  }
	}
public void outputM() throws IloException {	   
	
}
public boolean solve() {	
	try {
		cplex.exportModel("normalModel.lp");
		  if( cplex.solve()==true) {
		  System.out.println("problem solved");
		  System.out.println("obj"+cplex.getObjValue());
		  objdirec=cplex.getObjValue();
	 for(int k=0;k<K;k++) { 
		 double finalcost=0;
		  for(int i:N.keySet()) {
			  for(int j:N.keySet()) {
				  if(i != j) {
					  if(cplex.getValue(x[i][j][k])>=0.99 && cplex.getValue(x[i][j][k])<=1.01) {
						  System.out.println("x."+i+"."+j+"."+k+":"+cplex.getValue(x[i][j][k]));	
						  finalcost=finalcost+c[i][j];
						  //System.out.println(i+"--"+j+":"+t[i][j]);
					  }
				  }
				  }
			  }
		  System.out.println("routecost."+k+":"+finalcost);
		  }
	 
	 for(int i=0;i<K;i++) {
	 System.out.println("t0"+cplex.getValue(Tl[i]));
	 }
	 for(int i:P.keySet()) {		
		 double tempt = cplex.getValue(Tb[i]);
		 System.out.println("t."+i+"="+tempt);
		 //double tempr = tempt%60;
		 //System.out.println(tempt/60);
		 //double temps = (tempt-tempr)/60;
				 //System.out.println("t."+i+"="+temps+":"+tempr);
	 }
	 
	    for(int i=0;i<K;i++) {
		 System.out.println("T"+cplex.getValue(Tr[i]));
		 }
	    for(int i:Pa.keySet()) {
	    	System.out.println("Y."+i+":"+cplex.getValue(Y[i]));
	    }
		  //System.out.println("obj:"+cplex.getObjValue());*/
		  routecost=cplex.getObjValue();
		  return true;
		  }else {
			  System.out.println("problem does not have feasible solution");
			  return false;
		  }
		  //cplex.exportModel("equation.lp");
		}catch (IloException e) {				
			}
	return false;
	}

	public void solves() {
		try { 
			
			cplex.exportModel("InitialFproblem.lp");
			 if( cplex.solve()==true) {
				 System.out.println("initiate solved");
				  
			 for(int k=0;k<K;k++) { 
				 double finalcost=0;
				  for(int i:N.keySet()) {
					  for(int j:N.keySet()) {
						  if(i != j) {
							  if(cplex.getValue(x[i][j][k])>=0.99 && cplex.getValue(x[i][j][k])<=1.01) {
								  System.out.println("x."+i+"."+j+"."+k+":"+cplex.getValue(x[i][j][k]));	
								  finalcost=finalcost+c[i][j];
								  //System.out.println(i+"--"+j+":"+t[i][j]);
							  }
						  }
						  }
					  }
				  System.out.println("routecost."+k+":"+finalcost);
				  }
			 /*
			 for(int i=0;i<K;i++) {
			 System.out.println("t0"+cplex.getValue(Tl[i]));
			 }
			 for(int i:P.keySet()) {		
				 double tempt = cplex.getValue(Tb[i]);
				 System.out.println("t."+i+"="+tempt);
				 //double tempr = tempt%60;
				 //System.out.println(tempt/60);
				 //double temps = (tempt-tempr)/60;
						 //System.out.println("t."+i+"="+temps+":"+tempr);
			 }
			 
			 for(int i=0;i<K;i++) {
				 System.out.println("T"+cplex.getValue(Tr[i]));
				 }
				  //System.out.println("obj:"+cplex.getObjValue());*/
				  //routecost=cplex.getObjValue();
			 System.out.println("lp obj:"+cplex.getObjValue());
				  }else {
					  System.out.println("not solved");
				  }
		}catch (IloException e) {						
		}
	}
    double getcost() {
    	return routecost;
    }
    public double[] outputcost() {
    	double [] temp = new double[K+1];
    	try {
    		for(int k=0;k<K;k++) {
    			double cost=0;
    			for(int i:N.keySet()) {
    				for(int j:N.keySet()) {
    					if(i != j &&cplex.getValue(x[i][j][k])>=0.99 && cplex.getValue(x[i][j][k])<=1.01) {
    						cost = cost+c[i][j];
    					}
    				}
    			}
    			temp[k]=cost;
    		}
    	}catch(IloException e) {		
    	}
    	return temp;
    }
    public void output() {
   	 for(int k=0;k<K;k++) {
   		 
   		  for(int i:N.keySet()) {
   			  for(int j:N.keySet()) {
   					  try {
   						  
   						if( i!= j &&cplex.getValue(x[i][j][k])>=0.99 && cplex.getValue(x[i][j][k])<=1.01) {
   							  System.out.println("x."+i+"."+j+"."+k+":"+cplex.getValue(x[i][j][k]));	
   							 
   						  }
   					} catch (UnknownObjectException e) {
   						// TODO Auto-generated catch block
   						e.printStackTrace();
   					} catch (IloException e) {
   						// TODO Auto-generated catch block
   						e.printStackTrace();
   					}
   				  }
   			  }
   		
   		  }
   }
    public void getfeassol() {
    	try {
    		cplex.setParam(IloCplex.Param.MIP.Limits.Solutions, 1);
    	} catch (IloException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
    public double[][] outputmatrix(){
    	double[][] temp = new double[n+1][K+1];
    	try {		
    		for(int k=0;k<K;k++) {
    			for(int i:Pt.keySet()) {
    				for(int j:N.keySet()) {
    					if(i !=j &&cplex.getValue(x[i][j][k])>=0.99 && cplex.getValue(x[i][j][k])<=1.01) {
    						temp[i][k]=1;
    					}
    				}
    			}
    		}		
    	}catch(IloException e) {
    		
    	}
    	
    	return temp;
    }

}


