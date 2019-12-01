package principal;

import java.util.ArrayList;

public class Algoritmo extends Constantes{
    private Dados dadosTreino;
    private Dados dadosTeste;

    private int[][] matrizConfusao = new int[N_SAIDA][N_SAIDA];

    private Camada camadaEntrada;
    private Camada camadaIntermediaria;
    private Camada camadaSaida;

    double VPGeral = 0, VNGeral = 0, FPGeral = 0, FNGeral = 0;
    ArrayList<Double> alTPR = new ArrayList<>();
    ArrayList<Double> alFPR = new ArrayList<>();

    public Algoritmo() {
        dadosTreino = new Dados("dataset_treino");
        dadosTeste = new Dados("dataset_teste");

        camadaEntrada = new Camada(N_ENTRADA, CAMADA_ENTRADA);
        camadaIntermediaria = new Camada(N_INTERMEDIARIA, CAMADA_INTERMEDIARIA);
        camadaSaida = new Camada(N_SAIDA, CAMADA_SAIDA);

        executa();
    }

    private void executa() {
        treinaRede();
        testaRede();
        printaMatriz();
        calculaMetricas();
    }

    private void treinaRede(){
        double[] entrada;
        int it = 0;

        while(it < 700) {
            for(int i = 0;i < N_INSTANCIAS;i++){
                entrada = dadosTreino.entrada.get(i);

                //neurônios da camada de entrada propagam o mesmo valor de suas entradas
                for(int j = 0;j < N_ENTRADA;j++){
                    camadaEntrada.neuronios[j].entrada = entrada[j];
                    camadaEntrada.neuronios[j].saida = entrada[j];
                }

                //zera entrada dos neuronios da camada intermediraria
                for(int j = 0;j < N_INTERMEDIARIA;j++){
                    camadaIntermediaria.neuronios[j].entrada = 0.0;
                }

                //alimenta a entrada dos neurônios da camada intermediaria
                for(int j = 0;j < N_ENTRADA;j++){
                    for(int k = 0;k < N_INTERMEDIARIA;k++){
                        camadaIntermediaria.neuronios[k].entrada += camadaEntrada.neuronios[j].saida * camadaEntrada.neuronios[j].conexoesDeSaida[k];
                    }
                }

                //saida = 1 / (1 + Exp(-somatorio))
                //isto é, aplica função signoidal
                for(int j = 0;j < N_INTERMEDIARIA;j++){
                    camadaIntermediaria.neuronios[j].saida = 1.0 / (1.0 + Math.exp(-camadaIntermediaria.neuronios[j].entrada));
                }

                //zera a entrada dos neurônios da camada de saída
                for(int j = 0;j < N_SAIDA;j++){
                    camadaSaida.neuronios[j].entrada = 0.0;
                }

                //alimenta entrada dos neurônios da camada de saída
                for(int j = 0;j < N_INTERMEDIARIA;j++){
                    for(int k = 0;k < N_SAIDA;k++){
                        camadaSaida.neuronios[k].entrada += camadaIntermediaria.neuronios[j].saida * camadaIntermediaria.neuronios[j].conexoesDeSaida[k];
                    }
                }

                //saida = 1 / (1 + Exp(-somatorio))
                //isto é, aplica função sigmoidal
                for(int j = 0;j < N_SAIDA;j++){
                    camadaSaida.neuronios[j].saida = 1.0 / (1.0 + Math.exp(-camadaSaida.neuronios[j].entrada));
                }

                //método que calcula os erros dos neurônios
                calculaErro(i);

                //método que ajusta os pesos das conexões
                ajustaPesos();
            }
            it++;
        }
    }

