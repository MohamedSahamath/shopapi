requireLogin();
document.getElementById("navUser").textContent = getName();

let products = [];
let currentFeedbackProductId = null;

document.getElementById("navShop").onclick   = () => showView("Products");
document.getElementById("navCart").onclick   = () => { showView("Cart"); loadCart(); loadAddresses(); };
document.getElementById("navOrders").onclick = () => { showView("Orders"); loadOrders(); };
document.getElementById("navLogout").onclick = () => { clearSession(); location.href = "/index.html"; };
document.getElementById("btnBackToProducts").onclick = () => showView("Products");

function showView(name) {
    ["Products", "Cart", "Orders", "Feedback"].forEach(v =>
        document.getElementById("view" + v).classList.toggle("hidden", v !== name));
}

async function loadProducts() {
    try {
        products = await api.get("/products");
        renderProducts(products);
    } catch (e) { showAlert("alert", e.message); }
}

function renderProducts(list) {
    document.getElementById("productGrid").innerHTML = list.map(p => `
        <div class="product-card">
            <h4>${p.name}</h4>
            <div class="sku">${p.sku} · ${p.categoryName}</div>
            <div class="price">${money(p.sellingPrice)}</div>
            <div class="stock">
                Stock: ${p.stockQuantity}
                ${p.lowStock ? '<span class="badge-low">Low</span>' : ''}
            </div>
            <input type="number" id="qty-${p.id}" value="1" min="1" max="${p.stockQuantity}">
            <button class="small" onclick="addToCart(${p.id})"
                ${p.stockQuantity === 0 ? "disabled" : ""}>Add to cart</button>
            <button class="small secondary" onclick="openFeedback(${p.id}, '${p.name}')">
                Reviews</button>
        </div>`).join("");
}

document.getElementById("searchBox").oninput = (e) => {
    const term = e.target.value.toLowerCase();
    renderProducts(products.filter(p => p.name.toLowerCase().includes(term)));
};

async function addToCart(productId) {
    try {
        const qty = parseInt(document.getElementById("qty-" + productId).value);
        const cart = await api.post("/cart/items", { productId, quantity: qty });
        document.getElementById("cartCount").textContent = cart.totalItems;
        showAlert("alert", "Added to cart", "success");
    } catch (e) { showAlert("alert", e.message); }
}

async function loadCart() {
    try {
        const cart = await api.get("/cart");
        document.getElementById("cartCount").textContent = cart.totalItems;

        document.getElementById("cartTable").innerHTML = cart.items.length === 0
            ? "<tr><td>Your cart is empty</td></tr>"
            : cart.items.map(i => `
                <tr>
                    <td>${i.productName}<br><small>${i.sku}</small></td>
                    <td>${money(i.unitPrice)} × ${i.quantity}</td>
                    <td>${money(i.lineTotal)}</td>
                    <td><button class="danger small" onclick="removeItem(${i.id})">Remove</button></td>
                </tr>`).join("");

        document.getElementById("cartTotal").textContent = "Subtotal: " + money(cart.subtotal);
    } catch (e) { showAlert("alert", e.message); }
}

async function removeItem(itemId) {
    try { await api.del("/cart/items/" + itemId); loadCart(); }
    catch (e) { showAlert("alert", e.message); }
}

async function loadAddresses() {
    try {
        const list = await api.get("/addresses");
        document.getElementById("addressSelect").innerHTML = list.length === 0
            ? "<option value=''>Add an address below</option>"
            : list.map(a => `<option value="${a.id}">
                ${a.line1}, ${a.city}${a.isDefault ? " (default)" : ""}</option>`).join("");
    } catch (e) { showAlert("alert", e.message); }
}

document.getElementById("btnAddAddress").onclick = async () => {
    try {
        await api.post("/addresses", {
            line1:      document.getElementById("addrLine1").value,
            city:       document.getElementById("addrCity").value,
            postalCode: document.getElementById("addrPostal").value,
            isDefault:  true
        });
        showAlert("alert", "Address saved", "success");
        loadAddresses();
    } catch (e) { showAlert("alert", e.message); }
};

document.getElementById("btnCheckout").onclick = async () => {
    try {
        const addressId = parseInt(document.getElementById("addressSelect").value);
        if (!addressId) return showAlert("alert", "Please select a delivery address");

        const order = await api.post("/orders/checkout", { addressId });

        const payment = await api.post("/payments", {
            orderId: order.id,
            method: document.getElementById("payMethod").value,
            cardLastFour: document.getElementById("cardLast4").value || undefined
        });

        alert(payment.message
            + "\n\nOrder: " + order.orderNumber
            + "\nAmount: " + money(payment.amount)
            + "\nReference: " + payment.transactionReference);

        loadCart();
        loadProducts();
        showView("Orders");
        loadOrders();
    } catch (e) { showAlert("alert", e.message); }
};

async function loadOrders() {
    try {
        const orders = await api.get("/orders/my");
        document.getElementById("ordersList").innerHTML = orders.length === 0
            ? "<div class='card'>No orders yet</div>"
            : orders.map(o => `
                <div class="card">
                    <h3>${o.orderNumber} <small>(${o.status})</small></h3>
                    <p>${new Date(o.orderDate).toLocaleString()}</p>
                    <p>${o.deliveryAddress}</p>
                    <table>${o.items.map(i => `
                        <tr><td>${i.productName}</td><td>×${i.quantity}</td>
                        <td>${money(i.lineTotal)}</td></tr>`).join("")}</table>
                    <h3 style="margin-top:10px">Total: ${money(o.total)}</h3>
                    ${o.status === "PENDING"
                ? `<button class="danger small" onclick="cancelOrder(${o.id})">Cancel</button>`
                : ""}
                </div>`).join("");
    } catch (e) { showAlert("alert", e.message); }
}

async function cancelOrder(id) {
    try {
        await api.patch("/orders/" + id + "/cancel", {});
        showAlert("alert", "Order cancelled", "success");
        loadOrders();
        loadProducts();
    } catch (e) { showAlert("alert", e.message); }
}

async function openFeedback(productId, productName) {
    currentFeedbackProductId = productId;
    document.getElementById("feedbackTitle").textContent = "Reviews — " + productName;
    showView("Feedback");
    await loadFeedback();
}

async function loadFeedback() {
    try {
        const list = await api.get("/feedback/product/" + currentFeedbackProductId);
        document.getElementById("feedbackList").innerHTML = list.length === 0
            ? "<p>No reviews yet. Be the first.</p>"
            : list.map(f => `
                <div style="border-bottom:1px solid #e4e7eb;padding:10px 0">
                    <strong>${f.customerName}</strong>
                    <span class="stars">${stars(f.rating)}</span>
                    <p>${f.comment || ""}</p>
                    <small>${new Date(f.createdAt).toLocaleDateString()}</small>
                </div>`).join("");
    } catch (e) { showAlert("alert", e.message); }
}

document.getElementById("btnSubmitFeedback").onclick = async () => {
    try {
        await api.post("/feedback", {
            productId: currentFeedbackProductId,
            rating: parseInt(document.getElementById("fbRating").value),
            comment: document.getElementById("fbComment").value
        });
        document.getElementById("fbComment").value = "";
        showAlert("alert", "Feedback submitted", "success");
        loadFeedback();
    } catch (e) { showAlert("alert", e.message); }
};

loadProducts();
loadCart();