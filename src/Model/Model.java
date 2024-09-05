package Model;

import org.json.*;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Stores a JSON Array by converting a JSON string parameter on construction
 */
public class Model {
    JSONArray jsonArray;
    String[][] dataCache;
    /**
     * Stores a JSON Array by converting a JSON string parameter on construction
     * @param data String[][]
     * @param selectedCoin String
     * @param interval int
     */
    public Model(String[][] data, String selectedCoin, int interval) {
        this.dataCache = data;
        String jsonString;
        switch (selectedCoin) {
            case "bitcoin":
                jsonString = data[0][interval];
                break;
            case "ethereum":
                jsonString = data[1][interval];
                break;
            case "solana":
                jsonString = data[2][interval];
                break;
            default:
                jsonString = data[0][interval];
                break;
        }
        this.jsonArray = new JSONArray(jsonString);
    }

    /**
     * Getter method for the stored JSON Array
     * @return JSONArray
     */
    public JSONArray getJsonArray() {
        return this.jsonArray;
    }

    /**
     * Getter method for the stored data cache
     * @return String[]
     */
    public String[][] getDataCache() { return this.dataCache; }


    /**
     * Determines whether the data is old enough to be fetched again
     * Default threshold: 1hr
     * @param timestamp ZonedDateTime
     * @return Boolean
     */
    public Boolean isAlphaDecayed(ZonedDateTime timestamp) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        long timeDiffSeconds = Duration.between(timestamp, currentDateTime).toSeconds();
        System.out.println("Last fetch: " + timestamp);
        System.out.println("Current time: " + currentDateTime);
        System.out.println("Time difference in seconds: " + timeDiffSeconds + "\n");
        return timeDiffSeconds > (60*60);
    }
}
