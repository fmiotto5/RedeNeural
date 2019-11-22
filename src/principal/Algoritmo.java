package principal;


public class Algoritmo extends Constantes{
    private Dados dadosTreino;
    private Dados dadosTeste;

    private int[][] matrizConfusao = new int[N_SAIDA][N_SAIDA];

    private Camada camadaEntrada;
    private Camada camadaIntermediaria;
    private Camada camadaSaida;

    //saida, tem que obrigatoriamente estar acima de 0.5?
    // se entrar um dado bem fora dos padroes, tem que sair algum neuronio > 0.5?

    //como eu coloco a saida esperada de um caractere fora dos padroes?

    public Algoritmo(){
//        dadosTreino = new Dados("entrada");
        dadosTreino = new Dados("dataset_treino");
        dadosTeste = new Dados("dataset_teste");

        camadaEntrada = new Camada(N_ENTRADA, CAMADA_ENTRADA);
        camadaIntermediaria = new Camada(N_INTERMEDIARIA, CAMADA_INTERMEDIARIA);
        camadaSaida = new Camada(N_SAIDA, CAMADA_SAIDA);

        executa();
    }

    private void executa(){
        treinaRede();
        testaRede();
        printaMatriz();
        calculaMetricas();
    }

    private void treinaRede(){
        double[] entrada;
        int it = 0;

        while(it < 1000) {
            for(int i = 0;i < N_INSTANCIAS;i++){
                entrada = dadosTreino.entrada.get(i);

                for(int j = 0;j < N_ENTRADA;j++){
                    camadaEntrada.neuronios[j].entrada = entrada[j];
                    camadaEntrada.neuronios[j].saida = entrada[j];
                }

                //zera entrada dos neuronios da intermediraria
                for(int j = 0;j < N_INTERMEDIARIA;j++){
                    camadaIntermediaria.neuronios[j].entrada = 0.0;
                }

                //passa para camada intermediaria
                for(int j = 0;j < N_ENTRADA;j++){
                    for(int k = 0;k < N_INTERMEDIARIA;k++){
                        camadaIntermediaria.neuronios[k].entrada += camadaEntrada.neuronios[j].saida * camadaEntrada.neuronios[j].conexoesDeSaida[k];
                    }
                }

                //saida = 1 / (1 + Exp(-somatorio))
                //signoidal
                for(int j = 0;j < N_INTERMEDIARIA;j++){
                    camadaIntermediaria.neuronios[j].saida = 1.0 / (1.0 + Math.exp(-camadaIntermediaria.neuronios[j].entrada));
                }

                //zera a entrada da saida
                for(int j = 0;j < N_SAIDA;j++){
                    camadaSaida.neuronios[j].entrada = 0.0;
                }

                for(int j = 0;j < N_INTERMEDIARIA;j++){
                    for(int k = 0;k < N_SAIDA;k++){
                        camadaSaida.neuronios[k].entrada += camadaIntermediaria.neuronios[j].saida * camadaIntermediaria.neuronios[j].conexoesDeSaida[k];
                    }
                }

                //saida = 1 / (1 + Exp(-somatorio))
                //sigmoidal
                for(int j = 0;j < N_SAIDA;j++){
                    camadaSaida.neuronios[j].saida = 1.0 / (1.0 + Math.exp(-camadaSaida.neuronios[j].entrada));
                }

                calculaErro(i);

                ajustaPesos();

//                double[] s = dadosTreino.saidaEsperada.get(i);
//                if (i == 0)
//                    System.out.println("Iteração: " + it);
//
//                for (int j = 0; j < N_SAIDA; j++) {
//                    if(i == 0) {
//                        System.out.println("Saída (sigmoidal) do neuronio " + j + ": " + camadaSaida.neuronios[j].saida + " | saida esperada: " + s[j] + " | entrada (somatório): " + camadaSaida.neuronios[j].entrada + " | erro: " + camadaSaida.neuronios[j].erro);
//                    }
//                }

//                for (int j = 0; j < N_INTERMEDIARIA; j++) {
//                    if(i == 0) {
//                        System.out.println("Saída do neuronio " + j + ": " + camadaIntermediaria.neuronios[j].saida + " | entrada: " + camadaIntermediaria.neuronios[j].entrada + " | erro: " + camadaIntermediaria.neuronios[j].erro);
//                    }
//                }



//                if (i == 0)
//                    System.out.println("\n");

            }
            it++;
        }
    }

