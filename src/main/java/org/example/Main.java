package org.example;


import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.example.common.Notice;
import org.example.common.commonToken;
import org.example.common.logger;
import org.example.threads.CoterieEvents;
import org.example.threads.quekeThread;
import org.jetbrains.annotations.NotNull;


import javax.security.auth.login.LoginException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Slf4j
public class Main extends ListenerAdapter  {
    private quekeThread qt;

//    private static final Logger logger;
    public static void main(String[] args) throws LoginException, InterruptedException {
        log.info("DiscordBot Start.");
        Notice.info("DiscordBot Start");

        JDA jda = JDABuilder.createDefault(commonToken.TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)

                .enableCache(CacheFlag.VOICE_STATE)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("ずんだもん"))
//                .addEventListeners(new Main())
                .build();
        jda.awaitReady();
        log.info("DiscordBot LoginComp.");
        Notice.info("Login Compleated.");

        // コマンド登録
        SlashCommandData mathCmd = Commands.slash("math", "計算してくれます");
        mathCmd.addOption(OptionType.STRING,"math","計算式を入れてください");
        SlashCommandData TRPG_6Cmd = Commands.slash("trpg_6", "TRPG 第6版のキャラクターシートを生成します");
        SlashCommandData TRPG_7Cmd = Commands.slash("trpg_7", "TRPG 第7版のキャラクターシートを生成します");
        SlashCommandData rouCmd = Commands.slash("rou", "実装中なのだ");
        for(Guild guild : jda.getGuilds()){
            guild.updateCommands().addCommands(rouCmd).addCommands(mathCmd).addCommands(TRPG_6Cmd).addCommands(TRPG_7Cmd).queue();
        }

