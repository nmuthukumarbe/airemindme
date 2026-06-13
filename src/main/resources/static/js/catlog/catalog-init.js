/**
 * catalog-init.js
 */

document.addEventListener('DOMContentLoaded', () => {
    // init tab indicator
    const firstTab = document.getElementById('ct-plans');
    if (firstTab) {
        moveIndicator(firstTab);
    } else {
        const templatesTab = document.getElementById('ct-templates');
        if (templatesTab) {
            switchCatalogTab('templates');
        }
    }

    // load all data from Spring Boot safely
    if (typeof loadPlans === 'function') loadPlans();
    if (typeof loadProducts === 'function') loadProducts();
    if (typeof loadTemplates === 'function') loadTemplates();
    if (typeof loadRTemplates === 'function') loadRTemplates();
    if (typeof loadSummary === 'function') loadSummary();
});
