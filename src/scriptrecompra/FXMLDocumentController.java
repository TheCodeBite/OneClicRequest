/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptrecompra;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javax.swing.JFileChooser;
import scriptrecompra.controller.FileController;

/**
 *
 * @author kamartinezh
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Button btn_extraer, btn_save;

    @FXML
    private Text txt_info;

    private FileController fileController;
    private String pathFile = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fileController = new FileController();
        //btn_save.setVisible(true);

        btn_extraer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pathFile = fileController.getPathFile();

                if (pathFile.length() > 10) {
                    try {
                        processXML();
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        btn_save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if(fileController.crearFichero()) {
                        txt_info.setText("El archivo fue creado exitosamente! \n  Seleccione un archivo nuevo para generar nuevo REQUEST");
                    } else {
                        txt_info.setText("Opercion cancelada por el usuario! \n  Seleccione un archivo nuevo para generar nuevo REQUEST");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    txt_info.setText("Ocurrio un error! \n  " +  ex.getMessage());
                }
            }
        });
    }

    private void processXML() throws IOException {
        if (fileController.openFileXlsx(pathFile)) {
            btn_save.setVisible(true);
            txt_info.setText("¡REQUEST GENERADO CORRECTAMENTE!\n Seleccione un archivo nuevo para generar nuevo REQUEST");
        }
    }
}
