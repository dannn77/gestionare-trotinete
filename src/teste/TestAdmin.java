package teste;

import modele.Admin;
import modele.Cursa;
import modele.Problema;
import modele.Trotineta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestAdmin {

    private Admin admin;
    private Trotineta trotineta;
    private List<Cursa> curse;
    private List<Problema> probleme;

    @BeforeEach
    public void setup() {
        admin = new Admin("A001", "Admin Ion", "admin@gmail.com", 0.0, true, 30, "EMP001");
        trotineta = new Trotineta("T001", 80, true, "DISPONIBILA");
        curse = new ArrayList<>();
        probleme = new ArrayList<>();
    }

    // Set 1: lista curse goala
    @Test
    public void testRaportListaGoala() {
        assertDoesNotThrow(() -> admin.genereazaRaport(curse, probleme));
    }

    // Set 2: lista curse null
    @Test
    public void testRaportListaNull() {
        assertDoesNotThrow(() -> admin.genereazaRaport(null, null));
    }

    // Set 3: o cursa finalizata - venituri corecte
    @Test
    public void testRaportOCursaFinalizata() {
        Cursa cursa = new Cursa("C001",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 10, 30),
                1.0, 0.5, 120, trotineta);
        curse.add(cursa);
        assertDoesNotThrow(() -> admin.genereazaRaport(curse, probleme));
    }

    // Set 4: cursa fara timpFinal (activa) - trotinete ocupate
    @Test
    public void testRaportCursaActiva() {
        Cursa cursa = new Cursa("C002",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                null, 1.0, 0.5, 120, trotineta);
        curse.add(cursa);
        assertDoesNotThrow(() -> admin.genereazaRaport(curse, probleme));
    }

    // Set 5: probleme rezolvate si nerezolvate
    @Test
    public void testRaportCuProbleme() {
        Problema p1 = new Problema("P001", "Baterie descarcata", "BATERIE", trotineta);
        Problema p2 = new Problema("P002", "Trotineta defecta", "DEFECTIUNE", trotineta);
        p1.rezolva();
        probleme.add(p1);
        probleme.add(p2);
        Cursa cursa = new Cursa("C003",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 10, 30),
                1.0, 0.5, 120, trotineta);
        curse.add(cursa);
        assertDoesNotThrow(() -> admin.genereazaRaport(curse, probleme));
    }

    // Set 6: adaugare trotineta in flota
    @Test
    public void testAdaugaTrotineta() {
        List<Trotineta> flota = new ArrayList<>();
        admin.adaugaTrotineta(flota, trotineta);
        assertEquals(1, flota.size());
        assertEquals("T001", flota.get(0).getIdTrotineta());
    }

    // Set 7: stergere trotineta existenta din flota
    @Test
    public void testStergereTrotineta() {
        List<Trotineta> flota = new ArrayList<>();
        flota.add(trotineta);
        admin.stergereTrotineta(flota, "T001");
        assertEquals(0, flota.size());
    }

    // Set 8: stergere trotineta inexistenta - flota neschimbata
    @Test
    public void testStergereTrotinataInexistenta() {
        List<Trotineta> flota = new ArrayList<>();
        flota.add(trotineta);
        admin.stergereTrotineta(flota, "T999");
        assertEquals(1, flota.size());
    }

    // Set 9: rezolvare problema
    @Test
    public void testRezolvaProblema() {
        Problema problema = new Problema("P001", "Baterie descarcata", "BATERIE", trotineta);
        admin.rezolvaProblema(problema);
        assertTrue(problema.isRezolvata());
    }

    // Set 10: rezolvare problema deja rezolvata
    @Test
    public void testRezolvaProblemaDejaRezolvata() {
        Problema problema = new Problema("P001", "Baterie descarcata", "BATERIE", trotineta);
        problema.rezolva();
        assertDoesNotThrow(() -> admin.rezolvaProblema(problema));
        assertTrue(problema.isRezolvata());
    }

    // Set 11: flux complet — raport cu curse si probleme mixte
    @Test
    public void testFluxComplet() {
        // Cursa finalizata normala
        Cursa cursaFinalizata = new Cursa("C010",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 10, 30),
                1.0, 0.5, 120, trotineta);

        // Cursa de noapte finalizata
        Cursa cursaNoapte = new Cursa("C011",
                LocalDateTime.of(2026, 5, 10, 23, 0),
                LocalDateTime.of(2026, 5, 10, 23, 30),
                1.0, 0.5, 120, trotineta);

        // Cursa activa (fara timpFinal)
        Cursa cursaActiva = new Cursa("C012",
                LocalDateTime.of(2026, 5, 10, 12, 0),
                null, 1.0, 0.5, 120, trotineta);

        curse.add(cursaFinalizata);
        curse.add(cursaNoapte);
        curse.add(cursaActiva);

        // Problema rezolvata
        Problema p1 = new Problema("P001", "Baterie descarcata", "BATERIE", trotineta);
        p1.rezolva();

        // Problema nerezolvata
        Problema p2 = new Problema("P002", "Trotineta defecta", "DEFECTIUNE", trotineta);

        probleme.add(p1);
        probleme.add(p2);

        // Verifica ca raportul ruleaza fara exceptie
        assertDoesNotThrow(() -> admin.genereazaRaport(curse, probleme));

        // Verifica flota: adauga si sterge
        List<Trotineta> flota = new ArrayList<>();
        admin.adaugaTrotineta(flota, trotineta);
        assertEquals(1, flota.size());

        // Verifica rezolvare problema
        Problema p3 = new Problema("P003", "Accident", "ACCIDENT", trotineta);
        admin.rezolvaProblema(p3);
        assertTrue(p3.isRezolvata());

        admin.stergereTrotineta(flota, trotineta.getIdTrotineta());
        assertEquals(0, flota.size());
    }

    // Set 12: generarea raportului nu creeaza plati
    @Test
    public void testRaportNuCreeazaPlati() {
        Cursa cursa = new Cursa("C020",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 10, 30),
                1.0, 0.5, 120, trotineta);
        curse.add(cursa);
        admin.genereazaRaport(curse, probleme);
        assertNull(cursa.getPlata());
    }

    // Set 13: o cursa cu date invalide nu opreste raportul
    @Test
    public void testRaportCursaInvalida() {
        curse.add(new Cursa("C021",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 10, 0),
                1.0, 0.5, 120, trotineta));
        curse.add(new Cursa("C022",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 10, 30),
                1.0, 0.5, 120, trotineta));
        assertDoesNotThrow(() -> admin.genereazaRaport(curse, probleme));
    }
}