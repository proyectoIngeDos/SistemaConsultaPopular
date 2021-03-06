package com.example.proyvotaciones;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.FileResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;

public class inscribirTendencia extends Panel {

	public String nombre="";
	public int cantTot=0;
	public String tipo="plebiscito";
	public String procesoVotacionPertenece="";
	public String representante="";
	public String descripcion="";
	public String pagina="";
	public String contacto="";
	public String infoAd="";
	boolean validador=true;
	public ArrayList<String> miembros;
	public tendencias nuevaTendencia;
	public DBManager manejoDB;
	TextField nombreTendencia;
	TextArea descripcionTende;
	Label representanteNombre;
	TextField paginaWeb;
	TextField infoContacto;
	TextArea infoAdicional;
	Upload imgUpload;
	TextField nombreMiembro;
	Button agregarMiembro;
	Button quitarMiembro;
	Button OK; 
	ComboBox listaMiembors;
	ComboBox listaVotaciones;
	Button inscribirPlebiscito;
	Label mensaje;
	private File imgFile;
	
	public inscribirTendencia(){
		manejoDB=new DBManager();
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		listaVotaciones = new ComboBox("Procesos de votacion");
		listaVotaciones.setNullSelectionAllowed(false);
		manejoDB.procesosVotacion();

		for(int i=0;i<manejoDB.votaciones.size();i++){
			try {
				if(null!=manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("fininscripciontendencias", manejoDB.votaciones.get(i)))&&null!=manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioinscripciontendencias", manejoDB.votaciones.get(i)))){
					if((manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("fininscripciontendencias", manejoDB.votaciones.get(i)))).before(new Date())&&(manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioinscripciontendencias", manejoDB.votaciones.get(i)))).after(new Date())){
						listaVotaciones.addItem(manejoDB.votaciones.get(i));
					}
				}else{
					listaVotaciones.addItem(manejoDB.votaciones.get(i));			
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		  OK = new Button("Aceptar");
		  
		  OK.addClickListener(new Button.ClickListener() {
              public void buttonClick(ClickEvent event) {
            	  LayoutEscogido(listaVotaciones.getValue().toString());
            	  
              }
          });
		  layout.addComponent(listaVotaciones);
		  layout.addComponent(OK);
	}
	
	
	
	
	public void LayoutEscogido(String nombreVotacion){
		nombre=nombreVotacion;
		tipo=manejoDB.getTipoProceso(nombreVotacion);
		cantTot=manejoDB.getCantTendencias(nombreVotacion);
		miembros=new ArrayList();
		nuevaTendencia=new tendencias();
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
        layout.addStyleName("outlined");
        layout.setSizeFull();
        layout.setSpacing(true);
        listaMiembors = new ComboBox("Miembros");
        listaMiembors.setNullSelectionAllowed(false);
        inicializarTextFields();
        if(!(tipo.equals("R")&&cantTot==2)){
	        layout.addComponent(representanteNombre);
	        layout.addComponent(nombreTendencia);
	        layout.addComponent(descripcionTende);
	       
	        if(tipo.equals("P")){
	            layout.addComponent(nombreMiembro);
	            
	            agregarMiembro = new Button("Agregar Miembro");
	            agregarMiembro.addClickListener(new Button.ClickListener() {
	                public void buttonClick(ClickEvent event) {
	                	agregarMiembro();
	                }
	            });
	            
	            
	            layout.addComponent(agregarMiembro); 
	            quitarMiembro = new Button("Quitar Miembro");
	            quitarMiembro.addClickListener(new Button.ClickListener() {
	                public void buttonClick(ClickEvent event) {
	                	quitarMiembro();
	                }
	            });
	            
	            layout.addComponent(listaMiembors);
	            layout.addComponent(quitarMiembro);
	        }else{
	        	
	        	
	        }
	
	        
	
	
	        
	        layout.addComponent(paginaWeb);
	        layout.addComponent(infoContacto);
	        layout.addComponent(infoAdicional);
	        
		    final Image image = new Image("Imagen Subida");
		    image.setVisible(false);
	        final Upload imgUpload = new Upload("Archivo de la imagen ",
				new Upload.Receiver() {
					public OutputStream receiveUpload(String filename,
							String mimeType) {
						FileOutputStream fos = null; 
						String nombreDB=nombre+nombreTendencia.getValue();
						try {
							imgFile = new File(".\\" + nombreDB);
							fos = new FileOutputStream(imgFile);
						} catch (final java.io.FileNotFoundException e) {
							Notification.show("Could not open file<br/>",
									e.getMessage(),
									Notification.TYPE_ERROR_MESSAGE);
							return null;
						}
						return fos; 
					}
				});
	        imgUpload.addFinishedListener((new Upload.FinishedListener() {
		        @Override
		        public void uploadFinished(Upload.FinishedEvent finishedEvent) {
		        	image.setVisible(true);
					image.setSource(new FileResource(imgFile));
					
		        }
	        }));
			layout.addComponent(imgUpload);
			layout.addComponent(image);

	        
	        inscribirPlebiscito = new Button("Inscribir");
	        inscribirPlebiscito.addClickListener(new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	            	
	            	nuevaTendencia.miembros=miembros;
	            	nuevaTendencia.nombre=nombreTendencia.getValue();
	            	nuevaTendencia.procesoVotacionPertenece=nombre;
	            	nuevaTendencia.representante="114350464";
	            	nuevaTendencia.descripcion=descripcionTende.getValue();
	            	nuevaTendencia.pagina=paginaWeb.getValue();
	            	nuevaTendencia.contacto=infoContacto.getValue();
	            	nuevaTendencia.infoAd=infoAdicional.getValue();
	            	validador=true;
	            	verificarDatos();
	            	if(validador){
	            		try {
							nuevaTendencia.saveToDB();
							mensaje=new Label("Tendencia inscrita correctamente");
							inscribirPlebiscito.setVisible(false);
						} catch (UnsupportedOperationException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
							nombreTendencia.setComponentError(new UserError("Tendencia ya existe"));
							mensaje=new Label("Tendencia ya existe");
						}
	            	}else{
	            		
	            		mensaje=new Label("Faltan datos importantes");
	            	}
	            	layout.addComponent(mensaje);
	            	
	            }
	        });
	        layout.addComponent(inscribirPlebiscito);
		}else{
			mensaje=new Label("Ya estan las dos tendencias de este referendo inscritas");
			layout.addComponent(mensaje);
		}
	}
	
	
	
