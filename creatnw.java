
public class creatnw {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
           network nw = new network();
           nw.readexcel("20100113.xlsx", 3, 30);
           nw.savenw("20100113uc.txt");
	}

}
