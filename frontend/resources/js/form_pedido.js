// Arreglo de objetos con precios reales
const catalogoPanes = [
    { nombre: "Hogaza de Masa Madre", precio: 4.50 },
    { nombre: "Pack de 4 Croissants", precio: 6.20 },
    { nombre: "Baguette Tradición", precio: 1.80 }
];

window.addEventListener("DOMContentLoaded", () => {
    // 1. Capturar los elementos del DOM usando tus IDs reales
    const selectProducto = document.getElementById("producto");
    const inputCantidad = document.getElementById("cantidad");
    const textoTotal = document.getElementById("Total");
    const formulario = document.querySelector("form");

    let totalCalculado = 0;

    // Recuperar qué pan seleccionó el cliente en la pantalla anterior
    const panSeleccionadoPreviamente = localStorage.getItem("pan_carrito");

    if (panSeleccionadoPreviamente && selectProducto) {
        for (let i = 0; i < selectProducto.options.length; i++) {
            if (selectProducto.options[i].value === panSeleccionadoPreviamente || 
                selectProducto.options[i].text.includes(panSeleccionadoPreviamente)) {
                selectProducto.selectedIndex = i;
                break;
            }
        }
    }

    // Función matemática para calcular precios
    function actualizarPrecios() {
        if (!selectProducto || !inputCantidad || !textoTotal) return;

        const nombreSeleccionado = selectProducto.value;
        const cantidad = parseInt(inputCantidad.value) || 0;
        let precioUnitario = 0;

        for (let i = 0; i < catalogoPanes.length; i++) {
            if (nombreSeleccionado.includes(catalogoPanes[i].nombre) || catalogoPanes[i].nombre === nombreSeleccionado) {
                precioUnitario = catalogoPanes[i].precio;
                break;
            }
        }

        totalCalculado = precioUnitario * cantidad;
        textoTotal.textContent = `Total a pagar: S/ ${totalCalculado.toFixed(2)}`;
    }

    if (selectProducto && inputCantidad) {
        selectProducto.addEventListener("change", actualizarPrecios);
        inputCantidad.addEventListener("input", actualizarPrecios);
        actualizarPrecios();
    }

    // === NUEVA SECCIÓN DE PAGO CON BOTONES DINÁMICOS ===
    if (formulario) {
        formulario.addEventListener("submit", (event) => {
            event.preventDefault(); // Detiene el envío inmediato

            const cantidad = parseInt(inputCantidad.value) || 0;
            if (cantidad <= 0) {
                alert("Por favor, ingrese una cantidad válida de delicias.");
                return;
            }

            // Comprobamos si ya existe la ventana de pago para no duplicarla
            if (document.getElementById("contenedor-pago")) return;

            // Creamos un contenedor dinámico simulando una interfaz integrada
            const contenedorPago = document.createElement("div");
            contenedorPago.id = "contenedor-pago";
            
            // Aplicamos estilos directamente desde JS (Manipulación de estilos DOM)
            contenedorPago.style.border = "2px solid #b38f4f";
            contenedorPago.style.backgroundColor = "#fff9f0";
            contenedorPago.style.padding = "20px";
            contenedorPago.style.marginTop = "20px";
            contenedorPago.style.borderRadius = "10px";
            contenedorPago.style.textAlign = "center";

            // Estructura interna: Muestra el Importe Total arriba, los botones y el campo dinámico
            contenedorPago.innerHTML = `
                <h3 style="color: #6d4c41;">💳 Proceso de Pago</h3>
                <p style="font-size: 18px; font-weight: bold; color: #d32f2f;">Importe Total: S/ ${totalCalculado.toFixed(2)}</p>
                <p>Seleccione su método de pago:</p>
                
                <div style="margin-bottom: 15px;">
                    <button type="button" id="btn-tarjeta" style="background-color: #b38f4f; color: white; padding: 10px; margin: 5px; border: none; borderRadius: 5px; cursor: pointer;">Tarjeta de Crédito/Débito 💳</button>
                    <button type="button" id="btn-yape" style="background-color: #00bcd4; color: white; padding: 10px; margin: 5px; border: none; borderRadius: 5px; cursor: pointer;">Yape / Plin 📱</button>
                </div>

                <div id="campos-dinamicos" style="margin-top: 15px; min-height: 50px;">
                    </div>

                <button type="button" id="btn-finalizar" style="background-color: #4caf50; color: white; padding: 12px 25px; margin-top: 20px; border: none; font-weight: bold; cursor: pointer; display: none;">Confirmar y Finalizar Compra 🎉</button>
            `;

            // Insertamos el nuevo bloque de pago al final del formulario
            formulario.appendChild(contenedorPago);

            // Capturamos los elementos recién creados
            const btnTarjeta = document.getElementById("btn-tarjeta");
            const btnYape = document.getElementById("btn-yape");
            const camposDinamicos = document.getElementById("campos-dinamicos");
            const btnFinalizar = document.getElementById("btn-finalizar");

            // Evento al elegir Tarjeta
            btnTarjeta.addEventListener("click", () => {
                camposDinamicos.innerHTML = `
                    <label style="display:block; margin-bottom:5px;">Número de Tarjeta:</label>
                    <input type="text" id="nro-tarjeta" placeholder="1234 5678 1234 5678" style="padding:8px; width:80%; text-align:center;" maxlength="16">
                `;
                btnFinalizar.style.display = "inline-block"; // Muestra el botón de cierre
            });

            // Evento al elegir Yape
            btnYape.addEventListener("click", () => {
                camposDinamicos.innerHTML = `
                    <label style="display:block; margin-bottom:5px;">Número de Celular Vinculado:</label>
                    <input type="text" id="nro-celular" placeholder="987 654 321" style="padding:8px; width:80%; text-align:center;" maxlength="9">
                `;
                btnFinalizar.style.display = "inline-block"; // Muestra el botón de cierre
            });

            // Evento definitivo para la Compra Exitosa
            btnFinalizar.addEventListener("click", () => {
                const tarjetaInput = document.getElementById("nro-tarjeta");
                const celularInput = document.getElementById("nro-celular");

                // Condicionales de validación para los nuevos campos
                if (tarjetaInput && tarjetaInput.value.length < 16) {
                    alert("Por favor, ingrese un número de tarjeta válido (16 dígitos).");
                    return;
                }
                if (celularInput && celularInput.value.length < 9) {
                    alert("Por favor, ingrese un número de celular válido (9 dígitos).");
                    return;
                }

                alert(`🎉 ¡COMPRA EXITOSA!\n\nTu pedido por S/ ${totalCalculado.toFixed(2)} ha sido procesado correctamente.\n¡Gracias por comprar en Pancitoduro!`);
                
                // Limpieza y redirección
                localStorage.removeItem("pan_carrito");
                window.location.href = "index.html";
            });
        });
    }
});