package com.ricardo;

import java.util.ArrayList;
import java.util.List;

public class IndexTemplate {

    public static String getIndexPage(List<ProductData> originalProducts) {
        StringBuilder itemsHtml = new StringBuilder();
        StringBuilder searchData = new StringBuilder();
        StringBuilder sidebarHtml = new StringBuilder();

        List<ProductData> fullList = new ArrayList<>(originalProducts);
        while (fullList.size() < 6) fullList.addAll(originalProducts);

        for (int i = 0; i < fullList.size(); i++) {
            ProductData p = fullList.get(i);
            String uniqueId = "product-" + p.slug + "-" + i;
            
            itemsHtml.append("<div class='item' id='").append(uniqueId).append("' ")
                     .append("data-slug='").append(p.slug).append("' ")
                     .append("onclick='slideToProduct(\"").append(p.slug).append("\")' ") 
                     .append("style='background-image: url(").append(p.imageUrl).append(");'>")
                     .append("<div class='content'>")
                     .append("<div class='name'>").append(p.brand.toUpperCase()).append("</div>")
                     .append("<div class='des'>").append(p.model).append("</div>")
                     .append("<div class='short-desc'>").append(p.tagline).append("</div>")
                     .append("<div class='price'>$").append(p.price).append("</div>")
                     .append("<button onclick='navigateTo(\"").append(p.slug).append(".html\")'>Ver Detalles</button>")
                     .append("</div>")
                     .append("<div class='mini-label'>").append(p.model).append("</div>")
                     .append("</div>");
        }
        
        for (ProductData p : originalProducts) {
             String jsObj = "{name: \"" + p.brand + " " + p.model + "\", slug: \"" + p.slug + "\", img: \"" + p.imageUrl + "\"},";
             searchData.append(jsObj);
             sidebarHtml.append("<div class='sidebar-item' onclick='warpToProduct(\"").append(p.slug).append("\")'>")
                        .append(p.model)
                        .append("</div>");
        }

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Catálogo Luxury 2026</title>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
                <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@400;700&family=Playfair+Display:ital,wght@0,400;0,700;1,400&display=swap" rel="stylesheet">
                <style>
                    body { margin: 0; background: #050505; font-family: 'Playfair Display', serif; overflow: hidden; }
                    .container { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 100vw; height: 100vh; background: #000; overflow: hidden; }
                    :root { --gold: #d4af37; --gold-light: #f3e5ab; }

                    #page-transition {
                        position: fixed; top: 0; left: 0; width: 100%; height: 100%;
                        background: #000; z-index: 9999; pointer-events: none;
                        opacity: 1; transition: opacity 0.8s ease;
                    }

                    .slide { transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1); opacity: 1; filter: blur(0px); transform: scale(1); width: 100%; height: 100%; }
                    .slide.teleport-effect { opacity: 0.4; filter: blur(30px); transform: scale(0.95); }
                    .slide .item { width: 200px; height: 300px; position: absolute; top: 50%; transform: translate(0, -50%); border-radius: 5px; box-shadow: 0 30px 50px rgba(0,0,0,0.8); background-position: 50% 50%; background-size: cover; display: inline-block; transition: 0.5s; z-index: 10; border: 1px solid rgba(255,255,255,0.1); }
                    .slide .item:nth-child(n+3):hover { transform: translate(0, -50%) scale(1.05); cursor: pointer; z-index: 100; box-shadow: 0 0 25px var(--gold), 0 20px 40px rgba(0,0,0,0.9); border: 1px solid var(--gold); }
                    .slide .item:nth-child(1), .slide .item:nth-child(2) { top: 0; left: 0; transform: translate(0, 0); border-radius: 0; width: 100%; height: 100%; box-shadow: none; z-index: 1; border: none; }
                    .slide .item:nth-child(1)::after, .slide .item:nth-child(2)::after { content: ''; position: absolute; top:0; left:0; width:100%; height:100%; background: linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.8)); }
                    .slide .item:nth-child(3) { left: 50%; z-index: 20; }
                    .slide .item:nth-child(4) { left: calc(50% + 220px); z-index: 19; }
                    .slide .item:nth-child(5) { left: calc(50% + 440px); z-index: 18; }
                    .slide .item:nth-child(n+6) { left: calc(50% + 660px); opacity: 0; }
                    .item .content { position: absolute; top: 50%; left: 100px; width: 500px; text-align: left; color: #fff; transform: translate(0, -50%); display: none; z-index: 30; }
                    .slide .item:nth-child(2) .content { display: block; }
                    .content .name { font-family: 'Cinzel', serif; font-size: 70px; font-weight: 700; letter-spacing: 5px; color: var(--gold); opacity: 0; animation: show 1s ease-in-out 1 forwards; text-shadow: 0 5px 15px rgba(0,0,0,0.5); margin-bottom: 0; }
                    .content .des { font-family: 'Playfair Display', serif; font-size: 35px; font-style: italic; opacity: 0; animation: show 1s ease-in-out 0.3s 1 forwards; color: #fff; margin-bottom: 15px; }
                    .content .short-desc { font-family: 'Segoe UI', sans-serif; font-size: 13px; color: #ccc; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 25px; opacity: 0; animation: show 1s ease-in-out 0.4s 1 forwards; border-left: 2px solid var(--gold); padding-left: 15px; }
                    .content .price { font-family: 'Cinzel', serif; font-size: 45px; color: #fff; margin-bottom: 35px; opacity: 0; animation: show 1s ease-in-out 0.5s 1 forwards; }
                    .content button { padding: 15px 40px; border: 1px solid var(--gold); background: transparent; color: var(--gold); font-family: 'Cinzel', serif; font-size: 14px; letter-spacing: 2px; opacity: 0; animation: show 1s ease-in-out 0.6s 1 forwards; cursor: pointer; transition: 0.4s; text-transform: uppercase; }
                    .content button:hover { background: var(--gold); color: #000; box-shadow: 0 0 20px var(--gold); }
                    @keyframes show { from { opacity: 0; transform: translate(0, 50px); filter: blur(20px); } to { opacity: 1; transform: translate(0, 0); filter: blur(0); } }
                    .mini-label { position: absolute; bottom: -40px; left: 0; width: 100%; text-align: center; color: var(--gold); font-family: 'Cinzel', serif; font-size: 12px; letter-spacing: 2px; opacity: 0; transition: 0.5s; }
                    .slide .item:nth-child(3) .mini-label, .slide .item:nth-child(4) .mini-label, .slide .item:nth-child(5) .mini-label { opacity: 1; bottom: -30px; }
                    
                    /* BOTONES INFERIORES */
                    .buttons { position: absolute; bottom: 30px; z-index: 100; text-align: center; width: 100%; }
                    .buttons button { position: relative; width: 60px; height: 60px; border-radius: 50%; border: 1px solid rgba(255,255,255,0.2); transition: 0.3s; cursor: pointer; background: rgba(0,0,0,0.5); color: white; margin: 0 10px; }
                    .buttons button:hover { background: var(--gold); color: #000; border-color: var(--gold); }
                    
                    /* 🔥 INDICADOR VISUAL TECLADO (El Circulito) */
                    .buttons button.key-selected {
                        background: var(--gold); color: #000; border-color: var(--gold);
                        box-shadow: 0 0 20px var(--gold); transform: scale(1.1);
                    }
                    /* Pequeño punto indicador encima del botón seleccionado */
                    .buttons button.key-selected::after {
                        content: ''; position: absolute; top: -15px; left: 50%; transform: translateX(-50%);
                        width: 8px; height: 8px; background: var(--gold); border-radius: 50%;
                        box-shadow: 0 0 10px var(--gold);
                    }

                    .top-bar { position: absolute; top: 30px; right: 50px; z-index: 1000; display: flex; gap: 20px; align-items: center; }
                    .search-box { display: flex; align-items: center; background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(10px); padding: 10px 20px; border-bottom: 1px solid var(--gold); }
                    .search-box input { background: transparent; border: none; outline: none; color: white; width: 200px; margin-left: 10px; font-family: 'Playfair Display', serif; }
                    .search-box i { color: var(--gold); }
                    .menu-wrapper { position: relative; display: inline-block; padding-bottom: 20px; margin-bottom: -20px; }
                    .quick-btn { width: 50px; height: 50px; border-radius: 0; border: 1px solid var(--gold); background: transparent; color: var(--gold); cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.4s; font-size: 20px; }
                    .menu-wrapper:hover .quick-btn { background: var(--gold); color: #000; }
                    
                    /* SIDEBAR */
                    .sidebar-menu { position: fixed; top: 0; right: -400px; width: 350px; height: 100vh; background: #fff; border-left: 3px solid var(--gold); transition: right 0.5s cubic-bezier(0.77, 0, 0.175, 1); padding-top: 120px; overflow-y: auto; z-index: 990; box-shadow: -10px 0 50px rgba(0,0,0,0.5); }
                    .menu-wrapper:hover .sidebar-menu { right: 0; }
                    
                    .sidebar-header { padding: 0 40px 30px; color: #999; font-size: 11px; letter-spacing: 3px; font-family: 'Segoe UI', sans-serif; text-transform: uppercase; border-bottom: 1px solid #eee; margin-bottom: 20px; }
                    .sidebar-item { padding: 20px 40px; color: #000; font-family: 'Playfair Display', serif; font-size: 20px; font-style: italic; cursor: pointer; transition: 0.3s; border-bottom: 1px solid #f0f0f0; }
                    .sidebar-item:hover { background: #f9f9f9; color: #b8860b; padding-left: 50px; border-left: 5px solid #b8860b; }

                    .search-results { position: absolute; top: 60px; right: 0; width: 280px; background: #fff; display: none; overflow: hidden; border: 1px solid var(--gold); }
                    .search-result-item { padding: 15px 20px; color: #000; cursor: pointer; border-bottom: 1px solid #eee; font-family: 'Playfair Display', serif; font-size: 14px; display: flex; justify-content: space-between; }
                    .search-result-item:hover { background: var(--gold-light); color: #000; }
                </style>
            </head>
            <body>
                <div id="page-transition"></div>

                <div class="container">
                    <div id="slide" class="slide">
                        """ + itemsHtml.toString() + """
                    </div>
                    <div class="buttons">
                        <button id="prev"><i class="fa-solid fa-angle-left"></i></button>
                        <button id="next"><i class="fa-solid fa-angle-right"></i></button>
                    </div>
                    <div class="top-bar">
                        <div class="search-box">
                            <i class="fa-solid fa-magnifying-glass"></i>
                            <input type="text" id="searchInput" placeholder="Buscar modelo...">
                            <div id="results" class="search-results"></div>
                        </div>
                        <div id="menuWrapper" class="menu-wrapper">
                            <button class="quick-btn"><i class="fa-solid fa-bars"></i></button>
                            <div class="sidebar-menu">
                                <div class="sidebar-header">Colección Exclusiva</div>
                                """ + sidebarHtml.toString() + """
                            </div>
                        </div>
                    </div>
                </div>
                <script>
                    window.onload = () => { setTimeout(() => document.getElementById('page-transition').style.opacity = '0', 100); };
                    function navigateTo(url) {
                        document.getElementById('page-transition').style.opacity = '1';
                        setTimeout(() => window.location.href = url, 800);
                    }

                    const slider = document.getElementById('slide');
                    const searchInput = document.getElementById('searchInput');
                    const resultsDiv = document.getElementById('results');
                    const prevBtn = document.getElementById('prev');
                    const nextBtn = document.getElementById('next');

                    // --- LÓGICA DE TECLADO (SOLO SLIDER) ---
                    let activeBtn = null; // 'prev' o 'next'

                    document.addEventListener('keydown', (e) => {
                        // Si el usuario está escribiendo en el buscador, ignoramos las flechas de navegación
                        if (document.activeElement === searchInput) return;

                        if (e.key === 'ArrowLeft') {
                            activeBtn = 'prev';
                            updateBtnVisuals();
                        } else if (e.key === 'ArrowRight') {
                            activeBtn = 'next';
                            updateBtnVisuals();
                        } else if (e.key === 'Enter') {
                            if (activeBtn === 'prev') {
                                prevBtn.classList.add('key-selected'); 
                                setTimeout(() => prevClick(), 150);
                            } else if (activeBtn === 'next') {
                                nextBtn.classList.add('key-selected');
                                setTimeout(() => nextClick(), 150);
                            }
                        }
                    });

                    function updateBtnVisuals() {
                        prevBtn.classList.remove('key-selected');
                        nextBtn.classList.remove('key-selected');
                        
                        if (activeBtn === 'prev') prevBtn.classList.add('key-selected');
                        if (activeBtn === 'next') nextBtn.classList.add('key-selected');
                    }

                    // Funciones de movimiento
                    const nextClick = () => slider.appendChild(document.querySelectorAll('.item')[0]);
                    const prevClick = () => { let l = document.querySelectorAll('.item'); slider.prepend(l[l.length - 1]); };

                    document.getElementById('next').onclick = nextClick;
                    document.getElementById('prev').onclick = prevClick;
                    
                    const products = [
                    """ + searchData.toString() + """
                    ];
                    
                    searchInput.addEventListener('keyup', (e) => {
                        const term = e.target.value.toLowerCase();
                        resultsDiv.innerHTML = '';
                        if (term.length < 2) { resultsDiv.style.display = 'none'; return; }
                        const filtered = products.filter(p => p.name.toLowerCase().includes(term));
                        if (filtered.length > 0) {
                            resultsDiv.style.display = 'block';
                            filtered.forEach(p => {
                                const div = document.createElement('div');
                                div.className = 'search-result-item';
                                div.innerHTML = `<span>${p.name}</span> <i class="fa-solid fa-arrow-right"></i>`;
                                div.onclick = () => { warpToProduct(p.slug); };
                                resultsDiv.appendChild(div);
                            });
                        } else { resultsDiv.style.display = 'none'; }
                    });

                    function slideToProduct(targetSlug) {
                        if (slider.getAttribute('data-moving') === 'true') return;
                        let items = document.querySelectorAll('.item');
                        if (items[1].getAttribute('data-slug') === targetSlug) return; 
                        slider.setAttribute('data-moving', 'true');
                        let moveInterval = setInterval(() => {
                            let currentItems = document.querySelectorAll('.item');
                            if (currentItems[1].getAttribute('data-slug') === targetSlug) {
                                clearInterval(moveInterval);
                                slider.setAttribute('data-moving', 'false');
                            } else {
                                slider.appendChild(currentItems[0]);
                            }
                        }, 200); 
                    }

                    function warpToProduct(targetSlug) {
                        const items = Array.from(document.querySelectorAll('.item'));
                        if (items[1].getAttribute('data-slug') === targetSlug) {
                            searchInput.value = '';
                            resultsDiv.style.display = 'none';
                            return;
                        }
                        slider.classList.add('teleport-effect');
                        setTimeout(() => {
                            const allItems = document.querySelectorAll('.item');
                            allItems.forEach(item => item.style.transition = 'none');
                            let moves = 0;
                            while(document.querySelectorAll('.item')[1].getAttribute('data-slug') !== targetSlug && moves < 20) {
                                slider.appendChild(document.querySelectorAll('.item')[0]);
                                moves++;
                            }
                            void slider.offsetWidth; 
                            allItems.forEach(item => item.style.transition = '0.5s');
                            slider.classList.remove('teleport-effect');
                            searchInput.value = '';
                            resultsDiv.style.display = 'none';
                        }, 400);
                    }
                </script>
            </body>
            </html>
            """;
    }

    public static class ProductData {
        String brand, model, imageUrl, slug, tagline;
        double price;
        public ProductData(String brand, String model, double price, String imageUrl, String slug, String tagline) {
            this.brand = brand; this.model = model; this.price = price; this.imageUrl = imageUrl; this.slug = slug; this.tagline = tagline;
        }
    }
}