import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import com.google.common.base.Stopwatch;


public class Main {

	public static void main(String[] args) throws Exception {
		int koins = 0;
		Stopwatch st = Stopwatch.createStarted();
		koins = mineKoins("teststring",koins, 50);
		st.stop();
		System.out.println("mining " + koins + " koins took " + st.elapsed(TimeUnit.MILLISECONDS));
		
		/*
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
	    
	        
	         
	         
			Scanner scanner = new Scanner(System.in);
			HistoryBC blockchain = HistoryBC.getInstance();
	
			while(true){
				System.out.println("Enter command in the following format: '[ADD/SIGN/REMV] [String[] or String] [int index] [int koins]' " );
				System.out.println("OR type 'break' to break OR 'compile' to compile");
				System.out.println("OR type 'RDF' follow by a spcae and filename to upload an ontology ");
				
					String s;
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
					} else if (s.startsWith("RDF")) { 
						//read an ontology in RDFXML format
						//right now only invests 1 in each
						
						String filename = s.replaceFirst("RDF", "").trim();
						
						// read the file 'example-data-artists.ttl' as an InputStream.
						FileInputStream input = new FileInputStream(filename);
						
						// Rio also accepts a java.io.Reader as input for the parser.
						Model model = Rio.parse(input, "", RDFFormat.RDFXML);
						int a = 1;
						for (Statement v : model) {
							a++;
							if (a == 60 ) break;
							//adding all subj - predicate - obj into the blockchain 1 by 1
							blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getSubject()) + " 1", priv, pub));
							blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getPredicate()) + " 1", priv, pub));
							blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getObject()) + " 1", priv, pub));
							//TODO change to URI adding? and add the relationships between them?

						}
						
					} else {
						try {
							blockchain.add(CommandParser.parse(s, priv, pub));

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				
				System.out.println(blockchain.toString());
				
			}
			
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		*/
	}
	
	//extract resouce name form uri after # 
	public static String getResourceName(Object o) {
		String s = o.toString();
		return s.substring(s.lastIndexOf('#') + 1);
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
	
	//function that executes and mint hashcashes in order to accumulate koins for users
	//returns the new current no of koins
	public static int mineKoins(String string, int currentKoins, int koinsToBeMined) throws NoSuchAlgorithmException {
		int level = 7;
		for (int i = 0; i < koinsToBeMined && level < 27; i++) { //if level is ~30 it takes too long
			HashCash h = HashCash.mintCash(string, level);
			//increase the level every 5 koins mined to slowdown process
			if (i % 5 == 0) level ++; 
			currentKoins++;
		}
		return currentKoins;
	}
	
}
