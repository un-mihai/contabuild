package app.repository;

import app.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;

/**
 * Gestioneaza toate interactiunile cu baza de date PostgreSQL.
 * Aceasta clasa contine logica de persistenta, tranzactii si calcule financiare
 * critice (precum Costul Mediu Ponderat).
 */
public class BazaDeDate {

    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    static {
        try (InputStream input = BazaDeDate.class.getResourceAsStream("/application/properties.ini")) {
            Properties properties = new Properties();
            properties.load(input);
            URL = properties.getProperty("db.url");
            USERNAME = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Nu m-am conectat la baza: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Cauta un produs in stoc dupa numele exact.
     * @param numeCautat Numele produsului (case-sensitive de obicei in SQL).
     * @return Obiectul Produs complet populat sau null daca nu exista.
     */
    public static Produs getProdusDupaNume(String numeCautat){
        String sql = "SELECT id_produs, cost_mediu, adaos_comercial, cota_tva_vanzare, stoc_total FROM stoc WHERE nume=?";
        try(Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, numeCautat);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int idProdus = rs.getInt("id_produs");
                double pretUnitFaraTva = rs.getDouble("cost_mediu");
                double adaosComercial = rs.getDouble("adaos_comercial");
                double cotaTvaVanzare = rs.getDouble("cota_tva_vanzare");
                int stocTotal = rs.getInt("stoc_total");

                rs.close();
                return new Produs(idProdus ,numeCautat, pretUnitFaraTva, adaosComercial, cotaTvaVanzare, stocTotal);
            }
            else{
                return null;
            }

        }catch (SQLException e) {
            System.out.println("Ceva nu a mers la SQL" + e.getMessage());
        }
        return null;
    }

