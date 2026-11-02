package modele;

public class Trotineta {

    private String idTrotineta;
    private int nivelBaterie;
    private boolean esteDisponibila;
    private String status;

    public Trotineta(String idTrotineta, int nivelBaterie, boolean esteDisponibila, String status) {
        this.idTrotineta = idTrotineta;
        this.nivelBaterie = nivelBaterie;
        this.esteDisponibila = esteDisponibila;
        this.status = status;
    }

    public void deblocare() {
        if (!esteDisponibila) {
            System.out.println("Eroare: Trotineta " + idTrotineta + " nu este disponibila.");
            return;
        }
        esteDisponibila = false;
        status = "IN_UZ";
        System.out.println("Trotineta " + idTrotineta + " a fost deblocata.");
    }

    public void blocare() {
        esteDisponibila = true;
        status = "DISPONIBILA";
        System.out.println("Trotineta " + idTrotineta + " a fost blocata.");
    }

    public String getStatus() {
        return status;
    }

    public String getIdTrotineta() { return idTrotineta; }
    public int getNivelBaterie() { return nivelBaterie; }
    public boolean isEsteDisponibila() { return esteDisponibila; }

    public void setIdTrotineta(String idTrotineta) { this.idTrotineta = idTrotineta; }
    public void setNivelBaterie(int nivelBaterie) { this.nivelBaterie = nivelBaterie; }
    public void setEsteDisponibila(boolean esteDisponibila) { this.esteDisponibila = esteDisponibila; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Trotineta{id='" + idTrotineta + "', nivelBaterie=" + nivelBaterie +
                ", esteDisponibila=" + esteDisponibila + ", status='" + status + "'}";
    }
}