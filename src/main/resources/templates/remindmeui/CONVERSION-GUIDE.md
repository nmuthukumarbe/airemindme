# TSX to HTML Conversion Guide

## Overview

This guide documents how the React TSX components were converted to standalone HTML pages.

## Conversion Process

### 1. File Structure
```
Original TSX Structure:
/src/app/
  ├── pages/          (Main page components)
  ├── components/     (Reusable components)
  │   ├── modals/     (Modal dialogs)
  │   ├── ui/         (shadcn UI components)
  │   └── layout/     (Layout components)
  ├── context/        (React Context)
  ├── data/           (Mock data and stores)
  └── routes.ts       (React Router config)

Converted HTML Structure:
/html-exports/
  ├── index.html          (Navigation hub)
  ├── login.html
  ├── register.html
  ├── home.html
  ├── customers.html
  ├── engagement.html
  ├── campaigns.html
  ├── promotions.html
  ├── settings.html
  ├── super-admin.html
  ├── promo-landing.html
  └── README.md
```

### 2. React Features → Vanilla JS Conversion

#### State Management (useState, useContext)
**Before (TSX):**
```tsx
const [email, setEmail] = useState('');
const { currentUser } = useApp();
```

**After (HTML + JS):**
```javascript
let email = '';
const currentUser = JSON.parse(localStorage.getItem('currentUser'));
```

#### Event Handlers
**Before (TSX):**
```tsx
<button onClick={() => handleSubmit()}>Submit</button>
```

**After (HTML + JS):**
```html
<button onclick="handleSubmit()">Submit</button>
```

#### React Router → HTML Links
**Before (TSX):**
```tsx
import { useNavigate, Link } from 'react-router';
const navigate = useNavigate();
navigate('/app/customers');
<Link to="/login">Login</Link>
```

**After (HTML):**
```html
<a href="customers.html">Customers</a>
<button onclick="window.location.href='customers.html'">Go</button>
```

#### Conditional Rendering
**Before (TSX):**
```tsx
{isLoading && <Spinner />}
{items.map(item => <Card key={item.id} {...item} />)}
```

**After (HTML + JS):**
```html
<div id="spinner" class="hidden">Loading...</div>
<div id="itemsContainer"></div>

<script>
if (isLoading) {
  document.getElementById('spinner').classList.remove('hidden');
}

items.forEach(item => {
  const card = `<div class="card">${item.name}</div>`;
  document.getElementById('itemsContainer').innerHTML += card;
});
</script>
```

### 3. Component Libraries → HTML Equivalents

#### shadcn/ui Button
**Before (TSX):**
```tsx
import { Button } from './components/ui/button';
<Button variant="primary" size="lg">Click Me</Button>
```

**After (HTML):**
```html
<button class="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl">
  Click Me
</button>
```

#### shadcn/ui Dialog/Modal
**Before (TSX):**
```tsx
import { Dialog, DialogContent } from './components/ui/dialog';
<Dialog open={isOpen} onClose={closeModal}>
  <DialogContent>...</DialogContent>
</Dialog>
```

**After (HTML + JS):**
```html
<div id="modal" class="hidden fixed inset-0 bg-black/50 flex items-center justify-center">
  <div class="bg-white rounded-2xl p-6">...</div>
</div>

<script>
function openModal() {
  document.getElementById('modal').classList.remove('hidden');
}
function closeModal() {
  document.getElementById('modal').classList.add('hidden');
}
</script>
```

#### Recharts → Chart.js
**Before (TSX):**
```tsx
import { AreaChart, Area, XAxis, YAxis } from 'recharts';
<AreaChart data={data}>
  <Area dataKey="value" />
</AreaChart>
```

**After (HTML + JS):**
```html
<canvas id="myChart"></canvas>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
new Chart(document.getElementById('myChart'), {
  type: 'line',
  data: { labels: [...], datasets: [...] }
});
</script>
```

### 4. Styling Conversion

#### Tailwind CSS Classes (Preserved)
All Tailwind classes are maintained exactly as-is:
```html
<div class="flex items-center gap-3 px-4 py-2.5 rounded-xl bg-slate-50">
```

#### CSS Variables (theme.css → Inline)
**Before (theme.css):**
```css
:root {
  --primary: #030213;
  --foreground: #0f172a;
}
```

**After (Inline in HTML):**
```html
<style>
:root {
  --primary: #030213;
  --foreground: #0f172a;
}
</style>
```

### 5. Icons Conversion

#### Lucide React → Inline SVG
**Before (TSX):**
```tsx
import { Bell, Users, Send } from 'lucide-react';
<Bell className="w-5 h-5" />
```

**After (HTML):**
```html
<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
        d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
</svg>
```

### 6. Data Management

#### Mock Data
**Before (mockData.ts):**
```typescript
export const mockCustomers: Customer[] = [
  { id: '1', name: 'John Doe', ... },
];
```

**After (Inline in HTML):**
```html
<script>
const mockCustomers = [
  { id: '1', name: 'John Doe', email: 'john@example.com' },
];
</script>
```

#### Local Storage for State Persistence
```javascript
// Save state
localStorage.setItem('currentUser', JSON.stringify(user));

// Load state
const user = JSON.parse(localStorage.getItem('currentUser'));

// Clear state
localStorage.removeItem('currentUser');
```

