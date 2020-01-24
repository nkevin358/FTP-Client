
import java.io.*;
import java.net.Socket;

//
// This is an implementation of a simplified version of a command 
// line ftp client. The program always takes two arguments
//


public class CSftp {
    static final int MAX_LEN = 255;
    static final int ARG_CNT = 2;

    static final int DEAFULT_PORT = 21;

    public static Socket socketA = null;

    public static BufferedReader Areader = null;
    public static PrintWriter Awriter = null;

    private static void quitConnection() throws IOException {
        if (socketA != null){
            socketA.close();
        }
        if (Areader != null){
            Areader.close();
        }
        if (Awriter != null){
            Awriter.close();
        }
        System.exit(0);
    }

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
        int portNumber = DEAFULT_PORT;
        if (args.length == 2) {
            portNumber = Integer.parseInt(args[1]);
        }
        try {
            socketA = new Socket(hostName, portNumber);
            Awriter = new PrintWriter(socketA.getOutputStream(), true);
            Areader = new BufferedReader(new InputStreamReader(socketA.getInputStream()));

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("<-- " + Areader.readLine());

            for (int len = 1; len > 0; ) {
                System.out.print("csftp> ");
                /*len = System.in.read(cmdString);
                if (len <= 0)
                    break;*/

                // Start processing the command here.
                String input = stdIn.readLine();
                String[] inputWords = input.split("\\s+");

                if (inputWords.length == 0) {
                    System.out.print("0x001 Invalid command.");
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
                            Awriter.write(ftpCMD + "\r\n");
                            Awriter.flush();
                        }

                        // pass
                        else if (command.equals("pw")) {
                            String ftpCMD = "PASS " + inputWords[1];
                            System.out.println("--> " + ftpCMD);
                            Awriter.write(ftpCMD + "\r\n");
                            Awriter.flush();
                        }

                        // quit
                        else if (command.equals("quit")) {
                            String ftpCMD = "QUIT";
                            System.out.println("--> " + ftpCMD);
                            Awriter.write(ftpCMD + "\r\n");
                            Awriter.flush();
                            quitConnection();
                        }

                        // get
                        else if (command.equals("get")) {
                            System.out.println("--> " + "PASV");
                            Awriter.write("PASV\r\n");
                            Awriter.flush();
                        }

                        // features
                        else if (command.equals("features")) {
                            String ftpCMD = "FEAT";
                            System.out.println("--> " + ftpCMD);
                            Awriter.write(ftpCMD + "\r\n");
                            Awriter.flush();
                        }

                        // cd
                        else if (command.equals("cd")) {
                            String ftpCMD = "CWD " + inputWords[1];
                            System.out.println("--> " + ftpCMD);
                            Awriter.write(ftpCMD + "\r\n");
                            Awriter.flush();
                        }

                        // dir
                        else if (command.equals("dir")) {
                            System.out.println("--> " + "PASV");
                            Awriter.write("PASV\r\n");
                            Awriter.flush();
                        }

                        else {
                            System.out.println("0x001 Invalid command.");
                            continue;
                        }

                        String fromServer;
                        while ((fromServer = Areader.readLine()) != null) {
                            System.out.println("<-- " + fromServer);

                            // for get:
                            if (fromServer.contains("227")) {
                                String IP_Address = fromServer.split("[\\(\\)]")[1];
                                String[] nums = IP_Address.split(",");

                                String hostNameB = nums[0] + "." + nums[1] + "." + nums[2] + "." + nums[3];
                                int portNumberB = Integer.parseInt(nums[4]) * 256 + Integer.parseInt(nums[5]);

                                try (
                                        Socket socketB = new Socket(hostNameB, portNumberB);
                                        BufferedReader inB = new BufferedReader(new InputStreamReader(socketB.getInputStream()));
                                ) {
                                    if (command.equals("dir")) {
                                        System.out.println("--> " + "LIST");
                                        Awriter.write("LIST\r\n");
                                        Awriter.flush();

                                        String fromServerB;
                                        while ((fromServerB = inB.readLine()) != null) {
                                            System.out.println(fromServerB);
                                        }
                                    }
                                    else if (command.equals("get")) {
                                        String ftpCMD = "RETR" + inputWords[1];
                                        System.out.println("--> " + ftpCMD);
                                        Awriter.write("RETR " + inputWords[1] + "\r\n");
                                        Awriter.flush();

                                        BufferedInputStream inputBuffer = new BufferedInputStream(socketB.getInputStream());
                                        BufferedOutputStream outputBuffer = new BufferedOutputStream(new FileOutputStream(new File(inputWords[1])));

                                        byte[] buffer = new byte[4096];
                                        int bytesRead = 0;

                                        // readAllBytes
                                        while ((bytesRead = inputBuffer.read(buffer)) != -1) {
                                            outputBuffer.write(bytesRead);
                                        }
                                    }
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                    System.err.println("0xFFFE Input error while reading commands, terminating.");
                                }
                            }

                            // for features:
                            char[] toCharArray = fromServer.toCharArray();
                            if (toCharArray[3] == ' ') break;
                        }
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("0xFFFE Input error while reading commands, terminating.");
        }
    }
}
