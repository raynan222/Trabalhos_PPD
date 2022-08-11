package cliente;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// Class responsavel pelo recebimento de mensagens do socket
public class RecebeMensagem extends Thread {
    final DataInputStream correio;
    final Socket socket;
    private Jogo jogo;
    private Ui ui;

    // Construtor
    public RecebeMensagem(Socket socket, DataInputStream correio, Jogo jogo, Ui ui){
        this.socket = socket;
        this.correio = correio;
        this.jogo = jogo;
        this.ui = ui;
    }

    @Override
    public void run() {
        String recebida;
        while (true) {
            try {
                recebida = correio.readUTF();
                String[] arrayStrings = recebida.split("@",2);
                switch (arrayStrings[0]){
                    case "empate":
                        int popUp = JOptionPane.showConfirmDialog(
                                ui,
                                "Voce deseja aceita o empate proposto?",
                                "Proposta de empate",
                                JOptionPane.YES_NO_OPTION);
                        if (popUp == 0) {
                            ui.enviaRespostas("empateAceito@empateAceito");
                            ui.resetaJogo();
                        } else if (popUp == 1) {
                            ui.enviaRespostas("empateNegado@empateNegado");
                        }
                        break;
                    case "empateNegado":
                        System.out.println("Seu adversario negou o empate!");
                        break;
                    case "tuEhPrimeiro":
                        jogo.setEu(0);
                        ui.getCanva().setFazendOq("SelecionandoCor");
                        break;
                    case "tuEhSegundo":
                        jogo.setEu(1);
                        ui.getCanva().setFazendOq("EsperandoCorAdversaria");
                        break;
                    case "queroPrimeiro": //Quem recebe eh somente o servidor
                    case "queroSegundo":
                        ui.setClientePediu(arrayStrings[1]);
                        if(ui.getTituloJanela()=="Servidor" && ui.getQueroSer()!=""){
                            ui.setOrdem();
                        }
                        break;
                    case "queroParar":
                    case "rematchNegado":
                        //Pop Inimigo nao deseja rematch encerrar conexão
                        ui.destroy();
                        break;
                    case "queroNovamente":
                        int rematch = JOptionPane.showConfirmDialog(
                                ui,
                                "Seu adversario propos uma nova partida voce aceita?",
                                "Proposta de revanche",
                                JOptionPane.YES_NO_OPTION);
                        if (rematch == 0) {
                            ui.enviaRespostas("rematchAceito@rematchAceito");
                            ui.resetaJogo();
                        } else if (rematch == 1) {
                            ui.enviaRespostas("rematchNegado@rematchNegado");
                        }
                        break;
                    case "exit":
                        ui.destroy(); //Destroy the JFrame object
                        this.socket.close();
                        this.correio.close();
                        break;
                    case "posicionamento":
                    case "jogada":
                        recebeJogada(recebida, jogo, ui);
                        break;
                    case "reseta":
                    case "rematchAceito":
                    case "empateAceito":
                        ui.resetaJogo();
                        break;
                    case "chat":
                        System.out.println(arrayStrings[1]);
                        break;
                    case "recebeCorAdversaria":
                        ui.recebeCorAdversaria(Integer.parseInt(arrayStrings[1]));
                        break;
                    default:
                }

            } catch (IOException e) {
                ui.destroy();
                e.printStackTrace();
                break;
            }
        }
    }

    private static void recebeJogada(String jogadaRecebida, Jogo jogo, Ui ui) throws IOException {
        jogo.recebeJogada(jogadaRecebida);
        if (jogo.getVencedor()!=-1){
            ui.jogoAcabou(String.valueOf(jogo.getVencedor()));
        }
    }
}
