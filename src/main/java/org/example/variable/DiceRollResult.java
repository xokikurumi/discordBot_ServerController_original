package org.example.variable;

public class DiceRollResult {
    private long resultBol;
    private String resultStr;


    public DiceRollResult(long resultBol, String resultStr) {
        this.resultBol = resultBol;
        this.resultStr = resultStr;
    }

    public String getResultStr() {
        return resultStr;
    }

    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }

    public long getResultBol() {
        return resultBol;
    }

    public void setResultBol(long resultBol) {
        this.resultBol = resultBol;
    }

    public void addResultBol(long resultBol) {
        this.resultBol += resultBol;
    }
}