### 7. Form Handling

**Before (TSX):**
```tsx
const [email, setEmail] = useState('');
<input value={email} onChange={(e) => setEmail(e.target.value)} />
```

**After (HTML):**
```html
<input type="email" id="email" />
<script>
const emailInput = document.getElementById('email');
emailInput.addEventListener('input', (e) => {
  const email = e.target.value;
});
</script>
```

### 8. Component Mapping

| TSX Component | HTML Equivalent |
|--------------|----------------|
| `<Button>` | `<button class="...">` |
| `<Input>` | `<input class="...">` |
| `<Card>` | `<div class="bg-white rounded-2xl ...">` |
| `<Badge>` | `<span class="px-2 py-0.5 rounded-full ...">` |
| `<Dialog>` | `<div class="fixed inset-0 ...">` |
| `<Select>` | `<select class="...">` |
| `<Checkbox>` | `<input type="checkbox" class="...">` |
| `<Textarea>` | `<textarea class="...">` |
| `<Tabs>` | `<div>` + JavaScript tab switching |
| `<Dropdown>` | `<div>` + JavaScript toggle |
| `<Tooltip>` | `<div class="relative">` + hover states |

## File-by-File Conversion Status

### ✅ Completed Files

1. **login.html** (from Login.tsx)
   - Form handling
   - Password visibility toggle
   - Demo credentials
   - Mock authentication

2. **register.html** (from Register.tsx)
   - Business registration form
   - Form validation
   - Loading states

3. **home.html** (from Home.tsx)
   - Dashboard layout
   - Stats cards
   - Chart.js integration
   - Recent activity lists
   - Quick actions

4. **index.html** (Navigation hub)
   - Links to all pages
   - Technical documentation
   - Design system overview

### 🔄 Remaining Files to Convert

#### Pages (Priority)
- **customers.html** (from Customers.tsx) - Customer management with card/list views
- **engagement.html** (from Engagement.tsx) - Reminders and greetings tabs
- **campaigns.html** (from Campaigns.tsx) - Campaign creation and management
- **promotions.html** (from Promotions.tsx) - Promotional link creator
- **settings.html** (from SettingsPage.tsx) - Business and gateway settings
- **super-admin.html** (from SuperAdmin.tsx) - Admin dashboard
- **promo-landing.html** (from PromoLandingPage.tsx) - Public promo page

#### Detail Pages
- **customer-detail.html** (from CustomerDetail.tsx)
- **campaign-detail.html** (from CampaignDetail.tsx)
- **greeting-detail.html** (from GreetingDetail.tsx)
- **reminder-detail.html** (from ReminderDetail.tsx)

#### Modals (Can be embedded in parent pages)
- Add Customer Modal (AddCustomerModal.tsx)
- Create Reminder Modal (CreateReminderModal.tsx)
- Send Greeting Modal (SendGreetingModal.tsx)
- Create Promotion Modal (CreatePromotionModal.tsx)
- Bulk Upload Modal (BulkUploadModal.tsx)
- Onboard Business Modal (OnboardBusinessModal.tsx)

#### UI Components (Converted inline as needed)
All shadcn/ui components converted to vanilla HTML + Tailwind CSS

## Testing Checklist

For each converted page:

- [ ] Page loads without errors
- [ ] All styles render correctly
- [ ] Responsive design works (mobile, tablet, desktop)
- [ ] Navigation links work
- [ ] Forms validate properly
- [ ] Modals open/close correctly
- [ ] Charts render (if applicable)
- [ ] Local storage persists data
- [ ] Icons display properly
- [ ] Animations work smoothly
- [ ] Browser console has no errors

## Known Limitations

1. **No Server-Side Logic**: All authentication and data operations are mocked
2. **No Real-Time Updates**: Unlike React, no automatic re-rendering
3. **No Type Safety**: No TypeScript validation
4. **Manual State Management**: No React hooks or context
5. **Limited Code Reusability**: Components duplicated across pages

## Performance Considerations

✅ **Advantages:**
- No build process required
- Smaller file sizes (no React bundle)
- Faster initial load
- No JavaScript framework overhead
- Works without npm/node

⚠️ **Trade-offs:**
- Manual DOM manipulation
- More code duplication
- Less maintainable for large apps
- No component lifecycle management

## Deployment

These HTML files can be deployed to:
- Static file hosting (Netlify, Vercel, GitHub Pages)
- S3 + CloudFront
- Traditional web servers (Apache, Nginx)
- CDN

No build process or server-side rendering required!

## Best Practices Applied

1. **Semantic HTML**: Proper use of tags (nav, header, main, footer)
2. **Accessibility**: ARIA labels, keyboard navigation, focus states
3. **Performance**: Minified CDN resources, lazy loading
4. **SEO**: Meta tags, proper heading hierarchy
5. **Security**: Input sanitization, no inline eval()
6. **Maintainability**: Consistent naming, comments, structured code

## Resources

- [Tailwind CSS CDN](https://tailwindcss.com/docs/installation/play-cdn)
- [Chart.js Documentation](https://www.chartjs.org/docs/latest/)
- [MDN Web Docs](https://developer.mozilla.org/)
- [Lucide Icons](https://lucide.dev/)

---

**Last Updated**: March 11, 2026  
**Conversion Progress**: 4/11 main pages (36%)
