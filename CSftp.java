
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

        try(
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        )
        {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("<-- " + in.readLine());

            for (int len = 1; len > 0; ) {
                System.out.print("csftp> ");
                /*len = System.in.read(cmdString);
                if (len <= 0)
                    break;*/

                // Start processing the command here.
                String input = stdIn.readLine();
                String[] inputWords = input.split(" ");

                if (inputWords.length == 0) {
                    System.out.print("0x001 Invalid command test 1.");
                } else {
                    // User command
                    String command = inputWords[0];

                    if ((command.equals("user") || command.equals("pw") || command.equals("get") || command.equals("cd")) && inputWords.length != 2) {
                        System.out.println("0x002 Incorrect number of arguments");
                        continue;
                    } else if ((command.equals("quit") || command.equals("features") || command.equals("dir")) && inputWords.length > 1) {
                        System.out.println("0x002 Incorrect number of arguments");
                        continue;
                    } else {
                        // Command Handling

                        //  user
                        if (command.equals("user")) {
                            String ftpCMD = "USER " + inputWords[1];
                            System.out.println("--> " + ftpCMD);
                            writer.write(ftpCMD + "\r\n");
                            writer.flush();
                        }

                        // pass
                        else if (command.equals("pw")) {
                            String ftpCMD = "PASS " + inputWords[1];
                            System.out.println("--> " + ftpCMD);
                            writer.write(ftpCMD + "\r\n");
                            writer.flush();
                        }

                        // quit
                        else if (command.equals("quit")) {
                            String ftpCMD = "QUIT";
                            System.out.println("--> " + ftpCMD);
                            writer.write(ftpCMD + "\r\n");
                            writer.flush();
                        }

                        // get
                        else if (command.equals("get")) {
                            // TODO
                            // Go into passive mode and call RETR
                            /*
                            writer.write(ftpCMD + "\r\n");
                            writer.flush();
                            */
                        }

                        // features
                        else if (command.equals("features")) {
                            String ftpCMD = "FEAT";
                            System.out.println("--> " + ftpCMD);
                            writer.write(ftpCMD + "\r\n");
                            writer.flush();
                        }

                        // cd
                        else if (command.equals("cd")) {
                            String ftpCMD = "CWD " + inputWords[1];
                            System.out.println("--> " + ftpCMD);
                            writer.write(ftpCMD + "\r\n");
                            writer.flush();
                        }

                        // dir
                        else if (command.equals("dir")) {
                            // TODO Passive mode
                            /*
                            writer.write(ftpCMD + "\r\n");
                            writer.flush();
                            */
                        } else {
                            System.out.println("0x001 Invalid command.");
                            continue;
                        }

                        String fromServer;
                        while ((fromServer = in.readLine()) != null) {
                            System.out.println("<-- " + fromServer);
                            break;
                        }
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("0xFFFE Input error while reading commands, terminating.");
        }
    }
}
