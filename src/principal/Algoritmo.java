package principal;


public class Algoritmo extends Constantes{
    private Dados dados;

    private Camada camadaEntrada;
    private Camada camadaIntermediaria;
    private Camada camadaSaida;

    public Algoritmo(){
        dados = new Dados("entrada");

        camadaEntrada = new Camada(N_ENTRADA, CAMADA_ENTRADA);
        camadaIntermediaria = new Camada(N_INTERMEDIARIA, CAMADA_INTERMEDIARIA);
        camadaSaida = new Camada(N_SAIDA, CAMADA_SAIDA);

        executa();
    }

    private void executa(){
        byte[] entrada;
        int it = 0;

        while(it < 1000) {
            for (int i = 0; i < N_INSTANCIAS; i++) {
                entrada = (byte[]) dados.entrada.get(i);
                //alimenta cada neuronio da camada de entrada e seta o valor de saida
                for (int j = 0; j < camadaEntrada.neuronios.length; j++) {
                    camadaEntrada.neuronios[j].entrada = entrada[j];
                    camadaEntrada.neuronios[j].saida = entrada[j];
                }

                //cada neuronio da camada intermediaria irá ter o somatorio da saída * peso de cada neuronio da camada de entrada

                for (int j = 0; j < N_INTERMEDIARIA; j++) {
                    camadaIntermediaria.neuronios[j].entrada = 0.0;
                }

                for (int j = 0; j < N_ENTRADA; j++) {
                    for (int k = 0; k < N_INTERMEDIARIA; k++) {
                        camadaIntermediaria.neuronios[k].entrada += camadaEntrada.neuronios[j].saida * camadaEntrada.neuronios[j].conexoesDeSaida[k];
                    }
                }

                //gera a funcao de saida (signoidal) para cada neuronio da camada intermediaria
                for (int j = 0; j < N_INTERMEDIARIA; j++) {
                    //saida = 1 / (1 + Exp(-entrada))
                    camadaIntermediaria.neuronios[j].saida = 1 / (1 + Math.exp(-camadaIntermediaria.neuronios[j].entrada));
                }

                //cada neuronio da camada de saida irá ter o somatorio da saída * peso de cada neuronio da camada de intermediaria
                for(int j = 0;j < N_SAIDA;j++){
                    camadaSaida.neuronios[j].entrada = 0;
                }

                for (int j = 0; j < N_INTERMEDIARIA; j++) {
                    for (int k = 0; k < N_SAIDA; k++) {
                        camadaSaida.neuronios[k].entrada += camadaIntermediaria.neuronios[j].saida * camadaIntermediaria.neuronios[j].conexoesDeSaida[k];
                    }
                }

                byte[] s = dados.saidaEsperada.get(i);
                if (i == 0)
                    System.out.println("Iteração: " + it);
                for (int j = 0; j < N_SAIDA; j++) {
                    if(i == 0) {
                        camadaSaida.neuronios[j].saida = 1 / (1 + Math.exp(-camadaSaida.neuronios[j].entrada));
                        System.out.println("Neuronio " + j + ": " + camadaSaida.neuronios[j].saida + " | saida esperada: " + s[j]);
                    }
                }
                if (i == 0)
                    System.out.println("\n");


                calculaErro(i);

                ajustaPesos();
            }
            it++;
        }
    }

    private void calculaErro(int instancia) {
        double fatorErro = 0;
        byte[] saidaEsperada = dados.saidaEsperada.get(instancia);

        //calcula o erro de cada neuronio da camada de saida
        for (int i = 0; i < N_SAIDA; i++) {
            fatorErro = saidaEsperada[i] - camadaSaida.neuronios[i].saida;
            camadaSaida.neuronios[i].erro = camadaSaida.neuronios[i].saida * (1.0 - camadaSaida.neuronios[i].saida) * fatorErro;
        }

        //calcula o erro de cada neuronio da camada intermediaria
        fatorErro = 0;
        for (int i = 0; i < N_INTERMEDIARIA; i++) {
            for (int j = 0; j < N_SAIDA; j++) {
                fatorErro += camadaSaida.neuronios[j].erro * camadaIntermediaria.neuronios[i].conexoesDeSaida[j];
            }
            camadaIntermediaria.neuronios[i].erro = camadaIntermediaria.neuronios[i].saida * (1 - camadaIntermediaria.neuronios[i].saida) * fatorErro;
            fatorErro = 0;
        }
    }

    private void ajustaPesos(){
        //ajuste dos pesos das conexoes
        for (int i = 0; i < N_SAIDA; i++) {
            for (int j = 0; j < N_INTERMEDIARIA; j++) {
                //Novo_Peso de N = Peso_anterior de N * momentum + taxa_de_aprendizagem * N.Saida * EsteNeuronio.Erro
                camadaIntermediaria.neuronios[j].conexoesDeSaida[i] = camadaIntermediaria.neuronios[j].conexoesDeSaida[i] * MOMENTUM +
                        TAXA_APRENDIZAGEM * camadaIntermediaria.neuronios[j].saida * camadaSaida.neuronios[i].erro;
            }
        }

        //ajustes dos pesos dos neuronios da camada intermediaria
        for (int i = 0; i < N_INTERMEDIARIA; i++) {
            for (int j = 0; j < N_ENTRADA; j++) {
                //Novo_Peso de N = Peso_anterior de N * momentum + taxa_de_aprendizagem * N.Saida * EsteNeuronio.Erro
                camadaEntrada.neuronios[j].conexoesDeSaida[i] = camadaEntrada.neuronios[j].conexoesDeSaida[i] * MOMENTUM +
                        TAXA_APRENDIZAGEM * camadaEntrada.neuronios[j].saida * camadaIntermediaria.neuronios[i].erro;
            }
        }
    }
}