    private void calculaErro(int instancia) {
        double fatorErro = 0.0;
        double[] saidaEsperada = dadosTreino.saidaEsperada.get(instancia);


        for(int i = 0;i < N_SAIDA;i++){
            //FatorErro de neuronio na camada de saída=SaídaEsperada–SaídaAtualNeuronio
            fatorErro = saidaEsperada[i] - camadaSaida.neuronios[i].saida;
            //Neuronio.Erro = Neuronio.Saida * (1 - Neuronio.Saida) * FatorErro
            camadaSaida.neuronios[i].erro = camadaSaida.neuronios[i].saida * (1.0 - camadaSaida.neuronios[i].saida) * fatorErro;
        }

        fatorErro = 0.0;
        for(int i = 0;i < N_INTERMEDIARIA;i++){
            for(int j = 0;j < N_SAIDA;j++){
                fatorErro += camadaSaida.neuronios[j].erro * camadaIntermediaria.neuronios[i].conexoesDeSaida[j];
            }
            //X1.Erro = X1.Saida * (1 - X1.Saida) * FatorErro de X1
            camadaIntermediaria.neuronios[i].erro = camadaIntermediaria.neuronios[i].saida * (1.0 - camadaIntermediaria.neuronios[i].saida) * fatorErro;
            fatorErro = 0;
        }

        //calcula o erro de cada neuronio da camada de saida
//        for (int i = 0; i < N_SAIDA; i++) {
//            fatorErro = (double)saidaEsperada[i] - camadaSaida.neuronios[i].saida;
//            camadaSaida.neuronios[i].erro = camadaSaida.neuronios[i].saida * (1.0 - camadaSaida.neuronios[i].saida) * fatorErro;
//        }

        //calcula o erro de cada neuronio da camada intermediaria
//        fatorErro = 0;
//        for (int i = 0; i < N_INTERMEDIARIA; i++) {
//            for (int j = 0; j < N_SAIDA; j++) {
//                fatorErro += camadaSaida.neuronios[j].erro * camadaIntermediaria.neuronios[i].conexoesDeSaida[j];
//            }
//            camadaIntermediaria.neuronios[i].erro = camadaIntermediaria.neuronios[i].saida * (1.0 - camadaIntermediaria.neuronios[i].saida) * fatorErro;
//            fatorErro = 0;
//        }
    }

    private void ajustaPesos(){
        //Novo_peso=Peso_anterior*momentum+Taxa_aprendizagem*Saída_neurônio_anterior*Erro_neur_posterior

//        For cada Neuronio de Entrada N conectado a EsteNeuronio
//        Novo_Peso de N = Peso_anterior de N + taxa_de_aprendizagem * N.Saida
//                * EsteNeuronio.Erro
//        Next
        for(int i  = 0;i < N_ENTRADA;i++){
            for(int j = 0;j < N_INTERMEDIARIA;j++){
                camadaEntrada.neuronios[i].conexoesDeSaida[j] = camadaEntrada.neuronios[i].conexoesDeSaida[j] * MOMENTUM +
                        TAXA_APRENDIZAGEM * camadaEntrada.neuronios[i].saida * camadaIntermediaria.neuronios[j].erro;
            }
        }

        for(int i = 0;i < N_INTERMEDIARIA;i++){
            for(int j = 0;j < N_SAIDA;j++){
                camadaIntermediaria.neuronios[i].conexoesDeSaida[j] = camadaIntermediaria.neuronios[i].conexoesDeSaida[j] * MOMENTUM +
                        TAXA_APRENDIZAGEM * camadaIntermediaria.neuronios[i].saida * camadaSaida.neuronios[j].erro;
            }
        }

        //ajuste dos pesos das conexoes
//        for (int i = 0; i < N_SAIDA; i++) {
//            for (int j = 0; j < N_INTERMEDIARIA; j++) {
//                //Novo_Peso de N = Peso_anterior de N * momentum + taxa_de_aprendizagem * N.Saida * EsteNeuronio.Erro
//                //Novo_Peso de N é o peso da conexão ou do neuronio?
//                camadaIntermediaria.neuronios[j].conexoesDeSaida[i] = camadaIntermediaria.neuronios[j].conexoesDeSaida[i] * MOMENTUM +
//                        TAXA_APRENDIZAGEM * camadaIntermediaria.neuronios[j].saida * camadaSaida.neuronios[i].erro;
//            }
//        }
//
//        //ajustes dos pesos dos neuronios da camada intermediaria
//        for (int i = 0; i < N_INTERMEDIARIA; i++) {
//            for (int j = 0; j < N_ENTRADA; j++) {
//                //Novo_Peso de N = Peso_anterior de N * momentum + taxa_de_aprendizagem * N.Saida * EsteNeuronio.Erro
//                camadaEntrada.neuronios[j].conexoesDeSaida[i] = camadaEntrada.neuronios[j].conexoesDeSaida[i] * MOMENTUM +
//                        TAXA_APRENDIZAGEM * camadaEntrada.neuronios[j].saida * camadaIntermediaria.neuronios[i].erro;
//            }
//        }
    }

