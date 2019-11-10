package principal;

public class Constantes {
    public static final double MOMENTUM = 1;
    public static final double TAXA_APRENDIZAGEM = 0.1;

    public static final int CAMADA_ENTRADA = 1;
    public static final int CAMADA_INTERMEDIARIA = 2;
    public static final int CAMADA_SAIDA = 3;

    public static final int N_ENTRADA = 48;
    public static final int N_SAIDA = 10;
    public static final int N_INTERMEDIARIA = (Constantes.N_ENTRADA + Constantes.N_SAIDA)/2;

    public static final int N_INSTANCIAS = 10; //número de dígitos que fazem parte do conjunto de treinamento
    public static final int N_ITERACOES = 10; //número de dígitos que fazem parte do conjunto de testes

}