package org.example;


import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class YAML {

    public static Config getConfig(){
        Config result = new Config();
//        Yaml yaml = new Yaml();
//        try {
//
//            result = yaml.loadAs(Files.newInputStream(Paths.get(".\\conf\\NanoDiscordBotConfig.yml")), Config.class);
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
//        }
        try {
            YamlReader reader = new YamlReader(new FileReader("C:\\fulltimeRun\\bat\\conf\\NanoDiscordBotConfig.yml"));

            result = reader.read(Config.class);


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (YamlException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }


        return result;
    }
}
