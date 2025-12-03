
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.xml.ws.WebServiceClient;

@WebServiceClient(name = "ReservaServlet", targetNamespace = "http://controlador.reservas.casacaribe.com/", wsdlLocation = 
"http://localhost:8080/SistemaReservasCasaCaribe/ReservaServlet?wsdl")
/**
 * Servlet Controlador para gestionar las operaciones CRUD de las Reservas.
 */
public class ReservaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservaDAO reservaDAO;

    public void init() {
        reservaDAO = new ReservaDAO();
    }

    // Método principal para manejar peticiones GET
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");

        if (accion == null) {
            accion = "listar"; // Acción por defecto
        }

        try {
            switch (accion) {
                case "nuevo":
                    mostrarFormulario(request, response);
                    break;
                case "insertar":
                    // El método POST se encargará de la inserción, aquí solo se prepara el form
                    mostrarFormulario(request, response); 
                    break;
                case "editar":
                    mostrarFormularioEdicion(request, response);
                    break;
                case "eliminar":
                    eliminarReserva(request, response);
                    break;
                case "listar":
                default:
                    listarReservas(request, response);
                    break;
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
    
    // Método principal para manejar peticiones POST
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Se asegura la codificación de caracteres para datos enviados desde el formulario
        request.setCharacterEncoding("UTF-8");
        
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "insertar";
        }
        
        try {
            switch (accion) {
                case "insertar":
                    insertarReserva(request, response);
                    break;
                case "actualizar":
                    actualizarReserva(request, response);
                    break;
                default:
                    listarReservas(request, response);
                    break;
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    /**
     * Lee los datos del formulario y crea un objeto Reserva.
     * @param request La solicitud HTTP.
     * @return Objeto Reserva creado a partir de los parámetros.
     */
    private Reserva mapearReserva(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        int id = (idStr != null && !idStr.isEmpty()) ? Integer.parseInt(idStr) : 0;
        
        String nombreCliente = request.getParameter("nombreCliente");
        String emailCliente = request.getParameter("emailCliente");
        LocalDate fechaReserva = LocalDate.parse(request.getParameter("fechaReserva"));
        LocalTime horaReserva = LocalTime.parse(request.getParameter("horaReserva"));
        int numPersonas = Integer.parseInt(request.getParameter("numPersonas"));
        String estado = request.getParameter("estado");
        
        Reserva reserva = new Reserva();
        if (id > 0) {
            reserva.setId(id); // Solo se asigna ID si es una actualización
        }
        reserva.setNombreCliente(nombreCliente);
        reserva.setEmailCliente(emailCliente);
        reserva.setFechaReserva(fechaReserva);
        reserva.setHoraReserva(horaReserva);
        reserva.setNumPersonas(numPersonas);
        reserva.setEstado(estado);
        
        return reserva;
    }

    // --- Métodos de Control ---

    private void listarReservas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obtiene la lista de reservas desde el DAO
        List<Reserva> listaReservas = reservaDAO.listarReservas();
        
        // Establece la lista como atributo para que el JSP la consuma
        request.setAttribute("listaReservas", listaReservas);
        
        // Redirige (dispatch) al JSP de la lista de reservas (Vista)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/lista_reservas.jsp");
        dispatcher.forward(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Prepara el formulario para una nueva reserva (Reserva vacía)
        request.setAttribute("reserva", new Reserva());
        
        // Redirige al JSP del formulario
        RequestDispatcher dispatcher = request.getRequestDispatcher("/form_reserva.jsp");
        dispatcher.forward(request, response);
    }
    
    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        
        // Busca la reserva existente por ID
        Reserva reservaExistente = reservaDAO.buscarReserva(id);
        
        // Pone la reserva existente en el request para pre-llenar el formulario
        request.setAttribute("reserva", reservaExistente);
        
        // Redirige al JSP del formulario
        RequestDispatcher dispatcher = request.getRequestDispatcher("/form_reserva.jsp");
        dispatcher.forward(request, response);
    }

    private void insertarReserva(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        // Llama al método para mapear los parámetros del POST a un objeto Reserva
        Reserva nuevaReserva = mapearReserva(request);
        
        // Llama al DAO para guardar la reserva
        reservaDAO.insertarReserva(nuevaReserva);
        
        // Redirige al listado de reservas (patrón Post-Redirect-Get)
        response.sendRedirect("reservas?accion=listar");
    }

    private void actualizarReserva(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        // Llama al método para mapear los parámetros del POST a un objeto Reserva
        Reserva reservaActualizada = mapearReserva(request);
        
        // Llama al DAO para actualizar la reserva
        reservaDAO.actualizarReserva(reservaActualizada);
        
        // Redirige al listado de reservas
        response.sendRedirect("reservas?accion=listar");
    }

    private void eliminarReserva(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        
        // Llama al DAO para eliminar la reserva
        reservaDAO.eliminarReserva(id);
        
        // Redirige al listado de reservas
        response.sendRedirect("reservas?accion=listar");
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public ReservaDAO getReservaDAO() {
        return reservaDAO;
    }

    public void setReservaDAO(ReservaDAO reservaDAO) {
        this.reservaDAO = reservaDAO;
    }

    @Override
    public String toString() {
        return "ReservaServlet [reservaDAO=" + reservaDAO + ", getReservaDAO()=" + getReservaDAO() + "]";
    }
 