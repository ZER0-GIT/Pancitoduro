window.addEventListener("DOMContentLoaded", () => {
    const formularioLogin = document.querySelector("form");

    if (formularioLogin) {
        formularioLogin.addEventListener("submit", (event) => {
            // Captura de datos usando los IDs de tu login.html ("Usuario" y "Contraseña")
            const usuarioInput = document.getElementById("Usuario").value.trim();
            const contrasenaInput = document.getElementById("Contraseña").value;

            if (usuarioInput === "" || contrasenaInput === "") {
                alert("Por favor, rellene todos los campos.");
                event.preventDefault();
                return;
            }

            // === CONSULTA DINÁMICA A LA BASE DE DATOS LOCAL ===
            // Jalamos la lista de usuarios creados en la página de registro
            const usuariosBD = JSON.parse(localStorage.getItem("usuarios_pancitoduro")) || [];

            // Buscamos si hay algún usuario cuyo correo o nombre coincida con lo ingresado
            const usuarioEncontrado = usuariosBD.find(u => 
                (u.nombre_usuario === usuarioInput || u.email === usuarioInput) && u.contrasena === contrasenaInput
            );

            if (usuarioEncontrado) {
                alert(`¡Acceso concedido! Bienvenido/a de vuelta, ${usuarioEncontrado.nombre_usuario}.`);
                // Aquí el formulario continuará su curso hacia index.html de manera dinámica
            } else {
                alert("Error: Usuario o contraseña incorrectos. Verifique sus datos.");
                event.preventDefault(); // Detiene el ingreso si los datos no existen en la BD
            }
        });
    }
});