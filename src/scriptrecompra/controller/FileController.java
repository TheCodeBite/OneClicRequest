/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptrecompra.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author kamartinezh
 */
public class FileController {

    private String jsonObject = "";

    public String getPathFile() {
        System.out.println("Abriendo..");
        JFileChooser jFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Excel Files", "xlsx", "XLSX");

        jFileChooser.setFileFilter(filter);
        int returnVal = jFileChooser.showOpenDialog(jFileChooser);

        return returnVal == JFileChooser.APPROVE_OPTION ? jFileChooser.getSelectedFile().getPath() : null;
    }

    public boolean openFileXlsx(String pathFile) throws IOException {
        ArrayList<String> listaProductos = new ArrayList<>();
        jsonObject = "{\"url\":\"http://10.53.37.110:8084/OriginacionBazDigital/servicios/Recompra/consultaSkus\",\"jsonConPeticion\":{\"productos\":[";
        
        try {
            FileInputStream fis = new FileInputStream(new File(pathFile));
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file  

            int contador = 0;

            while (itr.hasNext()) {
                ArrayList<String> producto = new ArrayList<>();
                Row row = itr.next();
                Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column  

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (contador >= 2) {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:    //field that represents string cell type  
                                producto.add(cell.getStringCellValue());

                                break;
                            case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type
                                producto.add(((int) cell.getNumericCellValue()) + "");
                                //System.out.println("valor: " + cell.getStringCellValue());
                                break;
                            default:
                                producto.add("");
                                if (producto.get(0).equals("")) {
                                    producto = new ArrayList<>();
                                }
                                break;
                        }
                    }
                }
                if (contador >= 2) {
                    listaProductos.add(crearJson(producto));
                }
                contador++;
            }
        } catch (IOException e) {
        }

        crearJsonRequest(listaProductos);

        return true;
    }

    private String crearJson(ArrayList<String> producto) {
        String jsonProducto = "{\"posicionVenta\":" + producto.get(0) + ",\"sku\":" + producto.get(1)
                + ",\"modelo\":\"" + producto.get(2) + "\",\"precio\":" + producto.get(5) + ",\"precioCredito\":" + producto.get(4) + ",\"plazo\":"
                + producto.get(6) + ",\"abonoPuntual\":" + producto.get(7) + ",\"inventario\":" + producto.get(9)
                + ",\"carrier\":\"" + producto.get(8) + "\",\"plataformaVenta\":\"" + producto.get(9)
                + "\",\"rutaImagen\":\"" + (producto.get(15).equals("") || producto.get(15).isEmpty() ? producto.get(1) + ".jpg" : producto.get(15))
                + "\",\"caracteristicas\":[\"" + producto.get(11) + "\",\"" + producto.get(12) + "\",\"" + producto.get(13) + "\",\"" + producto.get(14)
                + "\"],\"idProducto\":" + producto.get(16) + ",\"idLinea\":" + producto.get(17) + "}";

        return jsonProducto;
    }

    public String crearJsonRequest(ArrayList<String> listaProductos) {
        String allObject = "";

        allObject = listaProductos.stream().map((item) -> item + ",").reduce(allObject, String::concat);

        allObject = allObject.substring(0, allObject.length() - 1);

        jsonObject += allObject + "]}}";

        return jsonObject;
    }

    /* CREACION DEL FICHERO JSON */
    public boolean crearFichero() throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Seleccione la ruta para guardar el archivo");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
            File file = new File(chooser.getSelectedFile() + "/script.json");
            
            if(!file.exists()) {
                file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(jsonObject);
            bw.close();
            return true;
        } else {
            System.out.println("No Selection ");
            return false;
        }
    }
}
