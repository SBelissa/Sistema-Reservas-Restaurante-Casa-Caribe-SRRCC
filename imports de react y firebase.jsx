//imports de react y firebase/firestore*/


import React, { useState, useEffect, useCallback, useMemo } from 'react';
// Importaciones de Firebase/Firestore (Estructura obligatoria para persistencia)
import { initializeApp } from 'firebase/app';
import { getAuth, signInAnonymously, signInWithCustomToken, onAuthStateChanged } from 'firebase/auth';
import { getFirestore, doc, setDoc, onSnapshot, collection, query, where } from 'firebase/firestore';

// ==============================================================================
// 1. CONFIGURACIÓN INICIAL Y COLORES TAILWIND
// ==============================================================================
// Definición de colores base para la temática caribeña
const customTailwindConfig = {
  theme: {
    extend: {
      colors: {
        'caribe-brown': '#8B4513',
        'caribe-teal': '#008080',
        'caribe-gold': '#FFD700',
        'caribe-off-white': '#F8F8F8',
      },
    },
  },
};

// Carga de la configuración Tailwind CSS
if (typeof tailwind !== 'undefined') {
  tailwind.config = customTailwindConfig;
}

// ----------------------------------------------------------------------
// Configuración de Firebase/Firestore (MANDATORIO)
// ----------------------------------------------------------------------
// Variables globales proporcionadas por el entorno
const appId = typeof __app_id !== 'undefined' ? __app_id : 'default-app-id';
const firebaseConfig = typeof __firebase_config !== 'undefined' ? JSON.parse(__firebase_config) : null;
const initialAuthToken = typeof __initial_auth_token !== 'undefined' ? __initial_auth_token : null;

// ==============================================================================
// 2. COMPONENTES ATÓMICOS
// ==============================================================================

/**
 * Componente atómico para mostrar el estado de una reserva.
 * Utiliza clases condicionales de Tailwind para el estilo.
 */
const StatusBadge = ({ estado }) => {
  let colorClasses = '';
  switch (estado) {
    case 'Confirmada':
      colorClasses = 'bg-green-100 text-green-800';
      break;
    case 'Pendiente':
      colorClasses = 'bg-caribe-gold text-caribe-brown';
      break;
    case 'Cancelada':
      colorClasses = 'bg-red-100 text-red-800';
      break;
    default:
      colorClasses = 'bg-gray-100 text-gray-800';
  }

  return (
    <span className={`inline-flex items-center px-3 py-1 text-xs font-medium rounded-full ${colorClasses}`}>
      {estado}
    </span>
  );
};

/**
 * Componente reutilizable para el botón principal.
 */
const Button = ({ children, onClick, color = 'primary', disabled = false }) => {
  let colorClasses = '';
  switch (color) {
    case 'primary':
      colorClasses = 'bg-caribe-teal hover:bg-caribe-brown text-white';
      break;
    case 'danger':
      colorClasses = 'bg-red-600 hover:bg-red-700 text-white';
      break;
    case 'secondary':
      colorClasses = 'bg-gray-200 hover:bg-gray-300 text-gray-700';
      break;
    case 'gold':
      colorClasses = 'bg-caribe-gold hover:bg-yellow-700 text-caribe-brown font-bold';
      break;
    default:
      colorClasses = 'bg-gray-500 hover:bg-gray-600 text-white';
  }

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={`py-2 px-4 rounded-lg transition-colors duration-200 shadow-md ${colorClasses} ${disabled ? 'opacity-50 cursor-not-allowed' : ''}`}
    >
      {children}
    </button>
  );
};

/**
 * Componente modal para confirmaciones (sustituye a alert/confirm).
 */
const Modal = ({ isOpen, title, message, onConfirm, onCancel }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-xl shadow-2xl max-w-sm w-full p-6">
        <h3 className="text-xl font-bold text-caribe-brown mb-4 border-b pb-2">{title}</h3>
        <p className="text-gray-700 mb-6">{message}</p>
        <div className="flex justify-end space-x-3">
          <Button color="secondary" onClick={onCancel}>
            Cancelar
          </Button>
          <Button color="danger" onClick={onConfirm}>
            Confirmar
          </Button>
        </div>
      </div>
    </div>
  );
};

