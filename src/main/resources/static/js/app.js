/**
 * CampusCart - Client-side Application Logic
 * Pure Vanilla JavaScript for high clarity and student explainability.
 */

// Application State
let allProducts = [];
let currentCategory = 'ALL';
let cart = []; // Array of { product: Product, quantity: number }

// SVG Icons for stationery categories/products
const ICONS = {
    'notebook': `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5Z"/><path d="M6 6h10M6 10h10M6 14h6"/></svg>`,
    'pen-blue': `<svg viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2"><path d="m12 19 7-7 3 3-7 7-3-3z"/><path d="m18 13-1.5-7.5L2 2l3.5 14.5L13 18z"/><path d="m2 2 7.586 7.586"/><circle cx="11" cy="11" r="2"/></svg>`,
    'pen-black': `<svg viewBox="0 0 24 24" fill="none" stroke="#0f172a" stroke-width="2"><path d="m12 19 7-7 3 3-7 7-3-3z"/><path d="m18 13-1.5-7.5L2 2l3.5 14.5L13 18z"/><path d="m2 2 7.586 7.586"/><circle cx="11" cy="11" r="2"/></svg>`,
    'pencil': `<svg viewBox="0 0 24 24" fill="none" stroke="#f59e0b" stroke-width="2"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>`,
    'folder': `<svg viewBox="0 0 24 24" fill="none" stroke="#0284c7" stroke-width="2"><path d="M4 20h16a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.93a2 2 0 0 1-1.66-.9l-.82-1.2A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13c0 1.1.9 2 2 2Z"/></svg>`,
    'ring-folder': `<svg viewBox="0 0 24 24" fill="none" stroke="#7c3aed" stroke-width="2"><rect width="18" height="18" x="3" y="3" rx="2"/><path d="M7 7h.01M7 12h.01M7 17h.01M11 7h6M11 12h6M11 17h6"/></svg>`,
    'calculator': `<svg viewBox="0 0 24 24" fill="none" stroke="#475569" stroke-width="2"><rect width="16" height="20" x="4" y="2" rx="2"/><line x1="8" x2="16" y1="6" y2="6"/><line x1="16" x2="16" y1="14" y2="18"/><path d="M8 10h.01M12 10h.01M16 10h.01M8 14h.01M12 14h.01M8 18h.01M12 18h.01"/></svg>`,
    'sheets': `<svg viewBox="0 0 24 24" fill="none" stroke="#059669" stroke-width="2"><rect width="18" height="18" x="3" y="3" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>`,
    'id-card': `<svg viewBox="0 0 24 24" fill="none" stroke="#0284c7" stroke-width="2"><rect width="18" height="14" x="3" y="5" rx="2"/><circle cx="9" cy="11" r="2"/><path d="M15 9h2M15 13h2M6 17c0-1.5 1.5-2 3-2s3 .5 3 2"/></svg>`,
    'highlighter': `<svg viewBox="0 0 24 24" fill="none" stroke="#eab308" stroke-width="2"><path d="m9 11-6 6v3h3l6-6"/><path d="m22 12-4.6 4.6a2 2 0 0 1-2.8 0l-5.2-5.2a2 2 0 0 1 0-2.8L14 4"/></svg>`
};

// Initialize app when DOM loads
document.addEventListener('DOMContentLoaded', () => {
    loadSavedCart();
    fetchProducts();
});

/**
 * Fetch product catalog from Spring Boot REST API
 */
async function fetchProducts() {
    const grid = document.getElementById('productGrid');
    const countText = document.getElementById('itemCountText');

    try {
        const response = await fetch('/api/products');
        if (!response.ok) {
            throw new Error(`Server returned HTTP ${response.status}`);
        }
        allProducts = await response.json();
        renderProducts();
    } catch (error) {
        console.error('Error fetching products:', error);
        grid.innerHTML = `
            <div class="empty-cart-view" style="grid-column: 1 / -1;">
                <p style="color: var(--danger); font-weight: 600;">Unable to connect to CampusCart backend.</p>
                <p style="font-size: 0.85rem; margin-top: 0.5rem;">Ensure Spring Boot application is running on port 8080.</p>
            </div>
        `;
        countText.innerText = '0 items found';
    }
}

