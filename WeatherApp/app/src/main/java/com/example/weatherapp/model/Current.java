package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

public class Current {
    @SerializedName("last_updated_epoch")
    private long lastUpdatedEpoch;

    @SerializedName("last_updated")
    private String lastUpdated;

    @SerializedName("temp_c")
    private double tempC; // Santigrat sıcaklık

    @SerializedName("temp_f")
    private double tempF; // Fahrenhayt sıcaklık

    @SerializedName("is_day")
    private int isDay; // 1 = Gündüz, 0 = Gece

    @SerializedName("condition")
    private Condition condition; // Hava durumu durumu (ikon, açıklama)

    @SerializedName("wind_mph")
    private double windMph;

    @SerializedName("wind_kph")
    private double windKph;

    @SerializedName("wind_degree")
    private int windDegree;

    @SerializedName("wind_dir")
    private String windDir;

    @SerializedName("pressure_mb")
    private double pressureMb;

    @SerializedName("pressure_in")
    private double pressureIn;

    @SerializedName("precip_mm")
    private double precipMm;

    @SerializedName("precip_in")
    private double precipIn;

    @SerializedName("humidity")
    private int humidity;

    @SerializedName("cloud")
    private int cloud;

    @SerializedName("feelslike_c")
    private double feelslikeC;

    @SerializedName("feelslike_f")
    private double feelslikeF;

    @SerializedName("vis_km")
    private double visKm;

    @SerializedName("vis_miles")
    private double visMiles;

    @SerializedName("uv")
    private double uv;

    @SerializedName("gust_mph")
    private double gustMph;

    @SerializedName("gust_kph")
    private double gustKph;

    // Getter methods
    public long getLastUpdatedEpoch() {
        return lastUpdatedEpoch;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public double getTempC() {
        return tempC;
    }

    public double getTempF() {
        return tempF;
    }

    public int getIsDay() {
        return isDay;
    }

    public Condition getCondition() {
        return condition;
    }

    public double getWindMph() {
        return windMph;
    }

    public double getWindKph() {
        return windKph;
    }

    public int getWindDegree() {
        return windDegree;
    }

    public String getWindDir() {
        return windDir;
    }

    public double getPressureMb() {
        return pressureMb;
    }

    public double getPressureIn() {
        return pressureIn;
    }

    public double getPrecipMm() {
        return precipMm;
    }

    public double getPrecipIn() {
        return precipIn;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getCloud() {
        return cloud;
    }

    public double getFeelslikeC() {
        return feelslikeC;
    }

    public double getFeelslikeF() {
        return feelslikeF;
    }

    public double getVisKm() {
        return visKm;
    }

    public double getVisMiles() {
        return visMiles;
    }

    public double getUv() {
        return uv;
    }

    public double getGustMph() {
        return gustMph;
    }

    public double getGustKph() {
        return gustKph;
    }

    // Setter methods (isteğe bağlı)
    public void setLastUpdatedEpoch(long lastUpdatedEpoch) {
        this.lastUpdatedEpoch = lastUpdatedEpoch;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setTempC(double tempC) {
        this.tempC = tempC;
    }

    public void setTempF(double tempF) {
        this.tempF = tempF;
    }

    public void setIsDay(int isDay) {
        this.isDay = isDay;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void setWindMph(double windMph) {
        this.windMph = windMph;
    }

    public void setWindKph(double windKph) {
        this.windKph = windKph;
    }

    public void setWindDegree(int windDegree) {
        this.windDegree = windDegree;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public void setPressureMb(double pressureMb) {
        this.pressureMb = pressureMb;
    }

    public void setPressureIn(double pressureIn) {
        this.pressureIn = pressureIn;
    }

    public void setPrecipMm(double precipMm) {
        this.precipMm = precipMm;
    }

    public void setPrecipIn(double precipIn) {
        this.precipIn = precipIn;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setCloud(int cloud) {
        this.cloud = cloud;
    }

    public void setFeelslikeC(double feelslikeC) {
        this.feelslikeC = feelslikeC;
    }

    public void setFeelslikeF(double feelslikeF) {
        this.feelslikeF = feelslikeF;
    }

    public void setVisKm(double visKm) {
        this.visKm = visKm;
    }

    public void setVisMiles(double visMiles) {
        this.visMiles = visMiles;
    }

    public void setUv(double uv) {
        this.uv = uv;
    }

    public void setGustMph(double gustMph) {
        this.gustMph = gustMph;
    }

    public void setGustKph(double gustKph) {
        this.gustKph = gustKph;
    }
}