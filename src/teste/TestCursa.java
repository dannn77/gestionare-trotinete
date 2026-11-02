package teste;

import modele.Cursa;
import modele.Plata;
import modele.Trotineta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TestCursa {

    private Trotineta trotineta;

    @BeforeEach
    public void setup() {
        trotineta = new Trotineta("T001", 80, true, "DISPONIBILA");
    }

    // Set 1: timpFinal inainte de timpStart
    @Test
    public void testDateInvalide() {
        Cursa cursa = new Cursa("C001",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 9, 0),
                1.0, 0.5, 120, trotineta);
        assertThrows(IllegalArgumentException.class, cursa::calculeazaCost);
    }

    // Set 2: durata zero
    @Test
    public void testDurataZero() {
        Cursa cursa = new Cursa("C002",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 10, 0),
                1.0, 0.5, 120, trotineta);
        assertThrows(IllegalArgumentException.class, cursa::calculeazaCost);
    }

    // Set 3: cursa normala 30 minute - taxaDeblocare + 30 * tarif
    @Test
    public void testCursaNormala() {
        Cursa cursa = new Cursa("C003",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 10, 30),
                1.0, 0.5, 120, trotineta);
        double cost = cursa.calculeazaCost();
        assertEquals(16.0, cost, 0.01); // 1.0 + 30 * 0.5 = 16.0
    }

    // Set 4: durata > 120 min - penalizare 10 RON
    @Test
    public void testPenalizareDurataMaxima() {
        Cursa cursa = new Cursa("C004",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 12, 30),
                1.0, 0.5, 120, trotineta);
        double cost = cursa.calculeazaCost();
        // 1.0 + 150 * 0.5 + 10 - 5 (discount >60min) = 81.0
        assertEquals(81.0, cost, 0.01);
    }

    // Set 5: cursa de noapte - +20%
    @Test
    public void testTarifNoapte() {
        Cursa cursa = new Cursa("C005",
                LocalDateTime.of(2026, 5, 10, 23, 0),
                LocalDateTime.of(2026, 5, 10, 23, 30),
                1.0, 0.5, 120, trotineta);
        double cost = cursa.calculeazaCost();
        // (1.0 + 30 * 0.5) * 1.20 = 16.0 * 1.20 = 19.2
        assertEquals(19.2, cost, 0.01);
    }

    // Set 6: durata > 60 min - discount 5 RON
    @Test
    public void testDiscountCursaLunga() {
        Cursa cursa = new Cursa("C006",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 11, 30),
                1.0, 0.5, 120, trotineta);
        double cost = cursa.calculeazaCost();
        // 1.0 + 90 * 0.5 - 5 = 41.0
        assertEquals(41.0, cost, 0.01);
    }

    // Set 7: noapte + durata > 120 + discount
    @Test
    public void testCombinatiePenalizareNoapteDiscount() {
        Cursa cursa = new Cursa("C007",
                LocalDateTime.of(2026, 5, 10, 23, 0),
                LocalDateTime.of(2026, 5, 11, 1, 30),
                1.0, 0.5, 120, trotineta);
        double cost = cursa.calculeazaCost();
        // (1.0 + 150 * 0.5 + 10) * 1.20 - 5 = 86.0 * 1.20 - 5 = 103.2 - 5 = 98.2
        assertEquals(98.2, cost, 0.01);
    }
    // Set 8: flux complet — verifica toate etapele calculeazaCost() in ordine
    @Test
    public void testFluxComplet() {
        // Cursa de zi, 90 min (> 60 - discount, <= 120 - fara penalizare)
        Cursa cursa = new Cursa("C008",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 11, 30),
                1.0, 0.5, 120, trotineta);

        // Verifica ca nu arunca exceptie
        assertDoesNotThrow(() -> cursa.calculeazaCost());

        // Verifica durata
        assertEquals(90, cursa.getDurata());

        // Verifica costul final
        double cost = cursa.calculeazaCost();
        // 1.0 + 90 * 0.5 - 5.0 (discount) = 41.0
        assertEquals(41.0, cost, 0.01);

        // Verifica ca plata a fost creata
        assertNotNull(cursa.getPlata());
        assertEquals(41.0, cursa.getPlata().getSuma(), 0.01);
    }

    // Set 9: calculeazaTarif nu creeaza plata
    @Test
    public void testCalculeazaTarifFaraPlata() {
        Cursa cursa = new Cursa("C009",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 10, 10, 30),
                1.0, 0.5, 120, trotineta);
        assertEquals(16.0, cursa.calculeazaTarif(), 0.01);
        assertNull(cursa.getPlata());
    }

    // Set 10: toString pe o cursa activa nu arunca exceptie
    @Test
    public void testToStringCursaActiva() {
        Cursa cursa = new Cursa("C010",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                null, 1.0, 0.5, 120, trotineta);
        assertTrue(cursa.esteActiva());
        assertTrue(cursa.toString().contains("activa"));
    }

    // Set 11: cost pentru o cursa activa - exceptie clara
    @Test
    public void testCostCursaActiva() {
        Cursa cursa = new Cursa("C011",
                LocalDateTime.of(2026, 5, 10, 10, 0),
                null, 1.0, 0.5, 120, trotineta);
        assertThrows(IllegalStateException.class, cursa::calculeazaCost);
    }
}

