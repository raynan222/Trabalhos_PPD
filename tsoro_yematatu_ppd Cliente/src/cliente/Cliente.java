package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Cliente {
    public static void main(String args[]){
        int h = 480;
        int w = 640;
        Jogo jogo = new Jogo();
        jogo.setEu(-1);

        Ui ui = new Ui(h, w, jogo,  "Cliente");
        ui.create();

        try{
            InetAddress ip = InetAddress.getByName("localhost");

            // Estabelece a porta de conexão
            Socket s = new Socket(ip, 5056);


            // Cria input e out streams
            DataInputStream dataInput = new DataInputStream(s.getInputStream());
            DataOutputStream dataOutput = new DataOutputStream(s.getOutputStream());

            //Criando as thread de comunicação
            Thread recebe = new RecebeMensagem(s, dataInput, jogo, ui);
            Thread envia = new EnviaJogadas(s, dataOutput, jogo, ui);

            recebe.start();
            envia.start();
            ui.setDataOutput(dataOutput);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}


//public class Cliente{
//    public static void main(String[] args) throws IOException
//    {
//        try
//        {
//            Scanner scn = new Scanner(System.in);
//
//            // getting localhost ip
//            InetAddress ip = InetAddress.getByName("localhost");
//
//            // establish the connection with server port 5056
//            Socket s = new Socket(ip, 5056);
//
//            // obtaining input and out streams
//            DataInputStream dataInput = new DataInputStream(s.getInputStream());
//            DataOutputStream dataOutput = new DataOutputStream(s.getOutputStream());
//
//            // the following loop performs the exchange of
//            // information between client and client handler
//            while (true)
//            {
//                System.out.println(dataInput.readUTF());
//                String toSend = scn.nextLine();
//                dataOutput.writeUTF(toSend);
//
//
//                // If client sends exit,close this connection
//                // and then break from the while loop
//                if(toSend.equals("Exit"))
//                {
//                    System.out.println("Closing this connection : " + s);
//                    s.close();
//                    System.out.println("Connection closed");
//                    break;
//                }
//
//                // printing date or time as requested by client
//                String received = dataInput.readUTF();
//                //System.out.println(received);
//                System.out.println(parOuImpar(Integer.parseInt(received), Integer.parseInt(toSend)));
//            }
//
//            // closing resources
//            scn.close();
//            dataInput.close();
//            dataOutput.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//    private static String parOuImpar(int j1, int j2) {
//        var resultado = ((j1+j2)%2)==0 ? "Eh par!" : "Eh impar!";
//        return "Serv="+j1+"\nCli="+j2+"\n"+resultado;
//    }
//}

