package modele;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {

    public static final double TAXA_DEBLOCARE = 1.0;
    public static final double TARIF_PER_MINUT = 0.5;
    public static final int DURATA_MAXIMA = 120;
    public static final int BATERIE_MINIMA = 15;

    // Contor pentru ID-uri unice ale curselor
    private static final AtomicInteger contorCurse = new AtomicInteger(0);

    private String id;
    private String nume;
    private String email;
    private double sold;
    private boolean esteVerificat;
    private int varsta;

    public Client(String id, String nume, String email, double sold, boolean esteVerificat, int varsta) {
        this.id = id;
        this.nume = nume;
        this.email = email;
        this.sold = sold;
        this.esteVerificat = esteVerificat;
        this.varsta = varsta;
    }

    public void autentificare() {
        System.out.println("Client " + nume + " s-a autentificat.");
    }

    public void deautentificare() {
        System.out.println("Client " + nume + " s-a deautentificat.");
    }

    public Cursa inchiriereTrotineta(Trotineta trotineta) {
        if (!esteVerificat) {
            System.out.println("Eroare: Contul nu este verificat.");
            return null;
        }
        if (varsta < 16) {
            System.out.println("Eroare: Varsta minima este 16 ani.");
            return null;
        }
        // Soldul trebuie sa acopere cel putin taxa de deblocare
        if (sold < TAXA_DEBLOCARE) {
            System.out.println("Eroare: Sold insuficient.");
            return null;
        }
        if (trotineta == null || !trotineta.isEsteDisponibila()) {
            System.out.println("Eroare: Trotineta nu este disponibila.");
            return null;
        }
        if (trotineta.getNivelBaterie() < BATERIE_MINIMA) {
            System.out.println("Eroare: Bateria trotinetei este prea descarcata ("
                    + trotineta.getNivelBaterie() + "%).");
            return null;
        }
        trotineta.deblocare();
        String idCursa = "C-" + id + "-" + contorCurse.incrementAndGet();
        Cursa cursa = new Cursa(idCursa, LocalDateTime.now(), null,
                TAXA_DEBLOCARE, TARIF_PER_MINUT, DURATA_MAXIMA, trotineta);
        System.out.println("Client " + nume + " a inchiriat trotineta " + trotineta.getIdTrotineta());
        return cursa;
    }

    public void terminareCursa(Trotineta trotineta, Cursa cursa) {
        if (cursa == null || trotineta == null) {
            System.out.println("Eroare: Cursa sau trotineta invalida.");
            return;
        }
        if (cursa.getTimpFinal() == null) {
            cursa.setTimpFinal(LocalDateTime.now());
        }
        // Se taxeaza minim un minut
        if (!cursa.getTimpFinal().isBefore(cursa.getTimpStart()) && cursa.getDurata() == 0) {
            cursa.setTimpFinal(cursa.getTimpStart().plusMinutes(1));
        }
        // Costul se calculeaza inainte de blocare: daca datele sunt invalide,
        // exceptia apare fara a lasa trotineta si soldul intr-o stare inconsistenta
        double cost = cursa.calculeazaCost();
        trotineta.blocare();
        this.sold -= cost;
        System.out.println("Client " + nume + " a terminat cursa. Cost: " + Plata.formatSuma(cost));
        if (sold < 0) {
            System.out.println("Atentie: Sold negativ (" + Plata.formatSuma(sold) + "). Alimentati contul "
                    + "pentru a putea inchiria din nou.");
        }
    }

    public Problema raportareProblema(String idProblema, String descriere,
                                      String categorie, Trotineta trotineta) {
        if (!esteVerificat) {
            System.out.println("Eroare: Contul nu este verificat.");
            return null;
        }
        // Problemele nu pot fi raportate in timpul unei curse
        if (trotineta != null && "IN_UZ".equals(trotineta.getStatus())) {
            System.out.println("Eroare: Nu se poate raporta o problema in timpul unei curse.");
            return null;
        }
        // Problema se valideaza inainte de a modifica trotineta
        Problema problema = new Problema(idProblema, descriere, categorie, trotineta);
        trotineta.setStatus("IN_SERVICE");
        trotineta.setEsteDisponibila(false);
        System.out.println("Client " + nume + " a raportat o problema: " + descriere);
        return problema;
    }

    public String getId() { return id; }
    public String getNume() { return nume; }
    public String getEmail() { return email; }
    public double getSold() { return sold; }
    public boolean isEsteVerificat() { return esteVerificat; }
    public int getVarsta() { return varsta; }

    public void setId(String id) { this.id = id; }
    public void setNume(String nume) { this.nume = nume; }
    public void setEmail(String email) { this.email = email; }
    public void setSold(double sold) { this.sold = sold; }
    public void setEsteVerificat(boolean esteVerificat) { this.esteVerificat = esteVerificat; }
    public void setVarsta(int varsta) { this.varsta = varsta; }

    @Override
    public String toString() {
        return "Client{id='" + id + "', nume='" + nume + "', email='" + email +
                "', sold=" + sold + ", esteVerificat=" + esteVerificat + ", varsta=" + varsta + "}";
    }
}