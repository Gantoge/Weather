import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Weather {
    public static void main(String[] args) {
        double lat = 55.75;
        double lon = 37.62;
        int limit = 7;

        try {
            String response = getWeatherData(lat, lon, limit);
            System.out.println("Все данные:");
            System.out.println(response);

            int temp = extractCurrentTemperature(response);
            System.out.println("Текущая температура: " + temp + "°C");

            double avgTemp = calculateAverageTemperature(response, limit);
            System.out.println("Средняя температура за " + limit + " дней: " + avgTemp + "°C");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getWeatherData(double lat, double lon, int limit) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.weather.yandex.ru/v2/forecast"))
                .header("X-Yandex-API-Key", "af1e3573-c917-490c-b5a1-6da08bd2ce20")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static int extractCurrentTemperature(String jsonResponse) {
        int tempIndex = jsonResponse.indexOf("\"temp\":");
        if (tempIndex != -1) {
            int tempStart = tempIndex + 7;
            int tempEnd = jsonResponse.indexOf(",", tempStart);
            return Integer.parseInt(jsonResponse.substring(tempStart, tempEnd).trim());
        }
        return 0;
    }

    public static double calculateAverageTemperature(String jsonResponse, int limit) {
        int totalTemp = 0;
        int daysCounted = 0;

        int index = 0;
        while (daysCounted < limit) {
            index = jsonResponse.indexOf("\"temp_avg\":", index);
            if (index == -1) {
                break;
            }

            int tempStart = index + 11;
            int tempEnd = jsonResponse.indexOf(",", tempStart);
            totalTemp += Integer.parseInt(jsonResponse.substring(tempStart, tempEnd).trim());

            daysCounted++;
            index = tempEnd;
        }

        return daysCounted == 0 ? 0 : (int) Math.round((double) totalTemp / daysCounted);
    }
}