import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // 1 часть задания
        System.out.println("Введите название города");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String city = bufferedReader.readLine().trim();
        if (!city.isEmpty()){
            String output = getUrlContent("https://api.openweathermap.org/data/2.5/forecast?q=" + city +"&appid=72a01929eb939cf53bfc049c3ebe9ac2&cnt=40&units=metric");
            if (!output.isEmpty()){
                JSONObject object = new JSONObject(output);
                int lenghtArray = object.getJSONArray("list").length();
                List<Integer> pressureList = new ArrayList<>();
                for (int i = 0; i < lenghtArray; i++) {
                    pressureList.add(object.getJSONArray("list").getJSONObject(i).getJSONObject("main").getInt("pressure"));
                }
                System.out.println("Максимальное давление за предстоящие 5 дней - " + Collections.min(pressureList));
                //2 часть задания
                Date date = new Date();
                SimpleDateFormat formatGetNowHour = new SimpleDateFormat("HH");
                SimpleDateFormat formaterGetToday = new SimpleDateFormat("dd");
                SimpleDateFormat formaterGetMonthAndYear = new SimpleDateFormat("yyyy-MM-");
                int todayIntForMorning;
                String monthAndYearNow = formaterGetMonthAndYear.format(date);
                if (Integer.parseInt(formatGetNowHour.format(date)) > 9){
                    todayIntForMorning = Integer.parseInt(formaterGetToday.format(date))+1;
                } else {
                    todayIntForMorning = Integer.parseInt(formaterGetToday.format(date));
                }
                List<Double> listMornTemp = new ArrayList<>();
                for (int i = 0; i < lenghtArray; i++) {
                    if (object.getJSONArray("list").getJSONObject(i).getString("dt_txt").equals(monthAndYearNow + todayIntForMorning + " 06:00:00")){
                        listMornTemp.add(object.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp"));
                        todayIntForMorning++;
                    }
                }
                int todayIntForNight = Integer.parseInt(formaterGetToday.format(date))+1;
                List<Double> listNightTemp = new ArrayList<>();
                for (int i = 0; i < lenghtArray; i++) {
                    if (object.getJSONArray("list").getJSONObject(i).getString("dt_txt").equals(monthAndYearNow + todayIntForNight + " 00:00:00")){
                        listNightTemp.add(object.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp"));
                        todayIntForNight++;
                    }
                }
                List<Double> differenceTemp = new ArrayList<>();
                for (int i = 0; i < listMornTemp.size(); i++) {
                    differenceTemp.add(listMornTemp.get(i)-listNightTemp.get(i));
                }
                double tmp = differenceTemp.get(0);
                int indexMin = 0;
                for (int i = 0; i < differenceTemp.size()-1; i++) {
                    if (Math.abs(tmp) > Math.abs(differenceTemp.get(i+1))){
                        tmp = Math.abs(differenceTemp.get(i+1));
                        indexMin = i+1;
                    }
                }
                System.out.println("День с минимальной разницей между ночной и утренней температурой - " + (indexMin+todayIntForMorning-5));
            }
        }
    }

    private static String getUrlContent(String urlAdress){
        StringBuilder stringBuilder = new StringBuilder();
        try{
            URL url = new URL(urlAdress);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Такой город не найден");
        }
        return stringBuilder.toString();
    }
}
