package com.exavalu.customer.product.portal.services.utility;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.exavalu.customer.product.portal.entities.salesforce.OrderDetails;
import com.exavalu.customer.product.portal.entities.salesforce.ItemsFromCartDetails;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class PdfGenerator {
	private static final Logger log = LogManager.getLogger(PdfGenerator.class);

    public byte[] generateInvoice(OrderDetails orderDetails) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            // Add content to the PDF
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            document.add(new Paragraph("Invoice", titleFont));
            document.add(new Paragraph("\n"));

            // Add customer details
            document.add(new Paragraph("Customer Details:", boldFont));
            document.add(new Paragraph("Customer ID: " + orderDetails.getCustomerId(), bodyFont));
            document.add(new Paragraph("Order ID: " + orderDetails.getOrderId(), bodyFont));
            document.add(new Paragraph("Order Date: " + orderDetails.getOrderDate(), bodyFont));
            document.add(new Paragraph("Expected Delivery Date: " + orderDetails.getDeliveryDate(), bodyFont));
            document.add(new Paragraph("\n"));

            // Add purchased items table
            document.add(new Paragraph("Purchased Items:", boldFont));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 2, 2, 2});
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addTableHeader(table);
            addRows(table, orderDetails.getItems());

            document.add(table);

            // Add total amount
            Paragraph totalAmount = new Paragraph("Total Amount: ₹" + orderDetails.getTotalPrice(), boldFont);
            totalAmount.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalAmount);

            // Add payment details
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Payment Details:", boldFont));
            document.add(new Paragraph("Payment Mode: " + orderDetails.getPaymentMode(), bodyFont));
            document.add(new Paragraph("Payment Status: Successful", bodyFont));
            document.add(new Paragraph("Payment Date: " + orderDetails.getOrderDate(), bodyFont));

        } catch (DocumentException e) {
        	log.error(e.toString());
            throw new IOException(e.getMessage());
        } finally {
            document.close();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Product Name", "Quantity", "Price", "Delivery Charge", "Total Price")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, Map<String, ItemsFromCartDetails> items) {
        for (ItemsFromCartDetails item : items.values()) {
            table.addCell(item.getProductTitle());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell("Rs " + item.getPrice());
            table.addCell("Rs " + item.getDeliveryCharge());
            table.addCell("Rs " + item.getTotalPrice());
        }
    }
}