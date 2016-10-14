package com.xsp.stocksrelativity.datafetch.sohu;

import com.xsp.stocksrelativity.entity.StockDailyPrice;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Shaopeng.Xu on 2016-10-14.
 */
public class GetStockDailyPrice {

    public static void main(String[] args) {

    }


    //http://q.stock.sohu.com/hisHq?code=cn_000001&start=20160602&end=20161011&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.2570124812116552&0.6611461189293653
    public static List<StockDailyPrice> getStockDataFromSohu(String code) {
        try {
            // http://table.finance.yahoo.com/table.csv?s=000001.sz
            String urlStr = "http://q.stock.sohu.com/hisHq?code=cn_" + code + "&start=20160602&end=20161011&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.2570124812116552&0.6611461189293653";
            URL url = new URL(urlStr);
            System.out.println(urlStr);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "GBK"));
            String line = "";
            String s = null;
            for (; (s = reader.readLine()) != null; ) {
                line += s;
            }
            System.out.println(line);
            List<StockDailyPrice> list = parseData(line, code);
            return list;
        } catch (Exception e) {
            //TODO error
            e.printStackTrace();
        }
        return new ArrayList<StockDailyPrice>();
    }

    public static List<StockDailyPrice> parseData(String s, String code) {
        List<StockDailyPrice> stockDailyPrices = new ArrayList<StockDailyPrice>();
        s = s.substring("historySearchHandler(".length(), s.length() - 1);
        JSONArray jo = new JSONArray(s);

        List<Object> list = jo.toList();
        for (Object obj: list) {
            Map<String, Object> map = (Map)obj;
            List<List> hq = (List<List>) map.get("hq");
            for (List<String> stockPrice : hq) {
                // date
                //[2016-06-03, 10.47, 10.50, 0.04, 0.38%, 10.41, 10.52, 423389, 44303.96, 0.35%]
                System.out.println(stockPrice);
                StockDailyPrice stockDailyPrice = new StockDailyPrice();
                stockDailyPrice.setCode(code);
                stockDailyPrice.setDate(stockPrice.get(0).replaceAll("\\-", ""));
                stockDailyPrice.setOpen(new BigDecimal(stockPrice.get(1)));
                stockDailyPrice.setClose(new BigDecimal(stockPrice.get(2)));
                stockDailyPrice.setLow(new BigDecimal(stockPrice.get(5)));
                stockDailyPrice.setHigh(new BigDecimal(stockPrice.get(6)));
                stockDailyPrice.setVolumn(Long.parseLong(stockPrice.get(7))*100);
                stockDailyPrices.add(stockDailyPrice);
            }
        }
        return stockDailyPrices;
    }

}
