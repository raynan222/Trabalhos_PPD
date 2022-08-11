package servidor;

import java.io.*;
import java.net.*;

public class Servidor
{
    public static void main(String[] args) throws IOException
    {
        int h = 480;
        int w = 640;
        Jogo jogo = new Jogo();

        jogo.setEu(-1);

        // server is listening on port 5056
        ServerSocket socket = new ServerSocket(5056);
        Ui ui = new Ui(h, w, jogo, "Servidor");
        ui.create();

        Socket s = null;
        try {
            s = socket.accept();

            System.out.println("Cliente conectado: " + s);

            // Cria input e out streams
            DataInputStream dataInput = new DataInputStream(s.getInputStream());
            DataOutputStream dataOutput = new DataOutputStream(s.getOutputStream());

            //Criando as thread de comunicação
            Thread recebe = new RecebeMensagem(s, dataInput, jogo, ui);
            Thread envia = new EnviaJogadas(s, dataOutput, jogo, ui);


            recebe.start();
            envia.start();
            ui.setDataOutput(dataOutput);

        }catch (Exception e){
            s.close();
            e.printStackTrace();
        }
    }
}

