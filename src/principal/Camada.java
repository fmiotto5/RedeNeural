package principal;

public class Camada {
    public Neuronio[] neuronios;

    public Camada(int nNeuronios, int camada){
        neuronios = new Neuronio[nNeuronios];
        inicializaNeuronios(camada);
    }

//    public Camada(int nNeuronios, int nConexoesEntrada, int nConexoesSaida){ //construtor da camada intermediária
//        neuronios = new Neuronio[nNeuronios];
//        inicializaNeuronios();
//    }
//
//    public Camada(int nNeuronios, int nConexoesEntrada){ //construtor da camada de saída
//        neuronios = new Neuronio[nNeuronios];
//        inicializaNeuronios();
//    }

    private void inicializaNeuronios(int camada){
//        if(camada == Constantes.CAMADA_ENTRADA){
            for(int i = 0;i < neuronios.length;i++){
                neuronios[i] = new Neuronio(camada);
            }
//        }else if(camada == Constantes.CAMADA_INTERMEDIARIA){
//            for(int i = 0;i < neuronios.length;i++){
//                neuronios[i] = new Neuronio();
//            }
//        }else{
//            for(int i = 0;i < neuronios.length;i++){
//                neuronios[i] = new Neuronio();
//            }
//        }


    }
}
