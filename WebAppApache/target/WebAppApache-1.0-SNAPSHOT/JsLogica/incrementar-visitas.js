// Global helper to increment visits for rutas/paquetes before navigation
(function(){
    const contextPath = window.CONTEXT_PATH || '';

    async function incAndNavigate({ tipo, nombre, id, href }){
        try{
            if (tipo === 'ruta' && nombre){
                const url = `${contextPath}/incrementarVisitasRuta?nombreRuta=${encodeURIComponent(nombre)}`;
                // fire-and-wait with short timeout
                const controller = new AbortController();
                const timeout = setTimeout(()=> controller.abort(), 800);
                try{
                    await fetch(url, { method: 'POST', credentials: 'include', signal: controller.signal });
                    // ignore result; best-effort
                }catch(e){ /* no-op */ }
                clearTimeout(timeout);
            } else if (tipo === 'paquete' && (id || nombre)){
                const param = id || nombre;
                const url = `${contextPath}/incrementarVisitasPaquete?id=${encodeURIComponent(param)}`;
                const controller = new AbortController();
                const timeout = setTimeout(()=> controller.abort(), 800);
                try{
                    await fetch(url, { method: 'POST', credentials: 'include', signal: controller.signal });
                }catch(e){ /* no-op */ }
                clearTimeout(timeout);
            }
        }finally{
            // finalmente, navegar
            if (href) window.location.href = href;
        }
    }

    // Attach to links with data-inc-type attribute (delegation)
    document.addEventListener('click', function(e){
        const el = e.target.closest && e.target.closest('a[data-inc-type]');
        if (!el) return;
        // evitar navegación por defecto
        e.preventDefault();
        const tipo = el.getAttribute('data-inc-type');
        const nombre = el.getAttribute('data-inc-nombre');
        const id = el.getAttribute('data-inc-id');
        const href = el.href;
        incAndNavigate({ tipo, nombre, id, href });
    }, false);
})();
