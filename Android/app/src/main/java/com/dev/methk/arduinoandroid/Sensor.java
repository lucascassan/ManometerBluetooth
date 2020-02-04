package com.dev.methk.arduinoandroid;

public class Sensor {


    private String nome;
    private String KPA;
    private String PSI;
    private String BAR;

    public Sensor(String nome, String BAR, String PSI, String KPA){
        this.nome = nome;
        this.KPA = KPA;
        this.PSI = PSI;
        this.BAR = BAR;

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getKPA() {
        return KPA;
    }

    public void setKPA(String KPA) {
        this.KPA = KPA;
    }

    public String getPSI() {
        return PSI;
    }

    public void setPSI(String PSI) {
        this.PSI = PSI;
    }

    public String getBAR() {
        return BAR;
    }

    public void setBAR(String BAR) {
        this.BAR = BAR;
    }
}