    /**
     * metoda folosita pentru a adauga un produs in baza de date (mai mult folosita pentru teste
     * deoarece logica de adaugare este folosita in tranzactiile de la adaugarea de factura)
     */
    public static void adaugaProdus(String nume, double pretUnitateFaraTva, double adaosComercial, double cotaTvaVanzare, int cantitate) {
        if(! (cantitate > 0)){
            System.out.println("Cantitatea trebuie sa fie mai mare de 0");
            return;
        }

        Produs produsActual = getProdusDupaNume(nume);
        if(produsActual == null){
           String sql = "INSERT INTO stoc (nume, cost_mediu, adaos_comercial, cota_tva_vanzare, stoc_total) VALUES (?, ?, ?, ?, ?)";
           try(Connection connection = getConnection();
               PreparedStatement stmt = connection.prepareStatement(sql)){

               stmt.setString(1, nume);
               stmt.setDouble(2, pretUnitateFaraTva);
               stmt.setDouble(3, adaosComercial);
               stmt.setDouble(4, cotaTvaVanzare);
               stmt.setInt(5, cantitate);
               int rezultatInsert = stmt.executeUpdate();
               if(rezultatInsert == 0){
                   System.out.println("Nu s-a executat insert-ul");
               }

           }catch (SQLException e){
               e.printStackTrace();
           }
        }
        else{
            String sql = "UPDATE stoc SET cost_mediu=?, stoc_total=? WHERE nume=?";
            try(Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){

                double costMediuNou = (produsActual.getCostMediu() * produsActual.getStocTotal() + pretUnitateFaraTva * cantitate) /
                        (produsActual.getStocTotal() + cantitate);
                int stocTotalNou = produsActual.getStocTotal() + cantitate;

                stmt.setDouble(1, costMediuNou);
                stmt.setInt(2, stocTotalNou);
                stmt.setString(3, nume);
                int rezultatUpdate = stmt.executeUpdate();
                if(rezultatUpdate == 0){
                    System.out.println("Nu s-a executat update-ul");
                }

            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Recupereaza lista completa a produselor din stoc, ordonata dupa ID.
     * @return O lista de obiecte Produs reprezentand inventarul actual al magazinului.
     */
    public static List<Produs> getInventar(){
        List<Produs> produse = new ArrayList<>();

        String sql = "SELECT * FROM stoc ORDER BY id_produs ASC";
        try(Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int idProdus = rs.getInt("id_produs");
                String nume = rs.getString("nume");
                double costMediu = rs.getDouble("cost_mediu");
                double adaosComercial = rs.getDouble("adaos_comercial");
                double cotaTvaVanzare = rs.getDouble("cota_tva_vanzare");
                int stocTotal = rs.getInt("stoc_total");
                produse.add(new Produs(idProdus, nume, costMediu, adaosComercial, cotaTvaVanzare, stocTotal));
            }

            rs.close();
            return produse;

        }catch (SQLException e){
            System.out.println("Ceva nu a mers la SQL" + e.getMessage());
        }

        return produse;
    }

    /**
     * Salveaza o factura (intrare sau iesire) si liniile aferente in baza de date.
     * Metoda foloseste tranzactii SQL pentru a salva toate datele in siguranta
     */
    public static void salveazaFactura(Factura factura) throws SQLException{
        Connection connection = null;
        PreparedStatement stmtFactura = null;
        PreparedStatement stmtLinie = null;

        String sqlFactura;
        if (factura instanceof FacturaIesire) {
            sqlFactura = "INSERT INTO facturi (numar_factura, data_emitere, tip, cumparator, valoare_baza, valoare_tva, profit) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            sqlFactura = "INSERT INTO facturi (numar_factura, data_emitere, tip, vanzator, valoare_baza, valoare_tva, profit) VALUES (?, ?, ?, ?, ?, ?, ?)";
        }

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            stmtFactura = connection.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS);

            stmtFactura.setString(1, factura.getNumarFactura());
            stmtFactura.setDate(2, java.sql.Date.valueOf(factura.getDataEmitere()));
            stmtFactura.setString(3, factura.getTip().getCod());
            if (factura instanceof FacturaIesire) {
                stmtFactura.setString(4, ((FacturaIesire) factura).getClient());
                stmtFactura.setDouble(7, ((FacturaIesire) factura).getProfit());
            } else {
                stmtFactura.setString(4, ((FacturaIntrare) factura).getVanzator());
                stmtFactura.setDouble(7, 0);
            }
            stmtFactura.setDouble(5, factura.getValoareBaza());
            stmtFactura.setDouble(6, factura.getValoareTotala() - factura.getValoareBaza());

            int affectedRows = stmtFactura.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Nu s-a salvat factura");
            }

            int idFacturaGenerat;
            try (ResultSet generatedKeys = stmtFactura.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idFacturaGenerat = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Eroare la factura, nu am primit id");
                }
            }

            String sqlLinie = "INSERT INTO linie_factura (id_factura, id_produs, pret_unit_fara_tva, cantitate, cota_tva_procent) VALUES (?, ?, ?, ?, ?)";
            stmtLinie = connection.prepareStatement(sqlLinie);

            for (LinieFactura linie : factura.getLiniiFactura()) {

                int idProdus;
                if (factura instanceof FacturaIntrare) {
                    idProdus = proceseazaIntrareTranzactional(connection, linie);
                } else {
                    idProdus = proceseazaIesireTranzactional(connection, linie);
                }

                stmtLinie.setInt(1, idFacturaGenerat);
                stmtLinie.setInt(2, idProdus);
                stmtLinie.setDouble(3, linie.getPretUnitFaraTva());
                stmtLinie.setInt(4, linie.getCantitate());
                stmtLinie.setDouble(5, linie.getCotaTvaProcent());
                stmtLinie.executeUpdate();
            }

