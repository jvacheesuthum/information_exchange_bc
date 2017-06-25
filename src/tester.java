import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

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
	// public static void main(String[] args) throws Exception {

		 
	  //  }
	 
	 public static boolean isThisMyIpAddress(InetAddress addr) {
		    // Check if the address is a valid special local or loop back
		    if (addr.isAnyLocalAddress() || addr.isLoopbackAddress())
		        return true;

		    // Check if the address is defined on any interface
		    try {
		        return NetworkInterface.getByInetAddress(addr) != null;
		    } catch (SocketException e) {
		        return false;
		    }
		}

	 

	    public static void startSender() {
	        (new Thread() {
	            @Override
	            public void run() {
	            	Socket socket = null;
	                String host = "/127.0.0.1";
	                InputStream in = null;
	                OutputStream out = null;
	                
	                try {
	                    socket = new Socket(host, 17724);
	    		        out = socket.getOutputStream();
	    		        String ip = "12.12.12.66ddsdasdsssssssssssssssaf666";
	    		        out.write(ip.getBytes());
	                    
	                } catch (Exception e) {
	                	e.printStackTrace();
	                } finally {
	                	try {
	    			        if (out != null) out.close();
	    			        if (socket != null) socket.close();
	                	} catch (Exception e) {
	                		e.printStackTrace();
	                	}
	                }
	            }
	        }).start();
	    }

	    
	    
	    
	    public static void startServer() throws Exception{
	        (new Thread() {
	            @Override
	            public void run() {
	            	ServerSocket newpeerServerSocket = null;
	        		Socket socket = null;
	        		OutputStream out = null;
	        		InputStream in = null;
	        		
	        	        
	        		try {
						newpeerServerSocket = new ServerSocket(17724);
					} catch (IOException e1) {
						e1.printStackTrace();
						System.exit(MAX_PRIORITY);
					}

	        		try {
		        	      		System.out.println(newpeerServerSocket.isClosed());
		        		        socket = newpeerServerSocket.accept();
		        			        
		        		        //save received update into file in chronological order
		        		        in = socket.getInputStream();
				                byte[] ip = new byte[16*1024];
		        		        in.read(ip);
		        		        		
		        		      //record this client IP to the file
	        		           	FileWriter fstream = new FileWriter("nodeaddr.txt", true); //true = appends
	        		            BufferedWriter writer = new BufferedWriter(fstream);
	        		            writer.write(new String(ip, "UTF-8"));
	        		            writer.newLine();
	        		            writer.close();
				                
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		} finally {
	        			try {
	        				if (out != null) out.close();
	        				if (in != null) in.close();
	        				if (socket != null) socket.close();
	        				if (newpeerServerSocket != null) newpeerServerSocket.close();
	        			} catch (Exception e) {
	        				e.printStackTrace();
	        			}
	       	        }
	            }
	            
	        }).start();
	    }

}