    private void testaRede(){
        double[] entrada;
        for (int i = 0; i < 94; i++) { //testa os 94 dos 100 dados do dataset de treino (6 deles são caraceteres aleatórios)
            entrada = dadosTeste.entrada.get(i);

            for (int j = 0; j < N_ENTRADA; j++) {
                camadaEntrada.neuronios[j].entrada = entrada[j];
                camadaEntrada.neuronios[j].saida = entrada[j];
            }

            //zera entrada dos neuronios da intermediraria
            for (int j = 0; j < N_INTERMEDIARIA; j++) {
                camadaIntermediaria.neuronios[j].entrada = 0.0;
            }

            //passa para camada intermediaria
            for (int j = 0; j < N_ENTRADA; j++) {
                for (int k = 0; k < N_INTERMEDIARIA; k++) {
                    camadaIntermediaria.neuronios[k].entrada += camadaEntrada.neuronios[j].saida * camadaEntrada.neuronios[j].conexoesDeSaida[k];
                }
            }

            //saida = 1 / (1 + Exp(-somatorio))
            //signoidal
            for (int j = 0; j < N_INTERMEDIARIA; j++) {
                camadaIntermediaria.neuronios[j].saida = 1.0 / (1.0 + Math.exp(-camadaIntermediaria.neuronios[j].entrada));
            }

            //zera a entrada da saida
            for (int j = 0; j < N_SAIDA; j++) {
                camadaSaida.neuronios[j].entrada = 0.0;
            }

            for (int j = 0; j < N_INTERMEDIARIA; j++) {
                for (int k = 0; k < N_SAIDA; k++) {
                    camadaSaida.neuronios[k].entrada += camadaIntermediaria.neuronios[j].saida * camadaIntermediaria.neuronios[j].conexoesDeSaida[k];
                }
            }

            //saida = 1 / (1 + Exp(-somatorio))
            //signoidal
            for (int j = 0; j < N_SAIDA; j++) {
                camadaSaida.neuronios[j].saida = 1.0 / (1.0 + Math.exp(-camadaSaida.neuronios[j].entrada));
            }

            double[] saidaEsperada = dadosTeste.saidaEsperada.get(i);

            //chama a função de avaliação do resultado passando o índice do resultado esperado (p. ex., se esperava o char 0, manda 0 como parâmetro; se esperava A, manda 11)
            for(int j = 0;j < N_SAIDA;j++){
                if(saidaEsperada[j] == 1.0)
                    avaliaResultado(j);
            }

//            System.out.println("it: " + i);
//            for (int j = 0; j < 36; j++) {
//                System.out.println("Saída (sigmoidal) do neuronio " + j + ": " + camadaSaida.neuronios[j].saida + " | saida esperada: " + s[j] + " | entrada (somatório): " + camadaSaida.neuronios[j].entrada + " | erro: " + camadaSaida.neuronios[j].erro);
//            }
//            System.out.println("");



//            verificaResultado();
        }
    }

    private void avaliaResultado(int indiceEsperado){
        int indiceMaior = 0;
        double valorMaior = 0;

        for(int i = 0;i < N_SAIDA;i++){
            if(camadaSaida.neuronios[i].saida > valorMaior){
                valorMaior = camadaSaida.neuronios[i].saida;
                indiceMaior = i;
            }
        }
        matrizConfusao[indiceEsperado][indiceMaior]++;
    }

    private void printaMatriz(){
        for(int i = 0;i < N_SAIDA;i++){
            for(int j = 0;j < N_SAIDA;j++){
                System.out.print(matrizConfusao[i][j] + "  ");
            }
            System.out.println();
        }
    }

    private void calculaMetricas(){
        double acuracia, erro, sensitividade, especificidade, precisao, TPR, FPR;
        int VP = 0, VN = 0, FP = 0, FN = 0;

        System.out.println();
        for(int i = 0;i < N_SAIDA;i++) {
            for (int j = 0; j < N_SAIDA; j++) {
                VN = FP = FN = 0;
                VP = matrizConfusao[j][j];
                for (int k = 0; k < N_SAIDA; k++) {
                    if (j != k) {
                        VN += matrizConfusao[k][k];
                        FP += matrizConfusao[k][j];
                        FN += matrizConfusao[j][k];
                    }
                }
            }
//            acuracia: todas as classes
            //precisao e racall: cada classe
            //roc para cada classe

            //Acurácia = (VP+VN)/(VP+FP+VN+FN)
            acuracia = (VP + VN) / (VP + FP + VN + FN);
            //Erro = 1-Acurácia
            erro = 1 - acuracia;
            //Sensitividade = VP / (VP + FN)
            sensitividade = VP / (VP + FN);
            //Especificidade = VN / (VN + FP)
            especificidade = VN / (VN + FP);
            //Precisão = VP / (VP + FN)
            precisao = VP / (VP + FN);
            //TPR = VP/(VP + FN)
            TPR = VP / (VP + FN);
            //FPR = FP/(VN + FP)
            FPR = FP / (VN + FP);

            System.out.println("## Instância " + i + " ##");
            System.out.println("Acurácia: " + acuracia);
            System.out.println("Erro: " + erro);
            System.out.println("Sensitividade: " + sensitividade);
            System.out.println("Precisão: " + precisao);
            System.out.println("TPR: " + TPR);
            System.out.println("FPR: " + FPR);
            System.out.println();
        }
    }
}
