import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		try {
				
			// we're generating new KeyPair for every user just for testing, in reality users will
			// have to login with their pub-priv key to keep the record of their koins, etc
			// we now have infinite number of koins at the moment, there's no limit
			// since the PoW will regain the same no of koin spent anyway, in reality -> another mining function
			//write details in report 
				
			 KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
	         SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
	
	         keyGen.initialize(512, random);
	
	         KeyPair pair = keyGen.generateKeyPair();
	         PrivateKey priv = pair.getPrivate();
	         PublicKey pub = pair.getPublic();
	         
	         System.out.println(Base64.getEncoder().encodeToString(pub.getEncoded()));
	    
	         

	         
	      /* Store Public Key. 
	 		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
	 				pub.getEncoded());
	 		FileOutputStream fos = new FileOutputStream("public.txt");
	 		fos.write(x509EncodedKeySpec.getEncoded());
	 		fos.close();
	  
	 		// Store Private Key.
	 		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
	 				priv.getEncoded());
	 		fos = new FileOutputStream("private.txt");
	 		fos.write(pkcs8EncodedKeySpec.getEncoded());
	 		fos.close();*/
			
	         
	         
			Scanner scanner = new Scanner(System.in);
			HistoryBC blockchain = HistoryBC.getInstance();
	
			while(true){
				String s;
				System.out.println("Enter command in the following format: '[ADD/SIGN/REMV] [String[] or String] [int index] [int koins]' " );
				System.out.println("OR type 'break' to break OR 'compile' to compile");
					s = scanner.nextLine();
					if (s.contains("break")) {
						break;
					} else if (s.contains("compile")) {
						//TODO check for any updates/conflicts in the server first, merge changes/ or even just add it on it shouldnt matter
						System.out.println("COMPILING");
						BCCompiler com = new BCCompiler();
						com.compile(blockchain);
						com.showState();
						//TODO push the change to server and GraphDB
					} else {
						try {
							blockchain.add(CommandParser.parse(s, priv, pub));

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				
				System.out.println(blockchain.toString());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//method to verify signature, move to other class later
	public boolean verifySig(byte[] signature, byte[] data, PublicKey pubKey) {
		try {
			Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
	        sig.initVerify(pubKey);
	        sig.update(data);
	        return sig.verify(signature);
		} catch (Exception e) {
        	e.printStackTrace();
        }
		return false;
	}
	
}
