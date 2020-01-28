import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

//
// This is an implementation of a simplified version of a command 
// line ftp client. The program always takes two arguments
//


public class CSftp {
    private static final int ARG_CNT = 2;

    private static final int DEAFAULT_PORT = 21;

    private static Socket controlConnection = null;
    private static Socket dataConnection = null;

    private static BufferedReader controlReader = null;
    private static BufferedReader dataReader = null;
    private static PrintWriter controlWriter = null;

    // Closes connections and program
    private static void quitConnection() throws IOException {

        if (controlConnection != null) {
            controlConnection.close();
        }
        if (controlReader != null) {
            controlReader.close();
        }
        if (controlWriter != null) {
            controlWriter.close();
        }
        if (dataConnection != null) {
            dataConnection.close();
        }
        if (dataReader != null) {
            dataReader.close();
        }
        System.exit(0);
    }

    // Reads response from Control Connection
    private static void readControl() {
        try {
            String fromServer;
            while ((fromServer = controlReader.readLine()) != null) {
                System.out.println("<-- " + fromServer);

                // for features:
                char[] toCharArray = fromServer.toCharArray();
                if (toCharArray[3] == ' ') break;
            }
        } catch (IOException e) {
            System.out.println("0xFFFD Control connection I/O error, closing control connection.");
            System.exit(1);
        }
    }

    // Write response to Control Connection
    private static void writeControl(String cmd) {
        try {
            System.out.println("--> " + cmd);
            controlWriter.write(cmd + "\r\n");
            controlWriter.flush();
        } catch (Exception e) {
            System.out.println("0xFFFD Control connection I/O error, closing control connection.");
            System.exit(1);
        }
    }

