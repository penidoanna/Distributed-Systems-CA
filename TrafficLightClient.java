package smartcityconnect2.traffic;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import smartcityconnect2.traffic.TrafficLightServiceGrpc.TrafficLightServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;

public class TrafficLightClient {
    
    private static final Logger logger = Logger.getLogger(TrafficLightClient.class.getName()); //creating a logger to print the message to the console
    
    public static void main(String[] args) throws Exception {
       String host = "localhost"; //declare host variable
       int port = 50051; //default port for gRPC
       
       // First a channel is being created to the server from client.
        ManagedChannel channel = ManagedChannelBuilder
               .forAddress(host, port)
               .usePlaintext() // As it is a local demo of GRPC, we can disable encryption.
               .build(); //finalizes the channel creation
    
       // Creating stubs for establishing the connection with server.
       TrafficLightServiceGrpc.TrafficLightServiceStub asyncStub = TrafficLightServiceGrpc.newStub(channel); // Asynch stub
       TrafficLightServiceGrpc.TrafficLightServiceBlockingStub blockingStub = TrafficLightServiceGrpc.newBlockingStub(channel); //Client will send a request and wait for a return
                 
       TrafficLightClient client = new TrafficLightClient(); //creating a new TrafficLightClient object
        
        try {
            // Test unary call
            client.getLightStatus(blockingStub);
            
            // Test server streaming call
            client.streamTrafficUpdates(asyncStub);

        } catch (StatusRuntimeException e){
            logger.log(Level.WARNING, "RPC failed (0)", e.getStatus());           
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } //closes try
    } //closes main method

    //blocking server-streaming
    private void getLightStatus(TrafficLightServiceGrpc.TrafficLightServiceBlockingStub blockingStub) {
        logger.info("GetLightStatus request received");
        
        // First creating a request message.
        LightStatusRequest request = LightStatusRequest
                .newBuilder()
                .setIntersectionId("01")
                .build();
        
        // As this call is blocking. The client will not proceed until all the messages in stream has been received. 
        try {
            LightStatusResponse response = blockingStub.getLightStatus(request); //Invoking the getLightStatus method and waiting for a response
            logger.info("Light Status: " + response.getStatus() + "Direction: " + response.getDirection()); //Print server's message, if successful
        } catch (StatusRuntimeException e){
            logger.log(Level.WARNING, "GetLightStatus request failed", e.getStatus());
        } 
    } //getLightStatus

    // Server streaming in Async stub
    private void streamTrafficUpdates(TrafficLightServiceStub asyncStub) {
        logger.info("streamTrafficUpdates request received");
        
        ZoneRequest request = ZoneRequest // First creating a request message.
                .newBuilder()
                .setZoneId("Z01")
                .build();
        
        CountDownLatch latch = new CountDownLatch(1);
        
        // Handling the stream from server using onNext (logic for handling each message in stream), onError, onCompleted (logic will be executed after the completion of stream)
        StreamObserver<TrafficUpdate> responseObserver = new StreamObserver<TrafficUpdate>(){
            @Override
            public void onNext(TrafficUpdate update) {
                logger.info("Traffic updates: - Intersection " + update.getIntersectionId() + "; - Status: " + update.getStatus());
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.WARNING, "streamTrafficUpdates request failed", t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("streamTrafficUpdates completed");
                latch.countDown();
            }      
        };
        
        try{
            asyncStub.streamTrafficUpdates(request, responseObserver);
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e){
            logger.log(Level.WARNING, "streamTrafficUpdates request interrupted", e.getMessage()); // TODO Auto-generated catch block
        }
    } //streamTrafficUpdates

} //closes class
