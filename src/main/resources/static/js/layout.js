// ======================================================
// LAYOUT.JS — Global UI Controller
// ======================================================

// ===== MOBILE NAV =====
function openMobileNav() {
    const drawer = document.getElementById("mobileNavDrawer");
    const bg = document.getElementById("mobileNavBg");

    if (drawer) {
        drawer.style.transform = "translateX(0)";
    }

    if (bg) {
        bg.classList.remove("hidden");
    }
}

function closeMobileNav() {
    const drawer = document.getElementById("mobileNavDrawer");
    const bg = document.getElementById("mobileNavBg");

    if (drawer) {
        drawer.style.transform = "translateX(-100%)";
    }

    if (bg) {
        bg.classList.add("hidden");
    }
}


// ===== AUTO ACTIVE NAV (Bottom + Sidebar) =====
document.addEventListener("DOMContentLoaded", () => {
    const currentPage = window.location.pathname.split("/").pop();

    const links = document.querySelectorAll("a[href]");

    links.forEach(link => {
        const href = link.getAttribute("href");

        if (!href) return;

        // Normalize (remove leading /)
        const cleanHref = href.replace("/", "");

        if (cleanHref === currentPage) {

            // Reset inactive style
            link.classList.remove("text-gray-400");
            link.classList.add("text-indigo-600");

            // Optional: highlight parent (sidebar items)
            link.classList.add("bg-indigo-50");

            // Make label bold
            const span = link.querySelector("span");
            if (span) {
                span.classList.add("font-semibold");
            }
        }
    });
});


// ===== CLOSE NAV WHEN CLICK OUTSIDE =====
document.addEventListener("click", function (e) {
    const drawer = document.getElementById("mobileNavDrawer");

    if (!drawer) return;

    const isClickInside = drawer.contains(e.target);
    const isMenuButton = e.target.closest("[onclick='openMobileNav()']");

    if (!isClickInside && !isMenuButton) {
        closeMobileNav();
    }
});


// ===== ESC KEY CLOSE (PRO UX) =====
document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
        closeMobileNav();
    }
});



function toggleTheme() {
    document.documentElement.classList.toggle("dark");
}



console.log("layout.js loaded ✅");