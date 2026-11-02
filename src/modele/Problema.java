package modele;

public class Problema {

    private String idProblema;
    private String descriere;
    private String categorie; // "BATERIE", "DEFECTIUNE", "ACCIDENT"
    private boolean rezolvata;
    private Trotineta trotineta;

    public Problema(String idProblema, String descriere, String categorie, Trotineta trotineta) {
        if (descriere == null || descriere.isEmpty()) {
            throw new IllegalArgumentException("Eroare: Descrierea nu poate fi goala.");
        }
        if (!"BATERIE".equals(categorie) && !"DEFECTIUNE".equals(categorie) && !"ACCIDENT".equals(categorie)) {
            throw new IllegalArgumentException("Eroare: Categorie invalida.");
        }
        if (trotineta == null) {
            throw new IllegalArgumentException("Eroare: Trotineta nu poate fi null.");
        }
        this.idProblema = idProblema;
        this.descriere = descriere;
        this.categorie = categorie;
        this.rezolvata = false;
        this.trotineta = trotineta;
    }

    public void rezolva() {
        if (rezolvata) {
            System.out.println("Problema " + idProblema + " este deja rezolvata.");
            return;
        }
        rezolvata = true;
        trotineta.setStatus("DISPONIBILA");
        trotineta.setEsteDisponibila(true);
        System.out.println("Problema " + idProblema + " a fost rezolvata.");
    }

    public String getIdProblema() { return idProblema; }
    public String getDescriere() { return descriere; }
    public String getCategorie() { return categorie; }
    public boolean isRezolvata() { return rezolvata; }
    public Trotineta getTrotineta() { return trotineta; }

    public void setIdProblema(String idProblema) { this.idProblema = idProblema; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public void setRezolvata(boolean rezolvata) { this.rezolvata = rezolvata; }

    @Override
    public String toString() {
        return "Problema{id='" + idProblema + "', categorie='" + categorie +
                "', descriere='" + descriere + "', rezolvata=" + rezolvata + "}";
    }
}