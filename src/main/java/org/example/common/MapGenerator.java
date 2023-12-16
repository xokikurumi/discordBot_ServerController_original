package org.example.common;

import org.example.common.models.URL;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapGenerator {

    private static final int MAP_GENERATOR_ZOOM = 12;

    public static File create(float lot, float lat){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmssSSS");
        File result = new File(sdf.format(cal.getTime()) + ".png");
        int letInt = Math.getLat(lat, MAP_GENERATOR_ZOOM);
        int lotInt = Math.getLot(lot, MAP_GENERATOR_ZOOM);
        List<File> fileList = new ArrayList<File>();
        for(int y = letInt -1 ; y <= letInt +1; y++){
            for(int x = lotInt -1 ; x <= lotInt +1; y++){
                try {
                    fileList.add(URL.getFile("https://cyberjapandata.gsi.go.jp/xyz/pale/" + MAP_GENERATOR_ZOOM + "/" + x +"/" + y+ ".png", y + "_" + x + ".png"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // 画像を合成
        // 中心画像(4番)に地震発生地を合成

        return result;
    }
}
