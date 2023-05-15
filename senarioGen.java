import java.util.HashMap;

import ilog.concert.IloException;

public class senarioGen {
	private int[] senario;
	private int node_num;
	private int num_bus;
	private HashMap<Integer,Double> prob;//probability list
 public senarioGen() {
	 prob = new HashMap<Integer,Double>();
	 prob.put(1,0.3);
	 prob.put(2,0.4);
	 prob.put(3, 0.6);
	 prob.put(4, 0.8);
	 prob.put(5, 0.4);
	 num_bus=2;
	 node_num=5;
 }
 public void scenarioA() {

	 senario = new int[] {1,0,1,1,0};
	 network exa = new network();
     exa.generateRandomNetwork(node_num,20,num_bus);
     exa.scenarioInput(senario);
     model exa1 = new model();
     exa1.creatModel(exa);
     exa1.solve();
 }
 public double probforS(int[] scenario) {
	 double temp=1;
	 for(int i=0;i<node_num;i++) {
	 	 if(scenario[i]==1) {
	 		 temp = prob.get(i+1)*temp;
	 		 //System.out.println(temp+"");
	 	 }
	 	 else {
	 		 temp = (1-prob.get(i+1))*temp;
	 	 }
	 }
	 return temp;
 }   
 public static void main(String[] args) throws IloException {
	 for(int j=0;j<5;j++) {
		 System.out.println("random set:"+j);
	 for(int i=0;i<5;i++) {
		 double a = Math.random(); 
		 System.out.println(i+"=="+a);
	 }
	 }
	senarioGen sce = new senarioGen();
	//exa.scenarioA();
	 int bus_num =2;
	int node_num = 5;
	model mod = new model();
	network exa = new network();
	exa.generateRandomNetwork(node_num, 20,bus_num);
//11111	
	int[] s1 = new int[]{1,1,1,1,1};
	exa.scenarioInput(s1);
    mod.creatModel(exa);
    mod.solve();
    double cost1 = mod.getcost();
    double p1 = sce.probforS(s1);
    System.out.print("cost1 :" + cost1+"prob1 :"+p1);
//11110    
	int[] s2 = new int[]{1,1,1,1,0};
	exa.scenarioInput(s2);
    mod.creatModel(exa);
    mod.solve();
    double cost2 = mod.getcost();
    double p2 = sce.probforS(s2);
    System.out.print("cost2 :" + cost2+"prob2 :"+p2);
//11101   
    int[] s3 = new int[]{1,1,1,0,1};
	exa.scenarioInput(s3);
    mod.creatModel(exa);
    mod.solve();
    double cost3 = mod.getcost();
    double p3 = sce.probforS(s3);
    System.out.print("cost3 :" + cost3+"prob3 :"+p3);
//11011 
    int[] s4 = new int[]{1,1,0,1,1};
	exa.scenarioInput(s4);
    mod.creatModel(exa);
    mod.solve();
    double cost4 = mod.getcost();
    double p4 = sce.probforS(s4);
    System.out.print("cost4 :" + cost4+"prob4 :"+p4);
//10111 
    int[] s5 = new int[]{1,0,1,1,1};
	exa.scenarioInput(s5);
    mod.creatModel(exa);
    mod.outputM();
    mod.solve();
    double cost5 = mod.getcost();
    double p5 = sce.probforS(s5);
    System.out.print("cost5 :" + cost5+"prob5 :"+p5);
 //01111 
    int[] s6 = new int[]{0,1,1,1,1};
	exa.scenarioInput(s6);
    mod.creatModel(exa);
    mod.solve();
    double cost6 = mod.getcost();
    double p6 = sce.probforS(s6);
    System.out.print("cost6 :" + cost6+"prob6 :"+p6);
//11100 
    int[] s7 = new int[]{1,1,1,0,0};
	exa.scenarioInput(s7);
    mod.creatModel(exa);
    mod.solve();
    double cost7 = mod.getcost();
    double p7 = sce.probforS(s7);
    System.out.print("cost7 :" + cost7+"prob7 :"+p7);
//11001
    int[] s8 = new int[]{1,1,0,0,1};
	exa.scenarioInput(s8);
    mod.creatModel(exa);
    mod.solve();
    double cost8 = mod.getcost();
    double p8 = sce.probforS(s8);
    System.out.print("cost8 :" + cost8+"prob8 :"+p8);
//10011    
    int[] s9 = new int[]{1,0,0,1,1};
	exa.scenarioInput(s9);
    mod.creatModel(exa);
    mod.solve();
    double cost9 = mod.getcost();
    double p9 = sce.probforS(s9);
    System.out.print("cost9 :" + cost9+"prob9 :"+p9);
//00111
    int[] s10 = new int[]{0,0,1,1,1};
	exa.scenarioInput(s10);
    mod.creatModel(exa);
    mod.solve();
    double cost10 = mod.getcost();
    double p10 = sce.probforS(s10);
    System.out.print("cost10 :" + cost10+"prob10 :"+p10);
//01011
    int[] s11 = new int[]{0,1,0,1,1};
	exa.scenarioInput(s11);
    mod.creatModel(exa);
    mod.solve();
    double cost11 = mod.getcost();
    double p11 = sce.probforS(s11);
    System.out.print("cost11 :" + cost11+"prob11 :"+p11);
//10101
    int[] s12 = new int[]{1,0,1,0,1};
   	exa.scenarioInput(s12);
       mod.creatModel(exa);
       mod.solve();
       double cost12 = mod.getcost();
       double p12 = sce.probforS(s12);
       System.out.print("cost12 :" + cost12+"prob12 :"+p12);
//11010
       int[] s13 = new int[]{1,1,0,1,0};
      	exa.scenarioInput(s13);
          mod.creatModel(exa);
          mod.solve();
          double cost13 = mod.getcost();
          double p13 = sce.probforS(s13);
          System.out.print("cost13 :" + cost13+"prob13 :"+p13);
//01101          
          int[] s14 = new int[]{0,1,1,0,1};
        	exa.scenarioInput(s14);
            mod.creatModel(exa);
            mod.solve();
            double cost14 = mod.getcost();
            double p14 = sce.probforS(s14);
            System.out.print("cost14 :" + cost14+"prob14 :"+p14);
//10110            
            int[] s15 = new int[]{1,0,1,1,0};
        	exa.scenarioInput(s15);
            mod.creatModel(exa);
            mod.solve();
            double cost15 = mod.getcost();
            double p15 = sce.probforS(s15);
            System.out.print("cost15 :" + cost15+"prob15 :"+p15);
//01110
            int[] s16 = new int[]{0,1,1,1,0};
        	exa.scenarioInput(s16);
            mod.creatModel(exa);
            mod.solve();
            double cost16 = mod.getcost();
            double p16 = sce.probforS(s16);
            System.out.print("cost16 :" + cost16+"prob16 :"+p16);
//11000            
            int[] s17 = new int[]{1,1,0,0,0};
        	exa.scenarioInput(s17);
            mod.creatModel(exa);
            mod.solve();
            double cost17 = mod.getcost();
            double p17 = sce.probforS(s17);
            System.out.print("cost17 :" + cost17+"prob17 :"+p17);
//01100            
            int[] s18 = new int[]{0,1,1,0,0};
        	exa.scenarioInput(s18);
            mod.creatModel(exa);
            mod.solve();
            double cost18 = mod.getcost();
            double p18 = sce.probforS(s18);
            System.out.print("cost18 :" + cost18+"prob18 :"+p18);
//00110            
            int[] s19 = new int[]{0,0,1,1,0};
        	exa.scenarioInput(s19);
            mod.creatModel(exa);
            mod.solve();
            double cost19 = mod.getcost();
            double p19 = sce.probforS(s19);
            System.out.print("cost19 :" + cost19+"prob19 :"+p19);
//00011
            int[] s20 = new int[]{0,0,0,1,1};
        	exa.scenarioInput(s20);
            mod.creatModel(exa);
            mod.solve();
            double cost20 = mod.getcost();
            double p20 = sce.probforS(s20);
            System.out.print("cost20 :" + cost20+"prob20 :"+p20);
//10100
            int[] s21 = new int[]{1,0,1,0,0};
        	exa.scenarioInput(s21);
            mod.creatModel(exa);
            mod.solve();
            double cost21 = mod.getcost();
            double p21 = sce.probforS(s21);
            System.out.print("cost21 :" + cost21+"prob21 :"+p21);
//01010            
            int[] s22 = new int[]{0,1,0,1,0};
        	exa.scenarioInput(s22);
            mod.creatModel(exa);
            mod.solve();
            double cost22 = mod.getcost();
            double p22 = sce.probforS(s22);
            System.out.print("cost22 :" + cost22+"prob22 :"+p22);
//00101            
            int[] s23 = new int[]{0,0,1,0,1};
        	exa.scenarioInput(s23);
            mod.creatModel(exa);
            mod.solve();
            double cost23 = mod.getcost();
            double p23 = sce.probforS(s23);
            System.out.print("cost23 :" + cost23+"prob23 :"+p23);
//10010            
            int[] s24 = new int[]{1,0,0,1,0};
        	exa.scenarioInput(s24);
            mod.creatModel(exa);
            mod.solve();
            double cost24 = mod.getcost();
            double p24 = sce.probforS(s24);
            System.out.print("cost24 :" + cost24+"prob24 :"+p24);
//01001            
            int[] s25 = new int[]{0,1,0,0,1};
        	exa.scenarioInput(s25);
            mod.creatModel(exa);
            mod.solve();
            double cost25 = mod.getcost();
            double p25 = sce.probforS(s25);
            System.out.print("cost25 :" + cost25+"prob25 :"+p25);
//10001
            int[] s26 = new int[]{1,0,0,0,1};
        	exa.scenarioInput(s26);
            mod.creatModel(exa);
            mod.solve();
            double cost26 = mod.getcost();
            double p26 = sce.probforS(s26);
            System.out.print("cost26 :" + cost26+"prob26 :"+p26);
            bus_num=1;
//10000
            int[] s27 = new int[]{1,0,0,0,0};
        	exa.scenarioInput(s27);
            mod.creatModel(exa);
            mod.solve();
            double cost27 = mod.getcost();
            double p27 = sce.probforS(s27);
            System.out.print("cost27 :" + cost27+"prob27 :"+p27);
//01000            
            int[] s28 = new int[]{0,1,0,0,0};
        	exa.scenarioInput(s28);
            mod.creatModel(exa);
            mod.solve();
            double cost28 = mod.getcost();
            double p28 = sce.probforS(s28);
            System.out.print("cost28 :" + cost28+"prob28 :"+p28);
//00100            
            int[] s29 = new int[]{0,0,1,0,0};
        	exa.scenarioInput(s29);
            mod.creatModel(exa);
            mod.solve();
            double cost29 = mod.getcost();
            double p29 = sce.probforS(s29);
            System.out.print("cost29 :" + cost29+"prob29 :"+p29);
//00010            
            int[] s30 = new int[]{0,0,0,1,0};
        	exa.scenarioInput(s30);
            mod.creatModel(exa);
            mod.solve();
            double cost30 = mod.getcost();
            double p30 = sce.probforS(s30);
            System.out.print("cost30 :" + cost30+"prob30 :"+p30);
//00001
            int[] s31 = new int[]{0,0,0,0,1};
        	exa.scenarioInput(s31);
            mod.creatModel(exa);
            mod.solve();
            double cost31 = mod.getcost();
            double p31 = sce.probforS(s31);
            System.out.println("cost31 :" + cost31+"prob31 :"+p31);
//00000
            int[] s32 = new int[]{0,0,0,0,0};
            double cost32 = 0;
            double p32 = sce.probforS(s32);
            System.out.print("cost32 :" + cost32+"prob32 :"+p32);
            
            double expectedcost = cost1*p1+cost2*p2+cost3*p3+cost4*p4+cost5*p5+cost6*p6+cost7*p7+cost8*p8+cost9*p9+cost10*p10+
            		cost11*p11+cost12*p12+cost13*p13+cost14*p14+cost15*p15+cost16*p16+cost17*p17+cost18*p18+cost19*p19+cost20*p20+cost21*p21+cost22*p22+
            		cost23*p23+cost24*p24+cost25*p25+cost26*p26+cost27*p27+cost28*p28+cost29*p29+cost30*p30+cost31*p31+cost32*p32;
            System.out.println("expected cost:"+expectedcost);
    }	 
 }   


