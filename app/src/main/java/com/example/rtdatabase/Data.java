package com.example.rtdatabase;

public class Data {
    public String dataTitle;
    public String dataArt;
    public String dataAlbum;
    public String dataImage;
    public String dataAudio;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Data(String dataTitle, String dataArt, String dataAlbum, String dataAudio) {
        this.dataArt = dataArt;
//        this.dataImage=dataImage;
        this.dataTitle=dataTitle;
        this.dataAlbum=dataAlbum;
        this.dataAudio=dataAudio;
    }

    public Data()
    {
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataArt() {
        return dataArt;
    }

    public String getDataAlbum() {
        return dataAlbum;
    }

    public String getDataImage() {
        return dataImage;
    }

    public String getDataAudio() {
        return dataAudio;
    }
}
