package org.example.threads;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.eclipse.jetty.util.StringUtil;
import org.example.Config;
import org.example.YAML;
import org.example.common.logger;
import org.example.db.Database;
import org.example.db.Query;
import org.w3c.dom.Text;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class quekeThread extends Thread {
    MessageReceivedEvent re;


    public quekeThread(MessageReceivedEvent re){
        this.re = re;
    }

    @Override
    public void run() {

        logger.info("thread", "[Thread][QuekeThread] QuekeThread","Thread Start!!!");

        while (true){
            LocalDateTime ldt = LocalDateTime.now();
            ZonedDateTime zdt = ldt.atZone(ZoneOffset.ofHours(+9));
            long epochMilli = zdt.toInstant().toEpochMilli();
            try {

                // 災害に関する情報を取得
                URL url = new URL("https://api.p2pquake.net/v2/history?codes=551&codes=552&codes=556&limit=3");

                InputStream is = url.openStream();
                InputStreamReader isr = new InputStreamReader(is);
                int i = isr.read();
                String str = "";
                while(i != -1) {
                    str += (char) i;
                    i = isr.read();
                }

                // 以下地震情報の生成
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(str);
//                System.out.println(str);
                String header = "";
                String body = "";
//                System.out.println(json.size());
                for(int r = 0; r < json.size() ; r++){
                    JsonNode jn = json.get(r);
                    if(jn.get("code").asInt() == 551){
//                        System.out.println(jn);
                        // 震度4以上
                        if(40 <= jn.get("earthquake").get("maxScale").asInt()){
                            String query = Query.EARTH_QUAKE_SELECT.replaceAll("\\$\\{0\\}",jn.get("id").asText());
                            query = query.replaceAll("\\$\\{1\\}",jn.get("earthquake").get("hypocenter").get("latitude").asText());
                            query = query.replaceAll("\\$\\{2\\}",jn.get("earthquake").get("hypocenter").get("longitude").asText());

                            if(!Database.executeCheckId(query)){
//                                System.out.println(jn);


                                String SelQuery = Query.EARTH_QUAKE_SELECT_WHERE_TIME;
                                SelQuery = SelQuery.replaceAll("\\$\\{1\\}",jn.get("earthquake").get("hypocenter").get("latitude").asText());
                                SelQuery = SelQuery.replaceAll("\\$\\{2\\}",jn.get("earthquake").get("hypocenter").get("longitude").asText());
                                SelQuery = SelQuery.replaceAll("\\$\\{3\\}",jn.get("earthquake").get("time").asText());
                                header = arthQuakeInfo(jn.get("earthquake"), SelQuery, jn.get("time").asText());
                                // 画像生成(予定)
                                body = arthQuakeLocationInfo(jn);

                                // 送信処理
                                Config conf = YAML.getConfig();
                                for(String id: conf.getEath_quake_infomation_id()){
                                    TextChannel tc = re.getJDA().getTextChannelById(id);
                                    if(tc.canTalk()){
                                        tc.sendMessage(header).queue();
                                        // 位置情報送信
                                        if(!(jn.get("earthquake").get("hypocenter").get("latitude").asText().equalsIgnoreCase("-200")
                                                && jn.get("earthquake").get("hypocenter").get("longitude").asText().equalsIgnoreCase("-200"))){
                                            String locationURL = "https://maps.google.com/maps/@${0},${1},14z?q=${0},${1}";

                                            locationURL = locationURL.replaceAll("\\$\\{0\\}",jn.get("earthquake").get("hypocenter").get("latitude").asText());
                                            locationURL = locationURL.replaceAll("\\$\\{1\\}",jn.get("earthquake").get("hypocenter").get("longitude").asText());

                                            tc.sendMessage(locationURL).queue();
                                        }
                                        System.out.println(header);
                                        System.out.println(body);
                                        logger.info("thread", "[Thread][QuekeThread] QuekeThread",header);
                                        logger.info("thread", "[Thread][QuekeThread] QuekeThread",body);
                                        List<String> ls = sendMsgTrim(body);
                                        for(String msg: ls){
                                            tc.sendMessage(msg).queue();
                                        }
                                    }
                                }

                                query = Query.EARTH_QUAKE_INSERT.replaceAll("\\$\\{0\\}",jn.get("id").asText());
                                query = query.replaceAll("\\$\\{1\\}",jn.get("earthquake").get("hypocenter").get("latitude").asText());
                                query = query.replaceAll("\\$\\{2\\}",jn.get("earthquake").get("hypocenter").get("longitude").asText());
                                query = query.replaceAll("\\$\\{3\\}",jn.get("earthquake").get("time").asText());
                                Database.save(query);
                            }
                        }
                    }

                    // 津波
                    if(jn.get("code").asInt() == 552){
                        String query = Query.TSUNAMI_SELECT.replaceAll("\\$\\{0\\}",jn.get("id").asText());
                        String[] checkResult = Database.executeCheckIdStr(query);
                        if(checkResult.length == 0){
                            // 未発信の場合
                            tsunamiInfo(jn);

                            // 対象データを保存
                            query = Query.TSUNAMI_INSERT.replaceAll("\\$\\{0\\}",jn.get("id").asText());
                            query = query.replaceAll("\\$\\{1\\}",jn.get("time").asText());
                            Database.save(query);
                        }

                    }

                    // 緊急地震速報（警報）
                    if(jn.get("code").asInt() == 556){
                        String query = Query.EEW_SELECT.replaceAll("\\$\\{0\\}",jn.get("id").asText());
                        String[] checkResult = Database.executeCheckIdStr(query);
                        if(checkResult.length == 0){
                            body = EEW_Info(jn);
                            List<String> line = sendMsgTrim(body);
                            for(String ln : line){
                                Config conf = YAML.getConfig();
                                for(String id: conf.getEath_quake_infomation_id()) {
                                    TextChannel tc = re.getJDA().getTextChannelById(id);
                                    tc.sendMessage(ln).queue();
                                }
                            }

                            query = Query.EEW_INSERT.replaceAll("\\$\\{0\\}",jn.get("id").asText());
                            query = query.replaceAll("\\$\\{1\\}",jn.get("time").asText());
                            Database.save(query);
                        }
                    }
                }
                // 処理時間(1分に一度の処理にするために演算)
                long nowTime = System.currentTimeMillis();
                long sleepTime = (1000 * 2) - (nowTime - epochMilli);
                if(sleepTime > 0){
                    Thread.sleep(sleepTime);
                }


            } catch (MalformedURLException e) {
//                throw new RuntimeException(e);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
            }
        }
    }


    /****************************
     * 以下地震に関する情報の整理
     ****************************/
    private String arthQuakeInfo(JsonNode json, String query, String time){
        System.out.println(query);
        String queakeInfo[] = Database.executeCheckIdStr(query);

        String result = "";
        if(queakeInfo[1].equalsIgnoreCase(json.get("hypocenter").get("latitude").asText())
                && queakeInfo[2].equalsIgnoreCase(json.get("hypocenter").get("longitude").asText())
                && queakeInfo[3].equalsIgnoreCase(time)){
//            result = "@everyone\n";
            result += ":shaking_face:【地震速報 (続報)】:shaking_face:" + "\n";
        }else{

            result += "<@&1089680055891927040>\n";
            result += "\n";
//            result += "@everyone\n";
            result += "# 🌎【地震速報】🌎" + "\n";
        }
        // HEADER

        result += "===========================================\n";
        result += "発生時刻: " + json.get("time").asText() + "\n";
        result += "震源地: " + json.get("hypocenter").get("name").asText() + "\n";
        if(json.get("hypocenter").get("depth").asInt() == -1){
            result += "震源の深さ: 情報なし\n";
        }else
        if(json.get("hypocenter").get("depth").asInt() == 0){
            result += "震源の深さ: すごく浅い\n";
        }else {
            result += "震源の深さ: " + String.format("%,d", json.get("hypocenter").get("depth").asInt()) + "km\n";
        }

        if(json.get("hypocenter").get("magnitude").asInt() == -1){
            result += "マグニチュード: 情報なし\n";
        }else {
            result += "マグニチュード: " + String.format("%,d", json.get("hypocenter").get("magnitude").asInt()) + "\n";
        }

        result += "最大震度: " + getScale(json.get("maxScale").asInt()) + "\n";
        result += "津波(国内): " + getTsunami(json.get("domesticTsunami").asText()) + "\n";
        result += "津波(海外): " + getTsunami(json.get("foreignTsunami").asText()) + "\n";
        result += "===========================================\n";

        return result;
    }

    private String arthQuakeLocationInfo(JsonNode json){
        StringBuilder builder = new StringBuilder();
        Map<String,List<JsonNode>> result = new HashMap<String, List<JsonNode>>();


        result.put("震度1",new ArrayList<>());
        result.put("震度2",new ArrayList<>());
        result.put("震度3",new ArrayList<>());
        result.put("震度4",new ArrayList<>());
        result.put("震度5弱",new ArrayList<>());
        result.put("震度5強",new ArrayList<>());
        result.put("震度6弱",new ArrayList<>());
        result.put("震度6強",new ArrayList<>());
        result.put("震度7",new ArrayList<>());

        // 震度 > 都道府県 > 市町村
        /*
                    {
                "addr": "宮城川崎町前川",
                "isArea": false,
                "pref": "宮城県",
                "scale": 20
            },
 */
        String scaled = "";
        for (JsonNode jn : json.get("points")){
            List<JsonNode> rowList = result.get(getScale(jn.get("scale").asInt()));
            rowList.add(jn);
            result.put(getScale(jn.get("scale").asInt()), rowList);
        }

        String[] scales = {"震度7", "震度6強", "震度6弱", "震度5強", "震度5弱", "震度4", "震度3"/*, "震度2", "震度1"*/};
        for(String key: scales){
            if(result.get(key).size() > 0){
                if(key.equals("震度7") || key.equals("震度6強") || key.equals("震度6弱")){
                    builder.append("## 【" + key + "】\n");
                } else {
                    builder.append("### 【" + key + "】\n");
                }
                String pref = "";
                for(JsonNode jn: result.get(key)){
                    if(!pref.equalsIgnoreCase(jn.get("pref").asText())){
                        builder.append(jn.get("pref").asText() + " " + jn.get("addr").asText()+ "\n");
                        pref = jn.get("pref").asText();
                    }else{
                        builder.append(jn.get("pref").asText().replaceAll(".*","　") + "　 " + jn.get("addr").asText() + "\n");
                    }
                }
//                builder.append("===========================================\n");
            }
        }


        return builder.toString();
    }


    private String getScale(int scale){
        String result = "";
        switch (scale){
            case -1:
                result = "情報なし";
                break;
            case 10:
                result = "震度1";
                break;
            case 20:
                result = "震度2";
                break;
            case 30:
                result = "震度3";
                break;
            case 40:
                result = "震度4";
                break;
            case 45:
                result = "震度5弱";
                break;
            case 50:
                result = "震度5強";
                break;
            case 55:
                result = "震度6弱";
                break;
            case 60:
                result = "震度6強";
                break;
            case 70:
                result = "震度7";
                break;
            case 99:
                result = "予測不能(震度7より大きい)";
                break;
        }

        return result;
    }


    private String getTsunami(String tsunami){
        String result = tsunami;
        if(tsunami.equalsIgnoreCase("None")){
            result = "なし";
        }
        if(tsunami.equalsIgnoreCase("Unknown")){
            result = "不明";
        }
        if(tsunami.equalsIgnoreCase("Checking")){
            result = "調査中";
        }
        if(tsunami.equalsIgnoreCase("NonEffective")){
            result = "海面変動が予想されるが、被害の心配なし";
        }
        if(tsunami.equalsIgnoreCase("Watch")){
            result = "津波注意報";
        }
        if(tsunami.equalsIgnoreCase("Warning")){
            result = "津波予報(種類不明)";
        }
        return result;
    }

    /*************************
     * 以下津波に関する情報の整理
     *************************/
    private String tsunamiInfo(JsonNode json){
        StringBuilder builder = new StringBuilder();
        List<JsonNode> MajorWarning = new ArrayList<>();
        List<JsonNode> Warning = new ArrayList<>();
        List<JsonNode> Watch = new ArrayList<>();
        List<JsonNode> Unknown = new ArrayList<>();
        for (JsonNode jn : json){
            if(jn.get("grade").asText().equalsIgnoreCase("MajorWarning")){
                MajorWarning.add(jn);
            }
            if(jn.get("grade").asText().equalsIgnoreCase("Warning")){
                Warning.add(jn);
            }
            if(jn.get("grade").asText().equalsIgnoreCase("Watch")){
                Watch.add(jn);
            }
            if(jn.get("grade").asText().equalsIgnoreCase("Unknown")){
                Unknown.add(jn);
            }
        }
        builder.append("<@&1089680055891927040>\n");
        builder.append("\n");
        if(json.get("cancelled").asBoolean()){
            builder.append("各津波予報が解除されました。\n");
            Config conf = YAML.getConfig();
            for(String id: conf.getEath_quake_infomation_id()) {
                TextChannel tc = re.getJDA().getTextChannelById(id);
                tc.sendMessage(builder.toString()).queue();
            }
        }else {
            /// 情報生成
            Config conf = YAML.getConfig();
            for(String id: conf.getEath_quake_infomation_id()) {
                TextChannel tc = re.getJDA().getTextChannelById(id);
                if(MajorWarning.size() > 0) {
//            builder.append("===========================================\n");
                    builder.append("# 【大津波警報】\n");
                    builder.append("** 3mを超えるの津波が予測されます。 **\n");
                    builder.append("木造家屋が全壊・流失し、人は津波による流れに巻き込まれます。\n" +
                            "沿岸部や川沿いにいる人は、** ただちに高台や避難ビルなど安全な場所へ避難してください。 **\n");
                    builder.append("\n");
                    builder.append("対象エリア:\n");

                    tc.sendMessage(builder.toString()).queue();
                    builder = new StringBuilder();
                    for (JsonNode jn: MajorWarning) {
                        builder.append("### " + jn.get("name").asText() + "\n");

                        boolean reached = false;
                        for (JsonNode firstHeight: jn.get("firstHeight")){
                            if(! firstHeight.get("arrivalTime").isEmpty()){
                                builder.append("> 津波到達予想時刻: " + firstHeight.get("arrivalTime").asText() + "\n");
                            }

                            builder.append("> " + firstHeight.get("condition").asText() + "\n");
                            if (!reached && !firstHeight.get("condition").asText().equals("第１波の到達を確認")){
                                reached = true;
                            }
                        }
                        if(!reached){
                            for (JsonNode maxHeight: jn.get("maxHeight")){

                                builder.append("> 予想される津波の高さ: " + maxHeight.get("description").asText() + "\n");
                            }
                        }
                        tc.sendMessage(builder.toString()).queue();
                        builder = new StringBuilder();

                    }
                }

                if(Warning.size() > 0) {
//            builder.append("===========================================\n");
                    builder.append("# 【津波警報】\n");
                    builder.append("** 1mを超3m以下の津波が予測されます。**\n");
                    builder.append("標高の低いところでは津波が襲い、浸水被害が発生します。人は津波による流れに巻き込まれます。\n" +
                            "沿岸部や川沿いにいる人は、** ただちに高台や避難ビルなど安全な場所へ避難してください。 **\n");
                    builder.append("\n");
                    builder.append("対象エリア:\n");

                    tc.sendMessage(builder.toString()).queue();
                    builder = new StringBuilder();

                    for (JsonNode jn: MajorWarning) {
                        builder.append("### "+ jn.get("name").asText() + "\n");
                        boolean reached = false;
                        for (JsonNode firstHeight: jn.get("firstHeight")){
                            if(! firstHeight.get("arrivalTime").isEmpty()){
                                builder.append("> 津波到達予想時刻: " + firstHeight.get("arrivalTime").asText() + "\n");
                            }

                            builder.append("> " + firstHeight.get("condition").asText() + "\n");
                            if (!reached && !firstHeight.get("condition").asText().equals("第１波の到達を確認")){
                                reached = true;
                            }
                        }
                        if(!reached){
                            for (JsonNode maxHeight: jn.get("maxHeight")){

                                builder.append("> 予想される津波の高さ: " + maxHeight.get("description").asText() + "\n");
                            }
                        }

                        tc.sendMessage(builder.toString()).queue();
                        builder = new StringBuilder();

                    }
                }

                if(Warning.size() > 0) {
//            builder.append("===========================================\n");
                    builder.append("# 【津波注意報】\n");
                    builder.append("** 0.2m超1m以下の津波が予測されます。**\n");
                    builder.append("海の中では人は速い流れに巻き込まれ、また、養殖いかだが流失し小型船舶が転覆します。\n");
                    builder.append("海の中にいる人はただちに海から上がって、** 海岸から離れてください。 **\n");
                    builder.append("\n");
                    builder.append("対象エリア:\n");

                    tc.sendMessage(builder.toString()).queue();
                    builder = new StringBuilder();

                    for (JsonNode jn: MajorWarning) {
                        builder.append("### "+ jn.get("name").asText() + "\n");

                        boolean reached = false;
                        for (JsonNode firstHeight: jn.get("firstHeight")){
                            if(! firstHeight.get("arrivalTime").isEmpty()){
                                builder.append("> 津波到達予想時刻: " + firstHeight.get("arrivalTime").asText() + "\n");
                            }

                            builder.append("> " + firstHeight.get("condition").asText() + "\n");
                            if (!reached && !firstHeight.get("condition").asText().equals("第１波の到達を確認")){
                                reached = true;
                            }
                        }
                        if(!reached){
                            for (JsonNode maxHeight: jn.get("maxHeight")){

                                builder.append("> 予想される津波の高さ: " + maxHeight.get("description").asText() + "\n");
                            }
                        }

                        tc.sendMessage(builder.toString()).queue();
                        builder = new StringBuilder();

                    }
                }

            }
        }



        return builder.toString();
    }


    /*************************
     * 以下緊急地震速報に関する情報の整理
     *************************/
    private String EEW_Info(JsonNode json) {
        StringBuilder builder = new StringBuilder();
        builder.append("@everyone\n");
        builder.append("# 📢【緊急地震速報】📢\n");
        JsonNode arthQuake = json.get("earthquake");
        JsonNode hypocenter = arthQuake.get("hypocenter");
        if(!arthQuake.get("condition").asText().isEmpty()){
            builder.append("##" + arthQuake.get("condition").asText() + "\n");
        }
        builder.append("** 震央地: **" + hypocenter.get("name").asText() + "\n");
        builder.append("** 地震発生時刻: **" + arthQuake.get("arrivalTime").asText() + "\n");
        builder.append("** 震源の深さ: **" + hypocenter.get("depth").asText() + "\n");
        builder.append("** マグニチュード: **" + hypocenter.get("magnitude").asText() + "\n");
        builder.append("\n");
        builder.append("\n");
        Map<String, List<JsonNode>> kindCode10 = new HashMap<String, List<JsonNode>>();
        Map<String, List<JsonNode>> kindCode11 = new HashMap<String, List<JsonNode>>();
        Map<String, List<JsonNode>> kindCode19 = new HashMap<String, List<JsonNode>>();
        String[] scales = {"震度7", "震度6強", "震度6弱", "震度5強", "震度5弱", "震度4", "震度3"/*, "震度2", "震度1"*/};

        // 初期化
        for(String str: scales){
            kindCode10.put(str, new ArrayList<>());
            kindCode11.put(str, new ArrayList<>());
            kindCode19.put(str, new ArrayList<>());
        }

        for(JsonNode jn : json.get("areas")) {
            if(jn.get("kindCode").asInt() == 10){
                List<JsonNode> lst = kindCode10.get(getScale(jn.get("scaleFrom").asInt()));
                lst.add(jn);
                kindCode10.put(getScale(jn.get("scaleFrom").asInt()), lst);
            }
            if(jn.get("kindCode").asInt() == 11){
                List<JsonNode> lst = kindCode11.get(getScale(jn.get("scaleFrom").asInt()));
                lst.add(jn);
                kindCode11.put(getScale(jn.get("scaleFrom").asInt()), lst);
            }
            if(jn.get("kindCode").asInt() == 19){
                List<JsonNode> lst = kindCode19.get(getScale(jn.get("scaleFrom").asInt()));
                lst.add(jn);
                kindCode19.put(getScale(jn.get("scaleFrom").asInt()), lst);
            }

        }

        for(String key: kindCode10.keySet()){
            if(kindCode10.get(key).size() > 0){
                builder.append("## 最大震度: " + key + "\n");
                builder.append("### 主要動について、未到達と予測" + "\n");
                List<JsonNode> lst = kindCode10.get(key);
                for(JsonNode jn : lst){
                    builder.append("> " + jn.get("name").asText() + "\n");
                }
            }
        }

        for(String key: kindCode11.keySet()){
            if(kindCode11.get(key).size() > 0){
                builder.append("## 最大震度: " + key + "\n");
                builder.append("### 主要動の到達予想なし（PLUM法による予想）" + "\n");
                List<JsonNode> lst = kindCode11.get(key);
                for(JsonNode jn : lst){
                    builder.append("> " + jn.get("name").asText() + "\n");
                }
            }
        }

        for(String key: kindCode19.keySet()){
            if(kindCode19.get(key).size() > 0){
                builder.append("## 最大震度: " + key + "\n");
                builder.append("### 主要動の到達予想なし（PLUM法による予想）" + "\n");
                List<JsonNode> lst = kindCode19.get(key);
                for(JsonNode jn : lst){
                    builder.append("> " + jn.get("name").asText() + "\n");
                }
            }
        }
        return builder.toString();
    }


    private List<String> sendMsgTrim(String msg) {
        List<String> result = new ArrayList<>();
        if(msg.length() <= 2000){
            // 2000文字以下の場合
            result.add(msg);
        } else {
            // 2000を超える場合
            String[] lines = msg.split("\n");
            String newLine = "";
            for(String ln : lines){
                if(new String(newLine + ln + "\n").length() < 2000) {
                    newLine += ln + "\n";
                }else {
                    result.add(newLine);
                    newLine = ln + "\n";
                }
            }

        }
        return result;
    }

}
