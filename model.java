
import java.util.HashMap;
import java.util.TreeMap;

import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;

public class model {
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
private TreeMap<Integer,Integer> Pa;
private TreeMap<Integer,Integer> Pb;
private TreeMap<Integer,Integer> P;
private TreeMap<Integer,Integer> N;
private double routecost;
public model( ){	

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
	public void InitializationSpecifically(){
		
		Q=200;
		L=500;
		T=500;
		n=4;
		//N=2*n+1;
		distance=new double[2*n+2][2*n+2];
		d = new double[2*n+2]; //load
		s = new double[2*n+2]; // service time
		t = new double[2*n+2][2*n+2];
		c = new double[2*n+2][2*n+2]; 
		a = new double[2*n+2];
		b = new double[2*n+2];
		//P = new int[] {3,4};
		//D = new int[] {7,8};
		//N = new int[] {0,1,2,3,4,5,6,7,8,9};
		double x[]= new double[2*n+2];
		double y[]= new double[2*n+2];
		x[0]=50;
		x[1]=50;
		//x[1]=(Math.random()*(100)+1);
		
		x[2]=70;
		x[3]=10;
		x[4]=30;
		x[5]=60;
		x[6]=80;
		x[7]=40;
		x[8]=20;
		x[9]=50;
		
		y[0]=50;
		y[1]=40;
		y[2]=30;
		y[3]=80;
		y[4]=70;
		y[5]=40;
		y[6]=75;
		y[7]=20;
		y[8]=40;
		y[9]=50;
		System.out.println("x-->"+x[1]+"||y-->"+y[1]);
		a[0]=0;
		a[1]=40;
		a[2]=70;
		a[3]=50;
		a[4]=110;
		a[5]=140;
		a[6]=180;
		a[7]=180;
		a[8]=210;
		a[9]=400;
		
		b[0]=0;
		b[1]=60;
		b[2]=100;
		b[3]=80;
		b[4]=140;
		b[5]=200;
		b[6]=220;
		b[7]=220;
		b[8]=240;
		b[9]=500;
		
		s[0]=0;
		s[1]=30;
		s[2]=25;
		s[3]=18;
		s[4]=25;
		s[5]=15;
		s[6]=20;
		s[7]=15;
		s[8]=10;
		s[9]=0;
		
		d[0]=0;
		d[1]=0;
		d[2]=40;
		d[3]=50;
		d[4]=60;
		d[5]=0;
		d[6]=-40;
		d[7]=-50;
		d[8]=-60;
		d[9]=0;
		
		for(int i:N.keySet()) {
			for(int j:N.keySet()) {
				t[i][j]=(Math.abs(x[i]-x[j])+Math.abs(y[i]-y[j]))/10;
				c[i][j]=Math.sqrt((x[i]-x[j])*(x[i]-x[j])+(y[i]-y[j])*(y[i]-y[j]));				
			}
		}
		
	}
	public void creatModel(network nw){
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
		
		routecost=0;
		try{			
			double M=10000;
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
						x[i][j][k]=cplex.boolVar();
						x[i][j][k].setName("x."+i+"."+j+"."+k);  
					}
				}
			}
			/*
			for(int i:N) {
				for(int k=0;k<K;k++) {
					B[i][k]=cplex.numVar(0, Double.MAX_VALUE);
					B[i][k].setName("B."+i+"."+k); 
					Qv[i][k]=cplex.numVar(0, Double.MAX_VALUE);
					Qv[i][k].setName("Qv."+i+"."+k); 
					Lv[i][k]=cplex.numVar(0, Double.MAX_VALUE);
					Lv[i][k].setName("Lv."+i+"."+k);
				}
			}	*/
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
			
//objective function		 
			for(int i:N.keySet()) {
		        for(int k=0;k<K;k++) {			  
				   for(int j:N.keySet()) {
					   obj.addTerm( c[i][j],x[i][j][k] );
				   }
			   }
		   }	   
		   cplex.addMinimize(obj);
//(6)	
		   for(int i:Pa.keySet()) {
			   expr=cplex.linearNumExpr();
			   for(int k=0;k<K;k++) {
				   for(int j:N.keySet()) {
					   expr.addTerm(1.0, x[i][j][k]);
				   }
			   }
			   cplex.addLe(expr, 1);
		   }

//(7)    
		  
