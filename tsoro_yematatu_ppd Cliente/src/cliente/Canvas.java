package cliente;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Canvas extends JPanel implements Runnable {
    //Remover
    //private static final long serialVersionUID = 1L;

    private Ui ui;
    private Thread gameloop = new Thread(this);

    private String[] coresDisponiveis;
    private String[] todasCores = {"tsoro_yematatu_ppd/Data/Vermelho.png", "tsoro_yematatu_ppd/Data/Bege.png", "tsoro_yematatu_ppd/Data/Azul.png", "tsoro_yematatu_ppd/Data/Magenta.png", "tsoro_yematatu_ppd/Data/Ciano.png", "tsoro_yematatu_ppd/Data/Amarelo.png"};
    private String minhaCor = "";
    private String corAdversaria = "";
    private String fazendOq = "";

    private int h = 0;
    private int w = 0;
    private Jogo jogo;

    //Construtor
    public Canvas(Ui ui) {
        this.ui = ui;
        this.h = ui.getAlturaAtual();
        this.w = ui.getLarguraAtual();
        this.jogo = ui.getJogo();
        gameloop.start();
        this.coresDisponiveis = todasCores;
    }

    //Reseta o canvas para nova partida
    public void resetaCanvas(){
        fazendOq="";
        coresDisponiveis = todasCores;
        minhaCor = "";
        corAdversaria = "";
    }

    //Redimensiona imagem(não utilizado)
    public static BufferedImage resize(BufferedImage img, int W, int H) {
        Image temp = img.getScaledInstance(W, H, Image.SCALE_SMOOTH);
        BufferedImage novaImagem = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = novaImagem.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();
        return novaImagem;
    }

    //Associação de FPS do jogo sleep(Frametime)
    public void sleep() {
        try {
            Thread.sleep(64);
        }
        catch(InterruptedException e) {
            Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //Thread run
    public void run() {
        long timer = System.currentTimeMillis();

        while(true) {
            repaint();
            sleep();
            //Atualiza o score a cada 1 segundo
            if(System.currentTimeMillis() - timer > 1000)
            {
                escreveStatus();
                timer+= 1000;
            }
        }
    }

    //Status do jogo
    public void escreveStatus() {
        this.ui.setOutpuStatus();
    }

    //Desenha os componentes na tela como tabuleiro, peças, stack de peças etc
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int x = 160;
        int y = 20;
        try {
            switch (this.fazendOq) {
                case "Jogando":
                    // paint background
                    BufferedImage tsoro = ImageIO.read(new File("tsoro_yematatu_ppd/Data/TsoroTrans.png"));
                    g2d.drawImage(tsoro, null, x, y);

                    // Stack de peças a jogar
                    if(jogo.getEuSou()==1){
                        printStackFora(g2d, corAdversaria, minhaCor);

                        // Peças no tabuleiro
                        printEmJogo(g2d, corAdversaria, minhaCor);
                    }else{
                        printStackFora(g2d, minhaCor, corAdversaria);

                        // Peças no tabuleiro
                        printEmJogo(g2d, minhaCor, corAdversaria);
                    }

                    break;
                case "SelecionandoCor":
                    if (minhaCor != "" && corAdversaria != "") {
                        fazendOq = "Jogando";
                    }
                    printEscolhaCor(g2d, coresDisponiveis);
                    break;
                case "EsperandoCorAdversaria":
                    BufferedImage holdCor = ImageIO.read(new File("tsoro_yematatu_ppd/Data/EsperandoCorAdversariaTrans.png"));
                    g2d.drawImage(holdCor, null, x, y);
                    break;
                case "OrdemJogada":
                    BufferedImage holdOrdem = ImageIO.read(new File("tsoro_yematatu_ppd/Data/BrancoTrans.png"));
                    g2d.drawImage(holdOrdem, null, x, y);
                    break;
                case "":
                    BufferedImage hold = ImageIO.read(new File("tsoro_yematatu_ppd/Data/EsperandoTrans.png"));
                    g2d.drawImage(hold, null, x, y);
                    break;
            }
        }catch(IOException e) {
            Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //Remove das cores disponvieis a selecionada por um jogoador
    public void removeCorSelecionada(int idx){
        LinkedList<String> linkedList = new LinkedList<String>();
        Collections.addAll(linkedList, coresDisponiveis);
        linkedList.remove(idx);
        String[] novasCores = linkedList.toArray(new String[linkedList.size()]);
        coresDisponiveis = novasCores;
    }

    //Print de um peça na tela
    private void printPeca (Graphics2D g2d, String cor,int x, int y){
        try {
            g2d.drawImage(ImageIO.read(new File(cor)),  null,  x, y);
        }
        catch(IOException e) {
            Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //Print as peças no jogo
    private void printEmJogo (Graphics2D g2d, String cor1, String cor2){
        int x,y;
        String corVitoria = "tsoro_yematatu_ppd/Data/verde.png";

        if(this.jogo.getTabuleiro()[0]!=-1){
            x = 295;
            y = 20;
            if (this.jogo.getTabuleiro()[0]==0){
                printPeca( g2d, cor1, x, y);
            }else if (this.jogo.getTabuleiro()[0]==1){
                printPeca( g2d, cor2, x, y);
            }else {
                printPeca( g2d, corVitoria, x, y);
            }
        }

        if(this.jogo.getTabuleiro()[1]!=-1){
            x = 222;
            y = 163;
            if (this.jogo.getTabuleiro()[1]==0){
                printPeca( g2d, cor1, x, y);
            }else if (this.jogo.getTabuleiro()[1]==1){
                printPeca( g2d, cor2, x, y);
            }else {
                printPeca( g2d, corVitoria, x, y);
            }
        }

        if(this.jogo.getTabuleiro()[2]!=-1){
            x = 295;
            y = 163;
            if (this.jogo.getTabuleiro()[2]==0){
                printPeca( g2d, cor1, x, y);
            }else if (this.jogo.getTabuleiro()[2]==1){
                printPeca( g2d, cor2, x, y);
            }else {
                printPeca( g2d, corVitoria, x, y);
            }
        }

        if(this.jogo.getTabuleiro()[3]!=-1){
            x = 368;
            y = 163;
            if (this.jogo.getTabuleiro()[3]==0){
                printPeca( g2d, cor1, x, y);
            }else if (this.jogo.getTabuleiro()[3]==1){
                printPeca( g2d, cor2, x, y);
            }else {
                printPeca( g2d, corVitoria, x, y);
            }
        }

        if(this.jogo.getTabuleiro()[4]!=-1){
            x = 165;
            y = 279;
            if (this.jogo.getTabuleiro()[4]==0){
                printPeca( g2d, cor1, x, y);
            }else if (this.jogo.getTabuleiro()[4]==1){
                printPeca( g2d, cor2, x, y);
            }else {
                printPeca( g2d, corVitoria, x, y);
            }
        }

        if(this.jogo.getTabuleiro()[5]!=-1){
            x = 295;
            y = 279;
            if (this.jogo.getTabuleiro()[5]==0){
                printPeca( g2d, cor1, x, y);
            }else if (this.jogo.getTabuleiro()[5]==1){
                printPeca( g2d, cor2, x, y);
            }else{
                printPeca( g2d, corVitoria, x, y);
            }
        }

        if(this.jogo.getTabuleiro()[6]!=-1){
            x = 425;
            y = 279;
            if (this.jogo.getTabuleiro()[6]==0){
                printPeca( g2d, cor1, x, y);
            }else if (this.jogo.getTabuleiro()[6]==1){
                printPeca( g2d, cor2, x, y);
            }else {
                printPeca( g2d, corVitoria, x, y);
            }
        }

    }

    //Print peças com cores disponiveis para a escolha
    private void printEscolhaCor (Graphics2D g2d, String[] cores){
        if(this.jogo.getEuSou()==0){
            int y=20;
            for (int i=0; cores.length>i; i++){
                try {
                    g2d.drawImage(ImageIO.read(new File(cores[i])),  null,  82,  y);
                }
                catch(IOException e) {
                    Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, e);
                }
                y += 40;
            }
        }else if (this.jogo.getEuSou()==1){
            int y=20;
            for (int i=0; cores.length>i; i++){
                try {
                    g2d.drawImage(ImageIO.read(new File(cores[i])),  null,  506,  y);
                }
                catch(IOException e) {
                    Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, e);
                }
                y += 40;
            }
        }
    }

    //Print da stack disponivel para posicionamento
    private void printStackFora (Graphics2D g2d, String cor1, String cor2){
        if( this.jogo.getPecas()[1]!=0 || this.jogo.getPecas()[0]!=0){
            int y=20;
            for (int i=0; this.jogo.getPecas()[0]!=i; i++){
                try {
                    g2d.drawImage(ImageIO.read(new File(cor1)),  null,  82,  y);
                }
                catch(IOException e) {
                    Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, e);
                }
                y += 20;
            }

            y=20;
            for (int i=0; this.jogo.getPecas()[1]!=i; i++){
                try {
                    g2d.drawImage(ImageIO.read(new File(cor2)),  null,  506,  y);
                }
                catch(IOException e) {
                    Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, e);
                }
                y += 20;
            }
        }
    }

    //Cores para impressão em tela
    public void setMinhaCor(String minhaCor) {
        this.minhaCor = minhaCor;
    }
    public void setCorAdversaria(String corAdversaria) {
        this.corAdversaria = corAdversaria;
    }

    //////////////////////////////////////////////////////////////////////
    public void setFazendOq(String fazendOq) {
        this.fazendOq = fazendOq;
    }
    public String getFazendOq() {
        return fazendOq;
    }
    public String getMinhaCor() {
        return minhaCor;
    }
    public String getCorAdversaria() {
        return corAdversaria;
    }
    public String[] getCoresDisponiveis() {
        return coresDisponiveis;
    }
}
