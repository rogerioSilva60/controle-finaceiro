package br.com.controlefinaceiro.financeiroapi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class DateTime {

    public static String formattedDate(int dia, int mes, int ano, String format) {
        if(mes >=1 &&  mes <= 12){
            mes -= 1;
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Calendar c = Calendar.getInstance();
            c.set(ano,mes,dia);

            String dataFormatada = sdf.format(c.getTime());
            return dataFormatada;
        }
        return null;
    }

    public static String formattedDate(Date data, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        String dataFormatada = sdf.format(c.getTime());
        return dataFormatada;
    }

    public static Date create(int dia, int mes, int ano) {
        if(mes >=1 &&  mes <= 12){
            mes -= 1;
            Calendar c = Calendar.getInstance();
            c.set(ano,mes,dia);

            return c.getTime();
        }
        return null;
    }

    public static Date createDateLast(int dia, int mes, int ano) {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.set(Calendar.DATE, dia);
        calendar.set(Calendar.MONTH, mes - 1);
        calendar.set(Calendar.YEAR, ano);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return new Date(calendar.getTimeInMillis());
    }

    public static Date createDateFirst(Date date) {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Date(calendar.getTimeInMillis());
    }

    public static Date createDateLast(Date date) {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return new Date(calendar.getTimeInMillis());
    }
}
