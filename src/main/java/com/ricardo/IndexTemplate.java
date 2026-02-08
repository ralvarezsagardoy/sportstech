package com.ricardo;

import java.util.ArrayList;
import java.util.List;

public class IndexTemplate {

    public static String getIndexPage(List<ProductData> originalProducts) {
        StringBuilder itemsHtml = new StringBuilder();
        StringBuilder searchData = new StringBuilder();
        StringBuilder sidebarHtml = new StringBuilder();
        
        // Duplicación inicial para asegurar efecto infinito
        List<ProductData> fullList = new ArrayList<>(originalProducts);
        while (fullList.size() < 6) fullList.addAll(originalProducts);

        for (int i = 0; i < fullList.size(); i++) {
            ProductData p = fullList.get(i);
            String uniqueId = "product-" + p.slug + "-" + i;
            
            itemsHtml.append("<div class='item' id='").append(uniqueId).append("' ")
                     .append("data-slug='").append(p.slug).append("' ")
                     .append("onclick='slideToProduct(\"").append(p.slug).append("\")'>") 
                     
                     // 1. IMAGEN OPTIMIZADA
                     .append("<img src='").append(p.imageUrl).append("' loading='lazy' alt='").append(p.model).append("' class='bg-img'>")
                     .append("<div class='overlay'></div>")

                     // 2. CONTENIDO GRANDE
                     .append("<div class='content'>")
                     .append("<div class='name'>").append(p.brand.toUpperCase()).append("</div>")
                     .append("<div class='des'>").append(p.model).append("</div>")
                     .append("<div class='short-desc'>").append(p.tagline).append("</div>")
                     .append("<div class='price'>$").append(p.price).append("</div>")
                     .append("<button onclick='navigateTo(\"").append(p.slug).append(".html\")'>Ver Detalles</button>")
                     .append("</div>")
                     
                     // 3. ETIQUETA PEQUEÑA
                     .append("<div class='mini-label'>").append(p.model).append("</div>")
                     .append("</div>");
        }
        
        // Datos para JS
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
                    .container { position: absolute; top: 50%%; left: 50%%; transform: translate(-50%%, -50%%); width: 100vw; height: 100vh; background: #000; overflow: hidden; }
                    :root { --gold: #d4af37; --gold-light: #f3e5ab; --bg-panel: rgba(10, 10, 10, 0.95); }
                    #page-transition { position: fixed; top: 0; left: 0; width: 100%%; height: 100%%; background: #000; z-index: 9999; pointer-events: none; opacity: 1; transition: opacity 0.8s ease; }
                    
                    /* --- ESTILOS SLIDER --- */
                    .slide { 
                        width: 100%%; height: 100%%; 
                        transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1); 
                    }
                    
                    /* TRANSICIÓN "CRISTALINA" (Blur sin negro) */
                    .slide.crystal-warp {
                        filter: blur(30px) brightness(1.1); 
                        transform: scale(1.02);
                        transition: all 0.6s cubic-bezier(0.25, 0.1, 0.25, 1);
                        pointer-events: none;
                    }
                    
                    /* ESTILO BASE DE TARJETA */
                    .slide .item { 
                        width: 200px; height: 300px; 
                        position: absolute; top: 50%%; transform: translate(0, -50%%); 
                        border-radius: 10px; 
                        box-shadow: 0 30px 50px rgba(0,0,0,0.8); 
                        background: #000; 
                        display: inline-block; 
                        transition: 0.5s cubic-bezier(0.4, 0, 0.2, 1); 
                        z-index: 10; 
                        border: 1px solid rgba(255,255,255,0.1); 
                        overflow: visible; 
                    }
                    
                    .item .bg-img { 
                        position: absolute; top: 0; left: 0; width: 100%%; height: 100%%; 
                        object-fit: cover; border-radius: 10px; z-index: 0; pointer-events: none; 
                    }
                    .item .overlay { 
                        position: absolute; top: 0; left: 0; width: 100%%; height: 100%%; 
                        background: linear-gradient(to bottom, rgba(0,0,0,0.1), rgba(0,0,0,0.6)); 
                        z-index: 1; pointer-events: none; border-radius: 10px; 
                    }

                    /* --- POSICIONAMIENTO Y EFECTOS 3D --- */

                    /* 1 y 2: FONDO ACTIVO */
                    .slide .item:nth-child(1), .slide .item:nth-child(2) { 
                        top: 0; left: 0; transform: translate(0, 0); 
                        border-radius: 0; width: 100%%; height: 100%%; 
                        box-shadow: none; z-index: 1; border: none; 
                    }
                    .slide .item:nth-child(1) .bg-img, .slide .item:nth-child(2) .bg-img { border-radius: 0; border: none; box-shadow: none; }
                    .slide .item:nth-child(1) .overlay, .slide .item:nth-child(2) .overlay { border-radius: 0; background: linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.9)); }

