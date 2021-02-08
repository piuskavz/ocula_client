package dummy_server;
//http://lakjeewa.blogspot.ug/2012/03/simple-android-application-to-send-file.html
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	 private static ServerSocket serverSocket;
	    private static Socket clientSocket;
	    private static InputStream inputStream;
	    private static FileOutputStream fileOutputStream;
	    private static BufferedOutputStream bufferedOutputStream;
	    private static int filesize = 10000000; // filesize temporary hardcoded 
	    private static int bytesRead;
	    private static int current = 0;
	    
	    private static FileInputStream fileInputStream;
	    private static BufferedInputStream bufferedInputStream;
	    private static OutputStream outputStream;

	    public static void main(String[] args) throws IOException {
	    	
	    	


	        serverSocket = new ServerSocket(4444);  //Server socket
	        System.out.println("Server started. Listening to the port 4444");

	        
	        while(true){
	        	

		        clientSocket = serverSocket.accept();


		        byte[] mybytearray = new byte[filesize];    //create byte array to buffer the file

		        inputStream = clientSocket.getInputStream();
		        //outputStream = clientSocket.getOutputStream();
		        
		        fileOutputStream = new FileOutputStream("D:\\output.jpg");
		        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

		        System.out.println("Receiving...");

		        //following lines read the input slide file byte by byte
		        bytesRead = inputStream.read(mybytearray, 0, mybytearray.length);
		        current = bytesRead;

		        do {
		            bytesRead = inputStream.read(mybytearray, current, (mybytearray.length - current));
		            if (bytesRead >= 0) {
		                current += bytesRead;
		            }
		        } while (bytesRead > -1);


		        bufferedOutputStream.write(mybytearray, 0, current);
		        bufferedOutputStream.flush();
		        bufferedOutputStream.close();
		        //inputStream.close();
		        //clientSocket.close();
		        
		        
		        try {
		            Thread.sleep(1000);                 //1000 milliseconds is one second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
		        //....send CSV
		        File CSVfile;
	            CSVfile = new File("D:\\CSVout.csv");
	            System.out.println("Sending CSV...");
	            
	            try{
	            	clientSocket = serverSocket.accept();//remove if test fails
	            	
	            	byte[] CSVbytearray = new byte[(int) CSVfile.length()]; //create a byte array to file

	                fileInputStream = new FileInputStream(CSVfile);
	                bufferedInputStream = new BufferedInputStream(fileInputStream);

	                bufferedInputStream.read(CSVbytearray, 0, CSVbytearray.length); //read the file

	                outputStream = clientSocket.getOutputStream(); //

	                outputStream.write(CSVbytearray, 0, CSVbytearray.length); //write file to the output stream byte by byte
	                System.out.println("CSV gone..."+ CSVbytearray[1]);
	                outputStream.flush();//place
	                bufferedInputStream.close();//these
	                outputStream.close();//lines
	            	
	            }catch(IOException e){
                	e.printStackTrace();
                	 System.out.println("Sending CSV failed...");
                }        
		                        
                //bufferedOutputStream.close();
		        inputStream.close();
                clientSocket.close();
                //...end of sending CSV
                
                //....CSV read debug code...
                BufferedReader fileReader = null;
                try{
                	String line = "";
                	fileReader = new BufferedReader(new FileReader("D:\\CSVout.csv"));
                	while((line = fileReader.readLine())!=null){
                		String[] row = line.split(",");
                		//System.out.println("x="+row[0] +" and y="+row[1]);
                		
                	}
                	
                }catch(Exception e){
                	
                }finally{
                	try {
                		 fileReader.close();
                		 } catch (IOException e) {
                		 System.out.println("Error while closing fileReader !!!");
                		 e.printStackTrace();
                		 }
                	
                }
                //....end of CSV read debug code...
                
		        
		        

		        System.out.println("Sever done");
	        	
	        }
	        
	        //serverSocket.close();
	        	        

	    }
}
