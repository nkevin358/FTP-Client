
import java.lang.System;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.io.*;
import java.net.*;

//
// This is an implementation of a simplified version of a command 
// line ftp client. The program always takes two arguments
//


public class CSftp {
    static final int MAX_LEN = 255;
    static final int ARG_CNT = 2;

    static final int DEFAULT_PORT = 21;

    public static void main(String [] args) {
        byte cmdString[] = new byte[MAX_LEN];

        // Get command line arguments and connected to FTP
        // If the arguments are invalid or there aren't enough of them
            // then exit.
        int portNumber = DEFAULT_PORT;

        if (args.length == 1){
            portNumber = Integer.parseInt(args[1]);
        }
        else if (args.length != ARG_CNT) {
            System.out.print("Usage: cmd ServerAddress ServerPort\n");
            return;
        }
            String hostName = args[0];

        try {
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String controlresponse = in.readLine();

                System.out.println("<-- " + controlresponse);

                for (int len = 1; len > 0;) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;

                // Start processing the command here.
                String input = new String(cmdString);
                String[] inputWords = input.split(" ");

                // Commands

                //  user
                if (inputWords[0].equals("user")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // pw
                else if (inputWords[0].equals("pw")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // quit
                else if (inputWords[0].equals("quit")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // get
                else if (inputWords[0].equals("get")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // features
                else if (inputWords[0].equals("features")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // cd
                else if (inputWords[0].equals("cd")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // dir
                else if (inputWords[0].equals("dir")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }



                //space in the 3rd index has a space

                else {
                    System.out.println("0x001 Invalid command.");
                    continue;
                }

                String fromServer;

                while ((fromServer = in.readLine()) != null) {
                    System.out.println("<-- " + fromServer);
                    break;
                }
	    }
	} catch (IOException exception) {
	    System.err.println("0xFFFE Input error while reading commands, terminating.");
	}
    }
}
