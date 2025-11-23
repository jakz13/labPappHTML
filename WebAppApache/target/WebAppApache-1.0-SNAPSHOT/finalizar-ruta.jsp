<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Finalizar Ruta de Vuelo</title>
    <style>
        .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
        .ruta-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .ruta-card.finalizable { background-color: #f0fff0; border-color: #4CAF50; }
        .ruta-card.no-finalizable { background-color: #fff0f0; border-color: #f44336; }
        .btn { padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
        .btn-finalizar { background-color: #4CAF50; color: white; }
        .btn-finalizar:disabled { background-color: #cccccc; cursor: not-allowed; }
        .error { color: #f44336; margin: 10px 0; }
        .success { color: #4CAF50; margin: 10px 0; }
        .vuelos-list { margin-left: 20px; }
        .loading { text-align: center; padding: 20px; }
    </style>
</head>
<body>
<div class="container">
    <h1>Finalizar Ruta de Vuelo</h1>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="success">${success}</div>
    </c:if>

    <h2>Rutas Finalizables</h2>
    <div id="rutasFinalizables">
        <div class="loading">Cargando rutas...</div>
    </div>

    <script>
        // Cargar rutas finalizables al cargar la página
        document.addEventListener('DOMContentLoaded', function() {
            cargarRutasFinalizables();
        });

        function cargarRutasFinalizables() {
            const aerolinea = '${sessionScope.usuario.nombre}'; // Ajusta según tu estructura de sesión

            // Mostrar loading
            const container = document.getElementById('rutasFinalizables');
            container.innerHTML = '<div class="loading">Cargando rutas...</div>';

            fetch('/WebAppApache/api/rutas-finalizables?aerolinea=' + encodeURIComponent(aerolinea))
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error en la respuesta del servidor: ' + response.status);
                    }
                    return response.json();
                })
                .then(rutas => {
                    container.innerHTML = '';

                    if (rutas.length === 0) {
                        container.innerHTML = '<p>No hay rutas que puedan ser finalizadas en este momento.</p>';
                        return;
                    }

                    rutas.forEach(ruta => {
                        const rutaDiv = document.createElement('div');
                        rutaDiv.className = 'ruta-card finalizable';
                        rutaDiv.innerHTML = `
                            <h3>\${ruta.nombre}</h3>
                            <p><strong>Ruta:</strong> \${ruta.origen} → \${ruta.destino}</p>
                            <p><strong>Estado:</strong> \${ruta.estado}</p>
                            <p><strong>Costo Turista:</strong> $\${ruta.costoTurista}</p>
                            <p><strong>Costo Ejecutivo:</strong> $\${ruta.costoEjecutivo}</p>
                            \${ruta.vuelos ? `<div class="vuelos-list">
                            <strong>Vuelos:</strong>
                        <ul>
                            \${ruta.vuelos.map(vuelo => `<li>\${vuelo.nombre} - \${vuelo.fecha}</li>`).join('')}
                        </ul>
                    </div>` : ''}
                            <button class="btn btn-finalizar" onclick="finalizarRuta('\${ruta.nombre}')">
                                Finalizar Ruta
                            </button>
                        `;
                        container.appendChild(rutaDiv);
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    container.innerHTML = '<p class="error">Error al cargar las rutas finalizables: ' + error.message + '</p>';
                });
        }

        function finalizarRuta(nombreRuta) {
            if (!confirm('¿Está seguro de que desea finalizar la ruta "' + nombreRuta + '"? Esta acción no se puede deshacer.')) {
                return;
            }

            const formData = new FormData();
            formData.append('nombreRuta', nombreRuta);

            fetch('/WebAppApache/api/finalizar-ruta', {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(result => {
                    if (result.success) {
                        alert('Ruta finalizada exitosamente');
                        cargarRutasFinalizables(); // Recargar la lista
                    } else {
                        alert('Error: ' + result.error);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error al finalizar la ruta');
                });
        }
    </script>
</div>
</body>
</html>