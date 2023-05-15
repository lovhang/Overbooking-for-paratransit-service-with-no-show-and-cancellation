

public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		network exa = new network();
	     exa.generateRandomNetwork(5,20,2);
	     //exa.scenarioInput(senario);
	     model exa1 = new model();
	     exa1.creatModel(exa);
	     exa1.solve();
/*
		System.out.println("probability-->"+probability);
		a.InitializationSpecifically();
		a.creatModel(new int[] {1,3,4}, new int[] {5,7,8}, new int[] {0,1,3,4,5,7,8,9},2);
		totalcost = totalcost + (-a.getcost()+100)*0.5*0.6*probability;
		System.out.println(totalcost);
		a.creatModel(new int[] {1,3}, new int[] {5,7}, new int[] {0,1,3,5,7,9},1);
		totalcost = totalcost + (-a.getcost()+200)*0.5*0.4*probability;
		a.creatModel(new int[] {1,4}, new int[] {5,8}, new int[] {0,1,4,5,8,9},1);
		totalcost = totalcost + (-a.getcost()+200)*0.5*0.6*probability;
		a.creatModel(new int[] {3,4}, new int[] {7,8}, new int[] {0,3,4,7,8,9},1);
		totalcost = totalcost + (-a.getcost()+200)*0.5*0.4*(1-probability);
		a.creatModel(new int[] {1}, new int[] {5}, new int[] {0,1,5,9},1);
		totalcost = totalcost + (-a.getcost()+100)*0.5*0.4*probability;
		a.creatModel(new int[] {3}, new int[] {7}, new int[] {0,3,7,9},1);
		totalcost = totalcost + (-a.getcost()+100)*0.5*0.4*(1-probability);
		a.creatModel(new int[] {4}, new int[] {8}, new int[] {0,4,8,9},1);
		totalcost = totalcost + (-a.getcost()+100)*0.5*0.6*(1-probability);
      
        model b = new model();
        a.creatModel(new int[] {3,4}, new int[] {7,8},new int[] {0,3,4,7,8,9} ,2);
        double precost=0;
        precost=precost+(-a.getcost()+200)*0.5*0.6;
        a.creatModel(new int[] {4}, new int[] {8},new int[] {0,4,8,9} ,2);
        precost=precost+(-a.getcost()+100)*0.5*0.6;
        a.creatModel(new int[] {3}, new int[] {7},new int[] {0,3,7,9} ,2);
        precost=precost+(-a.getcost()+100)*0.5*0.4;
        System.out.println("totalcost--"+totalcost);
        System.out.println("precost--"+precost);
        */
	}

}
