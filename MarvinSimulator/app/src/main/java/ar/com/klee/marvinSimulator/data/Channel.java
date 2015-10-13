package ar.com.klee.marvinSimulator.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Channel implements JSONPopulator {
    private Units units;
    private Item item;
   // private long expiration;
   // private String location;

    public Units getUnits() {
        return units;
    }

    public Item getItem() {
        return item;
    }

    /*public long getExpiration() {
        return expiration;
    }

    public String getLocation() {
        return location;
    }
    */

    @Override
    public void populate(JSONObject data) {

        units = new Units();
        units.populate(data.optJSONObject("units"));

        item = new Item();
        item.populate(data.optJSONObject("item"));

       // expiration = data.optLong("expiration");
        //location = data.optString("requestLocation");
    }

    @Override
    public JSONObject toJSON() {

        JSONObject data = new JSONObject();

        try {
            data.put("units", units.toJSON());
            data.put("item", item.toJSON());
           // data.put("expiration", expiration);
           // data.put("requestLocation", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

}