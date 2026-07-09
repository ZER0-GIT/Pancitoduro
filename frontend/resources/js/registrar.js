const API_URL = "http://127.0.0.1:8080";

window.addEventListener("DOMContentLoaded", () => {
    const formularioRegistro = document.getElementById("registroForm");

    const nombreInput = document.getElementById("nombre");
    const apellidoInput = document.getElementById("apellido");
    const correoInput = document.getElementById("correo");
    const telefonoInput = document.getElementById("telefono");
    const passwordInput = document.getElementById("password");
    const confirmarPasswordInput = document.getElementById("confirmarPassword");
    const terminosInput = document.getElementById("terminos");

    const botonRegistro = document.getElementById("btnRegistro");
    const mensajeRegistro = document.getElementById("mensajeRegistro");

    formularioRegistro.addEventListener("submit", async (event) => {
        event.preventDefault();

        const nombre = nombreInput.value.trim();
        const apellido = apellidoInput.value.trim();
        const correo = correoInput.value.trim().toLowerCase();
        const telefono = telefonoInput.value.trim();
        const password = passwordInput.value;
        const confirmarPassword = confirmarPasswordInput.value;

        limpiarMensaje();

        if (
            nombre === "" ||
            apellido === "" ||
            correo === "" ||
            telefono === "" ||
            password === "" ||
            confirmarPassword === ""
        ) {
            mostrarMensaje(
                "Por favor, completa todos los campos.",
                "error"
            );
            return;
        }

        if (password.length < 6) {
            mostrarMensaje(
                "La contraseña debe tener al menos 6 caracteres.",
                "error"
            );
            return;
        }

        if (password !== confirmarPassword) {
            mostrarMensaje(
                "Las contraseñas no coinciden.",
                "error"
            );
            return;
        }

        if (!terminosInput.checked) {
            mostrarMensaje(
                "Debes aceptar los términos y la política de privacidad.",
                "error"
            );
            return;
        }

        bloquearFormulario(true);

        try {
            const response = await fetch(
                `${API_URL}/api/auth/registro`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    credentials: "include",

                    body: JSON.stringify({
                        nombre,
                        apellido,
                        correo,
                        password,
                        telefono
                    })
                }
            );

            const data = await obtenerRespuestaJSON(response);

            if (!response.ok) {
                mostrarMensaje(
                    obtenerMensajeError(response, data),
                    "error"
                );

                bloquearFormulario(false);
                return;
            }

            mostrarMensaje(
                "Cuenta creada correctamente. Redirigiendo...",
                "exito"
            );

            setTimeout(() => {
                window.location.href = "login.html";
            }, 1200);

        } catch (error) {
            console.error("Error al registrar usuario:", error);

            mostrarMensaje(
                "No se pudo conectar con el servidor.",
                "error"
            );

            bloquearFormulario(false);
        }
    });

    async function obtenerRespuestaJSON(response) {
        try {
            return await response.json();
        } catch {
            return null;
        }
    }

    function obtenerMensajeError(response, data) {
        if (response.status === 409) {
            return data?.mensaje || "El correo ya está registrado.";
        }

        if (response.status === 400) {
            return data?.mensaje || "Los datos ingresados no son válidos.";
        }

        if (response.status >= 500) {
            return "Ocurrió un error interno en el servidor.";
        }

        return data?.mensaje || "No se pudo crear la cuenta.";
    }

    function mostrarMensaje(mensaje, tipo) {
        mensajeRegistro.textContent = mensaje;
        mensajeRegistro.className = tipo;
    }

    function limpiarMensaje() {
        mensajeRegistro.textContent = "";
        mensajeRegistro.className = "";
    }

    function bloquearFormulario(bloquear) {
        botonRegistro.disabled = bloquear;

        botonRegistro.textContent = bloquear
            ? "Registrando..."
            : "Registrar";
    }
});