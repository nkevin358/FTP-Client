
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

    public static void main(String[] args) {
        byte cmdString[] = new byte[MAX_LEN];

        // Get command line arguments and connected to FTP
        // If the arguments are invalid or there aren't enough of them
        // then exit.
        int portNumber = DEFAULT_PORT;

        if (args.length == 1) {
            portNumber = Integer.parseInt(args[1]);
        } else if (args.length != ARG_CNT) {
            System.out.print("Usage: cmd ServerAddress ServerPort\n");
            return;
        }
        String hostName = args[0];

        try {
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("<-- " + in.readLine());

            for (int len = 1; len > 0; ) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;

                // Start processing the command here.
                String input = new String(cmdString);
                String[] inputWords = input.split(" ");

                // Commands

                //  user
                if (inputWords[0].equals("USER") || inputWords[0].equals("user")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // pass
                else if (inputWords[0].equals("PASS") || inputWords[0].equals("pass")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // quit
                else if (inputWords[0].equals("QUIT") || inputWords[0].equals("quit")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // pasv
                else if (inputWords[0].equals("PASV") || inputWords[0].equals("pasv")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // retr
                else if (inputWords[0].equals("RETR") || inputWords[0].equals("retr")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                //space in the 3rd index has a space ??
                // feat
                else if (inputWords[0].equals("FEAT") || inputWords[0].equals("feat")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // cwd
                else if (inputWords[0].equals("CWD") || inputWords[0].equals("cwd")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

                // list
                else if (inputWords[0].equals("LIST") || inputWords[0].equals("list")) {
                    writer.write(input + "\r\n");
                    writer.flush();
                }

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
