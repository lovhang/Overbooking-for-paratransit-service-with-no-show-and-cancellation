
public class columnGenRun {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       model exa = new model();
       network ntw = new network();
       ntw.generateRandomNetwork(15, 10, 5);
       exa.creatModel(ntw);
       exa.getfeassol();
       exa.solve();
       exa.output();
	}

}
