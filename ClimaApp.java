import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ClimaApp {
    public static void main(String[] args) {
        String cidade;

        Scanner sc = new Scanner(System.in);
        System.out.println("Digite o nome da cidade: ");
        cidade = sc.nextLine();

        try {
            String dadosClimaticos = getDadosClimaticos(cidade);

            if (dadosClimaticos.contains("\"code\":1006")){
                System.out.println("Cidade não encontrada, tente novamente!");
            } else {
                imprimirDadosClimaticos(dadosClimaticos);
                }
            
        } catch (Exception ex) {
            ex.getMessage();
        }

    }

// bloco de solicitação de dados pra API

public static String getDadosClimaticos(String cidade) throws Exception { 
    String apikey = Files.readString( Paths.get("api-key.txt")).trim(); //leitura do arquivo com a APIKey

    String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
    String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apikey + "&q=" + formataNomeCidade; // criação da URL para solicitação (padrão + key + string)
    HttpRequest request = HttpRequest.newBuilder() //criando o request
            .uri(URI.create(apiUrl))
            .build();

    HttpClient client = HttpClient.newHttpClient();  //criando o requester

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); //armazenando o retorno

    return response.body();
    };


    //método para impressão dos dados

    public static void imprimirDadosClimaticos(String dados){
        //System.out.println("Dados originais JSON obtidos no site da API" + dados);
        JSONObject dadosJson = new JSONObject(dados);
        JSONObject informacoesAtualizadas = dadosJson.getJSONObject("current");

        String cidade = dadosJson.getJSONObject("location").getString("name");
        String pais = dadosJson.getJSONObject("location").getString("country");

        String condicaoTempo = informacoesAtualizadas.getJSONObject("condition").getString("text");
        int umidade = informacoesAtualizadas.getInt("humidity");
        float velocidadeVento = informacoesAtualizadas.getFloat("wind_kph");
        float pressaoatmos = informacoesAtualizadas.getFloat("pressure_mb");
        float sensacaoTermica = informacoesAtualizadas.getFloat("feelslike_c");
        float temperaturaAtual = informacoesAtualizadas.getFloat("temp_c");

        String dataHoraString = informacoesAtualizadas.getString("last_updated");

        System.out.println("Condição do tempo: " + condicaoTempo);
        System.out.println("Informações Meteorológicas para " + cidade + ", " + pais);
        System.out.println("Temperatura: " + temperaturaAtual + " ºC");
        System.out.println("Sensação térmica:  " + sensacaoTermica + " ºC");
        System.out.println("Umidade: " + umidade);
        System.out.println("Velocidade do vento: " + velocidadeVento + " Km/H");
        System.out.println("Pressão atmosférica: " + pressaoatmos + "mb");
        System.out.println("Dados atualizado as " + dataHoraString);

    }

}