        jda.addEventListener(new Main());
    }



    @Override
    public void onReady(ReadyEvent event) {
        log.info("Ready OK");
        super.onReady(event);

        quekeThread qt = new quekeThread(event);
        CoterieEvents ce = new CoterieEvents(event);
        Notice.info("Event Start.");

        qt.start();
//        ce.start();
        log.info("QuekeEvent Start!");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        Calendar calLog = Calendar.getInstance();
        SimpleDateFormat sdfLog = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS");
        SimpleDateFormat sdfYYYYMMDDHHMMSSLog = new SimpleDateFormat("YYYYMMddHHmmssSSS");

        StringBuilder logFileMsg = new StringBuilder();

        logFileMsg.append("[" + sdfLog.format(calLog.getTime()) + "]");
        logFileMsg.append("[" + e.getMember().getUser().getName() + "]");
//        logFileMsg.append("[" + e.getMember().getUser().get+ "]");


        List<StickerItem> stickerList = e.getMessage().getStickers();

        List<Message.Attachment> listAttachiment = e.getMessage().getAttachments();

        if(listAttachiment.size() != 0){
            logFileMsg.append("[ファイルあり]");

            /// ファイルダウンロードに時間がかかるため、Threadsを使用し処理停滞を回避
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(Message.Attachment attachment : listAttachiment){
                        logger.downloadFile(attachment.getUrl(),e.getGuild().getName(),
                                sdfYYYYMMDDHHMMSSLog.format(calLog.getTime()) + "_" + e.getMessageId() + "_" + attachment.getFileName());
                    }
                }
            });
            th.run();


        } else {
            logFileMsg.append("[ファイルなし]");
        }
        if(stickerList.size() != 0){
            logFileMsg.append("[ステッカーあり]");
            for (StickerItem si : stickerList) {
                logFileMsg.append(si.getId() +  "@"+ si.getName());

            }
        }else{
            logFileMsg.append("[ステッカーなし]");
        }
        logFileMsg.append(e.getMessage().getContentRaw());
        logger.info(e.getGuild().getName()
                , "[" + e.getChannel().getId() + "][" + e.getChannel().getType().toString() + "]" + e.getChannel().getName(), logFileMsg.toString());

        String msg = e.getMessage().getContentRaw().replaceAll("\n","").replaceAll("\r","");

        try {
            User user = e.getMember().getUser();
            if (e.getAuthor().isBot()) {

                log.info(e.getMember().getUser().getName() + "[TEXT][" + e.getChannel().getName() + "][BOT]" + ": " + msg);
                return;
            }else{
                if(e.getChannel().getType().isAudio()){
                    log.info(e.getMember().getUser().getName() + "[AUDIO][" + e.getChannel().getName() + "]" + ": " + msg);
                }else{
                    log.info(e.getMember().getUser().getName() + "[TEXT][" + e.getChannel().getName() + "]" + ": " + msg);
                }
            }

            if(isTange(msg)){
                e.getMessage().addReaction(Emoji.fromUnicode("⛩")).queue();
                e.getMessage().addReaction(Emoji.fromUnicode("🔥")).queue();
            }else{
                //0.01%で燃やす
                ChatCommands.sendFire(e);
            }

            // ナノ族
            if (isNanoCheck(msg)) {
                ChatCommands.sendNano(e);
            }

            if (isRamen(msg)) {
                ChatCommands.sendRamen(e);
            }

            if (isBuildHantai(msg)) {
                ChatCommands.sendConstructionOpposition(e);
            }
            // ダイス
            if (msg.matches("^[0-9]+[dD][0-9]+$")
                    || msg.matches("(([0-9]+[dD][0-9]+)\\s*[<>=]+\\s*([0-9]+[dD][0-9]+))")
                    || msg.matches("(([0-9]+[dD][0-9]+)\\s*[<>=]+\\s*[0-9]+)")
                    || msg.matches("(([0-9]+[dD][0-9]+|[0-9]+)+[\\+/\\*\\-\\^\\%\s])+([0-9]+[dD][0-9]+|[0-9]+)")
                    || msg.matches("(\\(*([0-9]+[dD][0-9]+|[0-9]+)+[\\(\\+\\-\\/\\*\\^%\\)]+)+([0-9]+[dD][0-9]+|[0-9]+)+\\)*")
                    || msg.matches("([0-9]+[\\+/\\*\\-])+[0-9]")) {
                ChatCommands.sendDice(e);
                return;
            }

            if(msg.startsWith("!rou")){
                ChatCommands.sendRoulette(e);
            }

            if(msg.startsWith("!math ")){
                Expression exp = new ExpressionBuilder(msg.replaceAll("!math ","")).build();
                double result = exp.evaluate();
                e.getMessage().getChannel().sendMessage("Result: " + result).queue();
            }
            if(msg.equalsIgnoreCase("!TRPG_6")){
                ChatCommands.sendTRPG_CharacterNo6(e);
            }

            if(msg.equalsIgnoreCase("!TRPG_7")){
                ChatCommands.sendTRPG_CharacterNo7(e);
            }

            if(msg.matches("^.*<@&969540806287433759>.*$")){
                // 管理ずん

                e.getMessage().getChannel().sendMessage("<@&992391412735803392>" + msg.replaceAll("<@&969540806287433759>","").replaceAll("<@&992391412735803392>","")).queue();
            }else
            if(msg.matches("^.*<@&992391412735803392>.*$")){
                // 副管理ずん

                e.getMessage().getChannel().sendMessage("<@&969540806287433759> " + msg.replaceAll("<@&969540806287433759>","").replaceAll("<@&992391412735803392>","")).queue();
            }

            if(msg.matches("^.*<:samusu:1106185990340620428>.*$")){
                e.getMessage().addReaction(Emoji.fromUnicode("🔥")).queue();
            }


            // コマンド系
            if(msg.startsWith("?")){
                msg = msg.substring(1,msg.length());
                System.out.println(msg);
                String args[] = msg.split(" ");
                System.out.println(args.length);

                if(args[0].equalsIgnoreCase("lottery")){
                    ChatCommands.sendLottery(e);
                }

                // 以下ボイスチャンネルテスト
                if (args[0].equalsIgnoreCase("j_join")) {

//                    if (e.getMember().getVoiceState().inAudioChannel()) {
                        String vcId = e.getMember().getVoiceState().getChannel().getId();
//                    String vcId = "977591589318844416";
                        if(vcId.equals("1")){
                            System.out.println("VC ERROR: ErrorCode: 1000 Type1");
                            e.getMessage().getChannel().sendMessage("ボイスチャットが識別できませんでした。").queue();
                            return;
                        }
                        System.out.println("Audio Channel id: " + vcId);
                        System.out.println("Audio Channel Name: " + e.getMember().getVoiceState().getChannel().getName());

                        AudioChannelUnion connectedChannel = e.getMember().getVoiceState().getChannel();
                        AudioManager am = e.getGuild().getAudioManager();
                        am.openAudioConnection(connectedChannel);
                        VoiceChannel vc = e.getMember().getVoiceState().getChannel().asVoiceChannel();

                        e.getMessage().getChannel().sendMessage("Botがボイスチャットに参加します.").queue();
                        e.getMessage().getChannel().sendMessage("録音を開始します.").queue();
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        vc.getGuild().getAudioManager().setReceivingHandler(new AudioReceiveHandler() {

                            @Override
                            public boolean canReceiveCombined() {
                                return false;
                            }

                            @Override
                            public boolean canReceiveEncoded() {
                                return false;
                            }

                            @Override
                            public boolean canReceiveUser() {
                                return true;
                            }

                            @Override
                            public void handleCombinedAudio(CombinedAudio ca) {

                            }

                            @Override
                            public void handleUserAudio(UserAudio ua) {
                                try {
                                    byte[] bytes = ua.getAudioData(1.0d);
                                    AudioFormat af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);

                                    InputStream is = new ByteArrayInputStream(bytes);
                                    AudioInputStream ais = new AudioInputStream(is,af,44100*2*2);
                                    AudioFileFormat.Type affType = AudioFileFormat.Type.WAVE;
                                    File file = new File("C:\\fulltimeRun\\bat\\temp\\"+ vcId+ "_" + sdf.format(cal.getTime()) + ".wav");

                                    AudioSystem.write(ais, affType,file);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
//                    }else{
//                        System.out.println("VC ERROR: ErrorCode: 1000 Type2");
//                        e.getMessage().getChannel().sendMessage("ボイスチャットが識別できませんでした。").queue();
//                    }

                }
                if (args[0].equalsIgnoreCase("j_leave")) {

                    if (e.getGuild().getMemberById(user.getId()).getVoiceState().inAudioChannel()) {
                        String vcId = e.getGuild().getMemberById(user.getId()).getVoiceState().getChannel().getId();
//                        String vcId = "977591589318844416";
                        System.out.println("Audio Channel id: " + vcId);

                        e.getGuild().getAudioManager().closeAudioConnection();

                        e.getMessage().getChannel().sendMessage("Botがボイスチャットから離れました.").queue();
//                    e.getMessage().getChannel().sendMessage("録音が終了しました.\n後ほどこれまでの会話の履歴をテキストファイルで配布します。").queue();
                    }

                }
            }
        } catch (Exception e2) {
            // TODO: handle exception
            System.err.println(e2.getMessage());
        }
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e){

        if(e.getName().equalsIgnoreCase("trpg_6")){
            ChatCommands.sendTRPG_CharacterNo6(e);
        }
        if(e.getName().equalsIgnoreCase("trpg_7")){
            ChatCommands.sendTRPG_CharacterNo7(e);
        }
        if(e.getName().startsWith("math")){
            Expression exp = new ExpressionBuilder(e.getOption("math").getAsString()).build();
            double result = exp.evaluate();
            e.reply("Result: " + result).queue();
        }
    }


    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e){
        Calendar calLog = Calendar.getInstance();
        SimpleDateFormat sdfLog = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS");

        StringBuilder logFileMsg = new StringBuilder();

        logFileMsg.append("[" + sdfLog.format(calLog.getTime()) + "]");

        logFileMsg.append(e.getUser().getName() + " Join");
        logger.info(e.getGuild().getName(), "#入退出ログ", logFileMsg.toString());
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent e){
        Calendar calLog = Calendar.getInstance();
        SimpleDateFormat sdfLog = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS");

        StringBuilder logFileMsg = new StringBuilder();

        logFileMsg.append("[" + sdfLog.format(calLog.getTime()) + "]");

        logFileMsg.append(e.getUser().getName() + " Remove");
        logger.info(e.getGuild().getName(), "#入退出ログ", logFileMsg.toString());
    }
    /***
     * メッセージ内にNanoDiscordBotConfig.ymlファイル内に定義された文字列が含まれているかチェック
     * @param msg メッセージ
     * @return boolean
     * */
    public boolean isNanoCheck(String msg){
        boolean result = false;
        Config conf = YAML.getConfig();
        for(String str : conf.getApplicable()){
            if(msg.matches(str)){
                result = true;
            }
        }


        for(String str : conf.getNot_applicable()){
            if(msg.matches(str)){
                result = false;
            }
        }


        return result;
    }
    private boolean isRamen(String msg){
        boolean result = false;
        Config conf = YAML.getConfig();
        for(String str : conf.getRamen()){
            if(msg.matches(str)){
                result = true;
            }
        }
        return result;
    }


    private boolean isBuildHantai(String msg){
        boolean result = false;
        Config conf = YAML.getConfig();
        for(String str : conf.getConstruction_opposition()){
//            System.out.println(str);
            if(msg.matches(str)){
                result = true;
            }
        }
        return result;
    }


    private boolean isTange(String msg){
        boolean result = false;
        Config conf = YAML.getConfig();
        for(String str : conf.getTange()){
//            System.out.println(str);
            if(msg.matches(str)){
                result = true;
            }
        }
        return result;
    }
}