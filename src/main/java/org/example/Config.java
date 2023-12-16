package org.example;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public Config() {
        this.applicable = new ArrayList<>();
        this.not_applicable = new ArrayList<>();
        this.ramen = new ArrayList<>();
        this.eath_quake_infomation_id = new ArrayList<>();
        this.construction_opposition = new ArrayList<>();
    }

    public List<String> applicable;
    public List<String> not_applicable;
    public List<String> ramen;
    public List<String> eath_quake_infomation_id;

    public List<String> construction_opposition;

    public List<String> tange;

    public List<String> getTange() {
        return tange;
    }

    public void setTange(List<String> tange) {
        this.tange = tange;
    }

    public List<String> getApplicable() {
        return applicable;
    }

    public void setApplicable(List<String> applicable) {
        this.applicable = applicable;
    }

    public List<String> getNot_applicable() {
        return not_applicable;
    }

    public void setNot_applicable(List<String> not_applicable) {
        this.not_applicable = not_applicable;
    }

    public List<String> getRamen() {
        return ramen;
    }

    public void setRamen(List<String> ramen) {
        this.ramen = ramen;
    }

    public List<String> getEath_quake_infomation_id() {
        return eath_quake_infomation_id;
    }

    public void setEath_quake_infomation_id(List<String> eath_quake_infomation_id) {
        this.eath_quake_infomation_id = eath_quake_infomation_id;
    }

    public List<String> getConstruction_opposition() {
        return construction_opposition;
    }

    public void setConstruction_opposition(List<String> construction_opposition) {
        this.construction_opposition = construction_opposition;
    }
}
