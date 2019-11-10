package principal;

import java.util.Random;

public class Neuronio {
    public double entrada;
    public double saida;
    public double erro;
    //erro do neuronio;
    //fator de erro;
//    public double[] conexoesDeEntrada;
    public double[] conexoesDeSaida;
    //função de ativação?

    public Neuronio(int camada){
        Random r = new Random();

        if(camada == Constantes.CAMADA_ENTRADA){
            conexoesDeSaida = new double[(Constantes.N_ENTRADA + Constantes.N_SAIDA)/2];
            for(int i = 0; i < conexoesDeSaida.length; i++){
                conexoesDeSaida[i] = r.nextDouble();
            }
        }else if(camada == Constantes.CAMADA_INTERMEDIARIA){
//            conexoesDeEntrada = new double[Constantes.N_ENTRADA];
            conexoesDeSaida = new double[Constantes.N_SAIDA];
//            for(int i = 0; i < conexoesDeEntrada.length; i++){
//                conexoesDeEntrada[i] = r.nextDouble();
//            }

            for(int i = 0; i < conexoesDeSaida.length; i++){
                conexoesDeSaida[i] = r.nextDouble();
            }
        }else{
//            conexoesDeEntrada = new double[Constantes.N_ENTRADA];
//            for(int i = 0; i < conexoesDeEntrada.length; i++){
//                conexoesDeEntrada[i] = r.nextDouble();
//            }
        }
    }

//    public Neuronio(int nEntradas, int nSaidas, int camada){
//        if(camada == Constantes.CAMADA_ENTRADA){
//            for(int i = 0;i<nSaidas;i++){
//                conexaoDeSaidas[i].neuronio = new Neuronio();
//            }
//        }else if(camada == Constantes.CAMADA_INTERMEDIARIA){
//            for(int i = 0;i<nEntradas;i++){
//                conexaoDeEntradas[i].neuronio = new Neuronio();
//            }
//
//            for(int i = 0;i<nSaidas;i++){
//                conexaoDeSaidas[i].neuronio = new Neuronio();
//            }
//        }else{
//            for(int i = 0;i<nEntradas;i++){
//                conexaoDeEntradas[i].neuronio = new Neuronio();
//            }
//        }
//
//
//    }

}
