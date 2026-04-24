import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

    public class ClimaApp extends Application{

        @Override
        public void start(Stage palco){

            Label cityLabel = new Label("Digite o nome da cidade: ");
            TextField cityField = new TextField();
            Button getResult = new Button("Resultado");
            Label result = new Label();
            

            getResult.setOnAction(e ->{
                String cityName = cityField.getText();

                try {
                    String dadosClimaticos = getDadosClimaticos(cityName);

                    if (dadosClimaticos.contains("\"code\":1006")){
                        result.setText(String.format("Cidade não encontrada, tente novamente!"));
            } else {
                JSONObject dadosJson = new JSONObject(dadosClimaticos);
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


                result.setText("Informações Meteorológicas para " + cidade + ", " + pais + 
                                "\nCondição do tempo: " + condicaoTempo +
                                "\nTemperatura: " + temperaturaAtual + " ºC" + 
                                "\nSensação térmica:  " + sensacaoTermica + " ºC" + 
                                "\nUmidade: " + umidade + " %" +
                                "\nVelocidade do vento: " + velocidadeVento + " km/H" + 
                                "\nPressão atmosférica: " + pressaoatmos + "mb" + 
                                "\nDados atualizado as " + dataHoraString);

                }
                    
                } catch (Exception ex) {
                    ex.getMessage();
                }
            });

                VBox vbox = new VBox(10, cityLabel, cityField, getResult, result);
                vbox.setPadding(new Insets(20));

                Scene cena = new Scene(vbox,350,350);

                palco.setScene(cena);
                palco.setTitle("App do Clima");
                palco.show();

        }

// bloco de solicitação de dados pra API

public static String getDadosClimaticos(String cidade) throws Exception { 
    String apikey = Files.readString( Paths.get("api-key.txt")).trim(); //leitura do arquivo com a APIKey

    String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
    String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apikey + "&q=" + formataNomeCidade + "&lang=pt"; // criação da URL para solicitação (padrão + key + string)
    HttpRequest request = HttpRequest.newBuilder() //criando o request
            .uri(URI.create(apiUrl))
            .build();

    HttpClient client = HttpClient.newHttpClient();  //criando o requester

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); //armazenando o retorno

    return response.body();
    };

    public static void main(String[] args) {  
        launch(args);

    }
}