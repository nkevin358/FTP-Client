
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

    public static void main(String[] args) {
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
                            System.out.println("--> " + "PASV");
                            writer.write("PASV\r\n");
                            writer.flush();
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

                            // for user, pw, quit, cd
                            if (fromServer.contains("331") || fromServer.contains("230") || fromServer.contains("221"))
                                break;

                            // for features:
                            if (fromServer.contains("End")) break;

                            // for get:
                            if (fromServer.contains("227")) {
                                String IP_Address = fromServer.split("[\\(\\)]")[1];
                                String[] nums = IP_Address.split(",");

                                String hostNameB = nums[0] + "." + nums[1] + "." + nums[2] + "." + nums[3];
                                int portNumberB = Integer.parseInt(nums[4]) * 256 + Integer.parseInt(nums[5]);

                                try (
                                        Socket socketB = new Socket(hostNameB, portNumberB);
                                        PrintWriter writerB = new PrintWriter(socketB.getOutputStream(), true);
                                        BufferedReader inB = new BufferedReader(new InputStreamReader(socketB.getInputStream()));
                                ) {
                                    System.out.println("hello");

                                } catch (IOException exception) {
                                    System.err.println("0xFFFE Input error while reading commands, terminating.");
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("0xFFFE Input error while reading commands, terminating.");
        }
    }
}
