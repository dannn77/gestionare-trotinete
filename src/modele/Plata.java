package modele;

public class Plata {

    private String idPlata;
    private double suma;
    private String metoda;

    public Plata(String idPlata, double suma, String metoda) {
        this.idPlata = idPlata;
        this.suma = suma;
        this.metoda = metoda;
    }

    public void proceseazaPlata() {
        if (suma <= 0) {
            System.out.println("Eroare: Suma invalida.");
            return;
        }
        System.out.println("Plata de " + formatSuma(suma) + " procesata prin " + metoda + ".");
    }

    // Suma cu 2 zecimale
    public static String formatSuma(double suma) {
        return String.format(java.util.Locale.ROOT, "%.2f RON", suma);
    }

    public String getIdPlata() { return idPlata; }
    public double getSuma() { return suma; }
    public String getMetoda() { return metoda; }

    public void setIdPlata(String idPlata) { this.idPlata = idPlata; }
    public void setSuma(double suma) { this.suma = suma; }
    public void setMetoda(String metoda) { this.metoda = metoda; }

    @Override
    public String toString() {
        return "Plata{id='" + idPlata + "', suma=" + suma + ", metoda='" + metoda + "'}";
    }
}