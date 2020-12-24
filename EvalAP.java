import java.util.*;
import java.io.*;

public class EvalAP{

    public int columnaF;  //numero de columnas
    private String estadoInicial, estadoFinal, estadoActual;
    private ArrayList<String[]> trans;
    private HashMap<String, HashMap<String,String[]>> tabla; 
    private HashMap<String, String[]> tablaAux; 
    private String[] simIni; 
    private Stack<String> pila; 
    public String cadena;

    public EvalAP(String config){
        tabla=new HashMap<String,HashMap<String,String[]>>();
        tablaAux=new  HashMap<String, String[]>();

        try{
            //Creacion y apertura del fichero
            File inputFile = new File(config);

            //Lector de archivo y buffer para el contenido del mismo
            FileReader fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);

            //Leer linea por linea
            String linea;
            String [] aux; 
            int i=0;
            linea = br.readLine();
            while(linea!=null){
                //Se hace la division para los estados
                String dato[]= linea.split(":|\\,");
                if(i==0){
                    //Si la línea empieza con @ 
                    if(linea.charAt(0)=='@'){
                        this.columnaF = dato.length;    //el No. de Columnas será la longitud del arreglo de datos
                        String[] aux3 = Arrays.toString(trans);
                        aux3 = Arrays.copyOfRange(dato,1,this.columnaF); //los simbolos seran los datos después de la posición 0 (@)
                    }else{  //Si no hay @ el archivo no es válido
                    System.out.println("Archivo no valido");
                    System.exit(0);
                    }
                }else if(dato[0].equals("Inicio")){ //Si la posicion en 0 de datos es igual a la palabra inicio
                    estadoInicial = dato[1]; //es el estado inicial
                    estadoActual = dato[1];
                }else if(dato[0].equals("Final")){ //Si la posicion en 0 de datos es igual a la palabra final
                    estadoFinal = dato[1]; //Es el estado final 
                }else{
                    /*de otra forma, los datos se guardaran en la variable auxiliar
                    y se pondrán en el hashmap auxiliar, luego lo que esté en el hash 
                    auxiliar se pondrá en el hashmap original
                    */
                    aux = Arrays.copyOfRange(dato,1,this.columnaF);  
                    tablaAux.put(dato[1],aux);                   
                    tabla.put(dato[0],tablaAux);
                    simIni=tablaAux.get(dato[2]);
                }
                linea = br.readLine();
                i++;
            }
            tabla.remove(""); //Eliminar espacio en blanco 
            fr.close(); //Cerrar lector
        }catch(FileNotFoundException e){
            System.err.println("ArchivoText: "+e);
            System.exit(0);
        }catch(IOException e){
            System.err.println("ArchivoText: "+e);
            System.exit(0);
        }
    }

    public void analizarCadena(){
        Scanner cad = new Scanner(System.in);
        System.out.print("Ingrese la cadena: ");
        cadena = cad.nextLine();
        for(int i=0; i<cadena.length(); i++){        // va a leer la cadena elemento por elemento 
            int indAux = -1;                         //se establece un indice auxiliar 
            for(int j=0; j<columnaF-1; j++){        //Va a pasar por cada columna e imprimira los caracteres 
                if(cadena.charAt(i) == this.trans[j].charAt(0)){
                    indAux = j;
                    break;
                }
            }
            if(indAux != -1){
                String[] auxEstado = this.tablaAux.get(this.estadoActual);
                this.transicionAP(auxEstado,indAux);
            }else{
                System.out.println("\nNo pertenece al Alfabeto Establecido"); // Indica que no pertenece al alfabeto establecido
                System.exit(0);
            }
        }
        pila.clear();
        String aux2 = simIni[0];
        pila.push(aux2);
        estadoActual=estadoInicial;
        boolean transiciones = true;
        int NoIteraciones = cadena.length();
        for(int i=0; i<NoIteraciones; i++){
            if(transiciones==true){
                String simbActual = cadena.substring(0,1);
                cadena=cadena.substring(1);
                transiciones=false;

                for(int j=0; j<trans.size(); j++){
                    if(estadoActual.equals(trans.get(j)[0]) && simbActual.equals(trans.get(j)[1]) && pila.peek().equals(trans.get(j)[2])){
                        estadoActual = trans.get(j)[3];
                        pila.pop();

                        if(trans.get(j)[4].length() > 1){
                            String aux = trans.get(j)[4];
                            for(int k=0; k < aux.length; k++){
                                String aux1= aux.substring(aux.length()-1);
                                aux = aux.substring(0,aux.length()-1);
                                pila.push(aux1);
                            }
                        }else{
                            if(trans.get(j)[4].equals("@")){
                                //nada 
                            }else{
                                pila.push(trans.get(j)[4]);
                            }
                        }
                        transiciones=true;
                        break;
                    }
                }
            }else{
                break;
            }
        }
        cadenaSi(cadena);
    }

    public void cadenaSi(String cadena){
        if(cadena.isEmpty() && pila.isEmpty()){
            System.out.println("Cadena aceptada");
        }else{
            System.out.println("Cadena No Aceptada");
        }
        if(cadena.isEmpty() && estadoActual.equals(estadoFinal)){
            System.out.println("Cadena aceptada");
            break;
        }else
            System.out.println("Cadena No Aceptada");
    }

    public void transicionAP(String[] estados,int ind){//Metodo transiciones
        this.estadoActual = estados[ind];
    }

    public void mostrarDato() { // Metodo que muestra los datos de la configuracion 
        String[] aux;
        System.out.print("\nAlfabeto aceptado: ");
        for(int i=0;i<this.columnaF-1;i++){
            System.out.print(trans[i]+" ");   //muestra el alfabeto aceptado
        }
        System.out.println();
        System.out.println("Estado inicial: "+estadoInicial);
        System.out.println("Estado final: "+estadoFinal);
    }

    public static void main(String[] args) {
        EvalAP ap = new EvalAP(args[0]);
        ap.mostrarDato();
        ap.analizarCadena();
    }
}