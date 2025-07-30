package smartcityconnect2.transport;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;
import io.grpc.stub.StreamObserver;
import smartcityconnect2.transport.PublicTransportServiceGrpc.PublicTransportServiceImplBase;
import io.grpc.stub.StreamObserver;

public class PublicTransportServiceServer extends PublicTransportServiceImplBase{
    
        private static final Logger logger = Logger.getLogger(PublicTransportServiceServer.class.getName()); //creating a logger to print the message to the console

        public static void main(String[] args) throws IOException, InterruptedException {
        
        PublicTransportServiceServer publicTransportServer = new PublicTransportServiceServer();
        
        int port = 50051;
        
        Server server = ServerBuilder.forPort(port) //starts building the server on the default port
                .addService(publicTransportServer) //register our server implementation
                .build()   //finalizes the server
                .start();  //launches the server so it can start getting requests
			
            logger.info("Server started, listening on " + port);
                        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server");
            server.shutdown();
        }));
        
        server.awaitTermination();
    } //main class
  

    @Override
    public StreamObserver<CrowdReport> sendCrowdReports(final StreamObserver<CrowdSummary> responseObserver) {
        return new StreamObserver<CrowdReport>() {
            
            private int totalCrowdCount, reportCount = 0;

            @Override
            public void onNext(CrowdReport request) {
                logger.info("Received CrowdReport from stop number: " + request.getStopNum() + ", count: " + request.getCount());
                totalCrowdCount += request.getCount();
                reportCount++;
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error in SendCrowdReports: " + t.getMessage());
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                String overallStatus;
                if (reportCount == 0) {
                    overallStatus = "Nobody's there.";
                } else {
                    int averageCrowd = (int) totalCrowdCount / reportCount;
                    if (averageCrowd > 50) {
                        overallStatus = "Very crowded";
                    } else if (averageCrowd > 20) {
                        overallStatus = "Moderate crowd";
                    } else {
                        overallStatus = "Low crowd";
                    }
                }
                logger.info("SendCrowdReports completed. Overall status: " + overallStatus);
                CrowdSummary summary = CrowdSummary.newBuilder()
                        .setOverallStatus(overallStatus)
                        .build();
                
                responseObserver.onNext(summary);
                responseObserver.onCompleted();
            } //onCompleted
        };
                       
    } //SendCrowdReports

} //closes class
