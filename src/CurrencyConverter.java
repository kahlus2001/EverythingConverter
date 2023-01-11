import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConverter {

    public static String[] getCurrencyPairs(String currencyPair) {
        if (currencyPair.length() != 6) {
            System.out.println("Cannot Recognize Currency Pair.");
            return null;
        } else {
            String fromCurrency = currencyPair.substring(0, 3);
            String toCurrency = currencyPair.substring(3, 6);

            String[] currencies = new String[2];
            currencies[0] = fromCurrency;
            currencies[1] = toCurrency;
            System.out.println("Currency Pair: " + fromCurrency + "/" + toCurrency + ".");
            return currencies;
        }
    }

    public static double sendHTTPGETRequestToGetExchangeRate(String fromCurrency, String toCurrency) throws IOException {

        String GET_URL = "https://openexchangerates.org/api/latest.json?app_id=457077f8bdd04f5a967c448167da3bc0";
        URL url = new URL(GET_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("header", "application/json");
        int responseCode = httpURLConnection.getResponseCode();
        System.out.println(responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // successful
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject obj = new JSONObject(response.toString());
            double fromExchangeRate = obj.getJSONObject("rates").getDouble(fromCurrency);
            double toExchangeRate = obj.getJSONObject("rates").getDouble(toCurrency);

            double rateFromTo = toExchangeRate / fromExchangeRate;
            return rateFromTo;
        }

        return -1;
    }

    public static double convertAmount(double amount, double exchangeRate) {
        if (exchangeRate > 0) {
            return exchangeRate * amount;
        } else {
            return -1;
        }
    }

    public static void displayConversion(double amount, String fromCurrency, String toCurrency, double converted) {
        if (converted > 0) {
            System.out.println(amount + fromCurrency + " is " + converted + toCurrency);
        } else {
            System.out.println("Conversion unsuccessful.");
        }
    }

    public static void displayOptions() {
        System.out.println("press 'q' to Quit");
        System.out.println("Press 'c' to convert currency");
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        displayOptions();

        while (!choice.equals("q")) {
            System.out.println("Enter an option and confirm with Enter:");
            choice = scanner.nextLine();
            if (choice.equals("q")) {
                break;
            }
            if (choice.equals("c")) {
                System.out.println("Please input currency exchange pair in format XXXYYY and confirm with Enter:");
                String currencyPair = scanner.nextLine();
                String[] currencies = getCurrencyPairs(currencyPair);
                String fromCurrency = currencies[0];
                String toCurrency = currencies[1];

                System.out.println("How much of " + fromCurrency + " would you like ot convert to " + toCurrency +
                        "? Confirm with Enter.");
                double amount = scanner.nextInt();
                double exchangeRate = sendHTTPGETRequestToGetExchangeRate(fromCurrency, toCurrency);
                double converted = convertAmount(amount, exchangeRate);
                displayConversion(amount,fromCurrency, toCurrency, converted);
            }
            else {
                System.out.println("Invalid Option.");
                displayOptions();
            }

        }

    }
}
