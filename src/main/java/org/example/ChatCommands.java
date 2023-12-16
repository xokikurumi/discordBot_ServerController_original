package org.example;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.example.common.Notice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

@Slf4j
public class ChatCommands {

    private static final String TALK_API_KEY = "DZZ9qgFWgdIQqGVpMz4E4X7Qcp6k2igR";
    private static final Logger logger = LoggerFactory.getLogger(ChatCommands.class);

    public static void sendDice(MessageReceivedEvent e) {

        String msg = e.getMessage().getContentRaw();
        String resultMsg[] = {"",""};

        logger.atLevel(Level.TRACE);

        if(msg.matches("(([0-9]+d[0-9]+)\\s*[<>=]+\\s*([0-9]+d[0-9]+))")){
            String[] dices = msg.split("[<>=]+");
            long[] results = {0,0};
            int cnt = 0;
            boolean multipleDice[] = {false,false};
            for(String diceRoll: dices){
                String[] dice = diceRoll.replaceAll("\\s+","").split("[dD]");
                long rowResult = 0;
                if(Long.parseLong(dice[0]) != 1) {
                    multipleDice[cnt] = true;
                    resultMsg[cnt] = "[ ";
                    long resultInt = 0;
                    for(int r = 0; r < Integer.parseInt(dice[0]); r++) {
                        long random = 0;
                        try {
                            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                            random = sr.nextLong(Long.parseLong(dice[1])) + 1;
                        } catch (NoSuchAlgorithmException e1) {
                            // TODO 自動生成された catch ブロック
                            e1.printStackTrace();
                        }
                        rowResult += random;
                        resultMsg[cnt] += random + " , ";
                    }
                    resultMsg[cnt] = resultMsg[cnt].substring(0, resultMsg[cnt].length() -2);
                    resultMsg[cnt] += "]";
                }else {
                    long random = 0;
                    try {
                        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                        rowResult = sr.nextLong(Long.parseLong(dice[1])) + 1;
                    } catch (NoSuchAlgorithmException e1) {
                        // TODO 自動生成された catch ブロック
                        e1.printStackTrace();
                    }
                }
                results[cnt] = rowResult;
                cnt++;
            }

            //演算子を取得
            String relationalOperator = msg.replaceAll("[0-9]+[dD][0-9]+","");
            relationalOperator = relationalOperator.replaceAll("\\s","");
            StringBuilder result = new StringBuilder();
            switch (relationalOperator){
                case "<":

                    logger.info(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + "<" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] < results[1]?" >> 成功" : " >> 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + "<" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] < results[1]?" >> 成功" : " >> 失敗")).queue();
                    break;
                case "<=":
                    logger.info(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + "<=" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] <= results[1]?" >> 成功" : " >> 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + "<=" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] <= results[1]?") >> 成功" : " >> 失敗")).queue();
                    break;
                case ">":
                    logger.info(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + ">" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] > results[1]?" >> 成功" : " >> 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + ">" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] > results[1]?" >> 成功" : " >> 失敗")).queue();
                    break;
                case ">=":
                    logger.info(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + ">=" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] >= results[1]?" >> 成功" : " >> 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + ">=" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] >= results[1]?" >> 成功" : " >> 失敗")).queue();
                    break;
                case "<>":
                    logger.info(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + "<>" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] != results[1]?" >> 成功" : " >> 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multipleDice[0]? resultMsg[0] + results[0] : results[0]) + "<>" + (multipleDice[1]? resultMsg[1] + results[1] : results[1]) + (results[0] != results[1]?" >> 成功" : " >> 失敗")).queue();
                    break;
                default:
                    break;
            }
        }else
        if(msg.matches("(([0-9]+d[0-9]+)\\s*[<>=]+\\s*[0-9]+)")){
            String[] dices = msg.replaceAll("\\s","").split("[<>=]+");
            long figure = 0;
            String result = "[ ";
            long resultInt = 0;
            boolean multi = false;
            for(int l = 0 ; l < dices.length ; l++){
//                System.out.println("L: " + dices[l]);
                if(dices[l].matches("([0-9]+d[0-9]+)")){
                    // ダイスロール
                    String[] dice = dices[l].split("[dD]");

                    if(Long.parseLong(dice[0]) != 1) {
                        multi = true;
                        for(int r = 0; r < Integer.parseInt(dice[0]); r++) {
                            long random = 0;
                            try {
                                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                                random = sr.nextLong(Long.parseLong(dice[1])) + 1;
                            } catch (NoSuchAlgorithmException e1) {
                                // TODO 自動生成された catch ブロック
                                e1.printStackTrace();
                            }
                            resultInt += random;
                            result += random + " , ";
                        }

                        result = result.substring(0, result.length() - 2) + "]";
                    }else {
                        long random = 0;
                        try {
                            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                            random = sr.nextLong(Long.parseLong(dice[1])) + 1;
                            result = "" + random;
                            resultInt = random;
                        } catch (NoSuchAlgorithmException e1) {
                            // TODO 自動生成された catch ブロック
                            e1.printStackTrace();
                        }
                    }

                }else{

                    figure = Integer.parseInt(dices[l]);
                }
            }


            //演算子を取得
            String relationalOperator = msg.replaceAll("[0-9]+[dD][0-9]+","");
            relationalOperator = relationalOperator.replaceAll("\\s","");
            relationalOperator = relationalOperator.replaceAll("[0-9]+","");

            switch (relationalOperator){
                case "<":
                    logger.info(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " < " + figure + " >> " + (resultInt < figure? " 成功":" 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " < " + figure + " >> " + (resultInt < figure? " 成功":" 失敗")).queue();
                    break;
                case "<=":
                    logger.info(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " <= " + figure + " >> " + (resultInt <= figure? " 成功":" 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " <= " + figure + " >> " + (resultInt <= figure? " 成功":" 失敗")).queue();
                    break;
                case ">":
                    logger.info(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " > " + figure + " >> " + (resultInt > figure? " 成功":" 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " > " + figure + " >> " + (resultInt > figure? " 成功":" 失敗")).queue();
                    break;
                case ">=":
                    logger.info(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " >= " + figure + " >> " + (resultInt >= figure? " 成功":" 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " >= " + figure + " > " + (resultInt >= figure? " 成功":" 失敗")).queue();
                    break;
                case "<>":
                    logger.info(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " <> " + figure + " >> " + (resultInt != figure? " 成功":" 失敗"));
                    e.getMessage().getChannel().sendMessage(msg + " >> " + (multi? result + " " + resultInt:resultInt) + " <> " + figure + " >> " + (resultInt != figure? " 成功":" 失敗")).queue();
                    break;
                default:
                    break;
            }
        }else
        if(msg.matches("(([0-9]+[dD][0-9]+|[0-9]+)+[\\+\\-/\\*\\^%])+([0-9]+[dD][0-9]+|[0-9]+)")){
            // 計算式が存在する場合
            String diceMsg = msg;
            String[] dices = msg.replaceAll("\\s","").split("[\\+\\-/\\*\\^%]");

            for(String dice: dices){
                if(!Objects.equals(null, dice) || !dice.isEmpty()){
                    try{
                        Integer.parseInt(dice);

                    }catch (Exception exp){
                        msg = msg.replaceFirst(dice , dice(dice, true));
                    }
                }
            }

            Expression exp = new ExpressionBuilder(msg).build();
            double result = exp.evaluate();
            msg = "("+ diceMsg +" >> " + msg + ") = " + result;

            e.getMessage().getChannel().sendMessage(msg).queue();
        }else
        if(msg.matches("(\\(*([0-9]+d[0-9]+|[0-9]+)+[\\(\\+\\-\\/\\*\\^%\\)]+)+([0-9]+d[0-9]+|[0-9]+)+\\)*")){
            // 計算式が存在する場合
            String diceMsg = msg;
            String[] dices = msg.replaceAll("\\s","").split("[\\(\\+\\-\\/\\*\\^%\\)]");

            for(String dice: dices){
                if(!Objects.equals(null, dice) || !dice.isEmpty()){
                    try{
                        Integer.parseInt(dice);

                    }catch (Exception exp){
                        msg = msg.replaceFirst(dice , dice(dice, true));
                    }
                }

            }

            Expression exp = new ExpressionBuilder(msg).build();
            double result = exp.evaluate();
            msg = "("+ diceMsg +" >> " + msg + ") = " + result;

            e.getMessage().getChannel().sendMessage(msg).queue();
        }else
        {
            String[] dice = msg.split("[dD]");
            if(Long.parseLong(dice[0]) != 1) {
                String result = "[ ";
                long resultInt = 0;
                for(int r = 0; r < Integer.parseInt(dice[0]); r++) {
                    long random = 0;
                    try {
                        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                        random = sr.nextLong(Long.parseLong(dice[1])) + 1;
                    } catch (NoSuchAlgorithmException e1) {
                        // TODO 自動生成された catch ブロック
                        e1.printStackTrace();
                    }
                    resultInt += random;
                    result += random + " , ";
                }
                logger.info(e.getMember().getUser().getName() + " >> " + e.getMessage().getContentRaw() + " > Dice: " + (result.subSequence(0, result.length()-2)) + " ] " + resultInt);
                e.getMessage().getChannel().sendMessage(msg + " >> " + (result.subSequence(0, result.length()-2)) + " ] " + resultInt).queue();
            }else {
                long random = 0;
                try {
                    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                    random = sr.nextLong(Long.parseLong(dice[1])) + 1;
                } catch (NoSuchAlgorithmException e1) {
                    // TODO 自動生成された catch ブロック
                    e1.printStackTrace();
                }

                logger.info(e.getMember().getUser().getName() + " >> " + e.getMessage().getContentRaw() + " > Dice: " + random);
                e.getMessage().getChannel().sendMessage(msg + " >> " + random).queue();
            }
        }
    }



    public static void sendRoulette(MessageReceivedEvent e){
        String msg = e.getMessage().getContentRaw().replaceAll("!rou", "");
        String roulettes[] = msg.split("[\\s\\n]");

        long random = 0;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            random = sr.nextLong(roulettes.length-1);
            logger.info(e.getMember().getUser().getName() + " >> " + e.getMessage().getContentRaw() + " > ルーレット: " + random);
            e.getMessage().getChannel().sendMessage("ルーレット結果 >> " + roulettes[(int) random + 1]).queue();
        } catch (NoSuchAlgorithmException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }

    }


    public static void sendNano(MessageReceivedEvent e) {
//        String msg = e.getMessage().getContentRaw();
        Logger logger = LoggerFactory.getLogger(ChatCommands.class);
        logger.atLevel(Level.TRACE);


        int random = 0;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            random = sr.nextInt(99) + 1;
        } catch (NoSuchAlgorithmException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }


        logger.info(e.getMember().getUser().getName() + " > " + e.getMessage().getContentRaw() + " > NANO Random: " + random);
        if(5 < random){
            e.getMessage().addReaction(Emoji.fromCustom("syukka", 1026820219039133696L , false)).queue();
        }
        if(random <= 5){
            e.getMessage().addReaction(Emoji.fromCustom("nyukayo", 1059074381244022825L , false)).queue();
        }

        if(5 < random && random <= 15) {

            e.getMessage().addReaction(Emoji.fromCustom("buchi", 1017767830579576832L , false)).queue();
        }
        if(random == 16) {
            Notice.info("FIRE!!!!");
            e.getMessage().addReaction(Emoji.fromUnicode("🔥")).queue();
        }
    }

    public static void sendRamen(MessageReceivedEvent e) {
        Logger logger = LoggerFactory.getLogger(ChatCommands.class);
        logger.atLevel(Level.TRACE);

//        String msg = e.getMessage().getContentRaw();
        e.getMessage().addReaction(Emoji.fromUnicode("🍜")).queue();
        logger.info(e.getMember().getUser().getName() + "> Send Ramen");
    }

    public static void sendConstructionOpposition(MessageReceivedEvent e) {
        Logger logger = LoggerFactory.getLogger(ChatCommands.class);
        logger.atLevel(Level.TRACE);

//        String msg = e.getMessage().getContentRaw();
        e.getMessage().addReaction(Emoji.fromCustom("tikaokuri", 1004515008056660061L , false)).queue();
        logger.info(e.getMember().getUser().getName() + "> Send ConstructionOpposition");
    }


    public static void sendLottery(MessageReceivedEvent e){
        Logger logger = LoggerFactory.getLogger(ChatCommands.class);
        logger.atLevel(Level.TRACE);

        String msg = e.getMessage().getContentRaw();
        msg = msg.substring(9 , msg.length());
        logger.info(e.getMember().getUser().getName() + "> Lottery: " + msg);
    }

    public static void sendFire(MessageReceivedEvent e){
        int random = 0;
        try {

            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            random = sr.nextInt(9999) + 1;
        } catch (NoSuchAlgorithmException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }
//        System.out.println("Fire: " + random);
        log.info("FireRandomLog: " + random);
        if(random == 1){
            Notice.info("FIRE!!!!");
            e.getMessage().reply("出火よー").queue();
            e.getMessage().addReaction(Emoji.fromUnicode("🔥")).queue();
        }
    }


    public static void chatBot(MessageReceivedEvent e){


    }

    public static void sendTRPG_CharacterNo6(MessageReceivedEvent e){
        String build = TRPG_6();
        e.getMessage().getChannel().sendMessage(build).queue();
        logger.info(build);
    }

    public static void sendTRPG_CharacterNo6(SlashCommandInteractionEvent e){
        String build = TRPG_6();
        e.reply(build).queue();
        logger.info(build);
    }



    public static void sendTRPG_CharacterNo7(MessageReceivedEvent e){
        String build = TRPG_7();
        e.getMessage().getChannel().sendMessage(build).queue();
        logger.info(build);
    }

    public static void sendTRPG_CharacterNo7(SlashCommandInteractionEvent e){
        String build = TRPG_7();
        e.reply(build).queue();
        logger.info(build);
    }


    private static String dice(String dice, boolean math){
        String result = "";
        String dices[] = dice.split("[dD]");
        if(Long.parseLong(dices[0]) != 1) {

            for(int r = 0; r < Integer.parseInt(dices[0]); r++) {
                long random = 0;
                try {
                    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                    random = sr.nextLong(Long.parseLong(dices[1])) + 1;
                } catch (NoSuchAlgorithmException e1) {
                    // TODO 自動生成された catch ブロック
                    e1.printStackTrace();
                }
                if(math){
                    result += random + "+";
                }else{
                    result += random + ", ";
                }
            }
            if(math){
                result = "("+ result.substring(0,result.length()-1) + ")";
            }else {
                result = result.substring(0,result.length()-1);
            }
        }else {
            long random = 0;
            try {
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                result = sr.nextLong(Long.parseLong(dices[1])) + 1 + "";
            } catch (NoSuchAlgorithmException e1) {
                // TODO 自動生成された catch ブロック
                e1.printStackTrace();
            }
        }

        return result;
    }

    private static int random(int dice, int rand){
        int result = 0;
        for(int r = 0; r < dice; r++){
            try {
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                result += sr.nextInt(rand) + 1;
            } catch (NoSuchAlgorithmException e1) {
                // TODO 自動生成された catch ブロック
                e1.printStackTrace();
            }
        }
        return result;
    }


    private static String TRPG_6(){
        StringBuilder builder = new StringBuilder();
        int STR = random(3,6);
        int CON = random(3,6);
        int POW = random(3,6);
        int DEX = random(3,6);
        int APP = random(3,6);
        int SIZ = random(2,6) + 6;
        int INT = random(2,6) + 6;
        int EDU = random(3,6) + 3;
        int damagePoint = STR + SIZ;
        builder.append("## TRPG 第6版 キャラクターシート\n");
        builder.append("> STR(筋力): "+ STR + "\n");
        builder.append("> CON(体力): "+ CON + "\n");
        builder.append("> POW(精神力): "+ POW + "\n");
        builder.append("> DEX(俊敏性): "+ DEX + "\n");
        builder.append("> APP(外見): "+ APP + "\n");
        builder.append("> SIZ(体格): "+ SIZ + "\n");
        builder.append("> INT(知性): "+ INT + "\n");
        builder.append("> EDU(教育): "+ EDU + "\n");
        builder.append("> =======================\n");
        builder.append("> SAN(正気度): "+ (POW * 5) + "\n");
        builder.append("> 幸運: "+ (POW * 5) + "\n");
        builder.append("> アイディア: "+ (INT * 5) + "\n");
        builder.append("> 知識: "+ (EDU * 5) + "\n");
        builder.append("> 耐久力: "+ ((CON +SIZ) / 2) + "\n");
        builder.append("> マジックポイント: "+ (POW) + "\n");
        builder.append("> 職業技能ポイント: "+ (EDU * 20) + "\n");
        builder.append("> 趣味技能ポイント: "+ (INT * 20) + "\n");
        builder.append("> ダメージポイント: ");
        if(2 <= damagePoint && damagePoint <= 12){
            builder.append("-1d6");
        }else if(13<= damagePoint && damagePoint <= 16){
            builder.append("-1d4");
        }else if(15<= damagePoint && damagePoint <= 24){
            builder.append("");
        }else if(25<= damagePoint && damagePoint <= 32){
            builder.append("+1d4");
        }else if(33<= damagePoint && damagePoint <= 40){
            builder.append("+1d6");
        }else if(41<= damagePoint && damagePoint <= 46){
            builder.append("+2d6");
        }else{
            builder.append("+3d6");
        }
        builder.append("\n");
        return builder.toString();
    }
    private static String TRPG_7(){
        StringBuilder builder = new StringBuilder();
        int STR = random(3,6) * 5;
        int CON = random(3,6) * 5;
        int POW = random(3,6) * 5;
        int DEX = random(3,6) * 5;
        int APP = random(3,6) * 5;
        int SIZ = (random(2,6) + 6 )* 5;
        int INT = (random(2,6) + 6 )* 5;
        int EDU = (random(2,6) + 3 )* 5;
        int LUK = random(3,6) * 5;
        int damagePoint = STR + SIZ;
        builder.append("## TRPG 第7版 キャラクターシート\n");
        builder.append("> STR(筋力): "+ STR + "\n");
        builder.append("> CON(体力): "+ CON + "\n");
        builder.append("> POW(精神力): "+ POW + "\n");
        builder.append("> DEX(俊敏性): "+ DEX + "\n");
        builder.append("> APP(外見): "+ APP + "\n");
        builder.append("> SIZ(体格): "+ SIZ + "\n");
        builder.append("> INT(知性): "+ INT + "\n");
        builder.append("> EDU(教育): "+ EDU + "\n");
        builder.append("> =======================\n");
        builder.append("> SAN(正気度): "+ POW + "\n");
        builder.append("> 幸運: "+ LUK + "\n");
        builder.append("> アイディア: "+ (INT) + "\n");
        builder.append("> 知識: "+ EDU + "\n");
        builder.append("> 耐久力: "+ ((CON +SIZ) / 10) + "\n");
        builder.append("> マジックポイント: "+ (POW / 5) + "\n");
        builder.append("> 興味技能ポイント: "+ (INT * 2) + "\n");
        if(2 <= damagePoint && damagePoint <= 64){
            builder.append("> ダメージポイント: -2\n");
            builder.append("> BUILD(ビルド): -2\n");
        }else if(64<= damagePoint && damagePoint <= 84){
            builder.append("> ダメージポイント: -1\n");
            builder.append("> BUILD(ビルド): -1\n");
        }else if(85<= damagePoint && damagePoint <= 124){
            builder.append("> ダメージポイント: 0\n");
            builder.append("> BUILD(ビルド): 0\n");
        }else if(125<= damagePoint && damagePoint <= 164){
            builder.append("> ダメージポイント: +1d4\n");
            builder.append("> BUILD(ビルド): 1\n");
        }else if(165<= damagePoint && damagePoint <= 204){
            builder.append("> ダメージポイント: +1d6\n");
            builder.append("> BUILD(ビルド): 2\n");
        }else if(205<= damagePoint && damagePoint <= 284){
            builder.append("> ダメージポイント: +2d6\n");
            builder.append("> BUILD(ビルド): 3\n");
        }else if(285<= damagePoint && damagePoint <= 364){
            builder.append("> ダメージポイント: +3d6\n");
            builder.append("> BUILD(ビルド): 4\n");
        }else if(365<= damagePoint && damagePoint <= 444){
            builder.append("> ダメージポイント: +4d6\n");
            builder.append("> BUILD(ビルド): 5\n");
        }else{
            builder.append("> ダメージポイント: +5d6\n");
            builder.append("> BUILD(ビルド): 6\n");
        }
        return builder.toString();
    }
}