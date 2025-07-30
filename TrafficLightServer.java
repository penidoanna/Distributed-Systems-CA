package smartcityconnect2.traffic;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;
import smartcityconnect2.traffic.TrafficLightServiceGrpc.TrafficLightServiceImplBase;

public class TrafficLightServer extends TrafficLightServiceImplBase{
    
        private static final Logger logger = Logger.getLogger(TrafficLightServer.class.getName()); //creating a logger to print the message to the console

        public static void main(String[] args) throws IOException, InterruptedException {
               
        int port = 50051;
            
        Server server = ServerBuilder.forPort(port) //starts building the server on the default port
			    .addService(new TrafficLightServer()) //register our server implementation
			    .build()   //finalizes the server
			    .start();  //launches the server so it can start getting requests
			                       
        logger.info("Server started, listening on " + port);
        
         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server");
            server.shutdown();
        })
         );
                 
        server.awaitTermination();
        } //main class
} //closes class
