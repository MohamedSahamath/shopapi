requireLogin();
if (!hasRole("ADMIN") && !hasRole("MANAGER")) location.href = "/shop.html";

document.getElementById("navUser").textContent = getName();
document.getElementById("navLogout").onclick = () => { clearSession(); location.href = "/index.html"; };

document.querySelectorAll(".tab").forEach(tab => {
    tab.onclick = () => {
        document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
        tab.classList.add("active");
        const view = tab.dataset.view;
        ["Reports", "Products", "Expenses", "Orders", "Feedback"].forEach(v =>
            document.getElementById("view" + v).classList.toggle("hidden", v !== view));
        if (view === "Products") loadProducts();
        if (view === "Expenses") loadExpenses();
        if (view === "Orders")   loadOrders();
        if (view === "Feedback") loadFeedback();
    };
});

const now = new Date();
document.getElementById("reportMonth").value =
    now.getFullYear() + "-" + String(now.getMonth() + 1).padStart(2, "0");

document.getElementById("btnLoadReport").onclick = loadReport;

async function loadReport() {
    if (!hasRole("ADMIN")) {
        return showAlert("alert", "Only ADMIN can view financial reports");
    }
    try {
        const [year, month] = document.getElementById("reportMonth").value.split("-");

        const s = await api.get(`/reports/monthly-summary?year=${year}&month=${month}`);

        const cls = s.netProfit >= 0 ? "positive" : "negative";
        document.getElementById("statsGrid").innerHTML = `
            <div class="stat"><div class="label">Revenue</div>
                <div class="value">${money(s.revenue)}</div></div>
            <div class="stat"><div class="label">Cost of goods</div>
                <div class="value">${money(s.costOfGoodsSold)}</div></div>
            <div class="stat"><div class="label">Gross profit</div>
                <div class="value">${money(s.grossProfit)}</div></div>
            <div class="stat"><div class="label">Expenses</div>
                <div class="value">${money(s.totalExpenses)}</div></div>
            <div class="stat"><div class="label">Net profit</div>
                <div class="value ${cls}">${money(s.netProfit)}</div></div>
            <div class="stat"><div class="label">Orders</div>
                <div class="value">${s.orderCount}</div></div>`;

        document.getElementById("breakdownTable").innerHTML =
            (s.expenseBreakdown || []).map(b => `
                <tr><td>${b.categoryName}</td><td>${money(b.totalAmount)}</td>
                <td>${b.percentageOfTotal}%</td></tr>`).join("")
            || "<tr><td colspan='3'>No expenses recorded</td></tr>";

        const top = await api.get("/reports/top-products?limit=5");
        document.getElementById("topProductsTable").innerHTML = top.map(p => `
            <tr><td>${p.productName}</td><td>${p.unitsSold}</td>
            <td>${money(p.revenue)}</td><td>${money(p.profit)}</td></tr>`).join("")
            || "<tr><td colspan='4'>No sales yet</td></tr>";

        const low = await api.get("/reports/low-stock");
        document.getElementById("lowStockTable").innerHTML = low.map(p => `
            <tr><td>${p.name}</td><td>${p.sku}</td>
            <td>${p.stockQuantity}</td><td>${p.reorderLevel}</td></tr>`).join("")
            || "<tr><td colspan='4'>All products well stocked</td></tr>";

    } catch (e) { showAlert("alert", e.message); }
}

async function loadProducts() {
    try {
        const [products, categories] = await Promise.all([
            api.get("/products?includeInactive=true"),
            api.get("/categories")
        ]);

        document.getElementById("pCategory").innerHTML =
            categories.map(c => `<option value="${c.id}">${c.name}</option>`).join("");

        document.getElementById("productTable").innerHTML = products.map(p => `
            <tr>
                <td>${p.name}</td><td>${p.sku}</td><td>${money(p.sellingPrice)}</td>
                <td>
                    <input type="number" id="stock-${p.id}" value="${p.stockQuantity}"
                           style="width:80px;margin:0">
                    <button class="small secondary" onclick="updateStock(${p.id})">Save</button>
                </td>
                <td><button class="small danger" onclick="deleteProduct(${p.id})">Remove</button></td>
            </tr>`).join("");
    } catch (e) { showAlert("alert", e.message); }
}

