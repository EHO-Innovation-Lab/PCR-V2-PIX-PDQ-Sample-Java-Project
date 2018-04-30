# PCR-V2-PIX-PDQ-Sample-Java-Project
Sample Java project that demonstrates consumption of innovation-lab's PCR V2 PIX/PDQ queries

This is a full Eclipse project and a jar.  You can import it into Eclipse as is, or just use the jar.

The Wrapper is designed to take HL7V2 messages and send them to the destination point.  It takes in 3 parameters:
1. Destination point
2. Port
3. Location of the file to send

ie:
lite.innovation-lab.ca 2100 C:\Users\arthur.krughkov\Documents\PCR\MLLP\PIX_PDQ_eHealth_LITE_Sample.hl7

For more details on the service, please go to www.innovation-lab.ca 


Details:
Built using Java 1.7
Libraries used/need to include:
hapi-base-2.2-sources
hapi-hl7overhttp-2.1.jar
log4j-1.2.17.jar
