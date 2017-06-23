import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class NodeServer {

	private volatile Thread updateServer;
	private volatile Thread welcomeServer;
    private boolean terminated;
    private ServerSocket serverSocket;
    private ServerSocket updateServerSocket;

	
	public NodeServer() {
		terminated = false;
		this.updateServer = (new Thread() {
            @Override
            public void run() {
            	System.out.println("uodateserver started");
            	updateServerSocket = null;
        		Socket socket = null;
        		OutputStream out = null;
        		InputStream in = null;
        		
        	        
        		try {
					updateServerSocket = new ServerSocket(11411);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(MAX_PRIORITY);
				}

        		try {
	        	    while(!terminated) {
	        	      		System.out.println(updateServerSocket.isClosed());
	        		        socket = updateServerSocket.accept();
	        			        
	        		        //save received update into file in chronological order
	        		        in = socket.getInputStream();
	        		        out = new FileOutputStream(new Date().getTime() + "updates.txt");
			                byte[] bytes = new byte[16*1024];
	
			                int count;
	
			                while ((count = in.read(bytes)) > 0) {
			                    out.write(bytes, 0, count);
			                }
			                
	       	        }
        		} catch (Exception e) {
        			e.printStackTrace();
        		} finally {
        			try {
        				if (out != null) out.close();
        				if (in != null) in.close();
        				if (socket != null) socket.close();
        				if (updateServerSocket != null) updateServerSocket.close();
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
       	        }
            }
		});
		
		
		this.welcomeServer = (new Thread() {

			@Override
            public void run() {
            	System.out.println("welcomeserver started");

        		Socket socket = null;
        		OutputStream out = null;
        		InputStream in = null;
        		BufferedWriter writer = null;
        		serverSocket = null;
        		
        		try {
					serverSocket = new ServerSocket(11412);

				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(1);
				}
        	        
        	        while(!terminated) {
        	        	try {
        			        socket = serverSocket.accept();
        			        String IP = socket.getInetAddress().toString() + ":" + socket.getPort();
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
        });
	}
	
	public void execute(){
		updateServer.start();
		welcomeServer.start();
	}
	
	public void end() throws IOException{
		terminated = true;
		serverSocket.close();
		updateServerSocket.close();
	}
	
}
