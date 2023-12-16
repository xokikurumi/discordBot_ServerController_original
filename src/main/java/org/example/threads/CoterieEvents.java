package org.example.threads;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.example.common.models.URL;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class CoterieEvents extends Thread{

    private ReadyEvent re;

    private static String URL_VOCALOID = "https://ketto.com/cgi-bin/s5.cgi?cd=94&w=%83%7B%81%5B%83J%83%8D%83C%83h+VOICEROID&ao=1&a=+";
    public CoterieEvents(ReadyEvent re){
        this.re = re;
    }

    @Override
    public void run() {
        while (true){
            try {
                LocalDateTime startLDT = LocalDateTime.now();
                LocalDateTime endLDT = null;

                if(1 <= startLDT.getDayOfMonth() && startLDT.getDayOfMonth() <= 14 ){
                    endLDT = LocalDateTime.of(startLDT.getYear(), startLDT.getMonth(), 15, 0,0,0,0);
                    System.out.println("- Corerie Event NextDate - " + startLDT.getMonthValue() + "/" + 15);
                }else
                if(15 <= startLDT.getDayOfMonth() && startLDT.getDayOfMonth() <= 32 ){
                    endLDT = LocalDateTime.of(startLDT.getYear(), startLDT.getMonthValue() + 1, 1, 0,0,0,0);
                    System.out.println("- Corerie Event NextDate - " + (startLDT.getMonthValue() + 1) + "/" + 1);
                }else{
                    // 処理は無い
                }
                /** HTML から JsonNodeへ変換 */
                String html = URL.getHTTP(URL_VOCALOID);

                System.out.println(html);
//                // HTMLから対象のデータを抽出
//                String param[] = html.split("\"VOICEROID ボーカロイド\"検索結果 [0-9]+/[0-9]+件");
//                param = param[1].split("[0-9]+/[0-9]+件");
//
//                // 抽出データからJsonを生成
//                param = param[0].split("<hr size=\"9\" noshade=\"\">");
//                for(String htm : param){
//
//                }




                startLDT = LocalDateTime.now();

                long sleepTime = ChronoUnit.MILLIS.between(startLDT,endLDT) * 1000;

                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
