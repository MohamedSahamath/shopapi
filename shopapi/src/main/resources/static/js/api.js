const API = "/api/v1";

function getToken()  { return sessionStorage.getItem("token"); }
function getRoles()  { return JSON.parse(sessionStorage.getItem("roles") || "[]"); }
function getName()   { return sessionStorage.getItem("fullName") || ""; }
function hasRole(r)  { return getRoles().includes("ROLE_" + r); }

function saveSession(data) {
    sessionStorage.setItem("token", data.token);
    sessionStorage.setItem("roles", JSON.stringify(data.roles));
    sessionStorage.setItem("fullName", data.fullName);
}

function clearSession() { sessionStorage.clear(); }

function requireLogin() {
    if (!getToken()) window.location.href = "/index.html";
}

async function request(path, options = {}) {
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    const token = getToken();
    if (token) headers["Authorization"] = "Bearer " + token;

    const res = await fetch(API + path, { ...options, headers });

    if (res.status === 204) return null;

    let body;
    try { body = await res.json(); } catch { body = null; }

    if (!res.ok) {
        if (res.status === 401) {
            clearSession();
            window.location.href = "/index.html";
        }
        const message = body?.message || "Request failed";
        const fields = body?.errors ? " — " + Object.values(body.errors).join(", ") : "";
        throw new Error(message + fields);
    }
    return body.data;
}

const api = {
    get:   (p)    => request(p),
    post:  (p, b) => request(p, { method: "POST",  body: JSON.stringify(b) }),
    put:   (p, b) => request(p, { method: "PUT",   body: JSON.stringify(b) }),
    patch: (p, b) => request(p, { method: "PATCH", body: JSON.stringify(b) }),
    del:   (p)    => request(p, { method: "DELETE" })
};

function showAlert(id, message, type = "error") {
    const el = document.getElementById(id);
    if (!el) return;
    el.className = "alert alert-" + type;
    el.textContent = message;
    el.classList.remove("hidden");
    setTimeout(() => el.classList.add("hidden"), 4000);
}

function money(v) {
    return "LKR " + Number(v || 0).toLocaleString("en-LK",
        { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function stars(n) { return "★".repeat(n) + "☆".repeat(5 - n); }