		   for(int i:P.keySet()) {
			   for(int k=0;k<K;k++) {
				   expr=cplex.linearNumExpr();
				   for(int j:N.keySet()) {
					   expr.addTerm(1.0, x[i][j][k]);
					   expr.addTerm(-1.0,x[j][i][k]);
				   }				   
				   cplex.addEq(expr, 0);
			   }
			   //System.out.println("number"+i);
		   }
//(8)
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
					   expr.addTerm(1.0, x[i][j][k]);
				   }
				   for(int j:N.keySet()) {
					   expr.addTerm(-1.0, x[j][n+i][k]);
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
					   expr = cplex.linearNumExpr();
					   expr.addTerm(1.0, Tb[i]);
					   expr.addTerm(-1.0, Tb[j]);
					   expr.addTerm(M,x[i][j][k] );
					   cplex.addLe(expr, M-s[i]-t[i][j]);
				   }
			   }
		   }
//(13)
		   for(int j:Pa.keySet()) {
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
			   for(int j:Pa.keySet()) {
				   for(int k=0;k<K;k++) {
					   expr=cplex.linearNumExpr();
					   expr.addTerm(M, x[i][j][k]);
					   expr.addTerm(1.0, Y[j]);
					   expr.addTerm(-1.0, Y[i]);
					   cplex.addLe(expr, M+d[j]);
				   }
			   }
		   }		   
		   for(int i:P.keySet()) {
			   for(int j:Pa.keySet()) {
				   for(int k=0;k<K;k++) {
					   expr=cplex.linearNumExpr();
					   expr.addTerm(M, x[i][j][k]);
					   expr.addTerm(-1.0, Y[j]);
					   expr.addTerm(1.0, Y[i]);
					   cplex.addLe(expr, M-d[j]);
				   }
			   }
		   }
//(19)
		   for(int i:P.keySet()) {
			   for(int j:Pb.keySet()) {
				   for(int k=0;k<K;k++) {
					   expr=cplex.linearNumExpr();
					   expr.addTerm(M, x[i][j][k]);
					   expr.addTerm(1.0, Y[j]);
					   expr.addTerm(-1.0, Y[i]);
					   cplex.addLe(expr, M-d[j-n]);			
				   }
			   }
		   }		   
		   for(int i:P.keySet()) {
			   for(int j:Pb.keySet()) {
				   for(int k=0;k<K;k++) {
					   expr=cplex.linearNumExpr();
					   expr.addTerm(M, x[i][j][k]);
					   expr.addTerm(-1.0, Y[j]);
					   expr.addTerm(1.0, Y[i]);
					   cplex.addLe(expr, M+d[j-n]);			
				   }
			   }
		   }
              
//(20)
		   
		   for(int j:Pa.keySet()) {
			   for(int k=0;k<K;k++) {
				   expr = cplex.linearNumExpr();
				   expr.addTerm(M, x[0][j][k]);
				   expr.addTerm(1.0, Y[j]);
				   expr.addTerm(-1.0, Y[0]);
				   cplex.addLe(expr, M+d[j]);				   
			   }
		   }
		   
		   for(int j:Pa.keySet()) {
			   for(int k=0;k<K;k++) {
				   expr = cplex.linearNumExpr();
				   expr.addTerm(M, x[0][j][k]);
				   expr.addTerm(-1.0, Y[j]);
				   expr.addTerm(1.0, Y[0]);
				   cplex.addLe(expr, M-d[j]);				   
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
		}catch(IloException e) {			   
	  }
	}
public void outputM() throws IloException {	   
	cplex.exportModel("normalModel.lp");
}
public void solve() {	
	try {
		  if( cplex.solve()==true) {
		  System.out.println("problem solved");
		  //System.out.println("obj:"+cplex.getObjValue());
		  routecost=cplex.getObjValue();
		  }else {
			  System.out.println("not solved");
		  }
		  //cplex.exportModel("equation.lp");
		}catch (IloException e) {
				System.err.println("Concert exception caught: " + e);  				
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
public void output() {
	 for(int k=0;k<K;k++) { 
		  for(int i:N.keySet()) {
			  for(int j:N.keySet()) {
					  try {
						if(cplex.getValue(x[i][j][k])>=0.99 && cplex.getValue(x[i][j][k])<=1.01) {
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
public double[][] outputmatrix(){
	double[][] temp = new double[n+1][K+1];
	try {		
		for(int k=0;k<K;k++) {
			for(int i:Pa.keySet()) {
				for(int j:N.keySet()) {
					if(cplex.getValue(x[i][j][k])>=0.99 && cplex.getValue(x[i][j][k])<=1.01) {
						temp[i][k]=1;
					}
				}
			}
		}		
	}catch(IloException e) {
		
	}
	
	return temp;
}
public double[] outputcost() {
	double [] temp = new double[K+1];
	try {
		for(int k=0;k<K;k++) {
			double cost=0;
			for(int i:N.keySet()) {
				for(int j:N.keySet()) {
					if(cplex.getValue(x[i][j][k])>=0.99 && cplex.getValue(x[i][j][k])<=1.01) {
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
    double getcost() {
    	return routecost;
    }
}
