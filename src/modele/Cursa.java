package modele;

import java.time.LocalDateTime;

public class Cursa {

    private String idCursa;
    private LocalDateTime timpStart;
    private LocalDateTime timpFinal;
    private double taxaDeblocare;
    private double tarifPerMinut;
    private int durataMaxima;
    private Trotineta trotineta;
    private Plata plata;

    public Cursa(String idCursa, LocalDateTime timpStart, LocalDateTime timpFinal,
                 double taxaDeblocare, double tarifPerMinut, int durataMaxima, Trotineta trotineta) {
        this.idCursa = idCursa;
        this.timpStart = timpStart;
        this.timpFinal = timpFinal;
        this.taxaDeblocare = taxaDeblocare;
        this.tarifPerMinut = tarifPerMinut;
        this.durataMaxima = durataMaxima;
        this.trotineta = trotineta;
    }

    public boolean esteActiva() {
        return timpFinal == null;
    }

    public int getDurata() {
        if (timpFinal == null) {
            throw new IllegalStateException("Eroare: Cursa " + idCursa + " este inca activa.");
        }
        return (int) java.time.Duration.between(timpStart, timpFinal).toMinutes();
    }

    // Calculeaza tariful cursei (nu creeaza si proceseaza plata)
    public double calculeazaTarif() {
        if (timpFinal == null) {
            throw new IllegalStateException("Eroare: Cursa " + idCursa + " nu este finalizata.");
        }

        // Exceptie date invalide
        if (timpFinal.isBefore(timpStart)) {
            throw new IllegalArgumentException("Eroare: timpFinal este inainte de timpStart.");
        }

        int durata = getDurata();

        // Exceptie durata zero
        if (durata == 0) {
            throw new IllegalArgumentException("Eroare: Durata cursei este zero.");
        }

        double cost = taxaDeblocare + (durata * tarifPerMinut);

        // Penalizare durata maxima depasita
        if (durata > durataMaxima) {
            cost += 10.0;
        }

        // Tarif noapte (ora >= 22 sau ora < 6)
        int ora = timpStart.getHour();
        if (ora >= 22 || ora < 6) {
            cost *= 1.20;
        }

        // Discount curse lungi (> 60 min)
        if (durata > 60) {
            cost -= 5.0;
        }

        return cost;
    }

    // Calculeaza costul, creeaza si proceseaza plata
    public double calculeazaCost() {
        double cost = calculeazaTarif();

        this.plata = new Plata("P-" + idCursa, cost, "card");
        plata.proceseazaPlata();

        return cost;
    }

    public String getIdCursa() { return idCursa; }
    public LocalDateTime getTimpStart() { return timpStart; }
    public LocalDateTime getTimpFinal() { return timpFinal; }
    public double getTaxaDeblocare() { return taxaDeblocare; }
    public double getTarifPerMinut() { return tarifPerMinut; }
    public int getDurataMaxima() { return durataMaxima; }
    public Trotineta getTrotineta() { return trotineta; }
    public Plata getPlata() { return plata; }

    public void setIdCursa(String idCursa) { this.idCursa = idCursa; }
    public void setTimpStart(LocalDateTime timpStart) { this.timpStart = timpStart; }
    public void setTimpFinal(LocalDateTime timpFinal) { this.timpFinal = timpFinal; }
    public void setTaxaDeblocare(double taxaDeblocare) { this.taxaDeblocare = taxaDeblocare; }
    public void setTarifPerMinut(double tarifPerMinut) { this.tarifPerMinut = tarifPerMinut; }
    public void setDurataMaxima(int durataMaxima) { this.durataMaxima = durataMaxima; }

    @Override
    public String toString() {
        String durata = esteActiva() ? "activa" : getDurata() + " min";
        return "Cursa{id='" + idCursa + "', start=" + timpStart +
                ", final=" + timpFinal + ", durata=" + durata + "}";
    }
}
