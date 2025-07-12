package smartcity.transport;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;
import smartcity.transport.PublicTransportServiceGrpc.PublicTransportServiceImplBase;
import io.grpc.stub.StreamObserver;

public class PublicTransportServiceServer extends PublicTransportServiceImplBase{
    
        private static final Logger logger = Logger.getLogger(PublicTransportServiceServer.class.getName()); //creating a logger to print the message to the console

        PublicTransportServiceServer trafficserver = new PublicTransportServiceServer();
        
        int port = 50051;
        
        try {
            Server server = ServerBuilder.forPort(port) //starts building the server on the default port
			    .addService(trafficserver) //register our server implementation
			    .build()   //finalizes the server
			    .start();  //launches the server so it can start getting requests
			
            server.awaitTermination();  //keeps the server on awaiting requests, otherwise it would shut down
                        
        } catch (IOException e) {
            e.printStackTrace();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        logger.info("Server started, listening on " + port); //message that will print once server has started running
        
    } //closes class