    private void calculaErro(int instancia) {
        double fatorErro = 0.0;
        double[] saidaEsperada = dadosTreino.saidaEsperada.get(instancia);

        //calcula o erro de cada neuronio da camada de saida
        for (int i = 0; i < N_SAIDA; i++) {
            //FatorErro de neuronio na camada de saída=SaídaEsperada–SaídaAtualNeuronio
            fatorErro = saidaEsperada[i] - camadaSaida.neuronios[i].saida;

            //Neuronio.Erro = Neuronio.Saida * (1 - Neuronio.Saida) * FatorErro
            camadaSaida.neuronios[i].erro = camadaSaida.neuronios[i].saida * (1.0 - camadaSaida.neuronios[i].saida) * fatorErro;
        }

        //calcula o erro de cada neuronio da camada intermediaria
        fatorErro = 0.0;
        for (int i = 0; i < N_INTERMEDIARIA; i++) {
            for (int j = 0; j < N_SAIDA; j++) {
                fatorErro += camadaSaida.neuronios[j].erro * camadaIntermediaria.neuronios[i].conexoesDeSaida[j];
            }

            //X1.Erro = X1.Saida * (1 - X1.Saida) * FatorErro de X1
            camadaIntermediaria.neuronios[i].erro = camadaIntermediaria.neuronios[i].saida * (1.0 - camadaIntermediaria.neuronios[i].saida) * fatorErro;
            fatorErro = 0;
        }
    }

    private void ajustaPesos(){

        //Ajuste dos pesos das conexoes
        //Novo_peso = Peso_anterior * momentum + Taxa_aprendizagem * Saída_neurônio_anterior * Erro_neur_posterior
        for(int i  = 0;i < N_ENTRADA;i++){
            for(int j = 0;j < N_INTERMEDIARIA;j++){
                camadaEntrada.neuronios[i].conexoesDeSaida[j] = camadaEntrada.neuronios[i].conexoesDeSaida[j] * MOMENTUM +
                        TAXA_APRENDIZAGEM * camadaEntrada.neuronios[i].saida * camadaIntermediaria.neuronios[j].erro;
            }
        }

        //Ajuste dos pesos dos neurônios da camada intermediária
        for(int i = 0;i < N_INTERMEDIARIA;i++){
            for(int j = 0;j < N_SAIDA;j++){
                camadaIntermediaria.neuronios[i].conexoesDeSaida[j] = camadaIntermediaria.neuronios[i].conexoesDeSaida[j] * MOMENTUM +
                        TAXA_APRENDIZAGEM * camadaIntermediaria.neuronios[i].saida * camadaSaida.neuronios[j].erro;
            }
        }
    }

    private void testaRede(){
        double[] entrada;
        for (int i = 0; i < 94; i++) { //testa os 94 dos 100 dados do dataset de treino (6 deles são caraceteres aleatórios e não farão parte das estatísticas)
            entrada = dadosTeste.entrada.get(i);

            //neurônios da camada de entrada propagam o mesmo valor de suas entradas
            for (int j = 0; j < N_ENTRADA; j++) {
                camadaEntrada.neuronios[j].entrada = entrada[j];
                camadaEntrada.neuronios[j].saida = entrada[j];
            }

            //zera entrada dos neuronios da intermediraria
            for (int j = 0; j < N_INTERMEDIARIA; j++) {
                camadaIntermediaria.neuronios[j].entrada = 0.0;
            }

            //alimenta a entrada dos neurônios da camada intermediaria
            for (int j = 0; j < N_ENTRADA; j++) {
                for (int k = 0; k < N_INTERMEDIARIA; k++) {
                    camadaIntermediaria.neuronios[k].entrada += camadaEntrada.neuronios[j].saida * camadaEntrada.neuronios[j].conexoesDeSaida[k];
                }
            }

            //saida = 1 / (1 + Exp(-somatorio))
            //isto é, aplica a função sigmoidal
            for (int j = 0; j < N_INTERMEDIARIA; j++) {
                camadaIntermediaria.neuronios[j].saida = 1.0 / (1.0 + Math.exp(-camadaIntermediaria.neuronios[j].entrada));
            }

            //zera a entrada da saida
            for (int j = 0; j < N_SAIDA; j++) {
                camadaSaida.neuronios[j].entrada = 0.0;
            }

            //alimenta entrada dos neurônios da camada de saída
            for (int j = 0; j < N_INTERMEDIARIA; j++) {
                for (int k = 0; k < N_SAIDA; k++) {
                    camadaSaida.neuronios[k].entrada += camadaIntermediaria.neuronios[j].saida * camadaIntermediaria.neuronios[j].conexoesDeSaida[k];
                }
            }

            //saida = 1 / (1 + Exp(-somatorio))
            //isto é, aplica a função sigmoidal
            for (int j = 0; j < N_SAIDA; j++) {
                camadaSaida.neuronios[j].saida = 1.0 / (1.0 + Math.exp(-camadaSaida.neuronios[j].entrada));
            }

            double[] saidaEsperada = dadosTeste.saidaEsperada.get(i);

            //chama a função de avaliação do resultado passando o índice do resultado esperado (p. ex., se esperava o char 0, manda 0 como parâmetro; se esperava A, manda 11)
            for(int j = 0;j < N_SAIDA;j++){
                if(saidaEsperada[j] == 1.0)
                    avaliaResultado(j);
            }
        }

        //printa o TPR e FPR de cada instância do dataset de teste
        /*System.out.println("alTPR");
        for(double i : alTPR){
            System.out.println(i);
        }

        System.out.println("alFPR");
        for(double i : alFPR){
            System.out.println(i);
        }*/
    }

