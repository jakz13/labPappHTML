// JsLogica/session-manager.js
document.addEventListener('DOMContentLoaded', function() {
    console.log('🔧 session-manager.js cargado');
    safeCheckSession();
});

function safeCheckSession() {
    console.log('🔍 Verificando elementos de sesión...');

    const userInfo = document.getElementById('userInfo');
    const loginBtn = document.getElementById('loginBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    // Verificar cada elemento individualmente antes de usarlo
    if (!userInfo) {
        console.warn('⚠️ userInfo no encontrado - puede ser normal en algunas páginas');
        return;
    }

    if (!loginBtn) {
        console.warn('⚠️ loginBtn no encontrado - puede ser normal en algunas páginas');
        // No hacemos return porque quizás solo falta el loginBtn pero tenemos userInfo
    }

    if (!logoutBtn) {
        console.warn('⚠️ logoutBtn no encontrado - puede ser normal en algunas páginas');
        // Continuamos porque quizás solo falta logoutBtn
    }

    console.log('✅ Elementos de sesión encontrados, procediendo...');

    // Solo si tenemos userInfo procedemos
    if (userInfo) {
        const userData = JSON.parse(localStorage.getItem('userData') || sessionStorage.getItem('userData') || 'null');

        if (userData) {
            // Usuario logueado
            console.log('👤 Usuario logueado:', userData.nickname);
            userInfo.innerHTML = `<i class="bi bi-person-circle"></i> ${userData.nickname || userData.email}`;
            safeClassRemove(userInfo, 'd-none');

            if (loginBtn) safeClassAdd(loginBtn, 'd-none');
            if (logoutBtn) safeClassRemove(logoutBtn, 'd-none');
        } else {
            // Usuario no logueado
            console.log('👤 Usuario no logueado - modo invitado');
            userInfo.innerHTML = '<i class="bi bi-person-circle"></i> Invitado';
            safeClassRemove(userInfo, 'd-none');

            if (loginBtn) safeClassRemove(loginBtn, 'd-none');
            if (logoutBtn) safeClassAdd(logoutBtn, 'd-none');
        }
    }
}

// Funciones auxiliares seguras
function safeClassAdd(element, className) {
    if (element && element.classList) {
        element.classList.add(className);
    }
}

function safeClassRemove(element, className) {
    if (element && element.classList) {
        element.classList.remove(className);
    }
}

// Manejo de login (solo si existe el formulario)
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', handleLogin);
    console.log('✅ Formulario de login configurado');
} else {
    console.log('ℹ️ Formulario de login no encontrado - página sin login');
}

// Manejo de logout (solo si existe el botón)
const logoutBtn = document.getElementById('logoutBtn');
if (logoutBtn) {
    logoutBtn.addEventListener('click', handleLogout);
    console.log('✅ Botón de logout configurado');
} else {
    console.log('ℹ️ Botón de logout no encontrado - página sin logout');
}

function handleLogin(event) {
    event.preventDefault();
    console.log('🔐 Procesando login...');
    // ... resto del código de login
}

function handleLogout() {
    console.log('🚪 Procesando logout...');
    // ... resto del código de logout
}

console.log('🎉 session-manager.js cargado completamente');