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
import java.net.SocketException;
import java.util.Date;

public class NodeServer {

	private volatile Thread updateServer;
	private volatile Thread welcomeServer;
	private volatile Thread newpeerServer;
    private boolean terminated;
    private ServerSocket serverSocket;
    private ServerSocket updateServerSocket;
	protected ServerSocket newpeerServerSocket;

	
	public NodeServer(Broadcaster broadcast) {
		terminated = false;
		this.updateServer = (new Thread() {
            @Override
            public void run() {
            	updateServerSocket = null;
        		Socket socket = null;
        		OutputStream out = null;
        		InputStream in = null;
        		
        	        
        		try {
					updateServerSocket = new ServerSocket(8845);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(MAX_PRIORITY);
				}

        		try {
	        	    while(!terminated) {
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
        			if (!(e instanceof SocketException)) {
		        		e.printStackTrace();
		        	} else {
		        		System.out.println("Socket closed: 8845");
		        	}
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
		//---------------------welcome-----------------------------------
		
		this.welcomeServer = (new Thread() {

			@Override
            public void run() {

        		Socket socket = null;
        		OutputStream out = null;
        		InputStream in = null;
        		BufferedWriter writer = null;
        		serverSocket = null;
        		
        		try {
					serverSocket = new ServerSocket(8846);

				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(1);
				}
        	        
        	        while(!terminated) {
        	        	try {
        			        socket = serverSocket.accept();
        			        String IP = socket.getInetAddress().toString().replaceAll("/", "");
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
        			        
        			        in.close();
        			        out.close();
        			        socket.close();
        			        
        			        
        			        //record this client IP to the file
        		           	FileWriter fstream = new FileWriter("nodeaddr.txt", true); //true = appends
        		            writer = new BufferedWriter(fstream);
        		            writer.write(IP);
        		            writer.newLine();
        		            writer.flush();
        		            writer.close();
        		            
        		            //broadcast this ip to others
        		            broadcast.advNewPeer(IP);
        		           	
        		            
        		            
        		            //now send nodeaddr.txt by connecting again, inefficient
        		            socket = serverSocket.accept();
        			        file = new File("nodeaddr.txt");
        			      
        			        // Get the size of the file
        			        length = file.length();
        			        
        			        if (length > Integer.MAX_VALUE) {
        			            System.out.println("File is too large.");
        			        }
        			        
        			        bytes = new byte[(int) length];
        			        
        			        in = new FileInputStream(file);
        			        out = socket.getOutputStream();
        			
        			        int count2;
        			        
        			        while ((count2 = in.read(bytes)) > 0) {
        			            out.write(bytes, 0, count2);
        			        }
        		            
        			        
        			        
        		        } catch (Exception e) {
        		        	if (e instanceof SocketException) {
        			        		System.out.println("Socket closed: 8846");
        		        		break;
        		        	} else {
        		        		e.printStackTrace();
        		        	}
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
		
		//-------------------peer adv----------------------------
		this.newpeerServer = (new Thread() {
            @Override
            public void run() {
            	newpeerServerSocket = null;
        		Socket socket = null;
        		OutputStream out = null;
        		InputStream in = null;
        		
        	        
        		try {
					newpeerServerSocket = new ServerSocket(8847);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(MAX_PRIORITY);
				}

        		try {
	        	    while(!terminated) {
	        		        socket = newpeerServerSocket.accept();
	        			        
	        		        in = socket.getInputStream();
			                byte[] ip = new byte[16*1024];
	        		        in.read(ip);

	        		      //record this  IP to the file
        		           	FileWriter fstream = new FileWriter("nodeaddr.txt", true); //true = appends
        		            BufferedWriter writer = new BufferedWriter(fstream);
        		            writer.write(new String(ip, "UTF-8"));
        		            writer.newLine();
        		            writer.close();
			                
	       	        }
        		} catch (Exception e) {
        			if (!(e instanceof SocketException)) {
		        		e.printStackTrace();
		        	} else {
		        		System.out.println("Socket closed: 8847");
		        	}
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
		});
	}
	
	public void execute(){
		updateServer.start();
		welcomeServer.start();
		newpeerServer.start();
	}
	
	public void end() throws IOException{
		terminated = true;
		serverSocket.close();
		updateServerSocket.close();
		newpeerServerSocket.close();
	}
	
}
