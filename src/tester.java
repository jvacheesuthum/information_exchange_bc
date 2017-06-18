import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class tester {

	/*public static void main(String[] args) throws Exception {
		/*Scanner sc = new Scanner(System.in);
		String filename = sc.nextLine();
		FileInputStream input = new FileInputStream(filename);

		Model model = Rio.parse(input, "", RDFFormat.RDFXML);
		System.out.println(model.size());
		int count =0;
		for (Statement v : model) {
			
			count += 3;
		}
		System.out.println(count);
		
		Runnable r = new Runnable() {
	         public void run() {
	        	 int a =0;
	             while(true){
	            	 try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	            	 a++;
	            	 System.out.println(a);
	             }
	         }
	     };
	     
	     Runnable v = new Runnable() {
	         public void run() {
	        	 int a =0;
	             while(true){
	            	 try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	            	 a++;
	            	 System.out.println(a);
	             }
	         }
	     };

	     new Thread(r).start();
	     new Thread(v).start();

		
	     int a =0;
         while(true){
        	 Thread.sleep(3000);
        	 a++;
        	 System.out.println("aaaaa" + a);
         }

	
	}*/
	 public static void main(String[] args) throws Exception {

		 System.out.println(new Date().getTime());
	        //startServer();
	       // startSender();
	    }

	    public static void startSender() {
	        (new Thread() {
	            @Override
	            public void run() {
	            	Socket socket = null;
	                String host = "127.0.0.1";
	                InputStream in = null;
	                OutputStream out = null;
	                
	                try {
		                socket = new Socket(host, 4444);
	
		                in = socket.getInputStream();
		                out = new FileOutputStream("nodeaddressesRRRxxx.txt");
		                byte[] bytes = new byte[16*1024];

		                int count;
		                while ((count = in.read(bytes)) > 0) {
		                    out.write(bytes, 0, count);
		                }

		                out.close();
		                in.close();
		                socket.close();
	
		                
	                } catch (Exception e) {
	                	e.printStackTrace();
	                }
	            }
	        }).start();
	    }

	    
	    
	    
	    public static void startServer() throws Exception{
	        (new Thread() {
	            @Override
	            public void run() {
	            	ServerSocket serverSocket = null;
	        		Socket socket = null;
	        		OutputStream out = null;
	        		InputStream in = null;
	        		BufferedWriter writer = null;
	        		
	        	        try {
							serverSocket = new ServerSocket(4444);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	        	        
	        	        while(true) {
	        	        	try {
	        	        
	        			        socket = serverSocket.accept();
	        			        String IP = socket.getInetAddress().toString() + socket.getPort();
	        			        File file = new File("nodeaddresses.txt");
	        			      
	        			        // Get the size of the file
	        			        long length = file.length();
	        			        
	        			        if (length > Integer.MAX_VALUE) {
	        			            System.out.println("File is too large.");
	        			        }
	        			        
	        			        byte[] bytes = new byte[(int) length];
	        			        
	        			        in = new FileInputStream(file);
	        			        out = socket.getOutputStream();
	        			
	        			        int count;
	        			        
	        			        while ((count = in.read(bytes)) > 0) {
	        			            out.write(bytes, 0, count);
	        			        }
	        			        
	        			        //record this client IP to the file
	        		           	FileWriter fstream = new FileWriter("nodeaddresses.txt", true); //true = appends
	        		            writer = new BufferedWriter(fstream);
	        		            writer.write(IP);
	        		            writer.newLine();
	        		           	
	        		        } catch (Exception e) {
	        		        	e.printStackTrace();
	        		        } finally {
	        		        	try{
	        		        	if (writer != null) writer.close();
	        			        if (out != null) out.close();
	        			        if (in != null) in.close();
	        			        if (socket != null) socket.close();
	        			        if (serverSocket != null) serverSocket.close();}catch (Exception e){
	        			        	
	        			        }
	        				}
	        		
	        	        }
	            }
	        }).start();
	    }

}
