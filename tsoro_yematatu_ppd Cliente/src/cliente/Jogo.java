package cliente;

import java.util.Arrays;

public class Jogo {
    private int[] tabuleiro = new int[] {-1, -1, -1, -1, -1, -1, -1}; //Tabuleiro do jogo, -1 vazio, 0 e 1 jogadores, 2 vencedor
    private int[] pecas =  new int[] {3, 3}; //Peças em stack ainda a ser posicionada
    private int[] score = new int[] {0,0}; //Score da partida idx [0(Eu) - 1(Adversario)]
    private int jogador = 0; //Turno em jogo
    private int eu = -1; //Utilizado para determina se usuario pode jogar
    private boolean jogando = false; //Flag para saber se ainda esta em setting
    private int pecaSelecionada = -1; //Peça selecionada para a jogada
    private String jogadaFeita = ""; //Jogada a ser enviada pelo canal
    private int vencedor = -1; //Vencedor da partida
    private String meuNome = "";

    public void setMeuNome(String s){
        meuNome = s;
    }
    //Utilizado para resetar o jogo para revanche, nova partida e etc...
    public void resetaJogo(){
        this.tabuleiro = new int[] {-1, -1, -1, -1, -1, -1, -1};
        this.pecas = new int[] {3, 3};
        this.jogador = 0;
        this.eu = -1;
        this.jogando = false;
        this.pecaSelecionada = -1;
        this.jogadaFeita = "";
        this.vencedor = -1;
    }
    //Recebe uma jogada
    public void recebeJogada(String jogadaRecebida){
        String[] arrayStrings = jogadaRecebida.split("@",2);
        if(arrayStrings[0].equals("posicionamento")){
            System.out.println("Pos: "+arrayStrings[1]);
            this.posicionando(Integer.parseInt(arrayStrings[1]));
        }else if(arrayStrings[0].equals("jogada")){
            String[] arrayJogada = arrayStrings[1].split(",");
            int j0 = Integer.parseInt(arrayJogada[0]);
            int j1 = Integer.parseInt(arrayJogada[1]);
            System.out.println("Mov: "+j0+"->"+j1);
            this.setPecaSelecionada(j0);
            this.setPecaSelecionada(j1);
        }
    }
    //Armazena a jogada feita para passagem atraves do canal de comunicação
    private void jogadaFeita(String afixo, int pecaSelecionada, int pecaSelecionada2){
        if(afixo.equals("posicionamento@")){
            this.jogadaFeita = afixo+Arrays.toString(new int[] {pecaSelecionada}).replaceAll("[\\s\\]\\[]","");
        }else if(afixo.equals("jogada@")){
            this.jogadaFeita = afixo+Arrays.toString(new int[] {pecaSelecionada2, pecaSelecionada}).replaceAll("[\\s\\]\\[]","");
        }
    }
    //Recebe a jogada e trabalha com a peça selecionada para saber se é valida
    private void jogada(int posicaoVazia){
        //Fazer a jogada usando o this.pecaSelecionada
        if (tabuleiro[posicaoVazia]==-1 && (isSalto(posicaoVazia) || isPassagem(posicaoVazia))) {
            //Passagem para o outro jogador
            if(eu==jogador){
                jogadaFeita("jogada@", posicaoVazia, pecaSelecionada);
            }
            tabuleiro[posicaoVazia]=jogador;
            tabuleiro[pecaSelecionada]=-1;
            pecaSelecionada = -1;
            trocaJogador();
        }else{
            System.out.println("Jogada Invalida!");
        }
        verificaVencedor();
    }
    //Verifica se a jogada foi um salto
    private boolean isSalto(int posicaoVazia){
        int[] vetorJogada = new int[]{posicaoVazia, pecaSelecionada};
        Arrays.sort(vetorJogada);

        int[][] vetorSaltos = new int[][]{{0,4},{0,5},{0,6},{1,3},{4,6}};
        for(int i=0; vetorSaltos.length>i; i++){
            if(Arrays.equals(vetorSaltos[i],vetorJogada)){
                return true;
            }
        }
        return false;
    }
    //verifica se a jogada foi uma passagem
    private boolean isPassagem(int posicaoVazia){
        int[] vetorJogada = new int[]{posicaoVazia, pecaSelecionada};
        Arrays.sort(vetorJogada);

        int[][] vetorPassagens = new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 4}, {2, 3}, {2, 5}, {3, 6}, {4, 5}, {5, 6}};
        for(int i=0; vetorPassagens.length>i; i++){
            if(Arrays.equals(vetorPassagens[i],vetorJogada)){
                return true;
            }
        }
        return false;
    }
    //Muda de jogador apos uma jogada
    private void trocaJogador(){
        if (jogador==0) {
            jogador = 1;
        }else{
            jogador = 0;
        }
    }
    //Posiciona peças
    public void posicionando(int posicao){
        if (tabuleiro[posicao]==-1){
            tabuleiro[posicao] = jogador;
            pecas[jogador] -= 1;

            if(eu==jogador){
                jogadaFeita("posicionamento@", posicao, 0);
            }

            trocaJogador();
        }else{
            System.out.println("Posicionamento Invalida!");
        }

        if(pecas[0]==0 && pecas[1]==0){
            jogando = true;
            verificaVencedor();
        }
    }
    //Verifica se houve vencer
    private void verificaVencedor() {
        if (tabuleiro[0]==tabuleiro[1] && tabuleiro[1]==tabuleiro[4]){
            vencedor = tabuleiro[0];
            jogador = vencedor;
            tabuleiro[0]=tabuleiro[1]=tabuleiro[4]=2;

        } else if (tabuleiro[0]==tabuleiro[2] && tabuleiro[2]==tabuleiro[5]){
            vencedor = tabuleiro[0];
            jogador = vencedor;
            tabuleiro[0]=tabuleiro[2]=tabuleiro[5]=2;

        } else if (tabuleiro[0]==tabuleiro[3] && tabuleiro[3]==tabuleiro[6]){
            vencedor = tabuleiro[0];
            jogador = vencedor;
            tabuleiro[0]=tabuleiro[3]=tabuleiro[6]=2;

        } else if (tabuleiro[1]==tabuleiro[2] && tabuleiro[2]==tabuleiro[3]){
            vencedor = tabuleiro[1];
            jogador = vencedor;
            tabuleiro[1]=tabuleiro[2]=tabuleiro[3]=2;

        } else if (tabuleiro[4]==tabuleiro[5] && tabuleiro[5]==tabuleiro[6]){
            vencedor = tabuleiro[4];
            jogador = vencedor;
            tabuleiro[4]=tabuleiro[5]=tabuleiro[6]=2;
        }

        if (vencedor!=-1){
            if(eu==vencedor){
                score[0]++;
            }else{
                score[1]++;
            }
        }
    }
    //Utilizado para permitir a leitura de clicks e posicionamento
    public boolean isJogando() {
        return jogando;
    }
    //Limpa jogada realizada apos o envio
    public void limpaJogadaFeita(){
        this.jogadaFeita="";
    }

    //Utilizado para ordenação de jogadas e verificações como vitoria
    public void setEu(int eu){
        this.eu = eu;
    }
    //Seta a peça selecionada para a jogada
    public void setPecaSelecionada(int peca) {
        if(jogador==tabuleiro[peca] && pecaSelecionada==-1){
            tabuleiro[peca] = 2;
            pecaSelecionada = peca;
        }else if(peca==pecaSelecionada){
            tabuleiro[pecaSelecionada] = jogador;
            pecaSelecionada = -1;
        }
        else if(pecaSelecionada!=-1){
            jogada(peca);
        }
    }
    //Seta uma string para destruição do jogo??
    public void setJogadaFeita(String s) {
        this.jogadaFeita=s;
    }

    public int getEuSou(){
        return eu;
    }
    public int[] getScore() {
        return score;
    }
    public int[] getPecas() {
        return pecas;
    }
    public int getJogador() {
        return jogador;
    }
    public int getVencedor() {
        return vencedor;
    }
    public String getMeuNome(){
        return meuNome;
    }
    public int[] getTabuleiro() {
        return tabuleiro;
    }
    public String getJogadaFeita(){
        return jogadaFeita;
    }
}
