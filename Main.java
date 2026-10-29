import java.util.ArrayList;
import java.util.List;


// PATRON CREACIONAL: BUILDER
// Problema que resuelve: un Libro tiene varios datos (titulo,
// autor, edición) y algunos pueden faltar. Con un constructor
// largo habría que escribir null o recordar el orden de los
// parámetros. Con Builder el libro se arma paso a paso y solo
// se indica lo que se necesita.
class Libro {


    private String titulo;
    private String autor;
    private String edicion;
    private boolean disponible;


    // Constructor sin public: solo lo usa LibroBuilder
    Libro(String titulo, String autor, String edicion) {
        this.titulo = titulo;
        this.autor = autor;
        this.edicion = edicion;
        this.disponible = true;
    }


    public String getTitulo() {
        return titulo;
    }


    public boolean isDisponible() {
        return disponible;
    }


    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }


    public String getFicha() {
        return "'" + titulo + "' de " + autor
            + " - Edicion: " + edicion;
    }
}


// Builder: cada método guarda un dato y devuelve this para
// poder encadenar. El método build() entrega el Libro terminado.
class LibroBuilder {


    private String titulo = "Sin titulo";
    private String autor = "Desconocido";
    private String edicion = "No indicada";


    public LibroBuilder titulo(String titulo) {
        this.titulo = titulo;
        return this;
    }


    public LibroBuilder autor(String autor) {
        this.autor = autor;
        return this;
    }


    public LibroBuilder edicion(String edicion) {
        this.edicion = edicion;
        return this;
    }


    public Libro build() {
        return new Libro(titulo, autor, edicion);
    }
}


// Clase base de los préstamos (OCP: abierta a extensión)
abstract class Prestamo {


    protected Libro libro;
    protected String usuario;


    public Prestamo(Libro libro, String usuario) {
        this.libro = libro;
        this.usuario = usuario;
    }


    public Libro getLibro() {
        return libro;
    }


    public String getUsuario() {
        return usuario;
    }


    public String getTipo() {
        return getClass().getSimpleName();
    }


    public abstract int getDiasPlazo();
}


class PrestamoEstudiante extends Prestamo {


    public PrestamoEstudiante(Libro libro, String usuario) {
        super(libro, usuario);
    }


    @Override
    public int getDiasPlazo() {
        return 7;
    }
}


class PrestamoDocente extends Prestamo {


    public PrestamoDocente(Libro libro, String usuario) {
        super(libro, usuario);
    }


    @Override
    public int getDiasPlazo() {
        return 15;
    }
}


// PATRON ESTRUCTURAL: DECORATOR
// Problema que resuelve: queremos agregar una función extra
// (días adicionales de plazo) a un préstamo ya existente, sin
// crear una subclase nueva por cada combinación posible.
// El decorador envuelve a un Préstamo, lo usa por dentro y le
// suma los días. Para el resto del programa sigue siendo un
// Préstamo normal.
class PrestamoConExtension extends Prestamo {


    private Prestamo prestamo; // el préstamo que se envuelve
    private int diasExtra;


    public PrestamoConExtension(Prestamo prestamo, int diasExtra) {
        super(prestamo.getLibro(), prestamo.getUsuario());
        this.prestamo = prestamo;
        this.diasExtra = diasExtra;
    }


    @Override
    public int getDiasPlazo() {
        return prestamo.getDiasPlazo() + diasExtra;
    }


    @Override
    public String getTipo() {
        return prestamo.getTipo() + " con extension de "
            + diasExtra + " dias";
    }
}


// Interfaz del Observer: lo que quiera recibir avisos
// implementa el metodo notificar
interface Notificador {
    void notificar(String mensaje);
}


interface Reporte {
    void mostrar(List<Prestamo> prestamos);
}


// Observadores concretos
class NotificadorConsola implements Notificador {


    @Override
    public void notificar(String mensaje) {
        System.out.println("[AVISO] " + mensaje);
    }
}


class NotificadorContador implements Notificador {


    private int total = 0;


    @Override
    public void notificar(String mensaje) {
        total++;
    }


    public int getTotal() {
        return total;
    }
}


class ReporteConsola implements Reporte {


