package projecte1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class Main231122Comentaris {
	public static void main(String[] args) {
		Random r=new Random();
		/* La primera dimensio de l'array bidimensional dadesGenerades, contindrà les etiquetes dels camps a generar, i 
		 * la segona tots els registres a generar.
		 * A sEntradaOriginal, guardem l'ordre d'entrada dels camps a generar del fitxer original, a sEntradaOrdenat, els camps ordenats per ID
		 * Errors[] ens permet guardar dinamicament els diferents missatges d'error que poden sortir, a les diferents funcions
		 * Els arrays d'ints idEntradaOriginal i idEntradaOrdenat ens permeten guardar les Id's en l'ordre original i endreçats de menor a major.
		 */
		String dadesGenerades[][] = new String[1][1], sEntradaOrdenat[] = new String[1], sEntradaOriginal[]=new String[1], errors[]= new String[1]; 
		int nombreRegistresGenerar=0, campFinal=0, i=0;
		int[] idEntradaOriginal = new int [1], idEntradaOrdenat = new int [1];
		String ruta="";
		boolean error=false;
		
		try {
			ruta=indicarRuta(ruta);
			File entrada=new File(ruta);
			//Si la ruta del fitxer d'entrada indicat no existeix o es un directori, seguirem demanant la ruta fins a trobar una valida
			while(!entrada.exists() || entrada.isDirectory()) {
				ruta = JOptionPane.showInputDialog(null, "Indiqui un fitxer existent", "Error!", JOptionPane.ERROR_MESSAGE);
				entrada=new File(ruta);
			}
		
			FileReader fr = new FileReader(entrada);
			BufferedReader br = new BufferedReader(fr);
			int j=0, campsGenerar=0;
			String linea1="", linea="", linea1Separat[] = null, lineaSeparat[];
			linea1=br.readLine();
			if(linea1==null){
				errors[0]="Arxiu vuit";
				error=true;
			}else {
				//Separarem els camps per ";"
				linea1Separat=linea1.split(";");
				/*Comprovarem si la ruta que ens indica pel fitxer de sortida existeix, 
				 * si no existeix la crearem i mostraremj un missatge que avisarà a l'usuari que l'ha creat*/
				
				String[] comprovaSortida = new String[10];
				comprovaSortida=linea1Separat[1].split("/");
				String rutaTemp="";
				for(i=0;i<comprovaSortida.length && comprovaSortida[i] != null;i++) {
					if(!comprovaSortida[i].equals("C:")) {
						rutaTemp=rutaTemp+comprovaSortida[i]+"/";
						File temp = new File(rutaTemp);
						while(!temp.exists()) {
							if(temp.mkdir()) {
								String message="La carpeta "+temp+" ha sigut creada";
								JOptionPane.showMessageDialog(null, message, "AVIS", JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}else {
						rutaTemp=comprovaSortida[i]+"/";
					}
				}
			}
			br.close();
			fr.close();
			//Ordenarem el fitxer d'entrada per a seguir la nostra estrategia, tot i que despres al fitxer de sortida sortirà igual que a l'entrada
			FileReader fr1 = new FileReader(entrada);
			BufferedReader br1 = new BufferedReader(fr1);
			//Escriurem un arxiu amb l'entrada ordenada
			FileWriter fw1 = new FileWriter(".\\Sortida\\Fitxer_entrada_ordenat.txt");
			linea1=br1.readLine();
			i=0;
			
			//contem les linies de camps demanats, per saber els camps a generar
			while((linea=br1.readLine()) != null) {
				i++;
			}
			fr1.close();
			br1.close();
			
			//Reinicialitzem el fitxer
			fr1 = new FileReader(entrada);
			br1 = new BufferedReader(fr1);
			
			//Creem els arrays on desarem l'ordre original dels camps demanats, i els camps ordenats
			sEntradaOriginal = new String[i];
			sEntradaOrdenat = new String [i];
			idEntradaOrdenat = new int [i];
			idEntradaOriginal = new int [i];
			i=0;
			
			//Llegim la primera linea, per escriure-la al fitxer ordenat
			linea1=br1.readLine();
			fw1.write(linea1+"\n");
			fw1.flush();
			//Emplenem els arrays
			while((linea=br1.readLine()) != null) {
				lineaSeparat=linea.split(";");
				//copiem id de cada linea en un array
				sEntradaOriginal[i]=linea;
				idEntradaOriginal[i]=Integer.parseInt(lineaSeparat[0]);
				idEntradaOrdenat[i]=Integer.parseInt(lineaSeparat[0]);
				sEntradaOrdenat[i]=linea;
				i++;
			}
			fr1.close();
			br1.close();
			boolean ordenat=false;
			for (int k = 0; k < idEntradaOriginal.length-1; k++) {
				if((idEntradaOriginal[k]<idEntradaOriginal[k+1])) {
					ordenat=true;
				}else {
					ordenat=false;
					break;
				}
			}
			//ordenem l'array
			int contaIntercambis=0;
			//Utilitzem un bucle niuat, que sortira quan estigui ordenat
			while(!ordenat){
				for(int l=0;l<idEntradaOrdenat.length-1;l++){
					if (idEntradaOrdenat[l]>idEntradaOrdenat[l+1]){
						//Intercambiem valors
						int aux=idEntradaOrdenat[l];
						String sAux=sEntradaOrdenat[l];
						idEntradaOrdenat[l]=idEntradaOrdenat[l+1];
						sEntradaOrdenat[l]=sEntradaOrdenat[l+1];
						idEntradaOrdenat[l+1]=aux;
						sEntradaOrdenat[l+1]=sAux;
						//Indiquem que hi ha canvi
						contaIntercambis++;
					}
				}
				//Si no hi ha intercambis, es que esta ordenat.
				if (contaIntercambis==0){
					ordenat=true;
					fr1 = new FileReader(entrada);
					br1 = new BufferedReader(fr1);
					linea1=br1.readLine();
					//Mostrem l'ordre per debug
					for (int k = 0; k < sEntradaOrdenat.length; k++) {
						fw1.write(sEntradaOrdenat[k]+"\n");
						System.out.println(sEntradaOrdenat[k]);
					}
					fw1.flush();
					entrada=new File(".\\Sortida\\Fitxer_entrada_ordenat.txt");
					fr = new FileReader(entrada);
					br = new BufferedReader(fr);
				}
				//Inicialitzem la variable de nou per a que començi a contar de nou
				contaIntercambis=0;
			}
			fr = new FileReader(entrada);
			br = new BufferedReader(fr);

			
			linea1=br.readLine();
			linea1Separat=linea1.split(";");
			if(linea1Separat.length != 3 || !(linea1Separat[0].equals("SQL") || linea1Separat[0].equals("XML") || linea1Separat[0].equals("CSV")) || Integer.parseInt(linea1Separat[2])<1) { //Si no estan tots els camps error
				errors[0]="Primera linia invalida";
				error=true;
			}
			//Definim cuantes dades haurem de generar
			nombreRegistresGenerar=Integer.parseInt(linea1Separat[2])+1;

			Scanner s = new Scanner(entrada);
			while(s.hasNextLine()) {
				s.nextLine();
				campsGenerar++;
			}
			campsGenerar--;
			
			//definim l'array de sortida de dades
			dadesGenerades = new String[campsGenerar][nombreRegistresGenerar];
			
			//Iniciem el bucle de generació de dades
			while(br.ready() && error==false){ //mentres hi hagi lineas
				
				//Iniciem la revisio/generació de dades del camps demanats
				linea=br.readLine();
				lineaSeparat=linea.split(";");
				if(Integer.parseInt(lineaSeparat[0])>23 || Integer.parseInt(lineaSeparat[0])<1 || lineaSeparat.length<2) {//Si hi ha un ID que no sigui cap dels anteriors, donarà error
					error=true;
					errors[0]="Falta ID o nom, o és incorrecte";
					break;
				}
				else if (lineaSeparat[0].equals("1")) { //Si el primer camp es 1, generarem noms
					File fNoms=new File(".\\Dades\\NOMS.txt"); 
					FileReader fn = new FileReader(fNoms);
					BufferedReader bn = new BufferedReader(fn); 
					String noms[]= new String[200];
					for (j=0;bn.ready();j++) {
						noms[j]=bn.readLine();
					}
					bn.close(); //S'han de tencar
					fn.close();

					//Emplenem l'array de control amb les dades demanades
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=noms[r.nextInt(noms.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("2")){ //Si el primer camp es 2, generarem cognoms
					File fCognoms=new File(".\\Dades\\COGNOMS.txt"); 
					FileReader fcn = new FileReader(fCognoms);
					BufferedReader bcn = new BufferedReader(fcn); 
					String cognoms[]= new String[250];
					for (j=0;bcn.ready();j++) {
						cognoms[j]=bcn.readLine();
					}
					bcn.close(); 
					fcn.close();

					//Emplenem l'array de control amb les dades demanades
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=cognoms[r.nextInt(cognoms.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("3")) { //Si el primer camp es 1, generarem noms
					File fCiutats=new File(".\\Dades\\CIUTATS.txt"); 
					FileReader fci = new FileReader(fCiutats);
					BufferedReader bci = new BufferedReader(fci); 
					String ciutats[]= new String[200];
					for (j=0;bci.ready();j++) {
						ciutats[j]=bci.readLine();
					}
					bci.close(); 
					fci.close();
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=ciutats[r.nextInt(ciutats.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("4")) {
					File fAdreses=new File(".\\Dades\\ADRECES.txt"); 
					FileReader fad = new FileReader(fAdreses);
					BufferedReader bad = new BufferedReader(fad);
					String adreces[]= new String[200];
					for (j=0;bad.ready();j++) {
						adreces[j]=bad.readLine();
					}
					bad.close(); 
					fad.close();
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=adreces[r.nextInt(adreces.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("5")) {
					File fProfesions=new File(".\\Dades\\PROFESIONS.txt"); 
					FileReader fpr = new FileReader(fProfesions);
					BufferedReader bpr = new BufferedReader(fpr); 
					String professions[]= new String[239];
					for (j=0;bpr.ready();j++) {
						professions[j]=bpr.readLine();
					}
					bpr.close(); 
					fpr.close();
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=professions[r.nextInt(professions.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("6")) {
					File fPais=new File(".\\Dades\\PAISOS.txt"); 
					FileReader fpa = new FileReader(fPais);
					BufferedReader bpa = new BufferedReader(fpa); 
					String pais[]= new String[446];
					for (j=0;bpa.ready();j++) {
						pais[j]=bpa.readLine();
					}
					bpa.close(); 
					fpa.close();
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=pais[r.nextInt(pais.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("7")) {
					File fEstudis=new File(".\\Dades\\Estudis.txt"); 
					FileReader fes = new FileReader(fEstudis);
					BufferedReader bes = new BufferedReader(fes); 
					String estudis[]= new String[253];
					for (j=0;bes.ready();j++) {
						estudis[j]=bes.readLine();

					}
					bes.close(); 
					fes.close();
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=estudis[r.nextInt(estudis.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("8")) {
					File fColors=new File(".\\Dades\\Colors.txt"); 
					FileReader fco = new FileReader(fColors);
					BufferedReader bco = new BufferedReader(fco); 
					String colors[]= new String[113];
					for (j=0;bco.ready();j++) {
						colors[j]=bco.readLine();
					}
					bco.close(); 
					fco.close();
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=colors[r.nextInt(colors.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("9")) {
					File fUrl=new File(".\\Dades\\URL.txt"); 
					FileReader fu = new FileReader(fUrl);
					BufferedReader bu = new BufferedReader(fu); 
					String url[]= new String[200];
					for (j=0;bu.ready();j++) {
						url[j]=bu.readLine();
					}
					bu.close(); 
					fu.close();
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=url[r.nextInt(url.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("10")) {
					File fCompanyia=new File(".\\Dades\\NOMS_DE_COMPANYIES.txt"); 
					FileReader fco = new FileReader(fCompanyia);
					BufferedReader bco = new BufferedReader(fco); 
					String companyia[]= new String[243];
					for (j=0;bco.ready();j++) {
						companyia[j]=bco.readLine();
					}
					bco.close(); 
					fco.close();
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=companyia[r.nextInt(companyia.length)];
					}
					campFinal++;
				}
				else if(lineaSeparat[0].equals("11")) {
					Boolean(lineaSeparat, r, dadesGenerades, campFinal);
					campFinal++;
				}
				else if (lineaSeparat[0].equals("12")) {
					if (lineaSeparat.length>5){ //Si hi han més opcions de les que toca donarà error
						errors[0]="Error al numero de camps de numeros";
						error=true;
						break;
					}
					error=Numeros(dadesGenerades, campFinal, lineaSeparat, r, error, errors);
					campFinal++;
				}
				else if (lineaSeparat[0].equals("13")) {
					if (lineaSeparat.length>4){ //Si hi han més opcions de les que toca donarà error
						errors[0]="Error numero de camps Email";
						error=true;
						break;
					}
					Email(dadesGenerades, campFinal, lineaSeparat, idEntradaOrdenat, nombreRegistresGenerar);
					campFinal++;
				}
				else if(lineaSeparat[0].equals("14")) {
					IP4(dadesGenerades, campFinal, r,lineaSeparat);
					campFinal++;
				}
				else if(lineaSeparat[0].equals("15")) {
					if (lineaSeparat.length!=8 || Integer.parseInt(lineaSeparat[7])<0){ //Si no hi han totes les opcions a password donarà error
						errors[0]="Error numero de camps Password";
						error=true;
						break;
					}
					error=Password(dadesGenerades, campFinal, lineaSeparat, r, error,errors);
					campFinal++;
				}
				else if (lineaSeparat[0].equals("16")) {
					if (lineaSeparat.length>4){ //Si no hi han totes les opcions donarà error
						errors[0]="Error numero de camps Dates";
						error=true;
						break;
					}
					error=Dates(dadesGenerades, campFinal, r,lineaSeparat, error,errors);
					campFinal++;
				}
				else if (lineaSeparat[0].equals("17")) {
					IBAN(dadesGenerades, campFinal,lineaSeparat);
					campFinal++;
				}
				else if (lineaSeparat[0].equals("18")) {
					DNI(dadesGenerades, campFinal, r,lineaSeparat);
					campFinal++;
				}
				else if (lineaSeparat[0].equals("19")) {
					if (lineaSeparat.length>3){ //Si no hi han totes les opcions a password donarà error
						errors[0]="Error numero de camps Autonumeric";
						error=true;
						break;
					}
					Autonumeric(dadesGenerades, campFinal, lineaSeparat);
					campFinal++;
				}
				else if (lineaSeparat[0].equals("20")) {
					if (lineaSeparat.length>4){ //Si hi han més opcions de les que toca donarà error
						errors[0]="Error al numero de camps de edats";
						error=true;
						break;
					}
					error=edat(dadesGenerades, campFinal, lineaSeparat, r, error, errors);
					campFinal++;
				}
				else if (lineaSeparat[0].equals("21")) { //Si el primer camp es 1, generarem noms
					File fCoches=new File(".\\Dades\\COCHES.txt"); 
					FileReader fch = new FileReader(fCoches);
					BufferedReader bch = new BufferedReader(fch); 
					String coches[]= new String[160];
					for (j=0;bch.ready();j++) {
						coches[j]=bch.readLine();
					}
					bch.close(); //S'han de tencar
					fch.close();

					//Emplenem l'array de control amb les dades demanades
					dadesGenerades[campFinal][0]=lineaSeparat[1];
					for (int k = 1; k < nombreRegistresGenerar; k++) {
						dadesGenerades[campFinal][k]=coches[r.nextInt(coches.length)];
					}
					campFinal++;
				}
				else if (lineaSeparat[0].equals("22")) {
					TallesSabates(dadesGenerades, campFinal, lineaSeparat, r);
					campFinal++;
				}
				else if (lineaSeparat[0].equals("23")) {
					Lletres(dadesGenerades, campFinal, lineaSeparat, r);
					campFinal++;
				}
			}

			if(error==true) {
				System.out.println("ERROR");
				missatgeError(errors);
			}
			else {
				entradaOriginal(dadesGenerades,campsGenerar,nombreRegistresGenerar,idEntradaOriginal,idEntradaOrdenat);
				
				//Mostra els camps per test
				for (int k = 0; k < campsGenerar; k++) {
					for (int k2 = 0; k2 < nombreRegistresGenerar; k2++) {
						System.out.print(dadesGenerades[k][k2]+" ");
					}
					System.out.println();
				}	
				if (linea1Separat[0].equals("XML"))
				{
					//si el firxer de sortida es XML executarem aquesta funcio per crear el archiu XML corresponent
					//aquesta bariable agafa tot el fitxer del xml
					String XML="";
					//aquesta agafa tot el fitxer xml per afegir un tros de codi
					String XMLPle="";
					//executem la funcio que genera el XML
					xml(dadesGenerades,nombreRegistresGenerar, linea1Separat);
					//senyalem l'entrada i la sortida
					File sortida=new File(linea1Separat[1]+"/Fitxer_xsd.xsd");
					File entrada2=new File(linea1Separat[1]+"/Fitxer_xml.xml");
					FileReader frx = new FileReader(entrada2);
					BufferedReader brx = new BufferedReader(frx);
					File sortida2=new File(linea1Separat[1]+"/Fitxer_xml.xml");
					//aquest bucle agafa tot el fitxer XML i el posa en un string
					while(brx.ready()) {
						XML = brx.readLine();
					}
					System.out.println(XML);
					//separem el artxiu xml per ">"
					String[] Xml=XML.split(">");
					//fem aquest bucle per implementar la linea de codi que li falta
					for(i=0;i<Xml.length;i++) {
						if (i==1) {
							XMLPle=XMLPle+"<?xml-stylesheet type=\"text/xsl\" href=\"Fitxer_xsl.xsl\"?>"+Xml[i]+" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"file:Fitxer_xsd.xsd\">";
						}
						else {
							XMLPle=XMLPle+Xml[i]+">";
						}
					}
					//reescribim el fitxer XML
					try (FileWriter escriptor1 =new FileWriter(sortida2);) {
						escriptor1.append(XMLPle).write('\n');
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
					//executem la funcio per generar el XSD i passem el XSD a un String
					String text=xsd(dadesGenerades);
					//Creem el artxiu XSD amb el String fet anteriorment
					try (FileWriter escriptor2 =new FileWriter(sortida);) {
						escriptor2.append(text).write('\n');
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
					//creem un arxiu de sortida per el xslt
					File sortida3=new File(linea1Separat[1]+"Fitxer_xsl.xsl");
					//agafem el contingut del que tindra el fitxer xslt i el bolquem al fitxer de sortida
					String xslt=xslt(dadesGenerades);
					try (FileWriter escriptor3 =new FileWriter(sortida3);) {
						escriptor3.append(xslt).write('\n');
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				}
				else if (linea1Separat[0].equals("SQL"))
				{
					//creem un fitxer de sortida
					File sortida=new File(linea1Separat[1]);
					sqlDades(dadesGenerades,idEntradaOrdenat,sortida);
				}
				else if (linea1Separat[0].equals("CSV")) {
					crearCSV(dadesGenerades,linea1Separat);
				}
				missatgeCorrecte();
			}

			br.close(); //S'han de tencar
			fr.close();
		} catch (Exception pasanCosas) {
			pasanCosas.printStackTrace();}
	}

	private static void entradaOriginal(String[][] dadesGenerades, int campsGenerar, int nombreRegistresGenerar,
			int[] idEntradaOriginal, int[] idEntradaOrdenat) {
		String aux[][]=new String[campsGenerar][nombreRegistresGenerar];
		int posicio=0;
		for (int k = 0; k < idEntradaOriginal.length; k++) {
			for (int k2 = 0; k2 < idEntradaOriginal.length; k2++) {
				if(idEntradaOriginal[k]==idEntradaOrdenat[k2]) {
					posicio=k2;
			}
			aux[k][0]=dadesGenerades[posicio][0];
				for (int l = 1; l < nombreRegistresGenerar; l++) {
					aux[k][l]=dadesGenerades[posicio][l];
				}
			}
		}
		
		for (int k = 0; k < campsGenerar; k++) {
			for (int k2 = 0; k2 < nombreRegistresGenerar; k2++) {
				dadesGenerades[k][k2]=aux[k][k2];
			}
		}	
	}

	//ID 11
	public static void Boolean(String[] lineaSeparat, Random r, String[][] dadesGenerades, int campFinal) {
		
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		for (int j = 1; j < dadesGenerades[campFinal].length; j++) {
			String numeroAleatori=String.valueOf(r.nextBoolean()); //Generem un boolean aleatori i el passem a string
			dadesGenerades[campFinal][j]=numeroAleatori;
		}
	}

	//ID 12
	public static boolean Numeros(String[][] dadesGenerades, int campFinal, String lineaSeparat[], Random r, boolean error, String[] errors) {
		int decimals=0, minim=0, maxim=1000;
		if(lineaSeparat.length>2) {
			if (lineaSeparat.length==3) {
				decimals=Integer.parseInt(lineaSeparat[2]);
			}else if (lineaSeparat.length==4) {
				decimals=Integer.parseInt(lineaSeparat[2]);
				minim=Integer.parseInt(lineaSeparat[3]);
			}else if (lineaSeparat.length==5) {
				decimals=Integer.parseInt(lineaSeparat[2]);
				minim=Integer.parseInt(lineaSeparat[3]);
				maxim=Integer.parseInt(lineaSeparat[4]);
			}
			if(Integer.parseInt(lineaSeparat[2])<0) {
				errors[0]="Error decimals (Numeros)";
				return error=true;
			}
		}

		int maxim2 = maxim-minim;
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		if(maxim<minim)
		{
			errors[0]="Error numero maxim mes petit que minim (Numeros)";
			return error=true;
		}
		for(int j = 1; j < dadesGenerades[campFinal].length; j++) {
			double generat=r.nextDouble(maxim2)+minim;
			BigDecimal redondeado = new BigDecimal(generat).setScale(decimals, RoundingMode.HALF_EVEN);
			dadesGenerades[campFinal][j]=redondeado.toString();
		}	
		return error;
	}

	//ID 13
	public static void Email(String[][] dadesGenerades, int campFinal, String[] lineaSeparat, int idEntradaOrdenat[], int nombreRegistresGenerar) throws IOException {
		Random rnd= new Random();
		boolean bNoms=false, bCompanyies=false, nomsJaGenerats=false, companyiesJaGenerades=false;
		int lineasNom = 0, lineasCompanyia = 0, i=0, aleatori=0, total=nombreRegistresGenerar-1, posicioArrayCompanyies=0, posicioArrayNoms=0;
		String extensio=".com", dominiDemanat="", noms[], nomsCompanyia[];

		if (lineaSeparat.length==3) {
			dominiDemanat=lineaSeparat[2];
		}
		if (lineaSeparat.length==4) {
			dominiDemanat=lineaSeparat[2];
			extensio=lineaSeparat[3];
		}

		for (int j = 0; j < dadesGenerades.length; j++) {
			if (idEntradaOrdenat[j]==1) {
				posicioArrayNoms=j;
				bNoms=true;
			}
			if (idEntradaOrdenat[j]==10) {
				posicioArrayCompanyies=j;
				bCompanyies=true;
			}
		}
		if (!bNoms) {
			//aqui declarem i inicialitzem el fitxer de noms
			File filenom = new File(".\\Dades\\NOMS.txt");
			Scanner scnom = new Scanner(filenom);
			FileReader frnom = new FileReader(filenom);
			BufferedReader brnom = new BufferedReader(frnom);
			//contem les linies del fitxer de noms
			while(scnom.hasNextLine()) {
				scnom.nextLine();
				lineasNom++;
			}
			scnom.close();
			noms= new String[lineasNom];
			//inicialitzem i emplenem l'array des del fitxer de noms
			while(brnom.ready()) {
				String nom = brnom.readLine();
				noms[i]=nom;
				i++;
			}
			brnom.close();
			frnom.close();
			i=0;
		}else {
			noms=new String[total];
			for (int j = 1, k =0; k < noms.length; j++, k++) {
				noms[k]=dadesGenerades[posicioArrayNoms][j];
			}
			lineasNom=nombreRegistresGenerar;
			nomsJaGenerats=true;
		}
		if (!bCompanyies) {
			//aqui declarem i inicialitzem el fitxer de companyies
			File fileCompanyia = new File(".\\Dades\\NOMS_DE_COMPANYIES.txt");
			Scanner scCompanyia = new Scanner(fileCompanyia);
			FileReader frCompanyia = new FileReader(fileCompanyia);
			BufferedReader brCompanyia = new BufferedReader(frCompanyia);
			//contem les linies del fitxer de companyies
			while(scCompanyia.hasNextLine()) {
				scCompanyia.nextLine();
				lineasCompanyia++;
			}
			scCompanyia.close();
			//declarem els arrays de string del fitxer de noms i del fitxer de companyies
			nomsCompanyia= new String[lineasCompanyia];

			//inicialitzem el array del fitxer de companyies
			while(brCompanyia.ready()) {
				String nom = brCompanyia.readLine();
				nomsCompanyia[i]=nom;
				i++;
			}
			brCompanyia.close();
			frCompanyia.close();
		}else {
			nomsCompanyia=new String[nombreRegistresGenerar-1];
			for (int j = 1, k =0; j < dadesGenerades[0].length; j++, k++) {
				nomsCompanyia[k]=dadesGenerades[posicioArrayCompanyies][j];
			}
			lineasCompanyia=nombreRegistresGenerar;
			companyiesJaGenerades=true;
		}
		//declarem els arrays per barrejar el fitxer de noms i de companyies, si es necessari
		String nomsAleatoris[]= new String[total], nomsRepetits[]= new String[total], NumerosNomsRepetits[]= new String[total], companyiesAleatoris[]= new String[total];
		//inicialitzem el array per barrejar el fitxer de noms

		if (!nomsJaGenerats) {
			for(i=0;i<total;i++) {
				aleatori=rnd.nextInt(200);
				nomsAleatoris[i]=noms[aleatori].toLowerCase();
				nomsRepetits[i]=nomsAleatoris[i];
			}			
		}else {
			for (int j = 0; j < noms.length; j++) {
				nomsAleatoris[j]=noms[j].toLowerCase();
			}			
		}

		//funcio per a poder canviar el nom dels que estan repetits
		for(i=0;i<total;i++) {
			for (int i2 = 0;i2<noms.length;i2++) {
				int fin=nomsRepetits.length-2;
				if (i2<fin&&nomsAleatoris[i].equals(nomsAleatoris[i2+1])) {
					aleatori=rnd.nextInt(lineasNom);
					nomsAleatoris[i]=nomsAleatoris[i]+aleatori;
				}
			}
		}
		if (!companyiesJaGenerades) {
			//inicialitzem el array per barrejar el fitxer de companyies si no demanen companyies
			for(i=0;i<total;i++) {
				aleatori=rnd.nextInt(lineasCompanyia);
				companyiesAleatoris[i]=nomsCompanyia[aleatori];
			}		
		}else {
			for (int j = 0; j < nomsCompanyia.length; j++) {
				companyiesAleatoris[j]=nomsCompanyia[j].toLowerCase();
			}
		}
		//inicialitzem el array de dominis per crear els dominis a partir de el nom de la companyia
		String dominis[]= new String[total];
		//inicialitzem el array que crea els dominis
		for (i=0;i<total;i++) {
			String[] parts = nomsCompanyia[i].split(" ");
			String domini="";
			for (int i2 = 0;i2<parts.length;i2++) {
				domini=domini+parts[i2];
			}
			dominis[i]=domini.toLowerCase();
		}
		//declarem el array per crear els emails
		String emails[]= new String[total];
		//inicialitzem el array per crear els emails
		//aquest if es per saver si no hi ha un domini solicitat
		if (dominiDemanat.isEmpty()) {
			for (i=0;i<total;i++) {
				emails[i]=nomsAleatoris[i]+"@"+dominis[i]+extensio;
			}
		}
		//aquest else es per saver si hi ha un domini solicitat
		else {
			for (i=0;i<total;i++) {
				emails[i]=nomsAleatoris[i]+"@"+dominiDemanat+extensio;
			}
		}
		//Emplenem amb els emails l'array de control
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		for (i=0;i<emails.length;i++) {
			dadesGenerades[campFinal][i+1]=emails[i];
		}

	}

	//ID 14
	public static void IP4(String[][] dadesGenerades, int campFinal, Random r, String[] lineaSeparat) {
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		for (int j = 1; j < dadesGenerades[campFinal].length; j++) {
			String ipFinal="";
			ipFinal=r.nextInt(256)+"."+r.nextInt(256)+"."+r.nextInt(256)+"."+r.nextInt(256);
			dadesGenerades[campFinal][j]=ipFinal;
		}
	}

	//ID 15
	public static boolean Password(String[][] dadesGenerades, int campFinal, String[] lineaSeparat, Random r, boolean error, String[] errors) {
		String opcionsGeneracio[] = new String[1], majMin[] = new String[1], passw="";
		int i=0, selector=0, num=0, posicio=0;
		char lletra;
		boolean lletres = false, numeros = false, majuscules = false, minuscules = false, simbols = false;
		char sim[]= {'~','@','#','_','^','*','%','/','.','+',':','='}; //Declarem un array per als simbols que es permeten a les contrassenyes
		//emplenem el primer camp de l'array de control
		dadesGenerades[campFinal][0]=lineaSeparat[1];

		//Mirem les opcions demanades
		if (lineaSeparat[2].equals("Si")) {
			lletres = true;
			if (lineaSeparat[4].equals("Si")) {
				majuscules = true;
			}
			if (lineaSeparat[5].equals("Si")){
				minuscules = true;
			}
			if (lineaSeparat[4].equals("No") && lineaSeparat[5].equals("No")) {
				majuscules = minuscules = true;
			}
		}else if (lineaSeparat[4].equals("Si") || lineaSeparat[5].equals("Si")){
			errors[0]="Error en el camp Password, lletres no seleccionades y maj o min si";
			error = true;
			return error;
		}
		if (lineaSeparat[3].equals("Si")) {
			numeros = true;
		}
		if (lineaSeparat[6].equals("Si")) {
			simbols = true;
		}

		//Segons les opcions demanades, generem diferents arrays de selecció
		if (lletres && numeros && simbols) {
			opcionsGeneracio = new String[]{"lletres", "numeros", "simbols"};
			if (majuscules && minuscules) {
				majMin = new String[]{"majuscules", "minuscules"};
			}else if (majuscules) {
				majMin = new String[]{"majuscules"};
			}else {
				majMin = new String[]{"minuscules"};
			}
		}else if (numeros && simbols) {
			opcionsGeneracio = new String[]{"numeros", "simbols"};		
		}else if (lletres && simbols) {
			opcionsGeneracio = new String[]{"lletres", "simbols"};		
			if (majuscules && minuscules) {
				majMin = new String[]{"majuscules", "minuscules"};
			}else if (majuscules) {
				majMin = new String[]{"majuscules"};
			}else {
				majMin = new String[]{"minuscules"};
			}
		}else if (lletres && numeros) {
			opcionsGeneracio = new String[]{"lletres", "numeros"};
			if (majuscules && minuscules) {
				majMin = new String[]{"majuscules", "minuscules"};
			}else if (majuscules) {
				majMin = new String[]{"majuscules"};
			}else {
				majMin = new String[]{"minuscules"};
			}
		}else if (lletres) {
			opcionsGeneracio = new String[]{"lletres"};
			if (majuscules && minuscules) {
				majMin = new String[]{"majuscules", "minuscules"};
			}else if (majuscules) {
				majMin = new String[]{"majuscules"};
			}else {
				majMin = new String[]{"minuscules"};
			}
		}else if (numeros) {
			opcionsGeneracio = new String[]{"numeros"};
		}else {
			opcionsGeneracio = new String[]{"simbols"};			
		}
		for (int j = 1; j < dadesGenerades[campFinal].length; j++) {
			passw="";
			i=0;
			while(i<Integer.parseInt(lineaSeparat[7])) {
				switch (opcionsGeneracio[r.nextInt(opcionsGeneracio.length)]) {
				case "lletres":
					switch (majMin[r.nextInt(majMin.length)]) {
					case "majuscules":
						lletra=(char)(r.nextInt(26)+'A');
						passw+=lletra;
						i++;
						break;
					case "minuscules":
						lletra=(char)(r.nextInt(26)+'a');
						passw+=lletra;
						i++;
						break;
					}
				case "numeros": 
					num=r.nextInt(10); //El 10 no estarà inclos
					passw=passw+num;
					i++;
					break;
				case "simbols":
					posicio=r.nextInt(12);
					passw=passw+sim[posicio]; //Agafarem una posició aleatoria del array per agafar un simbol aleatori
					i++;
					break;
				}
			}
			dadesGenerades[campFinal][j]=passw;
		}
		return error;
	}

	//ID 16
	public static boolean Dates(String[][] dadesGenerades, int campFinal, Random r, String[] lineaSeparat, boolean error, String[] errors) {
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		String anyMax = "2023";
		int dia, mes, any;
		String anyMin = "1900";
		if (lineaSeparat.length==3) {
			anyMin=lineaSeparat[2];
			if(Integer.parseInt(anyMin)<0)
			{
				errors[0]="Error numero de camps Dates (Minim)";
				return error=true;
			}
		}else if (lineaSeparat.length==4) {
			anyMin=lineaSeparat[2];
			anyMax=lineaSeparat[3];
			if(Integer.parseInt(lineaSeparat[3])<0 || Integer.parseInt(lineaSeparat[2])<0)
			{
				errors[0]="Error numero de camps Dates (Minim o Maxim)";
				return error=true;
			}
		}
		//Calculem el interval per a generar els anys aleatoris dins del rang que ens indiquen
		//Si l'any minim es mes gran que l'any maxim, donarà un error
		if(Integer.parseInt(anyMin)>Integer.parseInt(anyMax)) {
			errors[0]="Error numero maxim mes petit que minim (Date)";
			return error=true;
		}
		int maxim=Integer.parseInt(anyMax)-1;
		anyMax=String.valueOf(maxim);
		for (int j = 1; j < dadesGenerades[campFinal].length; j++) {
			
			try {  
	            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
	            Date fInicio= format.parse (anyMin+"-01-01"); // Construir la data inicial 
	            Date fFinal = format.parse (anyMax+"-12-31"); // Construir la data final 
	            // getTime () significa tornar la quantitat de mil·lisegons representats per aquest objecte Date  
	            long date = fInicio.getTime() + (long) (Math.random() * (fFinal.getTime() - fInicio.getTime())); 
	            Date fecha= new Date(date);

	            dadesGenerades[campFinal][j]=format.format(fecha).toString();
			}catch (Exception e) {  
	            e.printStackTrace();  
	        }  			
		}
		return error;
	} 

	//ID 17
	public static void IBAN (String dadesGenerades[][], int campFinal, String[] lineaSeparat) {
		int contador=0, tamany=0, banc, oficina, control1, control2, A=0, B=0, C, D=0, E;
		Random r = new Random();
		String BancsLleida[]=new String[1], oficinesBanc[]=new String[1], bancOficina="", compte="", IBAN, IBANsenseCC, CCC="", nomFitxer="", Rstring;
		boolean finalitzador=false;
		//Llegim l'arxiu dels bancs presents a la provincia de Lleida
		try {
			File f1 = new File(".\\Dades\\FitxersIBAN\\BancsLleida.txt");
			Scanner s = new Scanner(f1);
			FileReader fr = new FileReader(f1);
			BufferedReader br = new BufferedReader(fr);
			while(s.hasNextLine()) {
				s.nextLine();
				contador++;
			}
			tamany=contador;
			BancsLleida=new String[tamany];
			contador=0;
			while(contador<tamany) {
				BancsLleida[contador]=br.readLine();
				contador++;
			}
			contador=0;
		}catch (Exception e) {	
		}
		//Elegim el banc aleatoriament
		banc=r.nextInt(tamany);

		//Un cop tenim el banc, carreguem les seves oficines
		Path dir = Paths.get(".\\Dades\\FitxersIBAN\\");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir);) {
			for (Path fitxer: stream) {
				if (finalitzador==true) {
					break;
				}
				nomFitxer=fitxer.toString();
				if (nomFitxer.startsWith(".\\Dades\\FitxersIBAN\\"+BancsLleida[banc])) {
					File f2 = new File(fitxer.toString());
					Scanner s2 = new Scanner(f2);
					FileReader fr2 = new FileReader(f2);
					BufferedReader br2 = new BufferedReader(fr2);
					while(s2.hasNextLine()) {
						s2.nextLine();
						contador++;
					}
					tamany=contador;
					oficinesBanc=new String[tamany];
					contador=0;
					while(contador<tamany) {
						oficinesBanc[contador]=br2.readLine();
						contador++;
					}
					contador=0;
					finalitzador=true;
				}
			}
		} catch (IOException | DirectoryIteratorException ex) {
			System.err.println(ex);
		}

		//Elegim la oficina aleatoriament
		oficina=r.nextInt(tamany);

		//Completem banc i oficina

		//Bucle per emplenar l'array de control d'Ibans
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		for (int j = 1; j < dadesGenerades[campFinal].length; j++) {
			bancOficina=BancsLleida[r.nextInt(BancsLleida.length)]+oficinesBanc[r.nextInt(oficinesBanc.length)];
			compte="";
			for(int i = 0; i < 10; i++){
				compte += r.nextInt(10);
			}


			//Calculem el primer digit de control
			for (int i = 0; i < bancOficina.length(); i++) {
				if(i==0) A+=Character.getNumericValue(bancOficina.charAt(i))*4; 
				if(i==1) A+=Character.getNumericValue(bancOficina.charAt(i))*8; 
				if(i==2) A+=Character.getNumericValue(bancOficina.charAt(i))*5;
				if(i==3) A+=Character.getNumericValue(bancOficina.charAt(i))*10;
				if(i==4) B+=Character.getNumericValue(bancOficina.charAt(i))*9;
				if(i==5) B+=Character.getNumericValue(bancOficina.charAt(i))*7;
				if(i==6) B+=Character.getNumericValue(bancOficina.charAt(i))*3;
				if(i==7) B+=Character.getNumericValue(bancOficina.charAt(i))*6;
			}
			C=(A+B)%11;
			control1=11-C;
			if (control1==10) {
				control1=1;
			}
			if (control1==11) {
				control1=0;
			}
			//Calculem el 2n digit de control
			for (int i = 0; i < compte.length(); i++) {
				if(i==0) D+=Character.getNumericValue(compte.charAt(i))*1;
				if(i==1) D+=Character.getNumericValue(compte.charAt(i))*2;
				if(i==2) D+=Character.getNumericValue(compte.charAt(i))*4; 
				if(i==3) D+=Character.getNumericValue(compte.charAt(i))*8; 
				if(i==4) D+=Character.getNumericValue(compte.charAt(i))*5; 
				if(i==5) D+=Character.getNumericValue(compte.charAt(i))*10;
				if(i==6) D+=Character.getNumericValue(compte.charAt(i))*9;
				if(i==7) D+=Character.getNumericValue(compte.charAt(i))*7;
				if(i==8) D+=Character.getNumericValue(compte.charAt(i))*3;
				if(i==9) D+=Character.getNumericValue(compte.charAt(i))*6;
			}
			E=D%11;
			control2=11-E;
			if (control2==10) {
				control2=1;
			}
			if (control2==11) {
				control2=0;
			}
			CCC=bancOficina+control1+control2+compte;

			//Calculem els digits de control de l'IBAN
			IBANsenseCC=CCC+14+28;

			BigInteger calcularIban=new BigInteger(IBANsenseCC);
			BigInteger mod97 = new BigInteger("97");
			BigInteger RBigInt=calcularIban.mod(mod97);

			Rstring=RBigInt.toString();

			if (Rstring.length()<2) {
				IBAN="ES0"+Rstring+CCC;			
			}else {
				IBAN="ES"+Rstring+CCC;
			}
			//Escribim l'iban a l'array
			dadesGenerades[campFinal][j]=IBAN;	
		}
	}

	//ID 18
	public static void DNI(String[][] dadesGenerades, int campFinal,Random r, String[] lineaSeparat) {
		// TODO Auto-generated method stub
		//Creem un array per tenir predefinida la posició de les lletres, que sempre serà la mateixa
		String letra[] = {"T", "R","W","A","G","M","Y","F","P","D","X","B","N","J","Z","S","Q","V","H","L","C","K","E"};
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		for (int j = 1; j < dadesGenerades[campFinal].length; j++){
			String dni="";
			int i=0,posicionLetra; //creem un contador y una variable per calcular la posició de la lletra
			while (i!=8) { //En aquest bucle, generarem a cada numero del dni un numero aleatori
				dni=dni+r.nextInt(9);;
				i++;
			}
			posicionLetra=Integer.parseInt(dni)%23; //Calcularem la resta de la divisió del dni entre 23, per a saber quina lletra se li assignarà
			dni=dni+letra[posicionLetra]; //Li assignem la seva lletra corresponent a partir del numero tret, agafant aquesta n posició
			dadesGenerades[campFinal][j]=dni;
		}
	}

	//ID 19
	public static void Autonumeric (String dadesGenerades[][], int campFinal, String lineaSeparat[]) {
		int inici=0;
		if(lineaSeparat.length==3) {
			inici=Integer.parseInt(lineaSeparat[2]);
		}
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		for (int i = 1; i < dadesGenerades[campFinal].length; i++) {
			dadesGenerades[campFinal][i]=String.valueOf(inici);
			inici++;
		}
	}
	//ID 20
	public static boolean edat(String[][] dadesGenerades, int campFinal, String lineaSeparat[], Random r, boolean error, String[] errors) {
			int minim=0, maxim=100;
			if(lineaSeparat.length>2) {
				if (lineaSeparat.length==3) {
					minim=Integer.parseInt(lineaSeparat[2]);
				}else if (lineaSeparat.length==4) {
					minim=Integer.parseInt(lineaSeparat[2]);
					maxim=Integer.parseInt(lineaSeparat[3]);
					
					if(Integer.parseInt(lineaSeparat[3])<0) {
						errors[0]="Maxim menor que 0";
						return error=true;
					}
				}
				if(Integer.parseInt(lineaSeparat[2])<0) {
					errors[0]="Minim menor que 0";
					return error=true;
				}
			}
			if(maxim<minim){
				errors[0]="Error numero maxim mes petit que minim (Edat)";
				return error=true;
			}
			int maxim2 = maxim-minim;
			dadesGenerades[campFinal][0]=lineaSeparat[1];
			for(int j = 1; j < dadesGenerades[campFinal].length; j++) {
				int generat=r.nextInt(maxim2)+minim;
				dadesGenerades[campFinal][j]=Integer.toString(generat);
			}	
			return error;
		}
		
	//ID 22
		
	private static void TallesSabates(String[][] dadesGenerades, int campFinal, String[] lineaSeparat, Random r) {
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		for(int j = 1; j < dadesGenerades[campFinal].length; j++) {
			int generat=r.nextInt(24)+24;
			dadesGenerades[campFinal][j]=String.valueOf(generat);
		}	
	}
	
	//ID 23
	private static void Lletres(String[][] dadesGenerades, int campFinal, String[] lineaSeparat, Random r) {
		dadesGenerades[campFinal][0]=lineaSeparat[1];
		for(int j = 1; j < dadesGenerades[campFinal].length; j++) {
			int generat=r.nextInt(28)+'a';
			String lletra=Character.toString((char)generat);
			if (lletra.equals("{")) {
				lletra="ç";
			} else if (lletra.equals("|")) {
				lletra="l·l";
			}
			dadesGenerades[campFinal][j]=lletra;
		}			
	}
	
	private static void xml(String[][] dadesGenerades, int nombreRegistresGenerar, String[] linea1Separat)  {
		try {
			// Creo una instancia de DocumentBuilderFactory
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        // Creo un documentBuilder
	        DocumentBuilder builder = factory.newDocumentBuilder();;
	        // Creo un DOMImplementation
	        DOMImplementation implementation = builder.getDOMImplementation();
	        // Creo un document amb un element root
	        Document documento = implementation.createDocument(null, "dadesGenerades", null);
	        documento.setXmlVersion("1.0");
	        // Creo el element principal
	        Element dades = documento.createElement("dades");
	        // instancio el element principal com el fill del element root
	        documento.getDocumentElement().appendChild(dades);
	        //fem aquest bucle per crear el registres neccesaris
	        for (int x=1;x<nombreRegistresGenerar;x++) {
		        //creo el element de registres
		        Element registre = documento.createElement("Registre");
		        dades.appendChild(registre);
		        //fem aquest bucle per emplenar els element fills de l'elementde registres
		        for (int i=0; i<dadesGenerades.length; i++) {	
			        //agafem el tipus de element
			        dadesGenerades[i][0]=dadesGenerades[i][0].replaceAll("\\ ","_");
			        Element tipus = documento.createElement(dadesGenerades[i][0]);
			        //agafem el contingut de l'element
			        Text contingut = documento.createTextNode(dadesGenerades[i][x]);
		       	 	tipus.appendChild(contingut);
				    registre.appendChild(tipus);
			    }
	        }
	        // Asocio el source amb el Document
            Source source = new DOMSource(documento);
            // Creo el Result, indicant quin ficher es creara
            Result result = new StreamResult(new File(linea1Separat[1]+"/Fitxer_xml.xml"));
            // Creo un transformer, que crea el ficher XML
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
            
			} catch (ParserConfigurationException | TransformerException ex) {
	            System.out.println(ex.getMessage());
	        }
	}
	
	public static String xsd(String[][] dadesGenerades) {
		//Hem fet la capsalera del xsd
		String xsd="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">\r\n"
				+ "  <xs:element name=\"dadesGenerades\">\r\n"
				+ "    <xs:complexType>\r\n"
				+ "      <xs:sequence>\r\n"
				+ "        <xs:element ref=\"dades\"/>\r\n"
				+ "      </xs:sequence>\r\n"
				+ "    </xs:complexType>\r\n"
				+ "  </xs:element>\n"
				+ "  <xs:element name=\"dades\">\r\n"
				+ "    <xs:complexType>\r\n"
				+ "      <xs:sequence>\r\n"
				+ "        <xs:element ref=\"Registre\" maxOccurs=\"unbounded\"/>\r\n"
				+ "      </xs:sequence>\r\n"
				+ "    </xs:complexType>\r\n"
				+ "  </xs:element>\r\n"
				+ "  <xs:element name=\"Registre\">\r\n"
				+ "    <xs:complexType>\r\n"
				+ "      <xs:sequence>\n";
		//fem aquest bucle per agafar els noms dels camps per ordre
		for (int i=0;i<dadesGenerades.length;i++) {
			xsd=xsd+"        <xs:element name=\""+dadesGenerades[i][0]+"\" type=\"xs:string\"/>\n";
		}
		//fem el tancaments finals
		xsd=xsd+"      </xs:sequence>\r\n"
				+ "    </xs:complexType>\r\n"
				+ "  </xs:element>\r\n"
				+ "</xs:schema>\r";
		return xsd;
	}
	//funcio per crear el fitxer xslt
	public static String xslt(String[][] dadesGenerades) {
		//declarem la capsalera del fitxer
		String xslt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
    			+ "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">\r\n"
    			+ "<xsl:template match=\"/\">\r\n"
    			+ " <html>\r\n"
    			+ " <body>\r\n"
    			+ "  <h2>dadesGenerades</h2>\r\n"
    	 		+ "  <table border=\"1\">\r\n"
    			+ "   <tr>\r\n";
				//agafem les dades en ordre de generacio per implementar la capsalera de la taula
    	 		for (int i=0;i<dadesGenerades.length;i++) {
    	 			xslt =xslt+ "    <th>"+dadesGenerades[i][0]+"</th>\r\n";
    	 		}
    	 		//declarem els tancaments de la capsalera
    			xslt =xslt+ "   </tr>\r\n"
    			+ "   <xsl:for-each select=\"//Registre\">\r\n"
    			+ "   <tr>\r\n";
    			//agafem les etiquetes dels noms per generar les dades de cada etiqueta  
    			for (int i=0;i<dadesGenerades.length;i++) {
    	 			xslt =xslt+ "    <td><xsl:value-of select=\""+dadesGenerades[i][0]+"\"/></td>\r\n";
    	 		}
    			//declarem els tancaments del fitxer
    			xslt =xslt+ "   </tr>\r\n"
    			+ "   </xsl:for-each>\r\n"
    			+ "  </table>\r\n"
    			+ " </body>\r\n"
    			+ " </html>>\r\n"
    			+ "</xsl:template>\r\n"
    			+ "</xsl:stylesheet>";
		return xslt;
	}

	//SQL Insert into

	public static void sqlDades(String dadesGenerades[][], int[] linies, File sortida) throws IOException {
		//declarem un array que contindra totes les linies del script de creacio de taula
				String[] sqlSeparat=new String[linies.length+2];
				FileWriter escriptor =new FileWriter(sortida+"/Fitxer_sql.sql");
				BufferedWriter bw = new BufferedWriter(escriptor);
				//declarem una variable que contindra totes el script de creacio de taula
				String sql="";
				//fem un bucle per crear el scrip de creacio de taula
				int i=0;
				for (int j = 0;j<sqlSeparat.length;j++) {
					
					//comencem ha fer el inici de script
					if(j==0) {
						sqlSeparat[j]="CREATE TABLE dades (";
					}
					//fem el final del script
					else if(j==sqlSeparat.length-1) {
						sqlSeparat[j]=");";
					}
					//fem el contingut del mig del script
					else {
						dadesGenerades[i][0]=dadesGenerades[i][0].replaceAll("\\ ","_");
						if (linies[i]==1 || linies[i]==2 || linies[i]==3 || linies[i]==4 || linies[i]==5
								|| linies[i]==6|| linies[i]==7 || linies[i]==8 || linies[i]==9 || linies[i]==10
								|| linies[i]==13 || linies[i]==14 || linies[i]==15 || linies[i]==17 || linies[i]==18
								|| linies[i]==21 || linies[i]==23) {
						sqlSeparat[j]=dadesGenerades[i][0]+" Varchar (400)" ;
						}else if(linies[i]==11){
								sqlSeparat[j]=dadesGenerades[i][0]+" Boolean" ;
								
						}else if(linies[i]==12){
								sqlSeparat[j]=dadesGenerades[i][0]+" Double" ;
						}else if(linies[i]==16){
								sqlSeparat[j]=dadesGenerades[i][0]+" Date" ;
						}else if(linies[i]==19 || linies[i]==20 || linies[i]==22){
								sqlSeparat[j]=dadesGenerades[i][0]+" integer (5)" ;
						}
						i++;
					}
					
				}
				//fem aquest bucle per inserir el contingut del script a una variable string
				for (
						i = 0;i<sqlSeparat.length;i++) {
					if (i==0 || i==sqlSeparat.length-1 || i==sqlSeparat.length-2) {
						sql=sql+sqlSeparat[i];
					}
					else {
						sql=sql+sqlSeparat[i]+", ";
					}
				}
				bw.write(sql);
		//iniciem una variable que sera el script de incercio
		sql="\nINSERT INTO dades (";
		boolean comillas=false;
		//fem aquest bucle per decidir quines columnes seran emplenades de dades
		for (i=0;i<linies.length;i++) {
			if (i<linies.length-1) {
				sql=sql+dadesGenerades[i][0]+", ";
			}
			else if (i==linies.length-1){
				sql=sql+dadesGenerades[i][0]+") \nVALUES (";
			}
		}
		bw.write(sql);
		
		//tanquem la capsalera de incercio i iniciem la incercio de les dades
		
		int llarg=dadesGenerades[0].length-1;
		
		//fem aquest bucle per recollir les dades que inserirem a la taula
	
		for (int k = 1; k <= llarg; k++) {
			sql="";
			//fem un if per fer el tancaments del script
			if (k>1) {
				sql=sql+"), \n(";
			}
			//fem aquest bucle per crear les cometes dobles pels varchars i treurelsi els numeros
			for (int k2 = 0; k2 < dadesGenerades.length; k2++) {
				
				if(linies[k2]!=11) {
					sql=sql+"\""+dadesGenerades[k2][k]+"\", ";
				}else {
					sql=sql+dadesGenerades[k2][k]+", ";
				}
			}
			sql=sql.substring(0,sql.length()-2);
			//tanquem el script
			if (k==llarg) {
				sql=sql+");";
			}
			bw.write(sql);
			
		}
		bw.close();
		escriptor.close();
	}
	
	public static void crearCSV(String dadesGenerades[][], String[] linea1Separat) {
		//obrim el fitxer de sortida
		File csv =new File(linea1Separat[1]+"/Fitxer_CSV.csv");
		try {
			//obrim el escriptor per escriure el fitxer de sortida
			FileWriter fwcsv = new FileWriter(csv);
			//creem la capsalera del fitxer CSV
			for(int i=0;i<dadesGenerades.length;i++) {
				if (i<dadesGenerades.length-1 ) {
					fwcsv.append(dadesGenerades[i][0]);
					fwcsv.append(";");
				}
				else {
					fwcsv.append(dadesGenerades[i][0]);
					fwcsv.append("\n");
				}
			}
			//afegim les dades al escriptor CSV
			for(int i=1;i<dadesGenerades[0].length;i++) {
				for(int ii=0;ii<dadesGenerades.length;ii++) {
					if (ii<dadesGenerades.length-1) {
						fwcsv.append(dadesGenerades[ii][i]);
						fwcsv.append(";");
					}
					else {
						fwcsv.append(dadesGenerades[ii][i]);
						fwcsv.append("\n");
					}
				}
			}
			//fem el flush per escriure el fitxer CSV
			fwcsv.flush();
			//tanquem el escriptor
			fwcsv.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String indicarRuta(String ruta) {
		return ruta = JOptionPane.showInputDialog("Indica la ruta del fitxer d'entrada");
	}
	
	public static void missatgeError(String[] errors) {
		JOptionPane.showMessageDialog(null,
                "Hi ha hagut un error: "+ errors[0],
                "ERROR",
                JOptionPane.ERROR_MESSAGE);
	}
	
	public static void missatgeCorrecte() {
		Icon icon = new ImageIcon("C:/DAM2/PROJECTE_1/img/cherry.png"); //Creem un icon per a mostrar l'icona de l'aplicació quan tot hagi sortit be
		JOptionPane.showMessageDialog(null,
                "Generació de dades finalitzada",
                "COMPLETAT",JOptionPane.INFORMATION_MESSAGE,icon);
		//C:/DAM2/PROJECTE_1/Fitxers_de_e-s/Fitxer_entrada_amb_opcions.txt
	}
}