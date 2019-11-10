package principal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Dados {
    public ArrayList<byte[]> entrada;
    public ArrayList<byte[]> saidaEsperada;


    public Dados(String nomeArq){
        entrada = new ArrayList();
        saidaEsperada = new ArrayList();

        byte[] rawDataEntrada;
        byte[] rawDataSaidaEsperada;

        try {
            FileReader arq = new FileReader(nomeArq);
            BufferedReader lerArq = new BufferedReader(arq);

            String linha = lerArq.readLine();
            while (linha != null) {
                rawDataEntrada = new byte[Constantes.N_ENTRADA];
                rawDataSaidaEsperada = new byte[Constantes.N_SAIDA];

                String[] dataset = linha.split(" ");
                for(int i = 0;i < dataset[0].length();i++) {
                    rawDataEntrada[i] = (byte) Character.getNumericValue(dataset[0].charAt(i));
                }

                for(int i = 0;i < dataset[1].length();i++) {
                    rawDataSaidaEsperada[i] = (byte) Character.getNumericValue(dataset[1].charAt(i));
                }
                this.entrada.add(rawDataEntrada);
                this.saidaEsperada.add(rawDataSaidaEsperada);

                linha = lerArq.readLine();
            }

            arq.close();
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s\n",
                    e.getMessage());
        }
    }
}
