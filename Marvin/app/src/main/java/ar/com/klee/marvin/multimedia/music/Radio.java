package ar.com.klee.marvin.multimedia.music;


public class Radio {

    private String frequence;
    private String name;
    private String url;

    public Radio(String f, String n, String u){
        frequence = f;
        name = n;
        url = u;
    }

    public String getFrequence() {
        return frequence;
    }

    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