// ==============================================================================
// 3. COMPONENTES DE LAYOUT Y ESTRUCTURA
// ==============================================================================

/**
 * Componente Header: Barra de navegación superior.
 */
const Header = ({ onViewChange, userId }) => (
  <header className="bg-caribe-teal shadow-lg p-4 sticky top-0 z-10">
    <div className="max-w-7xl mx-auto flex justify-between items-center">
      <h1 
        className="text-white text-2xl font-extrabold cursor-pointer"
        onClick={() => onViewChange('list')}
      >
        <span className="text-caribe-gold">Casa Caribe</span> Reservas
      </h1>
      <div className="flex items-center space-x-4">
        <p className="text-white text-sm hidden sm:block">
          Usuario ID: <span className="font-mono text-xs bg-caribe-brown rounded px-2 py-1">{userId || 'Cargando...'}</span>
        </p>
        <Button color="gold" onClick={() => onViewChange('form')}>
          + Nueva Reserva
        </Button>
      </div>
    </div>
  </header>
);

// ==============================================================================
// 4. COMPONENTES DE VISTA CRUD
// ==============================================================================

/**
 * ReservationCard: Muestra los detalles de una reserva individual y sus acciones.
 */
const ReservationCard = ({ reserva, onEdit, onDelete }) => (
  <div className="bg-white shadow-lg rounded-xl p-5 border border-caribe-off-white transition duration-300 hover:shadow-xl">
    <div className="flex justify-between items-start mb-3">
      <h3 className="text-xl font-semibold text-caribe-brown">{reserva.nombreCliente}</h3>
      <StatusBadge estado={reserva.estado} />
    </div>

    <p className="text-gray-600 mb-2">
      <span className="font-medium">Fecha:</span> {reserva.fechaReserva} | 
      <span className="font-medium ml-2">Hora:</span> {reserva.horaReserva}
    </p>
    <p className="text-gray-600 mb-2">
      <span className="font-medium">Personas:</span> {reserva.numPersonas}
    </p>
    <p className="text-gray-600 mb-4 truncate text-sm">
      <span className="font-medium">Email:</span> {reserva.emailCliente}
    </p>

    <div className="flex space-x-3 justify-end pt-3 border-t">
      <Button color="secondary" onClick={() => onEdit(reserva)}>
        Editar
      </Button>
      <Button color="danger" onClick={() => onDelete(reserva.id)}>
        Cancelar
      </Button>
    </div>
  </div>
);

/**
 * ReservationList: Muestra todas las tarjetas de reserva.
 */