    private void avaliaResultado(int indiceEsperado){
        int indiceMaior = 0;
        double valorMaior = 0;

        double TPR = 0, FPR = 0;

        //verifica qual neurônio da camada de saída tem valor maior (como estamos lidando com uma codificação Dummy, considerei que o neurônio com valor maior é o que está ativo)
        for(int i = 0;i < N_SAIDA;i++){
            if(camadaSaida.neuronios[i].saida > valorMaior){
                valorMaior = camadaSaida.neuronios[i].saida;
                indiceMaior = i;
            }
        }
        //alimenta matriz de confusão
        matrizConfusao[indiceEsperado][indiceMaior]++;

        VNGeral = VPGeral;
        FPGeral = FNGeral;
        if(indiceEsperado == indiceMaior){
            VPGeral++;
        } else {
            FNGeral++;
        }

        //TPR = VP/(VP + FN)
        try {
            TPR = VPGeral / (VPGeral + FNGeral);
            if (Double.isNaN(TPR))
                TPR = 0;
        } catch (ArithmeticException e){
            System.out.println("Erro na divisão");
        }

        //FPR = FP/(VN + FP)
        try {
            FPR = FPGeral / (VNGeral + FPGeral);
            if(Double.isNaN(FPR))
                FPR = 0;
        } catch(ArithmeticException e){
            System.out.println("Erro na divisão");
        }

        alTPR.add(new Double(TPR));
        alFPR.add(new Double(FPR));
    }

    private void printaMatriz(){
        System.out.println("## Matriz de confusão ##");
        for(int i = 0;i < N_SAIDA;i++){
            for(int j = 0;j < N_SAIDA;j++){
                System.out.print(matrizConfusao[i][j] + "  ");
            }
            System.out.println();
        }
    }

    private void calculaMetricas(){
        double acuracia, erro, sensitividade, especificidade, precisao, TPR, FPR;
        double VP = 0, VN = 0, FP = 0, FN = 0;
        VPGeral = VNGeral = FPGeral = FNGeral = 0;

        System.out.println();
        for(int i = 0;i < N_SAIDA;i++) {
            VN = FP = FN = 0;
            VP = matrizConfusao[i][i];
            VPGeral += matrizConfusao[i][i];
            for(int j = 0;j < N_SAIDA;j++){
                if(i != j){
                    FP += matrizConfusao[j][i];
                    FPGeral += matrizConfusao[j][i];

                    FN += matrizConfusao[i][j];
                    FNGeral += matrizConfusao[i][j];

                    VN += matrizConfusao[j][j];
                }
            }

            //Sensitividade/recall  = VP / (VP + FN)
            sensitividade = VP / (VP + FN);
            //Especificidade = VN / (VN + FP)
            especificidade = VN / (VN + FP);
            //Precisão = VP / (VP + FN)
            precisao = VP / (VP + FN);

            System.out.println("## Classe " + i + " ##");
            System.out.println("Sensitividade/recall: " + sensitividade);
            System.out.println("Especificidade: " + especificidade);
            System.out.println("Precisão: " + precisao);
            System.out.println();
        }

        acuracia = VPGeral/(dadosTeste.entrada.size()-6);
        erro = 1 - acuracia;

        System.out.println("## Resultados gerais ##");
        System.out.println("Acurácia: " + acuracia);
        System.out.println("Erro: " + erro);
    }
}