            connection.commit();
            System.out.println("Factura salvata cu succes!");

        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.out.println("Eroare.. dau inapoi");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (stmtFactura != null) stmtFactura.close();
                if (stmtLinie != null) stmtLinie.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gestioneaza logica specifica intrarii de marfa in cadrul unei tranzactii.
     * Daca produsul exista, actualizeaza costul mediu si stocul.
     * Daca nu exista, il creeaza.
     */
    private static int proceseazaIntrareTranzactional(Connection conn, LinieFactura linie) throws SQLException {
        Produs p = linie.getProdus();
        String nume = p.getNume();
        int cantitate = linie.getCantitate();
        double pretAchizitie = linie.getValoareFaraTva() / cantitate;


        String checkSql = "SELECT id_produs, cost_mediu, stoc_total FROM stoc WHERE nume = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, nume);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int idProdus = rs.getInt("id_produs");
                double costMediuVechi = rs.getDouble("cost_mediu");
                int stocVechi = rs.getInt("stoc_total");

                double costMediuNou = ((costMediuVechi * stocVechi) + (pretAchizitie * cantitate)) / (stocVechi + cantitate);
                int stocNou = stocVechi + cantitate;

                String updateSql = "UPDATE stoc SET cost_mediu=?, stoc_total=? WHERE id_produs=?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, costMediuNou);
                    updateStmt.setInt(2, stocNou);
                    updateStmt.setInt(3, idProdus);
                    updateStmt.executeUpdate();
                }
                return idProdus;

            } else {
                String insertSql = "INSERT INTO stoc (nume, cost_mediu, adaos_comercial, cota_tva_vanzare, stoc_total) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, nume);
                    insertStmt.setDouble(2, pretAchizitie);
                    insertStmt.setDouble(3, p.getAdaosComercial());
                    insertStmt.setDouble(4, p.getCotaTvaVanzare());
                    insertStmt.setInt(5, cantitate);

                    insertStmt.executeUpdate();

                    ResultSet genKeys = insertStmt.getGeneratedKeys();
                    if (genKeys.next()) {
                        return genKeys.getInt(1);
                    } else {
                        throw new SQLException("Eroare la crearea produsului nou: " + nume);
                    }
                }
            }
        }
    }

    /**
     * Gestioneaza logica specifica iesirii de marfa in cadrul unei tranzactii.
     * Verifica daca exista stoc suficient inainte de a scadea cantitatea.
     * @throws SQLException Daca stocul este insuficient (va declansa rollback in salveazaFactura).
     */
    private static int proceseazaIesireTranzactional(Connection conn, LinieFactura linie) throws SQLException {
        Produs p = linie.getProdus();

        String checkSql = "SELECT id_produs, stoc_total FROM stoc WHERE nume = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, p.getNume());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int idProdus = rs.getInt("id_produs");
                int stocCurent = rs.getInt("stoc_total");

                if (stocCurent < linie.getCantitate()) {
                    throw new SQLException("Stoc insuficient pentru produsul: " + p.getNume());
                }

                String updateSql = "UPDATE stoc SET stoc_total = stoc_total - ? WHERE id_produs = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, linie.getCantitate());
                    updateStmt.setInt(2, idProdus);
                    updateStmt.executeUpdate();
                }
                return idProdus;
            } else {
                throw new SQLException("Produsul nu exista in stoc: " + p.getNume());
            }
        }
    }

    /**
     * Recupereaza istoricul complet al facturilor (intrari si iesiri) din baza de date.
     * Facturile sunt ordonate descrescator dupa data emiterii si ID.
     *
     * @return Lista de facturi incarcate (doar datele de antet, fara liniile de produs).
     */
    public static List<Factura> getToateFacturile() {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT * FROM facturi ORDER BY data_emitere DESC, id_factura DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_factura");
                String numar = rs.getString("numar_factura");
                LocalDate data = rs.getDate("data_emitere").toLocalDate();
                String tip = rs.getString("tip");
                double baza = rs.getDouble("valoare_baza");
                double tva = rs.getDouble("valoare_tva");

                Factura factura = null;

                if ("I".equals(tip)) {
                    String vanzator = rs.getString("vanzator");
                    factura = new FacturaIntrare(id, numar, data, vanzator);
                } else if ("E".equals(tip)) {
                    double profit = rs.getDouble("profit");

                    String client = rs.getString("cumparator");
                    factura = new FacturaIesire(id, numar, data, client, profit);
                }

                if (factura != null) {
                    factura.setValoareBaza(baza);
                    factura.setValoareTotala(baza + tva);
                    lista.add(factura);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    /**
     * Returneaza numarul de linii (produse distincte) asociate unei facturi.
     *
     * @param idFactura ID-ul unic al facturii.
     * @return Numarul de randuri din tabela linie_factura pentru factura data.
     */
    public static int getNumarLiniiFactura(int idFactura) {
        String sql = "SELECT COUNT(*) FROM linie_factura WHERE id_factura = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFactura);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Incarca si populeaza lista de linii (produse) pentru o factura specifica.
     * Este necesara deoarece metoda {@link #getToateFacturile()} nu incarca detaliile.
     *
     * @param factura Obiectul factura in care se vor incarca liniile.
     */
    public static void incarcaLiniiFactura(Factura factura) {

        String sql = "SELECT l.cantitate, l.pret_unit_fara_tva, l.cota_tva_procent, p.nume " +
                " FROM linie_factura l " +
                " JOIN stoc p ON l.id_produs = p.id_produs " +
                " WHERE l.id_factura = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, factura.getIdFactura());
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                String numeProdus = rs.getString("nume");
                int cantitate = rs.getInt("cantitate");
                double pret = rs.getDouble("pret_unit_fara_tva");
                double tva = rs.getDouble("cota_tva_procent");

                Produs produs = new Produs(numeProdus, pret, 0, tva);

                try {
                    LinieFactura linie = new LinieFactura(produs, cantitate, pret, tva);
                    factura.adaugaLinie(linie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genereaza urmatorul numar de factura de iesire disponibil, bazat pe ultima serie din baza.
     * Formatul standard este "IES-XXXXXX".
     *
     * @return Urmatorul numar de factura sub forma de String (ex: "IES-000005").
     */
    public static String genereazaUrmatorulNumarFacturaIesire() {
        String sql = "SELECT numar_factura FROM facturi WHERE tip = 'E' ORDER BY id_factura DESC LIMIT 1";
        String prefix = "IES-";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String ultimulNumar = rs.getString("numar_factura");

                if (ultimulNumar != null && ultimulNumar.startsWith(prefix)) {
                    try {
                        String parteaNumerica = ultimulNumar.substring(prefix.length());
                        int numar = Integer.parseInt(parteaNumerica);

                        return prefix + String.format("%06d", numar + 1);
                    } catch (NumberFormatException e) {
                        System.out.println("Format factura nerecunoscut, resetam seria.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prefix + "000001";
    }

    /**
     * Genereaza un raport financiar pentru o perioada specificata.
     * Calculeaza sumele direct in SQL pentru eficienta.
     */
    public static RaportFinanciar genereazaRaport(LocalDate start, LocalDate end) {
        RaportFinanciar raport = new RaportFinanciar(start, end);

        String sql = "SELECT tip, " +
                "SUM(valoare_baza) as total_baza, " +
                "SUM(valoare_tva) as total_tva, " +
                "SUM(profit) as total_profit " +
                "FROM facturi " +
                "WHERE data_emitere BETWEEN ? AND ? " +
                "GROUP BY tip";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String tip = rs.getString("tip");
                    double baza = rs.getDouble("total_baza");
                    double tva = rs.getDouble("total_tva");
                    double profit = rs.getDouble("total_profit");

                    if ("I".equals(tip)) {
                        raport.adaugaLaCheltuieli(baza, tva);
                    } else if ("E".equals(tip)) {
                        raport.adaugaLaVanzari(baza, tva, profit);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Eroare la generarea raportului: " + e.getMessage());
        }

        return raport;
    }

}
