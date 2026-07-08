window.addEventListener("DOMContentLoaded", () => {
    const formulario = document.querySelector("form");

    if (formulario) {
        formulario.addEventListener("submit", (event) => {
            // Captura de datos de tus IDs del HTML
            const nombre = document.getElementById("UsuarioR").value.trim();
            const correo = document.getElementById("email").value.trim();
            const contrasena = document.getElementById("ContraseñaR").value;
            const confirmarContrasena = document.getElementById("ConfirmarR").value;
            const terminos = document.getElementById("terminos").checked;

            // Validaciones básicas obligatorias
            if (nombre === "" || correo === "" || contrasena === "") {
                alert("Por favor, complete todos los campos obligatorios.");
                event.preventDefault();
                return;
            }

            if (contrasena.length < 6) {
                alert("La contraseña debe tener al menos 6 caracteres.");
                event.preventDefault(); 
                return;
            }

            if (contrasena !== confirmarContrasena) {
                alert("Las contraseñas no coinciden.");
                event.preventDefault(); 
                return;
            }

            if (!terminos) {
                alert("Debe aceptar los términos y condiciones.");
                event.preventDefault();
                return;
            }

            // === CONEXIÓN DINÁMICA (SIMULACIÓN DE BASE DE DATOS) ===
            // Traemos los usuarios que ya existan guardados, o creamos un arreglo vacío si es el primero
            let usuariosBD = JSON.parse(localStorage.getItem("usuarios_pancitoduro")) || [];

            // Validamos si el correo ya existe en nuestra base de datos local
            const correoExiste = usuariosBD.find(u => u.email === correo);
            if (correoExiste) {
                alert("Este correo electrónico ya está registrado.");
                event.preventDefault();
                return;
            }

            // Creamos el objeto con la misma estructura de tu tabla de MySQL
            const nuevoUsuario = {
                nombre_usuario: nombre,
                email: correo,
                contrasena: contrasena
            };

            // Agregamos el nuevo usuario al arreglo y lo guardamos de manera persistente
            usuariosBD.push(nuevoUsuario);
            localStorage.setItem("usuarios_pancitoduro", JSON.stringify(usuariosBD));

            alert("¡Registro exitoso en la base de datos! Redirigiendo al Login...");
        });
    }
});