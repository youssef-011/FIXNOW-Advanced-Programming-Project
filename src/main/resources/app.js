const storageKey = "fixnow.currentUser";

function saveCurrentUser(user) {
    localStorage.setItem(storageKey, JSON.stringify(user));
}

function getCurrentUser() {
    const raw = localStorage.getItem(storageKey);
    if (!raw) {
        return null;
    }
    try {
        return JSON.parse(raw);
    } catch (error) {
        localStorage.removeItem(storageKey);
        return null;
    }
}

async function logout() {
    try {
        await fetch("/api/auth/logout", { method: "POST" });
    } catch (error) {
        // Ignore network errors during logout cleanup.
    }
    localStorage.removeItem(storageKey);
    window.location.href = "/login.html";
}

function requireUser(roles) {
    const user = getCurrentUser();
    if (!user) {
        window.location.href = "/login.html";
        throw new Error("No active user");
    }
    if (roles && roles.length > 0 && !roles.includes(user.role)) {
        window.location.href = dashboardForRole(user.role);
        throw new Error("Wrong role");
    }
    return user;
}

function dashboardForRole(role) {
    if (role === "ADMIN") {
        return "/adminDashboard.html";
    }
    if (role === "TECHNICIAN") {
        return "/technicianDashboard.html";
    }
    return "/customerDashboard.html";
}

async function apiFetch(url, options = {}) {
    const response = await fetch(url, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem(storageKey);
        if (!url.startsWith("/api/auth/")) {
            window.location.href = "/login.html";
        }
        throw new Error("Your session has expired or you do not have access.");
    }

    if (!response.ok) {
        let message = "Request failed";
        try {
            const data = await response.json();
            message = data.message || data.error || message;
        } catch (error) {
            message = response.statusText || message;
        }
        throw new Error(message);
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function showFlash(id, message, type = "error") {
    const element = document.getElementById(id);
    if (!element) {
        return;
    }
    element.textContent = message;
    element.className = `flash show ${type}`;
}

function clearFlash(id) {
    const element = document.getElementById(id);
    if (!element) {
        return;
    }
    element.textContent = "";
    element.className = "flash";
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function navMarkup(user) {
    return `
        <div class="topbar">
            <div>
                <div class="brand">FIXNOW</div>
                <div class="muted">Signed in as ${escapeHtml(user.name || user.email)} (${escapeHtml(user.role)})</div>
            </div>
            <div class="actions">
                <a class="button secondary" href="${dashboardForRole(user.role)}">Dashboard</a>
                <button type="button" class="warn" onclick="logout()">Logout</button>
            </div>
        </div>
    `;
}

async function syncCurrentUser() {
    try {
        const user = await apiFetch("/api/auth/me");
        saveCurrentUser(user);
        return user;
    } catch (error) {
        return null;
    }
}
