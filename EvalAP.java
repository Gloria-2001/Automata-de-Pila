/*
    Instituto Politécnico Nacional 
    Escuela Superior de Cómputo 
    Teoria Computacional. 2CV1
    Profr. Benjamin Luna Benoso 
    Gloria Oliva Olivares Menez 
    26/12/2020

    Funcionamiento: Las configuraciones están en el archivo .rar con los nombres de C1.txt, C2.txt y C3.txt 
                    Para compilar el programa, se hace a través de la terminal (Linux) o del cmd (Windows).
                    Primero se coloca el comando javac seguido del nombre del archivo con la extension
                    .java, por tanto, quedaria de la siguiente forma: javac EvalAP.java 
                    Posteriormente, para ejecutarlo, se coloca el comando java, el nombre del archivo (sin la 
                    extension) y finalmente el nombre del archivo con la configuracion. Es decir: 
                    java EvalAP C1.txt

    Configuraciones: En los respectivos archivos txt viene el lenguaje que acepta cada una de las configuraciones.
*/

import java.util.*;
import java.io.*;

public class EvalAP{

    private String symbols[];   //array para guardar el alfabeto
    private String lenguaje[];  //array para guardar el lenguaje 
    private String ini, edoActual;
    private String fin[];
    private Stack<String> pila;   //declaración de la pila 
    private HashMap<String,HashMap<String,String[]>> table; 
    private String movimiento = "q0->"; //declaración del movimiento de la pila 

