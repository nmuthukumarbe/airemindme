// Dashboard Customer Engagement JS

var msgChartInstance = null;
var currentChartData = [];

function getChartOptions(isDark) {
    return {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: {
            legend: { display: false },
            tooltip: {
                backgroundColor: isDark ? '#1a1a1a' : '#fff',
                titleColor: isDark ? '#f0f0f0' : '#111',
                bodyColor: isDark ? '#909090' : '#64748b',
                borderColor: isDark ? '#2a2a2a' : '#e2e8f0',
                borderWidth: 1, padding: 10, cornerRadius: 8
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                grid: { color: isDark ? '#1e1e1e' : '#f1f5f9', drawBorder: false },
                ticks: { color: isDark ? '#888' : '#94a3b8', font: { size: 10 }, stepSize: 1 }
            },
            x: {
                grid: { display: false, drawBorder: false },
                ticks: { color: isDark ? '#888' : '#94a3b8', font: { size: 10 } }
            }
        }
    };
}

function buildChart(isDark) {
    if (msgChartInstance) { msgChartInstance.destroy(); }
    var ctx = document.getElementById('msgChart');
    if (!ctx) return;
    
    var ctx2d = ctx.getContext('2d');
    
    // Map labels and data from dynamic activity endpoint response
    var labels = currentChartData.map(function(item) {
        var d = new Date(item.date);
        return d.toLocaleDateString(undefined, { weekday: 'short', month: 'short', day: 'numeric' });
    });
    var dataCounts = currentChartData.map(function(item) {
        return item.count;
    });

    // Handle completely empty data scenario gracefully
    var totalActivities = dataCounts.reduce((a, b) => a + b, 0);
    var chartTitleText = document.getElementById('chart-subtitle');
    if (chartTitleText) {
        chartTitleText.textContent = totalActivities + " customer interactions recorded in the last 7 days";
    }

    msgChartInstance = new Chart(ctx2d, {
        type: 'line',
        data: {
            labels: labels.length ? labels : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            datasets: [
                {
                    label: 'Engagement Count',
                    data: dataCounts.length ? dataCounts : [0, 0, 0, 0, 0, 0, 0],
                    borderColor: '#4f46e5',
                    backgroundColor: isDark ? 'rgba(79, 70, 229, 0.05)' : 'rgba(79, 70, 229, 0.08)',
                    tension: 0.4, fill: true, borderWidth: 2.5, pointRadius: 3, pointHoverRadius: 6
                }
            ]
        },
        options: getChartOptions(isDark)
    });
}

function updateChartTheme(theme) {
    buildChart(theme === 'dark');
}

async function loadDashboardStats() {
    try {
        const response = await fetch('/dashboard/stats');
        const data = await response.json();

        document.getElementById('stat-customers').textContent = data.customers;
        document.getElementById('stat-reminders').textContent = data.upcomingReminders;
        document.getElementById('stat-promotions').textContent = data.promotions;
        document.getElementById('stat-scheduled-activities').textContent = data.scheduledActivities;
    } catch (err) {
        console.error('Dashboard stats error:', err);
    }
}

async function loadDashboardActivity() {
    try {
        const response = await fetch('/dashboard/activity');
        currentChartData = await response.json();
        var initTheme = localStorage.getItem('rm_theme') || 'light';
        buildChart(initTheme === 'dark');
    } catch (err) {
        console.error('Dashboard activity error:', err);
    }
}

async function loadRecentActivities() {
    var list = document.getElementById('recentMsgList');
    if (!list) return;

    try {
        const response = await fetch('/dashboard/recent-activities');
        const data = await response.json();

        list.innerHTML = '';

        if (!data || data.length === 0) {
            list.innerHTML = `
                <div class="px-5 py-8 text-center text-gray-500">
                    <svg class="w-12 h-12 text-gray-300 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <p class="text-sm font-medium text-gray-900">No customer activity yet</p>
                    <p class="text-xs text-gray-400 mt-1">Create your first reminder or appointment to start tracking engagement.</p>
                </div>
            `;
            return;
        }

        var chBg = { 
            Reminder: 'bg-blue-50', 
            Greeting: 'bg-emerald-50', 
            Appointment: 'bg-purple-50', 
            Promotion: 'bg-orange-50' 
        };
        var chTxt = { 
            Reminder: 'text-blue-600', 
            Greeting: 'text-emerald-600', 
            Appointment: 'text-purple-600', 
            Promotion: 'text-orange-600' 
        };
        
        // Define SVG icons for each activity type
        var icons = {
            Reminder: `<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/></svg>`,
            Greeting: `<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/></svg>`,
            Appointment: `<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>`,
            Promotion: `<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z"/></svg>`
        };

        data.forEach(function (m) {
            var dateStr = new Date(m.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
            list.innerHTML += `
                <div class="flex items-center gap-3 px-5 py-3 hover:bg-gray-50 transition cursor-pointer">
                    <div class="w-8 h-8 rounded-xl ${chBg[m.type] || 'bg-gray-100'} ${chTxt[m.type] || 'text-gray-600'} flex items-center justify-center flex-shrink-0">
                        ${icons[m.type] || ''}
                    </div>
                    <div class="flex-1 min-w-0">
                        <p class="text-sm font-semibold text-gray-900 truncate">${m.title}</p>
                        <p class="text-xs text-gray-500 truncate">Customer: ${m.customerName}</p>
                    </div>
                    <div class="flex flex-col items-end gap-0.5 flex-shrink-0">
                        <span class="text-[10px] px-2 py-0.5 rounded-full font-medium ${m.status === 'Completed' || m.status === 'Sent' ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'}">${m.status}</span>
                        <p class="text-[10px] text-gray-400">${dateStr}</p>
                    </div>
                </div>
            `;
        });
    } catch (err) {
        console.error('Recent activities load error:', err);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadDashboardStats();
    loadDashboardActivity();
    loadRecentActivities();
});
