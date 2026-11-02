package app;

import modele.Admin;
import modele.Client;
import modele.Cursa;
import modele.Plata;
import modele.Problema;
import modele.Trotineta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

    // Demonstratie a fluxului complet: flota, inchirieri, probleme si raport.

public class Main {

    public static void main(String[] args) {
        List<Trotineta> flota = new ArrayList<>();
        List<Cursa> curse = new ArrayList<>();
        List<Problema> probleme = new ArrayList<>();

        Admin admin = new Admin("A001", "Admin Ion", "admin@trotinete.ro", 0.0, true, 35, "EMP001");

        sectiune("1. Adaugare trotinete in flota");
        Trotineta t1 = new Trotineta("T001", 90, true, "DISPONIBILA");
        Trotineta t2 = new Trotineta("T002", 60, true, "DISPONIBILA");
        Trotineta t3 = new Trotineta("T003", 8, true, "DISPONIBILA");
        admin.adaugaTrotineta(flota, t1);
        admin.adaugaTrotineta(flota, t2);
        admin.adaugaTrotineta(flota, t3);

        Client maria = new Client("C001", "Maria", "maria@gmail.com", 100.0, true, 22);
        Client alex = new Client("C002", "Alex", "alex@gmail.com", 50.0, true, 14);
        Client ana = new Client("C003", "Ana", "ana@gmail.com", 50.0, false, 30);

        sectiune("2. Incercari de inchiriere esuate");
        alex.inchiriereTrotineta(t1);   // sub 16 ani
        ana.inchiriereTrotineta(t1);    // cont neverificat
        maria.inchiriereTrotineta(t3);  // baterie descarcata

        sectiune("3. Cursa de zi (45 min)");
        maria.autentificare();
        Cursa cursaZi = maria.inchiriereTrotineta(t1);
        cursaZi.setTimpStart(LocalDateTime.of(2026, 5, 10, 14, 0));
        cursaZi.setTimpFinal(LocalDateTime.of(2026, 5, 10, 14, 45));
        maria.terminareCursa(t1, cursaZi);
        curse.add(cursaZi);

        sectiune("4. Cursa de noapte lunga (90 min)");
        Cursa cursaNoapte = maria.inchiriereTrotineta(t2);
        cursaNoapte.setTimpStart(LocalDateTime.of(2026, 5, 10, 23, 0));
        cursaNoapte.setTimpFinal(LocalDateTime.of(2026, 5, 11, 0, 30));
        maria.terminareCursa(t2, cursaNoapte);
        curse.add(cursaNoapte);
        System.out.println("Sold ramas Maria: " + Plata.formatSuma(maria.getSold()));

        sectiune("5. Cursa activa");
        Cursa cursaActiva = maria.inchiriereTrotineta(t1);
        curse.add(cursaActiva);
        System.out.println(cursaActiva);

        sectiune("6. Raportare si rezolvare probleme");
        Problema p1 = maria.raportareProblema("P001", "Frana nu functioneaza", "DEFECTIUNE", t2);
        Problema p2 = maria.raportareProblema("P002", "Baterie descarcata", "BATERIE", t3);
        probleme.add(p1);
        probleme.add(p2);
        admin.rezolvaProblema(p1);
        System.out.println("Status " + t2.getIdTrotineta() + ": " + t2.getStatus());

        sectiune("7. Raport administrator");
        admin.genereazaRaport(curse, probleme);

        sectiune("8. Scoatere trotineta din flota");
        admin.stergereTrotineta(flota, "T003");
        System.out.println("Trotinete ramase in flota: " + flota.size());
        maria.deautentificare();
    }

    private static void sectiune(String titlu) {
        System.out.println();
        System.out.println("---- " + titlu + " ----");
    }
}