/**
 * Filter products by category
 */
function filterByCategory(category, buttonElement) {
    currentCategory = category;

    // Update active button state
    document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
    if (buttonElement) {
        buttonElement.classList.add('active');
    }

    renderProducts();
}

/**
 * Render product cards onto the grid
 */
function renderProducts() {
    const grid = document.getElementById('productGrid');
    const countText = document.getElementById('itemCountText');

    const filtered = currentCategory === 'ALL'
        ? allProducts
        : allProducts.filter(p => p.category === currentCategory);

    countText.innerText = `Showing ${filtered.length} products`;

    if (filtered.length === 0) {
        grid.innerHTML = `<div class="empty-cart-view" style="grid-column: 1 / -1;"><p>No products found in this category.</p></div>`;
        return;
    }

    grid.innerHTML = filtered.map(product => {
        const iconHtml = ICONS[product.icon] || ICONS['notebook'];
        return `
            <div class="product-card" id="product-card-${product.id}">
                <div>
                    <div class="card-top">
                        <span class="product-badge">${escapeHtml(product.category)}</span>
                        <span class="stock-tag">✓ In Stock</span>
                    </div>

                    <div class="product-visual">
                        ${iconHtml}
                    </div>

                    <div class="product-info">
                        <h4 class="product-name">${escapeHtml(product.name)}</h4>
                        <p class="product-desc">${escapeHtml(product.description)}</p>
                    </div>
                </div>

                <div class="product-action-row">
                    <div class="product-price">₹${product.price.toFixed(2)}</div>
                    <button class="btn btn-primary add-to-cart-btn" id="add-to-cart-${product.id}" onclick="addToCart(${product.id}, this)">
                        + Add to Cart
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

/**
 * Add a product to the cart
 */
function addToCart(productId, buttonElement) {
    const product = allProducts.find(p => p.id === productId);
    if (!product) return;

    const existingIndex = cart.findIndex(item => item.product.id === productId);
    if (existingIndex > -1) {
        cart[existingIndex].quantity += 1;
    } else {
        cart.push({ product, quantity: 1 });
    }

    saveCart();
    updateCartUI();

    // Button feedback
    if (buttonElement) {
        const originalText = buttonElement.innerText;
        buttonElement.innerText = '✓ Added!';
        buttonElement.classList.replace('btn-primary', 'btn-success');
        setTimeout(() => {
            buttonElement.innerText = originalText;
            buttonElement.classList.replace('btn-success', 'btn-primary');
        }, 800);
    }
}

/**
 * Adjust quantity of item in cart
 */
function updateCartQuantity(productId, delta) {
    const itemIndex = cart.findIndex(item => item.product.id === productId);
    if (itemIndex === -1) return;

    cart[itemIndex].quantity += delta;
    if (cart[itemIndex].quantity <= 0) {
        cart.splice(itemIndex, 1);
    }

    saveCart();
    updateCartUI();
}

/**
 * Remove an item from cart
 */
function removeFromCart(productId) {
    cart = cart.filter(item => item.product.id !== productId);
    saveCart();
    updateCartUI();
}

/**
 * Synchronize cart badge, list, and totals
 */
function updateCartUI() {
    const badge = document.getElementById('cartCountBadge');
    const itemsList = document.getElementById('cartItemsList');
    const subtotalEl = document.getElementById('cartSubtotal');
    const grandTotalEl = document.getElementById('cartGrandTotal');
    const footer = document.getElementById('cartFooter');

    const totalQuantity = cart.reduce((sum, item) => sum + item.quantity, 0);
    const subtotal = cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);

    badge.innerText = totalQuantity;

    if (cart.length === 0) {
        itemsList.innerHTML = `
            <div class="empty-cart-view">
                <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="1.5">
                    <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                </svg>
                <p>Your stationery cart is empty.</p>
                <p style="font-size: 0.8rem; margin-top: 0.25rem;">Browse products above and add what you need!</p>
            </div>
        `;
        subtotalEl.innerText = '₹0.00';
        grandTotalEl.innerText = '₹0.00';
        document.getElementById('proceedToCheckoutBtn').disabled = true;
        return;
    }

    document.getElementById('proceedToCheckoutBtn').disabled = false;

    itemsList.innerHTML = cart.map(item => `
        <div class="cart-item" id="cart-item-${item.product.id}">
            <div class="cart-item-details">
                <div class="cart-item-name">${escapeHtml(item.product.name)}</div>
                <div class="cart-item-unit-price">₹${item.product.price.toFixed(2)} each</div>
            </div>
            <div class="cart-item-controls">
                <button class="qty-btn" onclick="updateCartQuantity(${item.product.id}, -1)">−</button>
                <span class="qty-count">${item.quantity}</span>
                <button class="qty-btn" onclick="updateCartQuantity(${item.product.id}, 1)">+</button>
            </div>
            <div class="cart-item-subtotal">₹${(item.product.price * item.quantity).toFixed(2)}</div>
            <button class="remove-btn" title="Remove item" onclick="removeFromCart(${item.product.id})">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="18" y1="6" x2="6" y2="18"></line>
                    <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
            </button>
        </div>
    `).join('');

    subtotalEl.innerText = `₹${subtotal.toFixed(2)}`;
    grandTotalEl.innerText = `₹${subtotal.toFixed(2)}`;
}

/**
 * Toggle cart slide-out drawer
 */
function toggleCartDrawer() {
    const drawer = document.getElementById('cartDrawer');
    const overlay = document.getElementById('cartOverlay');
    drawer.classList.toggle('active');
    overlay.classList.toggle('active');
}

/**
 * Open Checkout Modal
 */
function openCheckoutModal() {
    if (cart.length === 0) return;

    // Close cart drawer
    toggleCartDrawer();

    const totalQuantity = cart.reduce((sum, item) => sum + item.quantity, 0);
    const subtotal = cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);

    document.getElementById('checkoutItemCount').innerText = totalQuantity;
    document.getElementById('checkoutTotalAmount').innerText = `₹${subtotal.toFixed(2)}`;
    document.getElementById('checkoutAlert').style.display = 'none';

    document.getElementById('checkoutModal').classList.add('active');
}

/**
 * Close Checkout Modal
 */
function closeCheckoutModal() {
    document.getElementById('checkoutModal').classList.remove('active');
}

/**
 * Handle checkout form submission and create order via backend API
 */
async function handlePlaceOrder(event) {
    event.preventDefault();

    const confirmBtn = document.getElementById('confirmOrderBtn');
    const alertBox = document.getElementById('checkoutAlert');

    const customerName = document.getElementById('studentName').value.trim();
    const studentId = document.getElementById('studentId').value.trim();
    const email = document.getElementById('studentEmail').value.trim();
    const hostelRoom = document.getElementById('hostelRoom').value.trim();
    const paymentMethod = document.getElementById('paymentMethod').value;

    const items = cart.map(item => ({
        productId: item.product.id,
        quantity: item.quantity
    }));

    const orderPayload = {
        customerName,
        studentId,
        email,
        hostelRoom,
        paymentMethod,
        items
    };

    confirmBtn.disabled = true;
    confirmBtn.innerText = 'Placing Order...';
    alertBox.style.display = 'none';

    try {
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderPayload)
        });

        if (!response.ok) {
            const errData = await response.json();
            throw new Error(errData.error || `HTTP ${response.status}`);
        }

        const createdOrder = await response.json();

        // Clear cart
        cart = [];
        saveCart();
        updateCartUI();

        // Close checkout and open confirmation
        closeCheckoutModal();
        showOrderConfirmation(createdOrder);
    } catch (err) {
        console.error('Failed to place order:', err);
        alertBox.innerText = `Failed to place order: ${err.message}`;
        alertBox.style.display = 'block';
    } finally {
        confirmBtn.disabled = false;
        confirmBtn.innerText = 'Place Order';
    }
}

/**
 * Display Order Confirmation Modal
 */
function showOrderConfirmation(order) {
    document.getElementById('confirmedOrderId').innerText = order.orderId;
    document.getElementById('confirmedCustomerName').innerText = `${order.customerName} (${order.studentId})`;
    document.getElementById('confirmedHostel').innerText = order.hostelRoom;
    document.getElementById('confirmedPaymentMethod').innerText = order.paymentMethod;
    document.getElementById('confirmedTotal').innerText = `₹${order.totalAmount.toFixed(2)}`;

    document.getElementById('orderConfirmModal').classList.add('active');
}

function closeOrderConfirmModal() {
    document.getElementById('orderConfirmModal').classList.remove('active');
}

/**
 * Order Tracking / Lookup Modal
 */
function openOrderLookupModal() {
    document.getElementById('lookupResult').style.display = 'none';
    document.getElementById('lookupOrderIdInput').value = '';
    document.getElementById('orderLookupModal').classList.add('active');
}

function closeOrderLookupModal() {
    document.getElementById('orderLookupModal').classList.remove('active');
}

async function lookupOrder() {
    const input = document.getElementById('lookupOrderIdInput').value.trim();
    const resultBox = document.getElementById('lookupResult');

    if (!input) {
        resultBox.innerHTML = `<p style="color: var(--danger); font-size: 0.9rem;">Please enter an Order ID.</p>`;
        resultBox.style.display = 'block';
        return;
    }

    resultBox.innerHTML = `<p style="color: var(--text-muted); font-size: 0.9rem;">Searching...</p>`;
    resultBox.style.display = 'block';

    try {
        const response = await fetch(`/api/orders/${encodeURIComponent(input)}`);
        if (response.status === 404) {
            resultBox.innerHTML = `<p style="color: var(--danger); font-size: 0.9rem;">Order "${escapeHtml(input)}" not found. Please verify your order ID.</p>`;
            return;
        }
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const order = await response.json();
        resultBox.innerHTML = `
            <div class="confirm-details-box" style="margin-top: 1rem;">
                <div class="detail-row"><span>Status:</span> <strong style="color: var(--success);">${order.status}</strong></div>
                <div class="detail-row"><span>Order ID:</span> <strong>${order.orderId}</strong></div>
                <div class="detail-row"><span>Name:</span> <strong>${escapeHtml(order.customerName)} (${escapeHtml(order.studentId)})</strong></div>
                <div class="detail-row"><span>Location:</span> <strong>${escapeHtml(order.hostelRoom)}</strong></div>
                <div class="detail-row"><span>Items:</span> <strong>${order.items.length} items</strong></div>
                <div class="detail-row"><span>Total:</span> <strong>₹${order.totalAmount.toFixed(2)}</strong></div>
                <div class="detail-row"><span>Payment:</span> <strong>${escapeHtml(order.paymentMethod)}</strong></div>
            </div>
        `;
    } catch (err) {
        resultBox.innerHTML = `<p style="color: var(--danger); font-size: 0.9rem;">Failed to fetch order: ${err.message}</p>`;
    }
}

// Local Storage helpers
function saveCart() {
    try {
        localStorage.setItem('campuscart_items', JSON.stringify(cart));
    } catch (e) {
        // Fallback gracefully
    }
}

function loadSavedCart() {
    try {
        const saved = localStorage.getItem('campuscart_items');
        if (saved) {
            cart = JSON.parse(saved);
            updateCartUI();
        }
    } catch (e) {
        cart = [];
    }
}

// Utility to escape HTML and prevent XSS
function escapeHtml(text) {
    if (!text) return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
