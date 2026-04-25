(function () {
    "use strict";

    var root = document.documentElement;

    function setPressed(buttons, activeValue, attribute) {
        buttons.forEach(function (button) {
            button.setAttribute("aria-pressed", String(button.getAttribute(attribute) === activeValue));
        });
    }

    function setupMobileNavigation() {
        var toggle = document.querySelector("[data-nav-toggle]");
        var nav = document.querySelector("[data-primary-nav]");

        if (!toggle || !nav) {
            return;
        }

        toggle.addEventListener("click", function () {
            var isOpen = nav.classList.toggle("is-open");
            toggle.setAttribute("aria-expanded", String(isOpen));
        });
    }

    function setupThemeToggle() {
        var buttons = Array.prototype.slice.call(document.querySelectorAll("[data-theme-choice]"));
        var savedTheme = localStorage.getItem("fixnow-theme") || "light";

        root.setAttribute("data-theme", savedTheme);
        setPressed(buttons, savedTheme, "data-theme-choice");

        buttons.forEach(function (button) {
            button.addEventListener("click", function () {
                var theme = button.getAttribute("data-theme-choice");
                root.setAttribute("data-theme", theme);
                localStorage.setItem("fixnow-theme", theme);
                setPressed(buttons, theme, "data-theme-choice");
            });
        });
    }

    function setupLanguageToggle() {
        var buttons = Array.prototype.slice.call(document.querySelectorAll("[data-lang-choice]"));
        var savedLanguage = localStorage.getItem("fixnow-language") || "en";

        root.setAttribute("lang", savedLanguage);
        root.setAttribute("dir", savedLanguage === "ar" ? "rtl" : "ltr");
        root.setAttribute("data-language", savedLanguage);
        setPressed(buttons, savedLanguage, "data-lang-choice");

        buttons.forEach(function (button) {
            button.addEventListener("click", function () {
                var language = button.getAttribute("data-lang-choice");
                root.setAttribute("lang", language);
                root.setAttribute("dir", language === "ar" ? "rtl" : "ltr");
                root.setAttribute("data-language", language);
                localStorage.setItem("fixnow-language", language);
                setPressed(buttons, language, "data-lang-choice");
            });
        });
    }

    function setupRatingOutput() {
        var output = document.querySelector("[data-rating-output]");
        var inputs = Array.prototype.slice.call(document.querySelectorAll("[data-rating-input]"));

        if (!output || inputs.length === 0) {
            return;
        }

        inputs.forEach(function (input) {
            input.addEventListener("change", function () {
                output.textContent = input.value + " / 5";
            });
        });
    }

    function setupPasswordReveal() {
        document.querySelectorAll("[data-password-toggle]").forEach(function (button) {
            var target = document.querySelector(button.getAttribute("data-password-toggle"));

            if (!target) {
                return;
            }

            button.addEventListener("click", function () {
                var showPassword = target.type === "password";
                target.type = showPassword ? "text" : "password";
                button.textContent = showPassword ? "Hide" : "Show";
            });
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        setupMobileNavigation();
        setupThemeToggle();
        setupLanguageToggle();
        setupRatingOutput();
        setupPasswordReveal();
    });
}());
