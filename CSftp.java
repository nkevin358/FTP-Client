
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

    public static void main(String [] args) {
        byte cmdString[] = new byte[MAX_LEN];

        // Get command line arguments and connected to FTP
        // If the arguments are invalid or there aren't enough of them
            // then exit.

        if (args.length != ARG_CNT) {
            System.out.print("Usage: cmd ServerAddress ServerPort\n");
            return;
        }

            String hostName = args[0];
            int portNumber = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                for (int len = 1; len > 0;) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;

                // Start processing the command here.
                String input = new String(cmdString);
                String[] inputWords = input.split(" ");

                // USER
                if (inputWords[0].equals("USER")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // PASS
                else if (inputWords[0].equals("PASS")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // QUIT
                //if () {

                //}

                //space in the 3rd index has a space

                else {
                    System.out.println("900 Invalid command.");
                    break;
                }

                String fromServer;

                in.readLine();
                while ((fromServer = in.readLine()) != null) {
                    System.out.println("Server: " + fromServer);
                    break;
                }
	    }
	} catch (IOException exception) {
	    System.err.println("998 Input error while reading commands, terminating.");
	}
    }
}
