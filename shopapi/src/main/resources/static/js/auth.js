document.getElementById("tabLogin").onclick = () => switchTab(true);
document.getElementById("tabRegister").onclick = () => switchTab(false);

function switchTab(login) {
    document.getElementById("tabLogin").classList.toggle("active", login);
    document.getElementById("tabRegister").classList.toggle("active", !login);
    document.getElementById("loginForm").classList.toggle("hidden", !login);
    document.getElementById("registerForm").classList.toggle("hidden", login);
}

document.getElementById("btnLogin").onclick = async () => {
    try {
        const data = await api.post("/auth/login", {
            email: document.getElementById("loginEmail").value,
            password: document.getElementById("loginPassword").value
        });
        saveSession(data);
        window.location.href = (hasRole("ADMIN") || hasRole("MANAGER"))
            ? "/admin.html" : "/shop.html";
    } catch (e) { showAlert("alert", e.message); }
};

document.getElementById("btnRegister").onclick = async () => {
    try {
        const data = await api.post("/auth/register", {
            fullName: document.getElementById("regName").value,
            email:    document.getElementById("regEmail").value,
            password: document.getElementById("regPassword").value,
            nic:      document.getElementById("regNic").value,
            phone:    document.getElementById("regPhone").value
        });
        saveSession(data);
        window.location.href = "/shop.html";
    } catch (e) { showAlert("alert", e.message); }
};