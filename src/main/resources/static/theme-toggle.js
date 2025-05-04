document.addEventListener("DOMContentLoaded", () => {
    const themes = ["theme-dark", "theme-light", "theme-solarized"];
    const body = document.body;
    const toggleBtn = document.getElementById("toggle-theme");

    // Detecta qual tema já está aplicado (se houver)
    let current = themes.findIndex(theme => body.classList.contains(theme));

    // Se nenhum tema aplicado, aplica o primeiro da lista
    if (current === -1) {
        current = 0;
        body.classList.add(themes[current]);
    }

    toggleBtn.addEventListener("click", () => {
        body.classList.remove(themes[current]);
        current = (current + 1) % themes.length;
        body.classList.add(themes[current]);
    });
});
