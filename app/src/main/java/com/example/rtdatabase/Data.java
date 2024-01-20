package com.example.rtdatabase;

public class Data {
    public String dataTitle;
    public String dataDesc;
    public String dataLang;
    public String dataImage;
    public String dataAudio;
    public Data(String dataTitle,String dataDesc, String dataLang,String dataImage,String dataAudio) {
        this.dataDesc = dataDesc;
        this.dataImage=dataImage;
        this.dataTitle=dataTitle;
        this.dataLang=dataLang;
        this.dataAudio=dataAudio;
    }

    public Data()
    {

    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataLang() {
        return dataLang;
    }

    public String getDataImage() {
        return dataImage;
    }

    public String getDataAudio() {
        return dataAudio;
    }
}
