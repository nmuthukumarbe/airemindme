// Main application JavaScript
class ReminderMeApp {
  constructor() {
    this.currentUser = null;
    this.isAuthenticated = false;
    this.init();
  }

  init() {
    //this.checkAuthentication();
    this.bindEvents();
    //this.loadUserData();
  }

  // Authentication methods
  checkAuthentication() {
    const authStatus = localStorage.getItem("reminderMe_authenticated");
    this.isAuthenticated = authStatus === "true";

    // Redirect based on current page and auth status
    const currentPage =
      window.location.pathname.split("/").pop() || "index.html";

    if (
      this.isAuthenticated &&
      (currentPage === "signin.html" || currentPage === "signup.html")
    ) {
      window.location.href = "dashboard.html";
    } else if (!this.isAuthenticated && currentPage === "dashboard.html") {
      window.location.href = "signin.html";
    }
  }

  loadUserData() {
    const userData = localStorage.getItem("reminderMe_user");
    if (userData) {
      this.currentUser = JSON.parse(userData);
    }
  }

  signOut() {
    localStorage.removeItem("reminderMe_authenticated");
    localStorage.removeItem("reminderMe_loginTime");
    this.isAuthenticated = false;
    this.currentUser = null;
    window.location.href = "index.html";
  }

  // Utility methods
  showNotification(message, type = "info", duration = 5000) {
    // Remove existing notifications
    const existing = document.querySelectorAll(".notification");
    existing.forEach((notification) => notification.remove());

    // Create notification
    const notification = document.createElement("div");
    notification.className = `notification fixed top-4 right-4 z-50 max-w-sm p-4 rounded-lg shadow-lg transform transition-transform duration-300 translate-x-full`;

    // Set color based on type
    const colors = {
      success: "bg-green-500 text-white",
      error: "bg-red-500 text-white",
      warning: "bg-yellow-500 text-white",
      info: "bg-blue-500 text-white",
    };

    notification.className += ` ${colors[type] || colors.info}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    // Animate in
    setTimeout(() => {
      notification.classList.remove("translate-x-full");
    }, 100);

    // Auto remove
    setTimeout(() => {
      notification.classList.add("translate-x-full");
      setTimeout(() => notification.remove(), 300);
    }, duration);
  }

  formatPhone(phone) {
    const cleaned = phone.replace(/\D/g, "");
    const match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
    if (match) {
      return `(${match[1]}) ${match[2]}-${match[3]}`;
    }
    return phone;
  }

  validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  }

  validatePhone(phone) {
    const cleaned = phone.replace(/\D/g, "");
    return cleaned.length >= 10;
  }

  // Event binding
  bindEvents() {
    // Smooth scrolling for anchor links
    document.addEventListener("click", (e) => {
      if (e.target.matches('a[href^="#"]')) {
        e.preventDefault();
        const href = e.target.getAttribute("href");
        // Check if href is valid and not just "#"
        if (href && href.length > 1 && href !== "#") {
          try {
            const target = document.querySelector(href);
            if (target) {
              target.scrollIntoView({ behavior: "smooth" });
            }
          } catch (error) {
            console.warn("Invalid selector:", href, error);
          }
        }
      }
    });

    // Close dropdowns when clicking outside
    document.addEventListener("click", (e) => {
      if (!e.target.closest(".dropdown-container")) {
        document.querySelectorAll(".dropdown-menu").forEach((menu) => {
          menu.classList.add("hidden");
        });
      }
    });
  }

  // Animation utilities
  animateNumber(element, start, end, duration = 1000) {
    const startTime = performance.now();

    const animate = (currentTime) => {
      const elapsed = currentTime - startTime;
      const progress = Math.min(elapsed / duration, 1);
      const current = Math.floor(start + (end - start) * progress);

      element.textContent = this.formatNumber(current);

      if (progress < 1) {
        requestAnimationFrame(animate);
      }
    };

    requestAnimationFrame(animate);
  }

  formatNumber(num) {
    return new Intl.NumberFormat().format(num);
  }

  // Form validation
  validateForm(formElement) {
    const inputs = formElement.querySelectorAll("input[required]");
    let isValid = true;

    inputs.forEach((input) => {
      if (!input.value.trim()) {
        this.showFieldError(input, "This field is required");
        isValid = false;
      } else {
        this.clearFieldError(input);
      }
    });

    return isValid;
  }

  showFieldError(input, message) {
    input.classList.add("border-red-500");

    // Remove existing error
    const existingError = input.parentNode.querySelector(".field-error");
    if (existingError) {
      existingError.remove();
    }

    // Add error message
    const errorEl = document.createElement("p");
    errorEl.className = "field-error text-red-500 text-sm mt-1";
    errorEl.textContent = message;
    input.parentNode.appendChild(errorEl);
  }

  clearFieldError(input) {
    input.classList.remove("border-red-500");
    const error = input.parentNode.querySelector(".field-error");
    if (error) {
      error.remove();
    }
  }

  // Loading states
  setLoading(button, isLoading) {
    if (isLoading) {
      button.disabled = true;
      button.dataset.originalText = button.textContent;
      button.innerHTML = `
        <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white inline" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Loading...
      `;
    } else {
      button.disabled = false;
      button.textContent = button.dataset.originalText || button.textContent;
    }
  }
}

// Password toggle functionality
function togglePassword(inputId) {
  const input = document.getElementById(inputId);
  const icon = document.getElementById(inputId + "-icon");

  if (input.type === "password") {
    input.type = "text";
    if (icon) {
      icon.innerHTML = `
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21"></path>
      `;
    }
  } else {
    input.type = "password";
    if (icon) {
      icon.innerHTML = `
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
      `;
    }
  }
}

// Mobile menu toggle
function toggleMobileMenu() {
  const menu = document.getElementById("mobile-menu");
  if (menu) {
    menu.classList.toggle("hidden");
  }
}

// Initialize app when DOM is loaded
document.addEventListener("DOMContentLoaded", () => {
  window.app = new ReminderMeApp();
});

// Export for module environments
if (typeof module !== "undefined" && module.exports) {
  module.exports = ReminderMeApp;
}
