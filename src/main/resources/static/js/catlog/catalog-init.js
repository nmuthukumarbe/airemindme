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

    // Check if source param is present (redirected from AI assistant studio flow)
    const params = new URLSearchParams(window.location.search);
    const source = params.get('source');
    if (source) {
        if (typeof openTemplateModal === 'function') {
            setTimeout(() => {
                openTemplateModal(null);
                const purposeSelect = document.getElementById('tm_purpose');
                if (purposeSelect) {
                    if (source === 'reminder') {
                        purposeSelect.value = 'Reminder';
                    } else if (source === 'greeting') {
                        purposeSelect.value = 'Greeting';
                    }
                    if (typeof onPurposeChange === 'function') {
                        onPurposeChange();
                    }
                }
            }, 400);
        }
    }
});
