package com.example.shareloc.managers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GeoJsonManager {

    private Context context;
    private GoogleMap map;

    public GeoJsonManager(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
    }

    public void loadGeoJsonLayer(String filename) {
        String geoJsonData = loadGeoJsonFromAsset(filename);
        if (geoJsonData != null) {
            addGeoJsonLayerToMap(geoJsonData, filename);
        }
    }

    private String loadGeoJsonFromAsset(String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            int bytesRead = is.read(buffer);
            if (bytesRead != size) {
                Log.w("GeoJsonManager", "Load Geo buffer size != actual size");
            }
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e("GeoJsonManager", "Error reading GeoJSON file: " + filename, ex);
            return null;
        }
    }

    private void addGeoJsonLayerToMap(String geoJsonData, String countryName) {
        try {
            JSONObject geoJson = new JSONObject(geoJsonData);
            GeoJsonLayer layer = new GeoJsonLayer(map, geoJson);
            GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
            style.setFillColor(Color.BLACK);
            style.setStrokeColor(Color.BLACK);
            style.setStrokeWidth(2f);
            layer.addLayerToMap();
        } catch (Exception e) {
            Log.e("GeoJsonManager", "Problem reading GeoJSON file for country: " + countryName, e);
        }
    }

    public String mapCountryNameToFileName(String countryName) {
        Map<String, String> countryNameToFileMap = new HashMap<>();

        //europe
        countryNameToFileMap.put("Albania", "albania");
        countryNameToFileMap.put("Austria", "austria");
        countryNameToFileMap.put("Belarus", "belarus");
        countryNameToFileMap.put("Belgium", "belgium");
        countryNameToFileMap.put("Bosnia and Herzegovina", "bosnia-and-herzegovina");
        countryNameToFileMap.put("Bulgaria", "bulgaria");
        countryNameToFileMap.put("Croatia", "croatia");
        countryNameToFileMap.put("Cyprus", "cyprus");
        countryNameToFileMap.put("Czech Republic", "czech-republic");
        countryNameToFileMap.put("Denmark", "denmark");
        countryNameToFileMap.put("Estonia", "estonia");
        countryNameToFileMap.put("Finland", "finland");
        countryNameToFileMap.put("France", "france");
        countryNameToFileMap.put("Germany", "germany");
        countryNameToFileMap.put("Greece", "greece");
        countryNameToFileMap.put("Hungary", "hungary");
        countryNameToFileMap.put("Ireland", "ireland");
        countryNameToFileMap.put("Italy", "italy");
        countryNameToFileMap.put("Latvia", "latvia");
        countryNameToFileMap.put("Lithuania", "lithuania");
        countryNameToFileMap.put("Luxembourg", "luxembourg");
        countryNameToFileMap.put("Malta", "malta");
        countryNameToFileMap.put("Moldova", "moldova");
        countryNameToFileMap.put("Montenegro", "montenegro");
        countryNameToFileMap.put("Netherlands", "netherlands");
        countryNameToFileMap.put("Norway", "norway");
        countryNameToFileMap.put("Poland", "poland");
        countryNameToFileMap.put("Portugal", "portugal");
        countryNameToFileMap.put("North Macedonia", "republic-of-north-macedonia");
        countryNameToFileMap.put("Romania", "romania");
        countryNameToFileMap.put("Serbia", "serbia");
        countryNameToFileMap.put("Slovakia", "slovakia");
        countryNameToFileMap.put("Slovenia", "slovenia");
        countryNameToFileMap.put("Spain", "spain");
        countryNameToFileMap.put("Sweden", "sweden");
        countryNameToFileMap.put("Switzerland", "switzerland");
        countryNameToFileMap.put("Ukraine", "ukraine");
        countryNameToFileMap.put("United Kingdom", "united-kingdom");
        countryNameToFileMap.put("Russia", "russia");
        countryNameToFileMap.put("Iceland", "iceland");


        // africa
        countryNameToFileMap.put("Algeria", "algeria");
        countryNameToFileMap.put("Angola", "angola");
        countryNameToFileMap.put("Benin", "benin");
        countryNameToFileMap.put("Botswana", "botswana");
        countryNameToFileMap.put("Burkina Faso", "burkina-faso");
        countryNameToFileMap.put("Burundi", "burundi");
        countryNameToFileMap.put("Cameroon", "cameroon");
        countryNameToFileMap.put("Chad", "chad");
        countryNameToFileMap.put("Congo", "congo");
        countryNameToFileMap.put("Central African Republic", "central-african-republic");
        countryNameToFileMap.put("Côte d'Ivoire", "cote-divoire");
        countryNameToFileMap.put("Djibouti", "djibouti");
        countryNameToFileMap.put("Egypt", "egypt");
        countryNameToFileMap.put("Equatorial Guinea", "equatorial");
        countryNameToFileMap.put("Eritrea", "eritrea");
        countryNameToFileMap.put("Ethiopia", "ethiopia");
        countryNameToFileMap.put("Gabon", "gabon");
        countryNameToFileMap.put("Gambia", "gambia");
        countryNameToFileMap.put("Ghana", "ghana");
        countryNameToFileMap.put("Guinea-Bissau", "guinea-bissau");
        countryNameToFileMap.put("Guinea", "guinea");
        countryNameToFileMap.put("Kenya", "kenya");
        countryNameToFileMap.put("Lesotho", "lesotho");
        countryNameToFileMap.put("Liberia", "liberia");
        countryNameToFileMap.put("Libya", "libyan-arab-jamahiriya");
        countryNameToFileMap.put("Madagascar", "madagascar");
        countryNameToFileMap.put("Malawi", "malawi");
        countryNameToFileMap.put("Mali", "mali");
        countryNameToFileMap.put("Mauritania", "mauritania");
        countryNameToFileMap.put("Morocco", "morocco");
        countryNameToFileMap.put("Mozambique", "mozambique");
        countryNameToFileMap.put("Namibia", "namibia");
        countryNameToFileMap.put("Niger", "niger");
        countryNameToFileMap.put("Nigeria", "nigeria");
        countryNameToFileMap.put("Rwanda", "rwanda");
        countryNameToFileMap.put("Senegal", "senegal");
        countryNameToFileMap.put("Sierra Leone", "sierra-leone");
        countryNameToFileMap.put("Somalia", "somalia");
        countryNameToFileMap.put("South Africa", "south-africa");
        countryNameToFileMap.put("South Sudan", "south-sudan");
        countryNameToFileMap.put("Sudan", "sudan");
        countryNameToFileMap.put("Swaziland", "swaziland");
        countryNameToFileMap.put("Tanzania", "tanzania");
        countryNameToFileMap.put("Togo", "togo");
        countryNameToFileMap.put("Tunisia", "tunisia");
        countryNameToFileMap.put("Uganda", "uganda");
        countryNameToFileMap.put("Western Sahara", "western-sahara");
        countryNameToFileMap.put("Zambia", "zambia");
        countryNameToFileMap.put("Republic Democratique du congo", "democratic-republic-of-the-congo");
        countryNameToFileMap.put("Zimbabwe", "zimbabwe");

        //America
        countryNameToFileMap.put("Argentina", "argentina");
        countryNameToFileMap.put("Bolivia", "bolivia");
        countryNameToFileMap.put("Brazil", "brazil");
        countryNameToFileMap.put("Canada", "canada");
        countryNameToFileMap.put("Chile", "chile");
        countryNameToFileMap.put("Colombia", "colombia");
        countryNameToFileMap.put("Costa Rica", "costa-rica");
        countryNameToFileMap.put("Cuba", "cuba");
        countryNameToFileMap.put("Ecuador", "ecuador");
        countryNameToFileMap.put("El Salvador", "el-salvador");
        countryNameToFileMap.put("Greenland", "greenland");
        countryNameToFileMap.put("Guatemala", "guatemala");
        countryNameToFileMap.put("Guyana", "guyana");
        countryNameToFileMap.put("Haiti", "haiti");
        countryNameToFileMap.put("Honduras", "honduras");
        countryNameToFileMap.put("Jamaica", "jamaica");
        countryNameToFileMap.put("Mexico", "mexico");
        countryNameToFileMap.put("Nicaragua", "nicaragua");
        countryNameToFileMap.put("Panama", "panama");
        countryNameToFileMap.put("Paraguay", "paraguay");
        countryNameToFileMap.put("Peru", "peru");
        countryNameToFileMap.put("Puerto Rico", "puerto-rico");
        countryNameToFileMap.put("Suriname", "suriname");
        countryNameToFileMap.put("United States", "united-states");
        countryNameToFileMap.put("Uruguay", "uruguay");
        countryNameToFileMap.put("Venezuela", "venezuela");

        //Asia
        countryNameToFileMap.put("Afghanistan", "afghanistan");
        countryNameToFileMap.put("Armenia", "armenia");
        countryNameToFileMap.put("Azerbaijan", "azerbaijan");
        countryNameToFileMap.put("Bangladesh", "bangladesh");
        countryNameToFileMap.put("Bhutan", "asia/bhutan");
        countryNameToFileMap.put("Brunei Darussalam", "brunei-darussalam");
        countryNameToFileMap.put("Cambodia", "cambodia");
        countryNameToFileMap.put("China", "china");
        countryNameToFileMap.put("Democratic People's Republic of Korea", "democratic-peoples-republic-of-korea");
        countryNameToFileMap.put("Georgia", "georgia");
        countryNameToFileMap.put("India", "india");
        countryNameToFileMap.put("Indonesia", "indonesia");
        countryNameToFileMap.put("Iran", "iran");
        countryNameToFileMap.put("Iraq", "iraq");
        countryNameToFileMap.put("Israel", "israel");
        countryNameToFileMap.put("Japan", "japan");
        countryNameToFileMap.put("Jordan", "jordan");
        countryNameToFileMap.put("Kazakhstan", "kazakhstan");
        countryNameToFileMap.put("Kuwait", "kuwait");
        countryNameToFileMap.put("Kyrgyzstan", "kyrgyzstan");
        countryNameToFileMap.put("Lao People's Democratic Republic", "lao-peoples-democratic-republic");
        countryNameToFileMap.put("Lebanon", "lebanon");
        countryNameToFileMap.put("Malaysia", "malaysia");
        countryNameToFileMap.put("Mongolia", "mongolia");
        countryNameToFileMap.put("Myanmar", "myanmar");
        countryNameToFileMap.put("Nepal", "nepal");
        countryNameToFileMap.put("Oman", "oman");
        countryNameToFileMap.put("Pakistan", "pakistan");
        countryNameToFileMap.put("Palestinian", "palestinian");
        countryNameToFileMap.put("Philippines", "philippines");
        countryNameToFileMap.put("Qatar", "qatar");
        countryNameToFileMap.put("Saudi Arabia", "saudi-arabia");
        countryNameToFileMap.put("South Korea", "south-korea");
        countryNameToFileMap.put("Sri Lanka", "sri-lanka");
        countryNameToFileMap.put("Syrian Arab Republic", "syrian-arab-republic");
        countryNameToFileMap.put("Taiwan", "asia/taiwan");
        countryNameToFileMap.put("Tajikistan", "tajikistan");
        countryNameToFileMap.put("Thailand", "thailand");
        countryNameToFileMap.put("Turkey", "turkey");
        countryNameToFileMap.put("Turkmenistan", "turkmenistan");
        countryNameToFileMap.put("United Arab Emirates", "united-arab-emirates");
        countryNameToFileMap.put("Uzbekistan", "uzbekistan");
        countryNameToFileMap.put("Viet Nam", "viet-nam");
        countryNameToFileMap.put("Yemen", "yemen");

        // Oceanie
        countryNameToFileMap.put("Australia", "australia");
        countryNameToFileMap.put("New Zealand", "new-zealand");
        countryNameToFileMap.put("Vanuatu", "vanuatu");
        countryNameToFileMap.put("Fiji", "fiji");
        countryNameToFileMap.put("Papua New Guinea", "papua-new-guinea");
        countryNameToFileMap.put("New Caledonia", "new-caledonia");
        countryNameToFileMap.put("Solomon Islands", "solomon-islands");


        String fileName = countryNameToFileMap.get(countryName);
        if (fileName != null) {
            return fileName + ".geojson";
        } else {
            Log.w("homePageActivity", "Country name mapping not found for: " + countryName);
            return null;
        }
    }

    public static Map<String, Boolean> createDefaultCountries() {
        Map<String, Boolean> countries = new HashMap<>();

        // europe
        countries.put("albania", false);
        countries.put("austria", false);
        countries.put("belarus", false);
        countries.put("belgium", false);
        countries.put("bosnia-and-herzegovina", false);
        countries.put("bulgaria", false);
        countries.put("croatia", false);
        countries.put("cyprus", false);
        countries.put("czech-republic", false);
        countries.put("denmark", false);
        countries.put("estonia", false);
        countries.put("finland", false);
        countries.put("france", false);
        countries.put("germany", false);
        countries.put("greece", false);
        countries.put("hungary", false);
        countries.put("ireland", false);
        countries.put("italy", false);
        countries.put("latvia", false);
        countries.put("lithuania", false);
        countries.put("luxembourg", false);
        countries.put("malta", false);
        countries.put("moldova", false);
        countries.put("montenegro", false);
        countries.put("netherlands", false);
        countries.put("norway", false);
        countries.put("poland", false);
        countries.put("portugal", false);
        countries.put("republic-of-north-macedonia", false);
        countries.put("romania", false);
        countries.put("serbia", false);
        countries.put("slovakia", false);
        countries.put("slovenia", false);
        countries.put("spain", false);
        countries.put("sweden", false);
        countries.put("switzerland", false);
        countries.put("ukraine", false);
        countries.put("united-kingdom", false);
        countries.put("russia", false);
        countries.put("iceland", false);

        //africa
        countries.put("algeria", false);
        countries.put("angola", false);
        countries.put("benin", false);
        countries.put("botswana", false);
        countries.put("burkina-faso", false);
        countries.put("burundi", false);
        countries.put("cameroon", false);
        countries.put("chad", false);
        countries.put("congo", false);
        countries.put("central-african-republic", false);
        countries.put("cote-divoire", false);
        countries.put("djibouti", false);
        countries.put("egypt", false);
        countries.put("equatorial", false);
        countries.put("eritrea", false);
        countries.put("ethiopia", false);
        countries.put("gabon", false);
        countries.put("gambia", false);
        countries.put("ghana", false);
        countries.put("guinea-bissau", false);
        countries.put("guinea", false);
        countries.put("kenya", false);
        countries.put("lesotho", false);
        countries.put("liberia", false);
        countries.put("libyan-arab-jamahiriya", false);
        countries.put("madagascar", false);
        countries.put("malawi", false);
        countries.put("mali", false);
        countries.put("mauritania", false);
        countries.put("morocco", false);
        countries.put("mozambique", false);
        countries.put("namibia", false);
        countries.put("niger", false);
        countries.put("nigeria", false);
        countries.put("rwanda", false);
        countries.put("senegal", false);
        countries.put("sierra-leone", false);
        countries.put("somalia", false);
        countries.put("south-africa", false);
        countries.put("south-sudan", false);
        countries.put("sudan", false);
        countries.put("swaziland", false);
        countries.put("tanzania", false);
        countries.put("togo", false);
        countries.put("tunisia", false);
        countries.put("uganda", false);
        countries.put("western-sahara", false);
        countries.put("zambia", false);
        countries.put("zimbabwe", false);
        countries.put("democratic-republic-of-the-congo", false);

        // America
        countries.put("argentina", false);
        countries.put("bolivia", false);
        countries.put("brazil", false);
        countries.put("canada", false);
        countries.put("chile", false);
        countries.put("colombia", false);
        countries.put("costa-rica", false);
        countries.put("cuba", false);
        countries.put("ecuador", false);
        countries.put("el-salvador", false);
        countries.put("greenland", false);
        countries.put("guatemala", false);
        countries.put("guyana", false);
        countries.put("haiti", false);
        countries.put("honduras", false);
        countries.put("jamaica", false);
        countries.put("mexico", false);
        countries.put("nicaragua", false);
        countries.put("panama", false);
        countries.put("paraguay", false);
        countries.put("peru", false);
        countries.put("puerto-rico", false);
        countries.put("suriname", false);
        countries.put("united-states", false);
        countries.put("uruguay", false);
        countries.put("venezuela", false);

        // Asia
        countries.put("afghanistan", false);
        countries.put("armenia", false);
        countries.put("azerbaijan", false);
        countries.put("bangladesh", false);
        countries.put("bhutan", false);
        countries.put("brunei-darussalam", false);
        countries.put("cambodia", false);
        countries.put("china", false);
        countries.put("democratic-peoples-republic-of-korea", false);
        countries.put("georgia", false);
        countries.put("india", false);
        countries.put("indonesia", false);
        countries.put("iran", false);
        countries.put("iraq", false);
        countries.put("israel", false);
        countries.put("japan", false);
        countries.put("jordan", false);
        countries.put("kazakhstan", false);
        countries.put("kuwait", false);
        countries.put("kyrgyzstan", false);
        countries.put("lao-peoples-democratic-republic", false);
        countries.put("lebanon", false);
        countries.put("malaysia", false);
        countries.put("mongolia", false);
        countries.put("myanmar", false);
        countries.put("nepal", false);
        countries.put("oman", false);
        countries.put("pakistan", false);
        countries.put("palestinian", false);
        countries.put("philippines", false);
        countries.put("qatar", false);
        countries.put("saudi-arabia", false);
        countries.put("south-korea", false);
        countries.put("sri-lanka", false);
        countries.put("syrian-arab-republic", false);
        countries.put("taiwan", false);
        countries.put("tajikistan", false);
        countries.put("thailand", false);
        countries.put("turkey", false);
        countries.put("turkmenistan", false);
        countries.put("united-arab-emirates", false);
        countries.put("uzbekistan", false);
        countries.put("viet-nam", false);
        countries.put("yemen", false);

        // Oceanie
        countries.put("australia", false);
        countries.put("new-zealand", false);
        countries.put("vanuatu", false);
        countries.put("fiji", false);
        countries.put("papua-new-guinea", false);
        countries.put("new-caledonia", false);
        countries.put("solomon-islands", false);

        return countries;
    }


}
