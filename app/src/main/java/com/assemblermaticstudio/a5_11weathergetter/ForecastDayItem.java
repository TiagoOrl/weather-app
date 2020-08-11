package com.assemblermaticstudio.a5_11weathergetter;

public class ForecastDayItem {
    public String s_hour0, s_hour1, s_hour2, s_hour3, s_hour4, s_hour5, s_hour6, s_hour7, s_day;

    public ForecastDayItem(String s_day, String hour0, String hour1, String hour2, String hour3, String hour4, String hour5, String hour6, String hour7){
        this.s_hour0 = hour0;
        this.s_hour1 = hour1;
        this.s_hour2 = hour2;
        this.s_hour3 = hour3;
        this.s_hour4 = hour4;
        this.s_hour5 = hour5;
        this.s_hour6 = hour6;
        this.s_hour7 = hour7;
        this.s_day = s_day;

    }
}
