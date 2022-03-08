// CPSC 441 Asignment 3 part 1
// Ping client
// Seongmok, Yoo (Eric)
// 10162624
// Client side implementation of Ping utility

import java.io.*;
import java.net.*;
import java.util.*;

public class PingClient
{

  public static void main(String[] args) throws Exception
  {
    // Get command line argument.
    if (args.length != 2) {
      System.out.println("Required arguments: host and port");
      return;
    }
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    InetAddress address = InetAddress.getByName(host);

    // declaration of variable that will be used for calculating RTTs
    long[] delays = new long[10];
    long max = 0;
    long min = 1000;
    long total = 0;
    long avdelay = 0;

    // Create a datagram socket for receiving and sending UDP packets
    // through the port specified on the command line.
    DatagramSocket socket = new DatagramSocket();

    // for loop for sending udp packets 10 times
    for(int i=0;i<10;i++){

      // creating datagram packet for each UDP packet sent containing Ping + index number + timestamp
      long sendtime = System.currentTimeMillis();
      String message = "Ping "+ i + " " + sendtime + "\n";
      DatagramPacket request = new DatagramPacket(message.getBytes(), message.length(), address, port);
      socket.send(request);

      // Create a datagram packet to hold incomming UDP packet.
      DatagramPacket reply = new DatagramPacket(new byte[1024], 1024);

      // each request sent will wait 1s for the reply
      socket.setSoTimeout(1000);

      // if reply is received calculate its delay and store them in an array
      try{
        socket.receive(reply);
        long receivetime = System.currentTimeMillis();
        long delay = receivetime - sendtime;
        delays[i] = delay;
        System.out.print("delay: " + delay + " ");
        printData(reply);
      // if reply is not received, record the delay as 1000 and print that packet is lost
      }catch (IOException e){
        delays[i] = 1000;
        System.out.println("packet lost");
      }

      // wait 1s before next iteration
      Thread.sleep(1000);

    }

    // calculate the max min delays by comparing each elements in array
    for(int i=0;i<10;i++){
      if(delays[i] < min){
        min = delays[i];
      }
      if(delays[i] > max){
        max = delays[i];
      }
      total += delays[i];
    }

    // when all 10 packets are sent calculate the average delay and display min max and average delay
    avdelay = total / 10;
    System.out.println("RTT: " + "max: " + max + " min: " + min + " average: " + avdelay);
  }

  /*
  * Print ping data to the standard output stream.
  */
  private static void printData(DatagramPacket request) throws Exception
  {
    // Obtain references to the packet's array of bytes.
    byte[] buf = request.getData();

    // Wrap the bytes in a byte array input stream,
    // so that you can read the data as a stream of bytes.
    ByteArrayInputStream bais = new ByteArrayInputStream(buf);

    // Wrap the byte array output stream in an input stream reader,
    // so you can read the data as a stream of characters.
    InputStreamReader isr = new InputStreamReader(bais);

    // Wrap the input stream reader in a bufferred reader,
    // so you can read the character data a line at a time.
    // (A line is a sequence of chars terminated by any combination of \r and \n.)
    BufferedReader br = new BufferedReader(isr);

    // The message data is contained in a single line, so read this line.
    String line = br.readLine();

    // Print host address and data received from it.
    System.out.println(
      "Received from " +
      request.getAddress().getHostAddress() +
      ": " +
      new String(line) );
  }
}