	public void agregarMiembro(){
		if(nombreMiembro.getValue()!=null&&nombreMiembro.getValue()!=""){
			miembros.add(nombreMiembro.getValue());
			listaMiembors.addItem(nombreMiembro.getValue());
			nombreMiembro.setValue("");
		}
	}
	
	public void quitarMiembro(){
		String nombre=listaMiembors.getValue().toString();
		boolean encontrar=true;
		int index=0;
		while(encontrar){
			if(miembros.get(index)==nombre){
				encontrar=false;
				miembros.remove(index);
			}else{
				index++;
			}
		}
		listaMiembors.removeAllItems();
		for(int i=0;i<miembros.size();i++){
			listaMiembors.addItem(miembros.get(i));
		}
		
	}
	
	
	public void inicializarTextFields(){
		nombreTendencia = new TextField("Nombre de la Tendencia", "");
		descripcionTende = new TextArea("Descripcion", "");
		representanteNombre = mensaje=new Label("Organizador: 114350464");;
		paginaWeb =new TextField("paginaWeb", "");
		infoContacto= new TextField("Informacion contacto", "");
		infoAdicional= new TextArea("Informacion adicional", "");
		nombreMiembro= new TextField("Nombre miembro", "");
		
		nombreTendencia.setRequired(true);
		descripcionTende.setRequired(true);
		//representanteNombre.setRequired(true);
		
		
	}
	
	public void verificarTextField(TextField textField){
		try{
			textField.validate();
			textField.setComponentError(null);
		}
		catch(Exception e){
			textField.setValidationVisible(true);
			textField.setComponentError(new UserError("Campo Requerido"));
			validador=false;
		} 
	}
	
	public void verificarTextArea(TextArea textArea){
		try{
			textArea.validate();
			textArea.setComponentError(null);
		}
		catch(Exception e){
			textArea.setValidationVisible(true);
			textArea.setComponentError(new UserError("Campo Requerido"));
			validador=false;
		} 
	}
	
	public void verificarDatos(){
		verificarTextField(nombreTendencia);
		verificarTextArea(descripcionTende);
	//	verificarTextField(representanteNombre);
	}	
	
	public void guardarImagen(File file) throws FileNotFoundException{
		
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);     
            }
        } catch (IOException ex) {
        }
        byte[] bytes = bos.toByteArray();

	}
	
	public Image sacarImagen(){
		Image imagen=new Image();
		
		return imagen;
	}
	
}