    @Override
    public void mostrar(List<Prestamo> prestamos) {
        System.out.println("--- PRESTAMOS ACTIVOS ---");
        if (prestamos.isEmpty()) {
            System.out.println("No hay prestamos activos.");
        }
        for (Prestamo p : prestamos) {
            String linea = p.getUsuario() + " (" + p.getTipo() + ")";
            linea += " - '" + p.getLibro().getTitulo() + "'";
            linea += " - Plazo: " + p.getDiasPlazo() + " dias";
            System.out.println(linea);
        }
        System.out.println("-------------------------");
    }
}


// PATRON DE COMPORTAMIENTO: OBSERVER
// Problema que resuelve: cuando pasa algo en la biblioteca
// (préstamo, devolución) pueden querer enterarse varios
// interesados (pantalla, contador, correo...).
// Biblioteca es el Subject: guarda una lista de observadores
// y a todos les avisa. No sabe quienes son ni cuántos son,
// solo que cumplen la interfaz Notificador. Para sumar un
// observador nuevo no se modifica Biblioteca.
class Biblioteca {


    private List<Prestamo> prestamos = new ArrayList<>();
    private List<Notificador> observadores = new ArrayList<>();
    private Reporte reporte;


    public Biblioteca(Reporte reporte) {
        this.reporte = reporte;
    }


    public void suscribir(Notificador observador) {
        observadores.add(observador);
    }


    // Avisa a todos los observadores suscritos
    private void avisar(String mensaje) {
        for (Notificador observador : observadores) {
            observador.notificar(mensaje);
        }
    }


    public void registrarPrestamo(Prestamo prestamo) {
        Libro libro = prestamo.getLibro();
        if (!libro.isDisponible()) {
            avisar("El libro '" + libro.getTitulo()
                + "' no esta disponible.");
            return;
        }
        libro.setDisponible(false);
        prestamos.add(prestamo);
        avisar(prestamo.getUsuario() + " tiene prestado '"
            + libro.getTitulo() + "' por "
            + prestamo.getDiasPlazo() + " dias.");
    }


    public void devolverLibro(Prestamo prestamo) {
        prestamos.remove(prestamo);
        prestamo.getLibro().setDisponible(true);
        avisar(prestamo.getUsuario() + " devolvio '"
            + prestamo.getLibro().getTitulo() + "'.");
    }


    public void mostrarReporte() {
        reporte.mostrar(prestamos);
    }
}


// Clase de prueba con datos fijos
public class Main {


    public static void main(String[] args) {


        // BUILDER: el segundo libro no indica edición, sin null
        Libro libro1 = new LibroBuilder()
            .titulo("Ingenieria de Software")
            .autor("Roger S. Pressman")
            .edicion("7ma Edicion")
            .build();
        Libro libro2 = new LibroBuilder()
            .titulo("Introduccion a la Arquitectura de Software")
            .autor("Oscar Javier Blancarte Iturralde")
            .build();


        System.out.println("--- LIBROS CREADOS CON BUILDER ---");
        System.out.println(libro1.getFicha());
        System.out.println(libro2.getFicha());
        System.out.println();


        // OBSERVER: dos observadores suscritos a la biblioteca
        NotificadorContador contador = new NotificadorContador();
        Biblioteca biblioteca = new Biblioteca(new ReporteConsola());
        biblioteca.suscribir(new NotificadorConsola());
        biblioteca.suscribir(contador);


        Prestamo p1 = new PrestamoEstudiante(libro1, "Ana");
        Prestamo p2 = new PrestamoDocente(libro2, "Prof. Rojas");


        biblioteca.registrarPrestamo(p1);
        biblioteca.registrarPrestamo(p2);


        // Luis pide el mismo libro: no está disponible
        biblioteca.registrarPrestamo(
            new PrestamoEstudiante(libro1, "Luis"));
        biblioteca.devolverLibro(p1);


        // DECORATOR: préstamo de estudiante con 3 dias extra (7 + 3)
        Prestamo base = new PrestamoEstudiante(libro1, "Luis");
        Prestamo p3 = new PrestamoConExtension(base, 3);
        biblioteca.registrarPrestamo(p3);


        biblioteca.mostrarReporte();
        System.out.println("Total de avisos enviados: "
            + contador.getTotal());
    }
}
