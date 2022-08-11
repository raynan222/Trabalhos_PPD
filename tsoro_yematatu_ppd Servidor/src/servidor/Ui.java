package servidor;

import javax.swing.*;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class Ui extends Component implements Runnable, ActionListener, MouseListener{
    //Jframe(ui) e jpanel(canva) do jogo
    private JFrame ui;
    private Canvas canva;
    //Componentes IO do user
    JTextArea textArea;
    JPanel inputUser = new JPanel();
    JPanel output = new JPanel();
    //Botoes presentes na Ui
    JButton pedirEmpate = new JButton("Pedir por um empate");
    JButton chat = new JButton("Enviar mensagem");

    //Debugger
    private JTextArea textAreaClientes = new JTextArea(5,33);
    private JTextArea textAreaStatus = new JTextArea(5,33);
    private JTextArea textAreaLog = new JTextArea(5,33);
    private TextAreaOutputStream consoleStream = new TextAreaOutputStream(textAreaLog, "/");

    //Dimensoes da ui
    private int h;
    private int w;

    //Atributos relacionados ao jogo e programa
    private String tituloJanela;
    private Jogo jogo;
    private String queroSer = "";
    private String clientePediu = "";
    //Canal de conexão
    private DataOutputStream dataOutput;


    ////////////////////////////////////////////////////////////////////////
    //Construtor
    public Ui(int altura, int largura, Jogo jogo, String tituloJanela) {
        this.h = altura;
        this.w = largura;
        this.jogo = jogo;
        this.tituloJanela = tituloJanela;
        jogo.setMeuNome(tituloJanela);
    }

    //Create da classe instancia buttoes e porções da Ui e seta visibilidade
    public void create() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        ui = new JFrame(tituloJanela);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.setSize(w, h);
        ui.setResizable(false);
        ui.setLayout(new BorderLayout());
        //Area dos inputs//
        inputUser.add(pedirEmpate);
        pedirEmpate.addActionListener(this);

        inputUser.add(chat);
        chat.addActionListener(this);

        ui.addMouseListener(this);
        // acoes para os butoes




        // add componentes de tela ao jpanel correspondente

        // console redirect implementation
        output.setLayout(new BorderLayout());
        textAreaStatus.setCaretColor(Color.WHITE);
        textAreaLog.setCaretColor(Color.WHITE);

        output.add(new JScrollPane(textAreaStatus, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        output.add(new JScrollPane(textAreaLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.LINE_START);

        System.setOut(new PrintStream(consoleStream));

        // criacao de instancia principal do jogo
        canva = new Canvas(this);

        ui.add(canva, BorderLayout.CENTER);
        ui.add(inputUser, BorderLayout.PAGE_START);
        ui.add(output, BorderLayout.PAGE_END);

        ui.setVisible(true);

    }
    //Destroy ui, canvas e etc.
    public void destroy(){
        JOptionPane.showMessageDialog(ui, "A conexao foi encerrada!\nO programa sera fechado!");
        jogo.setJogadaFeita("exit@exit");
        ui.setVisible(false);
        ui.dispose();
        System.exit(0);
    }

    //Ao receber a respostas, servidor determina ordem de jogador e informa contraparte!
    public void setOrdem(){
        try{
            if (clientePediu.equals(queroSer)) {
                Random rand = new Random();
                int x = rand.nextInt(2);
                setEuSou(x);
            } else {
                //Se ambos querem diferentes
                if (queroSer.equals("queroPrimeiro")) {
                    setEuSou(0);
                } else {
                    setEuSou(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Executa ao conectar o servidor e cliente determinando ordem de jogadas
    public void conectado(){
        canva.setFazendOq("OrdemJogada");

        String[] opcao = {"Quero ser o primeiro!", "Posso ser o segundo."};
        //Novo
        int popUp = JOptionPane.showOptionDialog(null, tituloJanela+" deseja ser o primeiro a jogar?",
                "Ordem de jogada",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opcao, opcao[0]);
        if (popUp == 0) {
            queroSer = "queroPrimeiro";
        }else if (popUp == 1){
            queroSer = "queroSegundo";
        }

        if(tituloJanela=="Servidor" && clientePediu!=""){
            setOrdem();
        }else if(tituloJanela=="Cliente"){
            try {
                enviaRespostas(queroSer+"@"+queroSer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //Recebe a cor que o adversario selecionou passando para a canva
    public void recebeCorAdversaria(int idx){
        if(canva.getMinhaCor().equals("")){
            canva.setCorAdversaria(canva.getCoresDisponiveis()[idx]);
            canva.removeCorSelecionada(idx);
            canva.setFazendOq("SelecionandoCor");
        }
        //Se não eu sou o primeiro esperando a cor do segundo
        else if (!canva.getMinhaCor().equals("")){
            canva.setCorAdversaria(canva.getCoresDisponiveis()[idx]);
            canva.setFazendOq("Jogando");
        }
    }

    //Envio de string atraves do canal
    public void enviaRespostas(String s) throws IOException {
        dataOutput.writeUTF(s);
    }

    //Popup invocado ao usuario no fim de uma partida
    //Ele determina se deseja uma revanche
    public void jogoAcabou(String vencedor){
        try {
            if(jogo.getEuSou()==Integer.parseInt(vencedor)){
                //Popup VOCE GANHOU PARABENS!
                int vitoria = JOptionPane.showConfirmDialog(
                        ui,
                        "Parabens voce ganhou!\nDeseja jogar novamente?",
                        "Vitoria!",
                        JOptionPane.YES_NO_OPTION);
                if (vitoria == 0) {
                    enviaRespostas("queroNovamente@queroNovamente");
                } else if (vitoria == 1) {
                    enviaRespostas("queroParar@queroParar");
                }
            }else {
                int derrota = JOptionPane.showConfirmDialog(
                        ui,
                        "Voce perdeu que pena!\nDeseja jogar novamente?",
                        "Derrota!",
                        JOptionPane.YES_NO_OPTION);
                if (derrota == 0) {
                    enviaRespostas("queroNovamente@queroNovamente");

                } else if (derrota == 1) {
                    enviaRespostas("queroParar@queroParar");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    //Reset do jogo em caso de fim de partida ou pedido de empate!
    public void resetaJogo(){
        // O jogo vai ser reiniciado
        jogo.resetaJogo();
        // Nao funciona desiste ai mano
        queroSer = "";
        clientePediu = "";
        canva.resetaCanvas();
        conectado();
    }
    //Output do status do jogo score e quem esta jogando no momento
    public void setOutpuStatus() {
        //Utilizar isso para relatorio sua vez vez do inimigo + quantidade de vitoria derrotas
        String eu = tituloJanela;
        String adversario;
        int[] score = jogo.getScore();

        if (tituloJanela=="Servidor"){
            adversario = "Cliente";
        }else{
            adversario = "Servidor";
        }

        if(canva.getFazendOq()!="OrdemJogada" && canva.getFazendOq()!=""){
            if(jogo.getJogador()==jogo.getEuSou()){
                eu = eu+"(J)";
            }else{
                adversario = adversario+"(J)";
            }
        }

        this.textAreaStatus.setText("<>Score</>\n"
                + eu +": "+ score[0]+"" + "\n"
                + adversario +": "+ score[1]+"\n");
    }

    //Seta o canal de comunicação
    public void setDataOutput(DataOutputStream dataOutput) {
        this.dataOutput = dataOutput;
        conectado();
    }
    //Interface com o jogo recebe um int e o coloca como seu, para ordem de jogadas
    public void setEuSou(int jogador) throws IOException {
        jogo.setEu(jogador);
        if (jogador==0){
            canva.setFazendOq("SelecionandoCor");
            enviaRespostas("tuEhSegundo@tuEhSegundo");

        }else if(jogador==1){
            canva.setFazendOq("EsperandoCorAdversaria");
            enviaRespostas("tuEhPrimeiro@tuEhPrimeiro");
        }
    }
    //Set pedido do cliente para que o servidor determine ordem
    public void setClientePediu(String pedido){
        clientePediu = pedido;
    }

    //Retorna o jogo associado a Ui, usado na canva para reset!
    public Jogo getJogo(){
        return this.jogo;
    }
    //Retorna a canva associada a Ui
    public Canvas getCanva() {
        return canva;
    }
    public String getQueroSer(){
        return this.queroSer;
    }
    public String getTituloJanela(){
        return this.tituloJanela;
    }
    public int getAlturaAtual() {
        return this.ui.getHeight();
    }
    public int getLarguraAtual() {
        return this.ui.getWidth();
    }

    //Identifica e trabalha no botão pressionado na Ui
    public void actionPerformed(ActionEvent ae) {
        try {
            String action = ae.getActionCommand();
            if (action.equals("Pedir por um empate")) {
                if(this.jogo.isJogando()){
                    int resposta = JOptionPane.showConfirmDialog(
                            ui,
                            "Voce deseja pedir por um empate?",
                            "Empate",
                            JOptionPane.YES_NO_OPTION);
                    if (resposta == 0) {
                        enviaRespostas("empate@empate");
                    } else {
                    }
                }else{
                    JOptionPane.showMessageDialog(ui, "Para pedir por um empate a distribuição de peças deve ter ocorrida!");
                }

            }else if(action.equals("Enviar mensagem")){
                String mensagem = JOptionPane.showInputDialog( "Escreve a mensagem:" );
                enviaRespostas("chat@"+tituloJanela+": "+mensagem);
                System.out.println(tituloJanela+": "+mensagem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Recebe os clicks em tela para determinar jogada seleção de peça etc...
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (canva.getFazendOq().equals("Jogando")){
            if (jogo.getEuSou()==jogo.getJogador()) {
                if ((y >= 87 && y <= 118) && (x >= 302 && x <= 334)) {
                    //Jogada para posição 0
                    if (this.jogo.isJogando()) {
                        this.jogo.setPecaSelecionada(0);
                    } else {
                        this.jogo.posicionando(0);
                    }

                } else if (y >= 229 && y <= 260) {
                    if (x >= 230 && x <= 260) {
                        //Jogada para posição 1
                        if (this.jogo.isJogando()) {
                            this.jogo.setPecaSelecionada(1);
                        } else {
                            this.jogo.posicionando(1);
                        }
                    } else if (x >= 302 && x <= 333) {
                        //Jogada para posição 2
                        if (this.jogo.isJogando()) {
                            this.jogo.setPecaSelecionada(2);
                        } else {
                            this.jogo.posicionando(2);
                        }
                    } else if (x >= 377 && x <= 407) {
                        //Jogada para posição 3
                        if (this.jogo.isJogando()) {
                            this.jogo.setPecaSelecionada(3);
                        } else {
                            this.jogo.posicionando(3);
                        }
                    }

                } else if (y >= 346 && y <= 378) {
                    if (x >= 173 && x <= 205) {
                        //Jogada para posição 4
                        if (this.jogo.isJogando()) {
                            this.jogo.setPecaSelecionada(4);
                        } else {
                            this.jogo.posicionando(4);
                        }
                    } else if (x >= 302 && x <= 334) {
                        //Jogada para posição 5
                        if (this.jogo.isJogando()) {
                            this.jogo.setPecaSelecionada(5);
                        } else {
                            this.jogo.posicionando(5);
                        }
                    } else if (x >= 433 && x <= 464) {
                        //Jogada para posição 6
                        if (this.jogo.isJogando()) {
                            this.jogo.setPecaSelecionada(6);
                        } else {
                            this.jogo.posicionando(6);
                        }
                    }
                }
            }
        }
        else if (canva.getFazendOq().equals("SelecionandoCor")){
            try{
                if(jogo.getEuSou()==0 && canva.getMinhaCor().equals("")){
                    for (int i=0; canva.getCoresDisponiveis().length>i;i++){
                        // y  começa em 86 a 116 incrementa passo 40
                        // x  varia de 48 a 78 sempre e 40
                        if(y>=86+(40*i) && y<=116+(40*i) ){
                            if(x>=90 && x<=122){
                                canva.setMinhaCor(canva.getCoresDisponiveis()[i]);
                                String idx = Integer.toString(i);
                                enviaRespostas("recebeCorAdversaria@"+idx);
                                canva.removeCorSelecionada(i);
                                canva.setFazendOq("EsperandoCorAdversaria");
                            }
                        }
                    }
                }else if(jogo.getEuSou()==1 && !canva.getCorAdversaria().equals("")){
                    for (int i=0; canva.getCoresDisponiveis().length>i;i++){
                        if(y>=86+(40*i) && y<=116+(40*i) ){
                            if(x>=513 && x<=545){
                                canva.setMinhaCor(canva.getCoresDisponiveis()[i]);
                                String idx = Integer.toString(i);
                                enviaRespostas("recebeCorAdversaria@"+idx);
                                canva.setFazendOq("Jogando");
                            }
                        }
                    }
                }
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////
    //Ferramentas para debug
    public void setOutpuLog(String texto) {
        this.textAreaLog.setText("<>LOG</>\n" + texto);
    }
    public void setOutpuClientes(String texto) {
        this.textAreaClientes.setText("<>CLIENTES</>\n" + texto);
    }
    //Obrigatorio pelo implements
    public void run() {
    }
    public void restart() {
    }
    public void mousePressed(MouseEvent e) {

    }
    public void mouseReleased(MouseEvent e) {

    }
    public void mouseEntered(MouseEvent e) {

    }
    public void mouseExited(MouseEvent e) {

    }
}
