package modele;

import java.util.List;

public class Admin extends Client {

    private String idAngajat;

    public Admin(String id, String nume, String email, double sold,
                 boolean esteVerificat, int varsta, String idAngajat) {
        super(id, nume, email, sold, esteVerificat, varsta);
        this.idAngajat = idAngajat;
    }

    public void adaugaTrotineta(List<Trotineta> flota, Trotineta trotineta) {
        flota.add(trotineta);
        System.out.println("Trotineta " + trotineta.getIdTrotineta() + " adaugata in flota.");
    }

    public void stergereTrotineta(List<Trotineta> flota, String idTrotineta) {
        boolean gasita = flota.removeIf(t -> t.getIdTrotineta().equals(idTrotineta));
        if (gasita) {
            System.out.println("Trotineta " + idTrotineta + " stearsa din flota.");
        } else {
            System.out.println("Eroare: Trotineta " + idTrotineta + " nu a fost gasita.");
        }
    }

    public void rezolvaProblema(Problema problema) {
        if (problema == null) {
            System.out.println("Eroare: Problema invalida.");
            return;
        }
        if (problema.isRezolvata()) {
            System.out.println("Problema este deja rezolvata.");
            return;
        }
        problema.rezolva();
        System.out.println("Admin " + getNume() + " a rezolvat problema " + problema.getIdProblema());
    }

    public void genereazaRaport(List<Cursa> curse, List<Problema> probleme) {
        if (curse == null || curse.isEmpty()) {
            System.out.println("Nu exista curse inregistrate.");
            return;
        }

        double totalVenituri = 0;
        double venitMaxim = -Double.MAX_VALUE;
        double venitMinim = Double.MAX_VALUE;
        int curseFinalizate = 0;
        int curseNoapte = 0;
        int curseLungi = 0;
        int trotineteOcupate = 0;
        int curseInvalide = 0;

        for (Cursa c : curse) {
            if (c.esteActiva()) {
                trotineteOcupate++;
                continue;
            }

            // calculeazaTarif() nu creeaza si nu proceseaza plati
            double cost;
            try {
                cost = c.calculeazaTarif();
            } catch (IllegalArgumentException e) {
                curseInvalide++;
                continue;
            }
            totalVenituri += cost;
            curseFinalizate++;

            int ora = c.getTimpStart().getHour();
            if (ora >= 22 || ora < 6) {
                curseNoapte++;
            }

            if (c.getDurata() > 60) {
                curseLungi++;
            }

            if (cost > venitMaxim) venitMaxim = cost;
            if (cost < venitMinim) venitMinim = cost;
        }

        double medieCost = curseFinalizate > 0 ? totalVenituri / curseFinalizate : 0;

        int problemeRezolvate = 0;
        int problemeInAsteptare = 0;
        int problemeBaterie = 0;
        int problemeDefectiune = 0;
        int problemeAccident = 0;

        if (probleme != null && !probleme.isEmpty()) {
            for (Problema p : probleme) {
                if (p.isRezolvata()) {
                    problemeRezolvate++;
                } else {
                    problemeInAsteptare++;
                }

                switch (p.getCategorie()) {
                    case "BATERIE": problemeBaterie++; break;
                    case "DEFECTIUNE": problemeDefectiune++; break;
                    case "ACCIDENT": problemeAccident++; break;
                }
            }
        }

        System.out.println("=== Raport ===");
        System.out.println("Total curse: " + curse.size());
        System.out.println("Curse finalizate: " + curseFinalizate);
        System.out.println("Curse active (trotinete ocupate): " + trotineteOcupate);
        if (curseInvalide > 0) {
            System.out.println("Curse cu date invalide (ignorate): " + curseInvalide);
        }
        System.out.println("Curse de noapte: " + curseNoapte);
        System.out.println("Curse lungi (>60 min): " + curseLungi);
        System.out.println("Total venituri: " + Plata.formatSuma(totalVenituri));
        System.out.println("Medie cost cursa: " + Plata.formatSuma(medieCost));
        if (curseFinalizate > 0) {
            System.out.println("Venit maxim dintr-o cursa: " + Plata.formatSuma(venitMaxim));
            System.out.println("Venit minim dintr-o cursa: " + Plata.formatSuma(venitMinim));
        }
        System.out.println("--- Probleme ---");
        System.out.println("Total probleme: " + (probleme != null ? probleme.size() : 0));
        System.out.println("Probleme rezolvate: " + problemeRezolvate);
        System.out.println("Probleme in asteptare: " + problemeInAsteptare);
        System.out.println("Probleme baterie: " + problemeBaterie);
        System.out.println("Probleme defectiune: " + problemeDefectiune);
        System.out.println("Probleme accident: " + problemeAccident);
    }

    public String getIdAngajat() { return idAngajat; }
    public void setIdAngajat(String idAngajat) { this.idAngajat = idAngajat; }

    @Override
    public String toString() {
        return "Admin{id='" + getId() + "', nume='" + getNume() +
                "', idAngajat='" + idAngajat + "'}";
    }
}