package app.service;

import app.model.Factura;
import app.model.FacturaIesire;
import app.model.FacturaIntrare;
import app.model.LinieFactura;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;

public class PdfService {

    /**
     * Genereaza un document PDF fizic pe disc pentru factura specificata.
     * Documentul include antetul fiscal, tabelul cu produse si totalurile financiare.
     *
     * @param factura Obiectul factura (Intrare sau Iesire) cu toate datele necesare.
     * @param caleFisier Calea completa (inclusiv numele fisierului) unde va fi salvat PDF-ul.
     * @throws FileNotFoundException Daca locatia de salvare nu este accesibila sau este protejata la scriere.
     */
    public static void genereazaPdfFactura(Factura factura, String caleFisier) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(caleFisier);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("FACTURA FISCALA")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(20));

        document.add(new Paragraph("\n"));

        String tip = (factura instanceof FacturaIntrare) ? "Factura Intrare (Achizitie)" : "Factura Iesire (Vanzare)";
        String partenerLabel = (factura instanceof FacturaIntrare) ? "Furnizor: " : "Client: ";
        String partenerNume = (factura instanceof FacturaIntrare) ?
                ((FacturaIntrare) factura).getVanzator() : ((FacturaIesire) factura).getClient();

        document.add(new Paragraph("Tip: " + tip));
        document.add(new Paragraph("Numar: " + factura.getNumarFactura()));
        document.add(new Paragraph("Data: " + factura.getDataEmitere().format(DateTimeFormatter.ISO_DATE)));
        document.add(new Paragraph(partenerLabel + partenerNume).setBold());

        document.add(new Paragraph("\n"));

        float[] latimeColoane = {1, 4, 2, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(latimeColoane));
        table.setWidth(UnitValue.createPercentValue(100));

        addCell(table, "Nr.", true);
        addCell(table, "Produs", true);
        addCell(table, "Cant.", true);
        addCell(table, "Pret Unit.", true);
        addCell(table, "TVA %", true);
        addCell(table, "Total (cu TVA)", true);

        int index = 1;
        for (LinieFactura linie : factura.getLiniiFactura()) {
            addCell(table, String.valueOf(index++), false);
            addCell(table, linie.getProdus().getNume(), false);
            addCell(table, String.valueOf(linie.getCantitate()), false);
            addCell(table, String.format("%.2f", linie.getPretUnitFaraTva()), false);
            addCell(table, String.format("%.0f%%", linie.getCotaTvaProcent()), false);
            addCell(table, String.format("%.2f", linie.getValoareCuTva()), false);
        }

        document.add(table);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Total Baza: " + String.format("%.2f RON", factura.getValoareBaza())));
        document.add(new Paragraph("Total General: " + String.format("%.2f RON", factura.getValoareTotala()))
                .setBold()
                .setFontSize(14));

        document.close();
        System.out.println("PDF generat cu succes la: " + caleFisier);
    }

    private static void addCell(Table table, String text, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(text));
        if (isHeader) {
            cell.setBold();
            cell.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
        }
        table.addCell(cell);
    }
}