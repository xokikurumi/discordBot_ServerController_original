package org.example;

import org.example.variable.DiceRollResult;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Dices {

    public static DiceRollResult roll(String str){
        DiceRollResult result = new DiceRollResult(0l, "");

        String strs[] = str.split("[dD]");
        if(strs[0].equalsIgnoreCase("1")){
            long singleLong = roll(Long.parseLong(strs[1]));
            result.setResultBol(singleLong);
            result.setResultStr("" + singleLong);
        }else{
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for(int r = 0; r < Integer.parseInt(strs[0]) ; r++ ){
                long singleLong = roll(Long.parseLong(strs[1]));
                result.addResultBol(singleLong);
                builder.append(" " + singleLong );
                if( (r + 1 ) != Integer.parseInt(strs[0])){
                    builder.append(" ,");
                }

            }
            builder.append("]");
        }

        return result;
    }



    private static long roll(long cnt){
        long result = 0l;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            result = sr.nextLong(cnt) + 1l;
        } catch (NoSuchAlgorithmException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }

        return result;
    }
}