    // Create control connection socket
    private static void connectControl(String hostName, int portNumber) {
        try {
            controlConnection = new Socket(hostName, portNumber);
            controlConnection.setSoTimeout(20000);
            controlWriter = new PrintWriter(controlConnection.getOutputStream(), true);
            controlReader = new BufferedReader(new InputStreamReader(controlConnection.getInputStream()));
        } catch (SocketTimeoutException e) {
            System.out.println("0xFFFC Control connection to " + hostName + " on port " + portNumber + " failed to open.");
            System.exit(1);
        } catch (UnknownHostException e) {
            System.out.println("0xFFFC Control connection to " + hostName + " on port " + portNumber + " failed to open.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("0xFFFC Control connection to " + hostName + " on port " + portNumber + " failed to open.");
            System.exit(1);
        }
    }

    // Create data transfer connection socket
    private static void connectDataTransfer(String hostName, int portNumber) {
        try {
            dataConnection = new Socket(hostName, portNumber);
            dataConnection.setSoTimeout(10000);
            dataReader = new BufferedReader(new InputStreamReader(dataConnection.getInputStream()));
        } catch (SocketTimeoutException e) {
            System.out.println("0x3A2 Data transfer connection to " + hostName + " on port" + portNumber + " failed to open");
        } catch (UnknownHostException e) {
            System.out.println("0x3A2 Data transfer connection to " + hostName + " on port" + portNumber + " failed to open");
        } catch (IOException e) {
            System.out.println("0x3A2 Data transfer connection to " + hostName + " on port" + portNumber + " failed to open");
        }
    }

    public static void main(String[] args) {

        if (args.length <= 0 || args.length > 2) {
            System.out.print("Usage: cmd ServerAddress ServerPort\n");
            return;
        }

        String hostName = args[0];
        int portNumber = DEAFAULT_PORT;
        if (args.length == ARG_CNT) {
            portNumber = Integer.parseInt(args[1]);
        }

        try {
            connectControl(hostName, portNumber);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("<-- " + controlReader.readLine());

            for (int len = 1; len > 0; ) {
                System.out.print("csftp> ");

                String input = null;
                String[] inputWords = null;
                String fromServer;

                try {
                    input = stdIn.readLine();
                    inputWords = input.split("\\s+");
                } catch (IOException exception) {
                    System.err.println("0xFFFE Input error while reading commands, terminating.");
                    System.exit(1);
                }

                if (inputWords.length == 0) {
                    System.out.println("0x001 Invalid command.");
                } else {
                    // User command
                    String command = inputWords[0];

                    if (input.equals("") || input.startsWith("#")) {
                        continue;
                    }

                    if ((command.equals("user") || command.equals("pw") || command.equals("get") || command.equals("cd")) && inputWords.length != 2) {
                        System.out.println("0x002 Incorrect number of arguments.");
                    } else if ((command.equals("quit") || command.equals("features") || command.equals("dir")) && inputWords.length > 1) {
                        System.out.println("0x002 Incorrect number of arguments.");
                    } else {
                        // Command Handling
                        //  user
                        switch (command) {
                            case "user": {
                                String ftpCMD = "USER " + inputWords[1];
                                writeControl(ftpCMD);
                                readControl();
                                continue;
                            }

                            // pass
                            case "pw": {
                                String ftpCMD = "PASS " + inputWords[1];
                                writeControl(ftpCMD);
                                readControl();
                                continue;
                            }

                            // quit
                            case "quit": {
                                String ftpCMD = "QUIT";
                                writeControl(ftpCMD);
                                readControl();
                                quitConnection();
                            }

                            // get
                            case "get":
                                controlWriter.write("PASV\r\n");
                                controlWriter.flush();
                                System.out.println("--> " + "PASV");
                                break;

                            // features
                            case "features": {
                                String ftpCMD = "FEAT";
                                writeControl(ftpCMD);
                                readControl();
                                continue;
                            }

                            // cd
                            case "cd": {
                                String ftpCMD = "CWD " + inputWords[1];
                                writeControl(ftpCMD);
                                readControl();
                                continue;
                            }

                            // dir
                            case "dir":
                                controlWriter.write("PASV\r\n");
                                controlWriter.flush();
                                System.out.println("--> " + "PASV");
                                break;
                            default:
                                System.out.println("0x001 Invalid command.");
                                continue;
                        }

                        while ((fromServer = controlReader.readLine()) != null) {
                            System.out.println("<-- " + fromServer);

                            // for dir and get to create data transfer connection socket
                            if (fromServer.contains("227")) {
                                // tell server to prepare for binary mode transfer
                                System.out.println("--> TYPE I");
                                controlWriter.write("TYPE I\r\n");
                                controlWriter.flush();
                                System.out.println("<-- " + controlReader.readLine());

                                String IP_Address = fromServer.split("[\\(\\)]")[1];
                                String[] nums = IP_Address.split(",");

                                String hostNameB = nums[0] + "." + nums[1] + "." + nums[2] + "." + nums[3];
                                int portNumberB = Integer.parseInt(nums[4]) * 256 + Integer.parseInt(nums[5]);

                                try {
                                    connectDataTransfer(hostNameB, portNumberB);

                                    // dir
                                    if (command.equals("dir")) {
                                        System.out.println("--> " + "LIST");
                                        controlWriter.write("LIST\r\n");
                                        controlWriter.flush();

                                        String fromServerB;
                                        while ((fromServer = controlReader.readLine()) != null) {
                                            System.out.println("<-- " + fromServer);

                                            while ((fromServerB = dataReader.readLine()) != null) {
                                                System.out.println(fromServerB);
                                            }

                                            if (fromServer.startsWith("226")) break;
                                        }
                                    }
                                    // get
                                    else if (command.equals("get")) {
                                        String ftpCMD = "RETR " + inputWords[1];
                                        System.out.println("--> " + ftpCMD);
                                        controlWriter.write("RETR " + inputWords[1] + "\r\n");
                                        controlWriter.flush();

                                        try {
                                            BufferedInputStream inputBuffer = new BufferedInputStream(dataConnection.getInputStream());
                                            BufferedOutputStream outputBuffer = null;
                                            byte[] allBytes = inputBuffer.readAllBytes();

                                            while ((fromServer = controlReader.readLine()) != null) {
                                                System.out.println("<-- " + fromServer);

                                                if (allBytes != null) {
                                                    outputBuffer = new BufferedOutputStream(new FileOutputStream(new File(inputWords[1])));
                                                    outputBuffer.write(allBytes);
                                                }

                                                if (fromServer.startsWith("226")) break;
                                            }
                                            inputBuffer.close();
                                            if (outputBuffer != null) {
                                                outputBuffer.close();
                                            }
                                        } catch (IOException e) {
                                            readControl();
                                            System.err.println("0x38E Access to local file " + inputWords[1] + " denied.");
                                        }
                                    }
                                } catch (IOException e) {
                                    readControl();
                                    System.err.println("0x3A7 Data transfer connection I/O error, closing data connection.");
                                    dataReader.close();
                                    dataConnection.close();
                                    continue;
                                }
                            }
                            char[] toCharArray = fromServer.toCharArray();
                            if (toCharArray[3] == ' ') break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("0xFFFF Processing error. " + e);
            System.exit(1);
        }
    }
}
