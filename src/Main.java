import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		HistoryBC blockchain = HistoryBC.getInstance();

		while(true){
			System.out.println("Enter command in the following format: '[ADD/SIGN/REMV] [String[] or String] [int index] [int koins]' " );
			try {
				String s = scanner.nextLine();
				blockchain.add(CommandParser.parse(s));
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			System.out.println(blockchain.toString());
		}
		
		System.out.println("end");

		
	}
	
	
}