                    /* 3: PRIMERA TARJETA */
                    .slide .item:nth-child(3) { 
                        left: 50%%; transform: translate(0, -50%%);
                        z-index: 30; opacity: 1; box-shadow: 0 20px 40px rgba(0,0,0,0.6); 
                        cursor: pointer;
                    }
                    
                    /* 4: SEGUNDA TARJETA (Interactiva ahora) */
                    .slide .item:nth-child(4) { 
                        left: calc(50%% + 140px); transform: translate(0, -50%%) scale(0.85) perspective(1000px) rotateY(-10deg); 
                        z-index: 20; opacity: 0.7; filter: blur(1px) brightness(0.7); 
                        cursor: pointer; /* AHORA CLICKABLE */
                    }
                    
                    /* 5: TERCERA TARJETA (Interactiva ahora) */
                    .slide .item:nth-child(5) { 
                        left: calc(50%% + 260px); transform: translate(0, -50%%) scale(0.65) perspective(1000px) rotateY(-20deg); 
                        z-index: 10; opacity: 0.4; filter: blur(3px) brightness(0.5); 
                        cursor: pointer; /* AHORA CLICKABLE */
                    }
                    
                    /* 6+: OCULTOS */
                    .slide .item:nth-child(n+6) { left: calc(50%% + 450px); opacity: 0; pointer-events: none; }

                    /* EFECTO HOVER GENERALIZADO (Para 3, 4 y 5) */
                    .slide .item:nth-child(n+3):hover { 
                        transform: translate(0, -50%%) scale(1.05); /* Crece un poco */
                        z-index: 100; /* Se pone por delante de todo */
                        border-color: var(--gold); 
                        box-shadow: 0 0 25px rgba(212, 175, 55, 0.6); /* Brillo dorado */
                        filter: blur(0) brightness(1.2); /* Quita el blur y da brillo */
                        opacity: 1;
                    }

