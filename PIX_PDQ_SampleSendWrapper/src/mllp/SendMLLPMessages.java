package mllp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.hoh.sockets.CustomCertificateTlsSocketFactory;
import ca.uhn.hl7v2.hoh.util.HapiSocketTlsFactoryWrapper;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Hl7InputStreamMessageIterator;

public class SendMLLPMessages {
	
	public static void main(String[] args) throws HL7Exception, LLPException, InterruptedException, IOException, FileNotFoundException {
				
		// Set log4j error level to ERROR only
		LogManager.getRootLogger().setLevel(Level.ERROR);
		
		// Parse out data passed in
		String ip = args[0].toString();
		int portNo = Integer.parseInt(args[1]);
		String messageFile = args[2].toString();
		String timeoutString = null;
		if (args.length > 3) timeoutString = args[3].toString();
				    				
		//set the initiator timeout, if used
		if (timeoutString != null) {
		    try {
		    	System.out.println("Setting the time (in milliseconds) that the initiator will wait for a response");
		        Integer timeoutValue = Integer.parseInt(timeoutString);
		        System.setProperty("ca.uhn.hl7v2.app.initiator.timeout", timeoutString);
		        System.out.println("\t The value has been set to: " + timeoutValue);
		    }
		    catch (NumberFormatException e) {
		    	System.out.println("\t '" + timeoutString + "'" + " -> is not a valid integer value! \n\t Defalut initiator timeout value will be used (10 seconds)");
		    }
		} 
		else {
			System.out.println("Initiator timeout value has not been provided. \n\t Defalut initiator timeout value will be used (10 seconds)");
		}

		// Prepare the file for reading
		FileReader reader = new FileReader(messageFile);
		Hl7InputStreamMessageIterator messageIterator = new Hl7InputStreamMessageIterator(reader);

		// Create a HapiContext
		HapiContext context = new DefaultHapiContext();
		MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
		mllp.setCharset("UTF-8");
		context.setLowerLayerProtocol(mllp);

		// Create a connection to MLLP Proxy and execute all the messages
		Connection conn = null;
		int messageCounter = 0;
		while (messageIterator.hasNext()) {

			// Initialize a connection if it doesn't already exist
			if (conn == null) {
				// Indicate weather or not it will use TLS and will require a certificate
				boolean useTLS = false;
								
				// Create a socket
				CustomCertificateTlsSocketFactory tlsSocket = new CustomCertificateTlsSocketFactory();

				// If using TLS, initialize it here
	            //		tlsSocket.setKeystoreFilename(certificateFile);
				//		tlsSocket.setKeystorePassphrase(certificatePassword);
				
				// Initialize the context adapter to use the our socket
				context.setSocketFactory(new HapiSocketTlsFactoryWrapper(tlsSocket));
				
				// Create the connection
				conn = context.newClient(ip, portNo, useTLS);
			}
			
			// Execute next message in the file 
			try {				
				Message currentMessage = messageIterator.next();
				messageCounter ++;

				System.out.print("-----------------------");
				System.out.print(" Sending message number " + messageCounter);
				System.out.println(" -----------------------");
				// Print the full message for debugging purposes
				// System.out.println("\t :Message Structure: \n" + currentMessage.printStructure());
				System.out.println(currentMessage);

				Message response = conn.getInitiator().sendAndReceive(currentMessage);
			
				System.out.println("\t\t The response to message number " + messageCounter + " is:\n" + response.encode());

			} catch (HL7Exception | IOException e) {
				System.out.println("ERROR while processing message number " + messageCounter + "\n" + e.toString());
				e.printStackTrace();
			}
		}
		// Cleanup
		conn.close();
	    conn = null;
		context.close();
		System.out.println("----------------------- END of Messages -----------------------");
	}
}