const ReservationList = ({ reservations, onEdit, onDelete, isLoading }) => {
  if (isLoading) {
    return (
      <div className="text-center py-10 text-caribe-teal">
        <svg className="animate-spin h-8 w-8 mx-auto mb-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p>Cargando reservas...</p>
      </div>
    );
  }

  if (reservations.length === 0) {
    return (
      <div className="text-center py-20 bg-white rounded-xl shadow-lg m-6 border border-gray-100">
        <p className="text-xl text-gray-500">No hay reservas activas en este momento.</p>
        <p className="text-caribe-teal mt-2">¡Presiona "Nueva Reserva" para empezar!</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
      {reservations.map(reserva => (
        <ReservationCard
          key={reserva.id}
          reserva={reserva}
          onEdit={onEdit}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
};

/**
 * ReservationForm: Componente para crear o editar una reserva.
 */
const ReservationForm = ({ currentReservation, onSave, onCancel }) => {
  const isEditing = currentReservation && currentReservation.id !== null;
  const initialState = {
    id: null,
    fechaReserva: '',
    horaReserva: '19:00',
    numPersonas: 2,
    nombreCliente: '',
    emailCliente: '',
    estado: 'Pendiente',
    ...currentReservation
  };

  const [formData, setFormData] = useState(initialState);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    // Si la reserva cambia (modo edición), actualiza el formulario
    setFormData(initialState);
  }, [currentReservation]);
  
  // Función genérica para manejar cambios en los campos del formulario
  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? parseInt(value) || '' : value,
    }));
    // Limpiar error al empezar a escribir
    if (errors[name]) {
        setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  // Función de validación simple
  const validate = () => {
    let newErrors = {};
    if (!formData.nombreCliente.trim()) newErrors.nombreCliente = 'El nombre del cliente es obligatorio.';
    if (!formData.emailCliente.match(/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/)) newErrors.emailCliente = 'Debe ser un email válido.';
    if (formData.numPersonas < 1 || formData.numPersonas > 10) newErrors.numPersonas = 'Capacidad entre 1 y 10 personas.';
    if (!formData.fechaReserva) newErrors.fechaReserva = 'La fecha es obligatoria.';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validate()) {
      onSave(formData);
    }
  };

  const inputClass = "w-full p-3 border border-gray-300 rounded-lg focus:ring-caribe-teal focus:border-caribe-teal transition duration-150";
  const labelClass = "block text-sm font-medium text-gray-700 mb-1";
  const errorClass = "text-red-500 text-xs mt-1";

  return (
    <div className="max-w-xl mx-auto p-6 bg-white shadow-2xl rounded-xl mt-8">
      <h2 className="text-3xl font-bold text-caribe-teal mb-6 border-b pb-3">
        {isEditing ? 'Editar Reserva ID ' + formData.id : 'Crear Nueva Reserva'}
      </h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        
        {/* Campo Nombre Cliente */}
        <div>
          <label htmlFor="nombreCliente" className={labelClass}>Nombre del Cliente</label>
          <input
            type="text"
            id="nombreCliente"
            name="nombreCliente"
            value={formData.nombreCliente}
            onChange={handleChange}
            placeholder="Ej: Sofía Gómez"
            className={inputClass}
          />
          {errors.nombreCliente && <p className={errorClass}>{errors.nombreCliente}</p>}
        </div>

        {/* Campo Email Cliente */}
        <div>
          <label htmlFor="emailCliente" className={labelClass}>Email</label>
          <input
            type="email"
            id="emailCliente"
            name="emailCliente"
            value={formData.emailCliente}
            onChange={handleChange}
            placeholder="ejemplo@correo.com"
            className={inputClass}
          />
          {errors.emailCliente && <p className={errorClass}>{errors.emailCliente}</p>}
        </div>

        <div className="grid grid-cols-2 gap-4">
          {/* Campo Fecha (DatePicker) */}
          <div>
            <label htmlFor="fechaReserva" className={labelClass}>Fecha</label>
            <input
              type="date"
              id="fechaReserva"
              name="fechaReserva"
              value={formData.fechaReserva}
              onChange={handleChange}
              className={inputClass}
            />
            {errors.fechaReserva && <p className={errorClass}>{errors.fechaReserva}</p>}
          </div>

          {/* Campo Hora (TimePicker) */}
          <div>
            <label htmlFor="horaReserva" className={labelClass}>Hora</label>
            <input
              type="time"
              id="horaReserva"
              name="horaReserva"
              value={formData.horaReserva}
              onChange={handleChange}
              className={inputClass}
            />
          </div>
        </div>

        {/* Campo Número de Personas */}
        <div>
          <label htmlFor="numPersonas" className={labelClass}>Número de Personas</label>
          <input
            type="number"
            id="numPersonas"
            name="numPersonas"
            min="1"
            max="10"
            value={formData.numPersonas}
            onChange={handleChange}
            className={inputClass}
          />
          {errors.numPersonas && <p className={errorClass}>{errors.numPersonas}</p>}
        </div>
        
        {/* Campo Estado (solo visible en modo edición) */}
        {isEditing && (
            <div>
                <label htmlFor="estado" className={labelClass}>Estado de la Reserva</label>
                <select
                    id="estado"
                    name="estado"
                    value={formData.estado}
                    onChange={handleChange}
                    className={inputClass}
                >
                    <option value="Pendiente">Pendiente</option>
                    <option value="Confirmada">Confirmada</option>
                    <option value="Cancelada">Cancelada</option>
                </select>
            </div>
        )}


        <div className="flex justify-end space-x-4 pt-4">
          <Button type="button" color="secondary" onClick={onCancel}>
            Volver
          </Button>
          <Button type="submit" color="gold">
            {isEditing ? 'Guardar Cambios' : 'Confirmar Reserva'}
          </Button>
        </div>
      </form>
    </div>
  );
};


