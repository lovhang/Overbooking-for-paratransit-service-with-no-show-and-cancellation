import java.util.ArrayList;
import java.util.Arrays;

import ilog.concert.IloNumVar;

public class clusterDriver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int num_node = 50;
		int num_bus =10;
		int bus_cap=3;
		int[] array_noshow=new int[] {};
		int[] array_cancel=new int[] {};
		ArrayList<ArrayList<Integer>> cluster;
		 network exa = new network();
         exa.generateRandomNetwork(num_node,bus_cap,num_bus);
         //exa.creatnew(array_noshow, array_cancel);
         exa.savenw("cluster.txt");
         
         exa.readnw("network.txt");
         exa.creatnew(array_noshow, array_cancel);
         exa.outprtnw();
         cluster = exa.cluster();
         //exa.outprtnw();
         
         ArrayList<Integer> busnumarr = new ArrayList<Integer>(Arrays.asList(2,1,1));
         //System.out.println(colexam.isneighbour(1, 4));
         ColumnGeneration colexam0 = new ColumnGeneration(exa);
         colexam0.testDP(num_bus);
         
         for(int i=0;i<cluster.size();i++) {
        	 System.out.println("==============solve cluster "+i+" ===============");
        	 //System.out.println(cluster.get(i));
        	 exa.changeclusternw(cluster.get(i),busnumarr.get(i));
        	 ColumnGeneration colexam1 = new ColumnGeneration(exa);
        	 colexam1.testDP(num_bus);
         }
     
	}

}
