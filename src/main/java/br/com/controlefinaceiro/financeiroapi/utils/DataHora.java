package br.com.controlefinaceiro.financeiroapi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class DataHora {

    public static String dataFormatada(int dia, int mes, int ano, String format) {
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

    public static String dataFormatada(Date data, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        String dataFormatada = sdf.format(c.getTime());
        return dataFormatada;
    }

    public static Date criar(int dia, int mes, int ano) {
        if(mes >=1 &&  mes <= 12){
            mes -= 1;
            Calendar c = Calendar.getInstance();
            c.set(ano,mes,dia);

            return c.getTime();
        }
        return null;
    }


}