                    /* --- CONTENIDOS --- */
                    .item .content { position: absolute; top: 50%%; left: 100px; width: 500px; text-align: left; color: #fff; transform: translate(0, -50%%); display: none; z-index: 30; }
                    .slide .item:nth-child(2) .content { display: block; }

                    .content .name { font-family: 'Cinzel', serif; font-size: 70px; font-weight: 700; letter-spacing: 5px; color: var(--gold); opacity: 0; animation: show 1s ease-in-out 1 forwards; text-shadow: 0 5px 15px rgba(0,0,0,0.5); margin: 0; }
                    .content .des { font-family: 'Playfair Display', serif; font-size: 35px; font-style: italic; opacity: 0; animation: show 1s ease-in-out 0.3s 1 forwards; color: #fff; margin-bottom: 15px; }
                    .content .short-desc { font-family: 'Segoe UI', sans-serif; font-size: 13px; color: #ccc; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 25px; opacity: 0; animation: show 1s ease-in-out 0.4s 1 forwards; border-left: 2px solid var(--gold); padding-left: 15px; }
                    .content .price { font-family: 'Cinzel', serif; font-size: 45px; color: #fff; margin-bottom: 35px; opacity: 0; animation: show 1s ease-in-out 0.5s 1 forwards; }
                    .content button { padding: 15px 40px; border: 1px solid var(--gold); background: transparent; color: var(--gold); font-family: 'Cinzel', serif; font-size: 14px; letter-spacing: 2px; opacity: 0; animation: show 1s ease-in-out 0.6s 1 forwards; cursor: pointer; transition: 0.4s; text-transform: uppercase; }
                    .content button:hover { background: var(--gold); color: #000; box-shadow: 0 0 20px var(--gold); }
                    @keyframes show { from { opacity: 0; transform: translate(0, 50px); filter: blur(20px); } to { opacity: 1; transform: translate(0, 0); filter: blur(0); } }

                    /* --- MINI ETIQUETAS --- */
                    .mini-label { 
                        position: absolute; bottom: -35px; left: 0; width: 100%%; 
                        text-align: center; color: var(--gold); 
                        font-family: 'Cinzel', serif; font-size: 12px; 
                        letter-spacing: 2px; opacity: 0; transition: 0.5s; z-index: 30;
                        text-shadow: 0 2px 5px rgba(0,0,0,1);
                    }
                    .slide .item:nth-child(1) .mini-label, .slide .item:nth-child(2) .mini-label { opacity: 0; }
                    .slide .item:nth-child(3) .mini-label, .slide .item:nth-child(4) .mini-label, .slide .item:nth-child(5) .mini-label { opacity: 1; }

                    /* --- BOTONES NAVEGACIÓN --- */
                    .buttons { position: absolute; bottom: 30px; z-index: 100; text-align: center; width: 100%%; }
                    .buttons button { position: relative; width: 60px; height: 60px; border-radius: 50%%; border: 1px solid rgba(255,255,255,0.2); transition: 0.3s; cursor: pointer; background: rgba(0,0,0,0.5); color: white; margin: 0 10px; }
                    .buttons button:hover { background: var(--gold); color: #000; border-color: var(--gold); }
                    .buttons button.key-selected { background: var(--gold); color: #000; border-color: var(--gold); box-shadow: 0 0 20px var(--gold); transform: scale(1.1); }
                    .buttons button:focus { outline: none; }

                    /* --- UI SUPERIOR --- */
                    .top-bar { position: absolute; top: 30px; right: 50px; z-index: 1000; display: flex; gap: 20px; align-items: center; }
                    .search-box { display: flex; align-items: center; background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(10px); padding: 10px 20px; border-bottom: 1px solid var(--gold); }
                    .search-box input { background: transparent; border: none; outline: none; color: white; width: 200px; margin-left: 10px; font-family: 'Playfair Display', serif; }
                    .search-box i { color: var(--gold); }
                    .search-results { position: absolute; top: 60px; right: 0; width: 280px; background: #fff; display: none; overflow: hidden; border: 1px solid var(--gold); }
                    .search-result-item { padding: 15px 20px; color: #000; cursor: pointer; border-bottom: 1px solid #eee; font-family: 'Playfair Display', serif; font-size: 14px; display: flex; justify-content: space-between; }
                    .search-result-item:hover { background: var(--gold-light); color: #000; }

                    .menu-wrapper { position: relative; display: inline-block; padding-bottom: 20px; margin-bottom: -20px; }
                    .quick-btn { width: 50px; height: 50px; border-radius: 0; border: 1px solid var(--gold); background: transparent; color: var(--gold); cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.4s; font-size: 20px; }
                    .menu-wrapper:hover .quick-btn { background: var(--gold); color: #000; }
                    .sidebar-menu { position: fixed; top: 0; right: -400px; width: 350px; height: 100vh; background: #fff; border-left: 3px solid var(--gold); transition: right 0.5s cubic-bezier(0.77, 0, 0.175, 1); padding-top: 120px; overflow-y: auto; z-index: 990; box-shadow: -10px 0 50px rgba(0,0,0,0.5); }
                    .menu-wrapper:hover .sidebar-menu { right: 0; }
                    .sidebar-header { padding: 0 40px 30px; color: #999; font-size: 11px; letter-spacing: 3px; font-family: 'Segoe UI', sans-serif; text-transform: uppercase; border-bottom: 1px solid #eee; margin-bottom: 20px; }
                    .sidebar-item { padding: 20px 40px; color: #000; font-family: 'Playfair Display', serif; font-size: 20px; font-style: italic; cursor: pointer; transition: 0.3s; border-bottom: 1px solid #f0f0f0; }
                    .sidebar-item:hover { background: #f9f9f9; color: #b8860b; padding-left: 50px; }
                </style>
            </head>
            <body>
                <div id="page-transition"></div>

                <div class="container">
                    <div id="slide" class="slide">
                        %s
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
                                %s
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
                    const nextClick = () => {
                        let items = slider.querySelectorAll('.item');
                        if(items.length > 0) slider.appendChild(items[0]);
                    };
                    const prevClick = () => { 
                        let items = slider.querySelectorAll('.item');
                        if(items.length > 0) slider.prepend(items[items.length - 1]); 
                    };

                    document.getElementById('next').onclick = function() { this.blur(); nextClick(); };
                    document.getElementById('prev').onclick = function() { this.blur(); prevClick(); };

                    // --- TECLADO ---
                    let activeBtn = null;
                    const prevBtn = document.getElementById('prev');
                    const nextBtn = document.getElementById('next');
                    const searchInput = document.getElementById('searchInput');

                    document.addEventListener('keydown', (e) => {
                        if (document.activeElement === searchInput) return;
                        if (e.key === 'ArrowLeft') {
                            activeBtn = 'prev'; updateBtnVisuals();
                        } else if (e.key === 'ArrowRight') {
                            activeBtn = 'next'; updateBtnVisuals();
                        } else if (e.key === 'Enter') {
                            e.preventDefault();
                            if (activeBtn === 'prev') { prevBtn.classList.add('key-selected'); setTimeout(() => prevClick(), 150); }
                            else if (activeBtn === 'next') { nextBtn.classList.add('key-selected'); setTimeout(() => nextClick(), 150); }
                        }
                    });

                    function updateBtnVisuals() {
                        prevBtn.classList.remove('key-selected');
                        nextBtn.classList.remove('key-selected');
                        if (activeBtn === 'prev') prevBtn.classList.add('key-selected');
                        if (activeBtn === 'next') nextBtn.classList.add('key-selected');
                    }

                    // --- BUSCADOR ---
                    const resultsDiv = document.getElementById('results');
                    const products = [ %s ];
                    
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

                    // --- CLIC EN TARJETA ---
                    function slideToProduct(targetSlug) {
                        // Usamos la misma lógica inteligente que el buscador
                        // Si está cerca (visible), se desplazará.
                        warpToProduct(targetSlug);
                    }

                    // --- NAVEGACIÓN INTELIGENTE (CRYSTAL WARP) ---
                    function warpToProduct(targetSlug) {
                         const items = Array.from(document.querySelectorAll('.item'));
                         
                         let targetIndex = -1;
                         for(let i=0; i<items.length; i++) {
                             if(items[i].getAttribute('data-slug') === targetSlug) {
                                 targetIndex = i;
                                 break;
                             }
                         }

                         if (targetIndex === 1) {
                             searchInput.value = '';
                             resultsDiv.style.display = 'none';
                             return;
                         }

                         // Si está visible (tarjetas derecha), usar deslizamiento suave
                         if (targetIndex > 1 && targetIndex <= 4) {
                             let moves = targetIndex - 1;
                             let count = 0;
                             let interval = setInterval(() => {
                                 if(count >= moves) { clearInterval(interval); return; }
                                 nextClick();
                                 count++;
                             }, 200); 
                         } else {
                             // ESTRATEGIA: DESENFOQUE CRISTALINO (Sin negro)
                             slider.classList.add('crystal-warp');
                             
                             setTimeout(() => {
                                 const allItems = document.querySelectorAll('.item');
                                 allItems.forEach(item => item.style.transition = 'none');
                                 
                                 let safety = 0;
                                 while(document.querySelectorAll('.item')[1].getAttribute('data-slug') !== targetSlug && safety < 100) {
                                     slider.appendChild(document.querySelectorAll('.item')[0]);
                                     safety++;
                                 }
                                 
                                 void slider.offsetWidth; 
                                 
                                 allItems.forEach(item => item.style.transition = '');
                                 slider.classList.remove('crystal-warp');
                                 
                             }, 600); 
                         }
                         
                         searchInput.value = '';
                         resultsDiv.style.display = 'none';
                    }
                </script>
            </body>
            </html>
            """.formatted(itemsHtml.toString(), sidebarHtml.toString(), searchData.toString());
    }

    public static class ProductData {
        String brand, model, imageUrl, slug, tagline;
        double price;
        public ProductData(String brand, String model, double price, String imageUrl, String slug, String tagline) {
            this.brand = brand; this.model = model; this.price = price; this.imageUrl = imageUrl; this.slug = slug; this.tagline = tagline;
        }
    }
}