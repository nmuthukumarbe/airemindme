/**
 * 
 */
package com.server.realsync.util;

/**
 * https://github.com/Auties00/Cobalt/tree/master
 * <!-- https://mvnrepository.com/artifact/com.github.auties00/cobalt -->
		<dependency>
			<groupId>com.github.auties00</groupId>
			<artifactId>cobalt</artifactId>
			<version>0.0.7</version>
		</dependency>
 */
public class WhatsappWeb {

	/**
	 * 
	 */
	public WhatsappWeb() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Create a fixed thread pool with 5 threads
		/*WhatsappWeb whatsappWeb = new WhatsappWeb();
		whatsappWeb.sendWhatsapp("Message");*/

	}

	/*public void sendWhatsapp(String message1) {
		//var phoneNumber = 919789562069l;
		var phoneNumber = 919940398267l;
		try {
			Whatsapp.webBuilder() // Use the Web api
			.lastConnection() // Deserialize the last connection, or create a new one if it doesn't exist
			.unregistered(phoneNumber, PairingCodeHandler.toTerminal()) // Print the pairing code to the terminal
			//.unregistered(QrHandler.toTerminal())
			.addLoggedInListener(api -> {
				//System.out.printf("Connected: %s%n", api.store().privacySettings());
				var chatJid = Jid.of("919940398267");
				//var chatJid = Jid.of("919791439784");
				// api.sendChatMessage(chatJid, message1);
				// List of messages to send
		        List<String> messages = List.of("Muthu Message 1", "Muthu Message 2");

		        // Send messages sequentially
		        messages.forEach(message -> {
		        	
		            api.sendChatMessage(chatJid, message)
		                .thenAccept(sentMessage -> System.out.printf("Message sent: %s%n", message))
		                .exceptionally(e -> {
		                    System.out.printf("Failed to send message: %s%n", message);
		                    return null;
		                });
		            try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        });
		        
		     // Schedule the disconnect after a 5-second delay and shut down the executor
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    api.disconnect();
                    scheduler.shutdown(); // Shut down the scheduler after disconnecting
                }, 3, TimeUnit.SECONDS);
				
			}) // Print a message when connected
			.addDisconnectedListener(reason -> System.out.printf("Disconnected: %s%n", reason)) // Print a message
			//.addNewChatMessageListener(message -> System.out.printf("New message: %s%n", message.toJson())) // Print
			.connect() // Connect to Whatsapp asynchronously
			.join() // Await the result
			.awaitDisconnection(); // Wait
		} catch(Exception e) {
			System.out.println("Exception in sendWhatsapp = "+e);
		}
		
	}*/

}

class Task implements Runnable {
    private final int taskId;

    public Task(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        System.out.println("Task " + taskId + " is running on thread: " + Thread.currentThread().getName());
        String message = "Ignore: From AiPixture. Test-" + taskId;
        
		
        try {
        	/*WhatsappWeb whatsappWeb = new WhatsappWeb();
    		whatsappWeb.sendWhatsapp(message);*/
            // Simulate some work with sleep
            Thread.sleep(2000);
        } catch (Exception e) {
            //Thread.currentThread().interrupt();
            System.err.println("Task " + taskId + " was interrupted");
        }
        
        System.out.println("Task " + taskId + " is completed on thread: " + Thread.currentThread().getName());
    }
}
