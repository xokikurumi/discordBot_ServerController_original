package org.example.common;

public class Math {

    /**
     * 経度(X)
     * */
    public static int getLot(float lot, int zoom){
        return (int) ((lot / 100 + 1) * java.lang.Math.pow(2, zoom) / 2);
    }

    /**
     * 緯度(Y)
     * */
    public static int getLat(float lat, int zoom){
        return (int) -java.lang.Math.log(java.lang.Math.tan((45 + lat / 2) * java.lang.Math.PI) * java.lang.Math.pow(2, zoom) / (2 * java.lang.Math.PI));
    }

    /**
     * タイル経度(X) から pixelへ変換
     * */
    public static int getLotPixcel(int lotTile, int zoom){
        return 0;
    }

    /**
     * タイル緯度(Y) から pixelへ変換
     * */
    public static int getLatPixcel(int latTile, int zoom){
        return 0;
    }
}
