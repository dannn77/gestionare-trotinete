package teste;

import modele.Client;
import modele.Cursa;
import modele.Trotineta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestClient {

    private Trotineta trotineta;

    @BeforeEach
    public void setup() {
        trotineta = new Trotineta("T001", 80, true, "DISPONIBILA");
    }

    // Set 1: cont neverificat
    @Test
    public void testContNeverificat() {
        Client client = new Client("C001", "Ion", "ion@gmail.com", 50.0, false, 20);
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        assertNull(cursa);
        assertTrue(trotineta.isEsteDisponibila());
    }

    // Set 2: varsta sub 16
    @Test
    public void testVarstaSub16() {
        Client client = new Client("C002", "Alex", "alex@gmail.com", 50.0, true, 14);
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        assertNull(cursa);
        assertTrue(trotineta.isEsteDisponibila());
    }

    // Set 3: sold insuficient
    @Test
    public void testSoldInsuficient() {
        Client client = new Client("C003", "Maria", "maria@gmail.com", 0.0, true, 20);
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        assertNull(cursa);
        assertTrue(trotineta.isEsteDisponibila());
    }

    // Set 4: trotineta indisponibila
    @Test
    public void testTrotinataIndisponibila() {
        trotineta.setEsteDisponibila(false);
        Client client = new Client("C004", "Ana", "ana@gmail.com", 50.0, true, 20);
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        assertNull(cursa);
        assertFalse(trotineta.isEsteDisponibila());
    }

    // Set 5: toate conditiile ok
    @Test
    public void testInchiriereReusita() {
        Client client = new Client("C005", "Mihai", "mihai@gmail.com", 50.0, true, 20);
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        assertNotNull(cursa);
        assertFalse(trotineta.isEsteDisponibila());
        assertEquals("IN_UZ", trotineta.getStatus());
    }

    // Set 6: varsta exact 16
    @Test
    public void testVarstaExact16() {
        Client client = new Client("C006", "Radu", "radu@gmail.com", 50.0, true, 16);
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        assertNotNull(cursa);
        assertFalse(trotineta.isEsteDisponibila());
        assertEquals("IN_UZ", trotineta.getStatus());
    }

    // Set 7: terminare cursa - trotineta devine disponibila, sold scade
    @Test
    public void testTerminareCursa() {
        Client client = new Client("C007", "Elena", "elena@gmail.com", 50.0, true, 20);
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        assertNotNull(cursa);
        cursa.setTimpFinal(cursa.getTimpStart().plusMinutes(30));
        client.terminareCursa(trotineta, cursa);
        assertTrue(trotineta.isEsteDisponibila());
        assertEquals("DISPONIBILA", trotineta.getStatus());
        assertTrue(client.getSold() < 50.0);
    }

    // Set 8: flux complet — inchiriere + terminare cursa
    @Test
    public void testFluxComplet() {
        Client client = new Client("C008", "Flux", "flux@gmail.com", 100.0, true, 20);

        // Pasul 1: verifica ca trotineta e disponibila
        assertTrue(trotineta.isEsteDisponibila());

        // Pasul 2: inchiriere reusita
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        assertNotNull(cursa);
        assertFalse(trotineta.isEsteDisponibila());
        assertEquals("IN_UZ", trotineta.getStatus());

        // Pasul 3: setam durata cursei
        cursa.setTimpFinal(cursa.getTimpStart().plusMinutes(30));

        // Pasul 4: terminare cursa
        client.terminareCursa(trotineta, cursa);
        assertTrue(trotineta.isEsteDisponibila());
        assertEquals("DISPONIBILA", trotineta.getStatus());

        // Pasul 5: verifica ca soldul a scazut
        assertTrue(client.getSold() < 100.0);

        // Pasul 6: verifica ca plata a fost creata
        assertNotNull(cursa.getPlata());
        assertTrue(cursa.getPlata().getSuma() > 0);
    }

    // Set 9: baterie sub nivelul minim - inchiriere refuzata
    @Test
    public void testBaterieDescarcata() {
        trotineta.setNivelBaterie(10);
        Client client = new Client("C009", "Dan", "dan@gmail.com", 50.0, true, 20);
        assertNull(client.inchiriereTrotineta(trotineta));
        assertTrue(trotineta.isEsteDisponibila());
    }

    // Set 10: sold pozitiv dar sub taxa de deblocare
    @Test
    public void testSoldSubTaxaDeblocare() {
        Client client = new Client("C010", "Ioana", "ioana@gmail.com", 0.5, true, 20);
        assertNull(client.inchiriereTrotineta(trotineta));
        assertTrue(trotineta.isEsteDisponibila());
    }

    // Set 11: doua curse ale aceluiasi client au ID-uri diferite
    @Test
    public void testIdUniceCurse() {
        Client client = new Client("C011", "Vlad", "vlad@gmail.com", 100.0, true, 20);
        Cursa c1 = client.inchiriereTrotineta(trotineta);
        c1.setTimpFinal(c1.getTimpStart().plusMinutes(10));
        client.terminareCursa(trotineta, c1);
        Cursa c2 = client.inchiriereTrotineta(trotineta);
        assertNotNull(c2);
        assertNotEquals(c1.getIdCursa(), c2.getIdCursa());
    }

    // Set 12: cursa terminata imediat - se taxeaza minim 1 minut, fara exceptie
    @Test
    public void testTerminareCursaImediata() {
        Client client = new Client("C012", "Rapid", "rapid@gmail.com", 50.0, true, 20);
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        assertDoesNotThrow(() -> client.terminareCursa(trotineta, cursa));
        assertEquals(1, cursa.getDurata());
        assertTrue(trotineta.isEsteDisponibila());
        assertTrue(client.getSold() < 50.0);
    }

    // Set 13: date invalide la terminare - trotineta si soldul raman neschimbate
    @Test
    public void testTerminareCursaDateInvalide() {
        Client client = new Client("C013", "Invalid", "inv@gmail.com", 50.0, true, 20);
        Cursa cursa = client.inchiriereTrotineta(trotineta);
        cursa.setTimpFinal(cursa.getTimpStart().minusMinutes(5));
        assertThrows(IllegalArgumentException.class, () -> client.terminareCursa(trotineta, cursa));
        assertEquals("IN_UZ", trotineta.getStatus());
        assertEquals(50.0, client.getSold(), 0.001);
    }

    // Set 14: raportare problema cu categorie invalida - trotineta ramane disponibila
    @Test
    public void testRaportareProblemaCategorieInvalida() {
        Client client = new Client("C014", "Raport", "rap@gmail.com", 50.0, true, 20);
        assertThrows(IllegalArgumentException.class,
                () -> client.raportareProblema("P001", "Ceva", "ALTCEVA", trotineta));
        assertTrue(trotineta.isEsteDisponibila());
        assertEquals("DISPONIBILA", trotineta.getStatus());
    }

    // Set 15: raportare problema valida - trotineta intra in service
    @Test
    public void testRaportareProblemaValida() {
        Client client = new Client("C015", "Raport", "rap@gmail.com", 50.0, true, 20);
        assertNotNull(client.raportareProblema("P001", "Frana slaba", "DEFECTIUNE", trotineta));
        assertFalse(trotineta.isEsteDisponibila());
        assertEquals("IN_SERVICE", trotineta.getStatus());
    }
}