package servidor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// Class responsavel pelo envio de mensagens do jogo
public class EnviaJogadas extends Thread {
    final DataOutputStream destino;
    final Socket socket;
    private Jogo jogo;
    private Ui ui;

    // Construtor
    public EnviaJogadas(Socket socket, DataOutputStream destino, Jogo jogo, Ui ui) {
        this.socket = socket;
        this.destino = destino;
        this.jogo = jogo;
        this.ui = ui;
    }

    @Override
    public void run() {
        String enviar;
        while (true) {
            enviar=jogo.getJogadaFeita();
            String[] arrayStrings = enviar.split("@",2);
            try{
                switch (arrayStrings[0]) {
                    case "exit":
                        destino.writeUTF(jogo.getJogadaFeita());
                        this.socket.close();
                        this.destino.close();
                        break;
                    case "jogada":
                    case "posicionamento":
                        realizaJogada(destino, jogo);
                        break;
                    case "reseta":
                        jogo.resetaJogo();
                        System.out.println("Jogo reiniciado!");
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }


    private static void realizaJogada(DataOutputStream dataOutput, Jogo jogo) throws IOException {
        dataOutput.flush();
        dataOutput.writeUTF(jogo.getJogadaFeita());
        jogo.limpaJogadaFeita();
    }
}
