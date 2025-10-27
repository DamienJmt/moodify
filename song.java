public class song {
    private String title;
    private String path;
    private String humeur;

    public song(String title, String path, String humeur){
        this.title = title;
        this.path = path;
        this.humeur = humeur;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getHumeur() {
        return humeur;
    }
}