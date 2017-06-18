import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class NodeServer {

	public NodeServer() {
		
	}
	
	public void execute(){
		runNodeServer();
		runUpdateServer();
	}
	
	//start server for receiving updates fro other peers
	private void runUpdateServer() {
		(new Thread() {
            @Override
            public void run() {
            	ServerSocket serverSocket = null;
        		Socket socket = null;
        		OutputStream out = null;
        		InputStream in = null;
        		
        	        
        	    while(true) {
        	      	try {
        	      		serverSocket = new ServerSocket(6666);
        		        socket = serverSocket.accept();
        			        
        		        //save received update into file in chronological order
        		        in = socket.getInputStream();
        		        out = new FileOutputStream(new Date().getTime() + "updates.txt");
		                byte[] bytes = new byte[16*1024];

		                int count;
		                while ((count = in.read(bytes)) > 0) {
		                    out.write(bytes, 0, count);
		                }
		                
		                
                	} catch (Exception e) {
       	        		e.printStackTrace();
       	        	} finally {
       	        		try {
    				        if (out != null) out.close();
    				        if (in != null) in.close();
    				        if (socket != null) socket.close();
    				        if (serverSocket != null) serverSocket.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
       	        	}
       	        }
            }
        }).start();
	}
	
	

	//start p2p node, this waits for anyone to connect and ask for a copy of the blockchain
		private static void runNodeServer() {
			 (new Thread() {
		            @Override
		            public void run() {
		            	ServerSocket serverSocket = null;
		        		Socket socket = null;
		        		OutputStream out = null;
		        		InputStream in = null;
		        		BufferedWriter writer = null;
		        		
		        	        
		        	        while(true) {
		        	        	try {
		        	        
		        	        		serverSocket = new ServerSocket(5555);
		        			        socket = serverSocket.accept();
		        			        String IP = socket.getInetAddress().toString() + socket.getPort();
		        			        File file = new File("history.txt");
		        			      
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
		        		        	try {
		        		        		if (writer != null) writer.close();
		        				        if (out != null) out.close();
		        				        if (in != null) in.close();
		        				        if (socket != null) socket.close();
		        				        if (serverSocket != null) serverSocket.close();
									} catch (Exception e) {
										e.printStackTrace();
									}
		        				}
		        	        }
		            }
		        }).start();
		}
}