    public EvalAP(String name){
        this.table = new HashMap<String,HashMap<String,String[]>>(); //se instancia el hashmap
        pila = new Stack<String>();  //se instancia la pila
        pila.push("Z");     //se agrega la base de la pila
        try{
            // Se crea y se abre un fichero.
            File inputFile = new File(name);

            // Se crea un lector del archivo y un buffer
            // que contendrá el texto del archivo
            FileReader fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);

            // Se lee linea por linea el archivo
            String linea;
            String[] aux;
            linea = br.readLine();
            while(linea != null){
                // Se divide la cadena y se guarda en un array
                String dato[] = linea.split(":"); //los edos se dividen por 2 puntos
                if(linea.charAt(0) == '@'){
                    symbols = dato[1].split(",");  //los datos de dividen por comas 
                    lenguaje = dato[2].split(":");
                }else if(dato[0].equals("Inicio")){
                    ini = dato[1];
                    edoActual = dato[1];
                }else if(dato[0].equals("Final")){
                    fin = Arrays.copyOfRange(dato, 1, dato.length);
                }else{
                    HashMap<String,String[]> tabAux = new HashMap<String,String[]>(); //se instancia el hashmap auxiliar 
                    aux = Arrays.copyOfRange(dato, 1, dato.length);  
                    for(String transi : aux){
                        String []daux = transi.split(",");
                        tabAux.put(daux[0],Arrays.copyOfRange(daux, 1, daux.length));  //se guardan los datos en el hash auxiliar 
                    }
                    table.put(dato[0], tabAux); //se guardan los datos en el hash principal 
                }
                // System.out.println("Linea "+ i +": "+linea);
                linea = br.readLine();
            }
        
            table.remove("");   // Elimino espacio en blanco

            // Se cierra el lector del archivo
            fr.close();

        }catch(FileNotFoundException e){
            System.err.println("ArchivoText: " + e);
            System.exit(0);
        }catch(IOException e){
            System.err.println("ArchivoText: " + e);
            System.exit(0);
        }
    }

    public void showData(){
        System.out.println("Alfabeto: " +Arrays.toString(symbols));  // se muestra el alfabeto
        System.out.println("Lenguaje: " +Arrays.toString(lenguaje));  //se muestra el lenguaje 
        /*                                                              //Todo el código comentado es para que se muestren los estados y las transiciones 
        for (String i : table.keySet()) {
            System.out.println("Estado: " + i + "; Transiciones:");
            HashMap<String,String[]> aux = table.get(i);
            for(String j : aux.keySet()){
                System.out.print("\t" + j + ":");
                String []tranAux = aux.get(j);
                for(String k : tranAux){
                    System.out.print(k+", ");
                }
                System.out.println();
            }
            System.out.println();
        }

        System.out.println("Estado inicial: "+ini);
        System.out.print("Estados finales: ");
        for(int i=0;i<this.fin.length;i++){
            System.out.print(fin[i]+",");
        }
        System.out.println();
        */
    }

    //se analiza la cadena 
    public void analizarCadena(){
        String cadena; 
        Scanner cad = new Scanner(System.in);
        System.out.print("Ingrese la cadena: ");   //la cadena la ingresa el usuario 
        cadena = cad.nextLine();
        cadena = cadena+"@";
        int tam=cadena.length();
        String cadAux = cadena;        // se declara una cadena auxiliar, que será la misma cadena pero cada vez mas reducida  
        System.out.println(cadAux); 
       for(int i=0;i<tam;i++){
           char cc = cadena.charAt(i);
            cadAux = cadAux.substring(1);
            System.out.println(cadAux); 
            if(alfabeto(cc) || cc=='@'){  //si la cadena tiene un caracter @ o el metodo alfabeto es true 
                acciones(cc); //se corre el metodo de acciones 
            }else{
                System.out.println("No pertenece al alfabeto");  //De otra forma, como no pertenence al alfabeto, termina el programa
                System.out.println(movimiento); 
                System.exit(0);
             }
        }
       System.out.println(movimiento);     //se muestra el movimiento que hace la pila 
       System.out.println("Cadena no valida");  //si se sale del ciclo la cadena no es valida 
    }
    
    public boolean alfabeto(char c){   //este metodo indica si la cadena pertenece al alfabeto 
        int tam=symbols.length;
        for(int i=0;i<tam;i++){      //se recorre el arreglo de symbols  
            if(String.valueOf(c).equals(symbols[i])){   //si el caracter ingresado de la cadena corresponde a alguno de symbols
                return true;  //es verdadero 
            }
        }
        return false;  //de otra forma es falso 
    }

    public void acciones(char b){  //este metodo hace las acciones de la pila 
        String sb = String.valueOf(b);  //se convierte en string el caracter de la cadena de entrada 
        try{
            HashMap<String,String[]> aux = table.get(this.edoActual);   //se obtiene el estado actual 
            String[] aux1 = aux.get(sb);  //se pone en un auxiliar las pequeñas cadenas 
            if(!aux1[1].equals("@")){ //si esta en la primera posicion va a sacarse de la pila 
                sb=pila.pop();
            }
            if(!aux1[2].equals("@")){   //si esta en la segunda posicion se mete a la pila 
                pila.push(aux1[2]);
            }
            this.edoActual = aux1[0];
            movimiento+=(Arrays.toString(aux1)+"->");  //se va a ir haciendo el movimiento de la pila 
            if(sb.equals("Z") && eval()){    //si al final la cadena se iguala a Z (base de la pila) y cumple el metodo eval(), la cadena es aceptada
                System.out.println(movimiento+"Fin");      
                System.out.println("Cadena aceptada");
                System.exit(0);
            }
        }catch(Exception e){
            System.out.println(movimiento);
            System.out.println("Cadena no valida"); //De otra forma se muestra el movimiento y se rechaza la cadena 
            System.exit(0);
        }
    }

    public boolean eval(){
        for(int i=0;i<fin.length;i++){   //Si el estado actual de la cadena es igual al estado final, la cadena es compatible
            if(this.edoActual.equals(this.fin[i])){
                return true;
            }
        }
        return false; 
    }

    public static void main(String[] args) { //se implementan los metodos 
        EvalAP ap = new EvalAP(args[0]);
        ap.showData();
        ap.analizarCadena();
    }
}