document.getElementById("btnAddProduct").onclick = async () => {
    try {
        await api.post("/products", {
            categoryId:    parseInt(document.getElementById("pCategory").value),
            name:          document.getElementById("pName").value,
            sku:           document.getElementById("pSku").value.toUpperCase(),
            sellingPrice:  parseFloat(document.getElementById("pSelling").value),
            costPrice:     parseFloat(document.getElementById("pCost").value),
            stockQuantity: parseInt(document.getElementById("pStock").value)
        });
        showAlert("alert", "Product added", "success");
        loadProducts();
    } catch (e) { showAlert("alert", e.message); }
};

async function updateStock(id) {
    try {
        await api.patch("/products/" + id + "/stock", {
            stockQuantity: parseInt(document.getElementById("stock-" + id).value)
        });
        showAlert("alert", "Stock updated", "success");
    } catch (e) { showAlert("alert", e.message); }
}

async function deleteProduct(id) {
    try { await api.del("/products/" + id); loadProducts(); }
    catch (e) { showAlert("alert", e.message); }
}

async function loadExpenses() {
    try {
        const [expenses, categories] = await Promise.all([
            api.get("/expenses"),
            api.get("/expenses/categories")
        ]);

        document.getElementById("eCategory").innerHTML =
            categories.map(c => `<option value="${c.id}">${c.name}</option>`).join("");

        document.getElementById("expenseTable").innerHTML = expenses.map(e => `
            <tr><td>${e.expenseDate}</td><td>${e.categoryName}</td>
            <td>${e.description}</td><td>${money(e.amount)}</td></tr>`).join("")
            || "<tr><td colspan='4'>No expenses recorded</td></tr>";
    } catch (e) { showAlert("alert", e.message); }
}

document.getElementById("btnAddExpense").onclick = async () => {
    try {
        await api.post("/expenses", {
            expenseCategoryId: parseInt(document.getElementById("eCategory").value),
            description:       document.getElementById("eDescription").value,
            amount:            parseFloat(document.getElementById("eAmount").value),
            expenseDate:       document.getElementById("eDate").value
        });
        showAlert("alert", "Expense recorded", "success");
        loadExpenses();
    } catch (e) { showAlert("alert", e.message); }
};

async function loadOrders() {
    try {
        const orders = await api.get("/orders");
        document.getElementById("orderTable").innerHTML = orders.map(o => `
            <tr>
                <td>${o.orderNumber}</td><td>${o.customerName}</td>
                <td>${new Date(o.orderDate).toLocaleDateString()}</td>
                <td>${money(o.total)}</td><td>${o.status}</td>
                <td>
                    <select onchange="changeStatus(${o.id}, this.value)">
                        ${["PENDING","PAID","PROCESSING","SHIPPED","COMPLETED","CANCELLED"]
            .map(st => `<option ${st === o.status ? "selected" : ""}>${st}</option>`)
            .join("")}
                    </select>
                </td>
            </tr>`).join("") || "<tr><td colspan='6'>No orders</td></tr>";
    } catch (e) { showAlert("alert", e.message); }
}

async function changeStatus(id, status) {
    try {
        await api.patch("/orders/" + id + "/status", { status });
        showAlert("alert", "Status updated to " + status, "success");
    } catch (e) { showAlert("alert", e.message); }
}

async function loadFeedback() {
    try {
        const list = await api.get("/feedback");
        document.getElementById("feedbackTable").innerHTML = list.map(f => `
            <tr><td>${f.customerName}</td><td>${f.productName}</td>
            <td class="stars">${stars(f.rating)}</td><td>${f.comment || ""}</td></tr>`).join("")
            || "<tr><td colspan='4'>No feedback yet</td></tr>";
    } catch (e) { showAlert("alert", e.message); }
}

loadReport();