// ==============================================================================
// 5. COMPONENTE PRINCIPAL (AppLayout)
// ==============================================================================

const INITIAL_RESERVATIONS = [
  { id: 1, fechaReserva: '2025-12-05', horaReserva: '20:00', numPersonas: 4, nombreCliente: 'Juan Pérez', emailCliente: 'juan@ejemplo.com', estado: 'Confirmada' },
  { id: 2, fechaReserva: '2025-12-06', horaReserva: '19:30', numPersonas: 2, nombreCliente: 'María Lopez', emailCliente: 'maria@ejemplo.com', estado: 'Pendiente' },
  { id: 3, fechaReserva: '2025-12-07', horaReserva: '18:45', numPersonas: 8, nombreCliente: 'Carlos Ruiz', emailCliente: 'carlos@ejemplo.com', estado: 'Cancelada' },
];

/**
 * App: Componente principal que maneja el estado global y el routing simple.
 * Implementa las operaciones CRUD simuladas en el estado.
 */
export default function App() {
  // Estado de autenticación de Firebase
  const [db, setDb] = useState(null);
  const [auth, setAuth] = useState(null);
  const [userId, setUserId] = useState(null);
  const [isAuthReady, setIsAuthReady] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  // Estado del CRUD (Simulación, reemplazable por Firestore)
  const [reservations, setReservations] = useState(INITIAL_RESERVATIONS);
  const [nextId, setNextId] = useState(INITIAL_RESERVATIONS.length + 1);
  const [view, setView] = useState('list'); // 'list', 'form'
  const [reservationToEdit, setReservationToEdit] = useState(null);
  
  // Estado del Modal de confirmación
  const [modalOpen, setModalOpen] = useState(false);
  const [idToDelete, setIdToDelete] = useState(null);


  // ----------------------------------------------------------------------
  // LÓGICA DE FIREBASE/FIRESTORE (BOILERPLATE)
  // ----------------------------------------------------------------------
  useEffect(() => {
    if (!firebaseConfig) {
      console.warn("Configuración de Firebase no disponible. Ejecutando en modo local (solo estado).");
      setIsAuthReady(true);
      setUserId('DEMO_USER_LOCAL');
      setIsLoading(false);
      return;
    }

    try {
      const app = initializeApp(firebaseConfig);
      const firestore = getFirestore(app);
      const authInstance = getAuth(app);
      setDb(firestore);
      setAuth(authInstance);

      // 1. Manejo de la autenticación
      const unsubscribeAuth = onAuthStateChanged(authInstance, (user) => {
        if (user) {
          setUserId(user.uid);
        } else {
          // Si no está autenticado (debe ocurrir solo en el primer inicio si no hay token)
          signInAnonymously(authInstance).then(anonUser => {
            setUserId(anonUser.user.uid);
          }).catch(error => {
            console.error("Fallo la autenticación anónima:", error);
          });
        }
        setIsAuthReady(true);
      });
      
      // Intento de inicio de sesión con token personalizado (si está disponible)
      if (initialAuthToken) {
          signInWithCustomToken(authInstance, initialAuthToken).catch(error => {
              console.error("Fallo la autenticación con token personalizado:", error);
          });
      }
      
      return () => unsubscribeAuth();
    } catch (error) {
      console.error("Fallo la inicialización de Firebase:", error);
      setIsAuthReady(true); // Continúa en modo local si falla la inicialización
      setIsLoading(false);
    }
  }, []);

  // ----------------------------------------------------------------------
  // LÓGICA CRUD SIMULADA (REEMPLAZAR CON FIRESTORE EN PRODUCCIÓN)
  // ----------------------------------------------------------------------

  /**
   * Maneja la creación (Insert) o actualización (Update) de una reserva.
   * Utiliza useCallback para optimizar la función.
   */
  const handleCreateOrUpdate = useCallback((newReservationData) => {
    // 1. Actualización (Update)
    if (newReservationData.id) {
      setReservations(prev =>
        prev.map(res =>
          res.id === newReservationData.id ? { ...res, ...newReservationData } : res
        )
      );
      console.log(`Reserva ID ${newReservationData.id} actualizada.`);
    } 
    // 2. Creación (Create)
    else {
      const newId = nextId;
      const newReserva = {
        ...newReservationData,
        id: newId,
        estado: 'Pendiente',
      };
      setReservations(prev => [...prev, newReserva]);
      setNextId(prev => prev + 1);
      console.log(`Nueva reserva creada con ID ${newId}.`);
    }

    // Volver a la lista y limpiar la edición
    setView('list');
    setReservationToEdit(null);
  }, [nextId]);

  /**
   * Inicia el proceso de eliminación mostrando el modal de confirmación.
   */
  const confirmDelete = useCallback((id) => {
    setIdToDelete(id);
    setModalOpen(true);
  }, []);

  /**
   * Maneja la eliminación (Delete) después de la confirmación del modal.
   */
  const handleDeleteConfirmed = useCallback(() => {
    if (idToDelete !== null) {
      setReservations(prev => prev.filter(res => res.id !== idToDelete));
      console.log(`Reserva ID ${idToDelete} eliminada.`);
      setIdToDelete(null);
    }
    setModalOpen(false);
  }, [idToDelete]);

  /**
   * Prepara el formulario para editar una reserva existente.
   */
  const handleEdit = useCallback((reserva) => {
    setReservationToEdit(reserva);
    setView('form');
  }, []);

  /**
   * Cancela la edición o creación y regresa a la lista.
   */
  const handleCancelForm = useCallback(() => {
    setReservationToEdit(null);
    setView('list');
  }, []);

  /**
   * Determina qué componente de vista renderizar.
   */
  const renderView = useMemo(() => {
    if (view === 'form') {
      return (
        <ReservationForm
          currentReservation={reservationToEdit}
          onSave={handleCreateOrUpdate}
          onCancel={handleCancelForm}
        />
      );
    }
    // vista 'list'
    return (
      <ReservationList
        reservations={reservations}
        onEdit={handleEdit}
        onDelete={confirmDelete}
        isLoading={!isAuthReady || isLoading}
      />
    );
  }, [view, reservations, reservationToEdit, handleCreateOrUpdate, handleCancelForm, handleEdit, confirmDelete, isAuthReady, isLoading]);


  return (
    // AppLayout: Contenedor principal
    <div className="min-h-screen bg-caribe-off-white font-sans">
      
      <Header onViewChange={setView} userId={userId} />

      <main className="max-w-7xl mx-auto py-8">
        {renderView}
      </main>

      {/* Modal de confirmación de eliminación */}
      <Modal 
        isOpen={modalOpen}
        title="Confirmar Cancelación"
        message={`¿Está seguro que desea cancelar la reserva ID ${idToDelete}? Esta acción no se puede deshacer.`}
        onConfirm={handleDeleteConfirmed}
        onCancel={() => setModalOpen(false)}
      />

      {/* Footer (Simple para este demo) */}
      <footer className="bg-gray-800 text-white text-center p-4 mt-10">
        <p className="text-sm">
          © {new Date().getFullYear()} Restaurante Casa Caribe. Desarrollado con React & Tailwind.
        </p>
      </footer>
    </div>
  );
}