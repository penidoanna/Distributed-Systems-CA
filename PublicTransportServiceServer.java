package smartcityconnect2.transport;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;
import io.grpc.stub.StreamObserver;
import smartcityconnect2.transport.PublicTransportServiceGrpc.PublicTransportServiceImplBase;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import smartcityconnect2.NamingServiceGrpc;
import smartcityconnect2.RegistrationResponse;
import smartcityconnect2.ServiceInfo;

public class PublicTransportServiceServer extends PublicTransportServiceImplBase {

    private static final Logger logger = Logger.getLogger(PublicTransportServiceServer.class.getName()); //creating a logger to print the message to the console

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051; //Default port
        PublicTransportServiceServer publicTransportServer = new PublicTransportServiceServer();

        //Register with naming service
        publicTransportServer.registerWithNamingService("PublicTransportService", port);

        Server server = ServerBuilder.forPort(port) //starts building the server on the default port
                .addService(publicTransportServer) //register our server implementation
                .build() //finalizes the server
                .start();  //launches the server so it can start getting requests

        logger.info("Server started, listening on " + port);

        server.awaitTermination();
    } //main class

    private void registerWithNamingService(String serviceName, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50050)
                .usePlaintext()
                .build();

        try {
            NamingServiceGrpc.NamingServiceBlockingStub namingStub = NamingServiceGrpc.newBlockingStub(channel);

            ServiceInfo request = ServiceInfo.newBuilder()
                    .setServiceName(serviceName)
                    .setHost("localhost")
                    .setPort(port)
                    .build();

            RegistrationResponse response = namingStub.registerService(request);
            logger.info("Service registration status: " + response.getSuccess());
        } finally {
            channel.shutdown();
        }
} //registerWithNamingService

    @Override
    public void sendCrowdReports(CrowdReport request, StreamObserver<CrowdSummary> responseObserver) {

        String stopNum = request.getStopNum();
        int crowdCount = (int) (Math.random() * 100); //random crowd count
        String status = calculateCrowdStatus(crowdCount);

        logger.info("Crowd report for stop " + stopNum + ": " + status);

        responseObserver.onNext(CrowdSummary
                .newBuilder()
                .setOverallStatus(status)
                .build());
        responseObserver.onCompleted();
    } //SendCrowdReports
    
    @Override
    public StreamObserver<CrowdReport> reportMultipleStops(StreamObserver<CrowdSummary> responseObserver) {
        return new StreamObserver<CrowdReport>() {
            int totalStops, totalCrowd = 0;
                    
            @Override
            public void onNext(CrowdReport request) {
                String stopNum = request.getStopNum();
                int crowdCount = (int)(Math.random() * 100);
                totalCrowd += crowdCount;
                totalStops++;
                logger.info("Report for stop: " + stopNum + " received.");
            }
            

            @Override
            public void onError(Throwable t) {
                logger.warning("Error in reportMultipleStops: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                int avgCrowd = totalStops > 0 ? totalCrowd / totalStops : 0;
                String status = PublicTransportServiceServer.this.calculateCrowdStatus(avgCrowd);
                
                responseObserver.onNext(CrowdSummary.newBuilder()
                        .setOverallStatus("Average status: " + status)
                        .build());
                responseObserver.onCompleted();
            }
        };
    } //reportMultipleStops

    public String calculateCrowdStatus(int avgCrowd) {
        if (avgCrowd == 0) {
            return "Nobody's there";
        } else if (avgCrowd > 50) {
            return "Very crowded";
        } else if (avgCrowd > 20) {
            return "Moderate crowd";
        } else {
            return "Low crowd";
        }
    }//calculateCrowdStatus
} //closes class
