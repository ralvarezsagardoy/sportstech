package com.ricardo;

import java.util.ArrayList;
import java.util.List;

public class IndexTemplate {

    public static String getIndexPage(List<ProductData> originalProducts) {
        StringBuilder itemsHtml = new StringBuilder();
        StringBuilder searchData = new StringBuilder();
        StringBuilder quickAccessHtml = new StringBuilder();

        // Duplicamos lista para el efecto infinito
        List<ProductData> fullList = new ArrayList<>(originalProducts);
        while (fullList.size() < 6) fullList.addAll(originalProducts);

        for (int i = 0; i < fullList.size(); i++) {
            ProductData p = fullList.get(i);
            String uniqueId = "product-" + p.slug + "-" + i;
            
            itemsHtml.append("<div class='item' id='").append(uniqueId).append("' style='background-image: url(").append(p.imageUrl).append(");'>")
                     .append("<div class='content'>")
                     .append("<div class='name'>").append(p.brand.toUpperCase()).append("</div>")
                     // Micro-descripción IA
                     .append("<div class='short-desc'>").append(p.tagline).append("</div>")
                     .append("<div class='des'>").append(p.model).append("</div>")
                     .append("<div class='price'>$").append(p.price).append("</div>")
                     .append("<a href='").append(p.slug).append(".html'><button>Ver Detalles</button></a>")
                     .append("</div>")
                     // Etiqueta mini debajo
                     .append("<div class='mini-label'>").append(p.model).append("</div>")
                     .append("</div>");
        }
        
        // Datos para buscador y menú rápido
        for (ProductData p : originalProducts) {
             String jsObj = "{name: \"" + p.brand + " " + p.model + "\", id: \"product-" + p.slug + "-0\", img: \"" + p.imageUrl + "\"},";
             searchData.append(jsObj);
             
             quickAccessHtml.append("<div class='qa-item' onclick='goToProduct(\"product-").append(p.slug).append("-0\")'>")
                            .append("<img src='").append(p.imageUrl).append("'>")
                            .append("<span>").append(p.model).append("</span>")
                            .append("</div>");
        }

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Catálogo Pro 2026</title>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
                <style>
                    body { margin: 0; background: #000; font-family: 'Segoe UI', sans-serif; overflow: hidden; }
                    .container { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 100vw; height: 100vh; background: #000; overflow: hidden; }

                    /* --- SLIDER --- */
                    .slide .item {
                        width: 200px; height: 300px; position: absolute; top: 50%; transform: translate(0, -50%);
                        border-radius: 20px; box-shadow: 0 30px 50px rgba(0,0,0,0.8);
                        background-position: 50% 50%; background-size: cover; display: inline-block;
                        transition: 0.5s; z-index: 10;
                    }
                    /* ESTADOS */
                    .slide .item:nth-child(1), .slide .item:nth-child(2) { top: 0; left: 0; transform: translate(0, 0); border-radius: 0; width: 100%; height: 100%; box-shadow: none; z-index: 1; }
                    .slide .item:nth-child(1)::after, .slide .item:nth-child(2)::after { content: ''; position: absolute; top:0; left:0; width:100%; height:100%; background: rgba(0,0,0,0.4); }
                    
                    .slide .item:nth-child(3) { left: 50%; z-index: 20; }
                    .slide .item:nth-child(4) { left: calc(50% + 220px); z-index: 19; }
                    .slide .item:nth-child(5) { left: calc(50% + 440px); z-index: 18; }
                    .slide .item:nth-child(n+6) { left: calc(50% + 660px); opacity: 0; }

                    /* --- TEXTO GRANDE --- */
                    .item .content { position: absolute; top: 50%; left: 100px; width: 500px; text-align: left; color: #eee; transform: translate(0, -50%); display: none; z-index: 30; }
                    .slide .item:nth-child(2) .content { display: block; }
                    
                    .content .name { font-size: 60px; font-weight: 800; opacity: 0; animation: show 1s ease-in-out 1 forwards; text-transform: uppercase; line-height: 1; margin-bottom: 10px; }
                    
                    /* MICRO-DESCRIPCIÓN IA */
                    .content .short-desc { 
                        font-size: 18px; color: #cbd5e1; font-style: italic; margin-bottom: 5px; 
                        opacity: 0; animation: show 1s ease-in-out 0.2s 1 forwards; border-left: 3px solid #4ade80; padding-left: 10px;
                    }

                    .content .des { font-size: 24px; font-weight: bold; opacity: 0; animation: show 1s ease-in-out 0.3s 1 forwards; color: #fff; margin-bottom: 15px; }
                    .content .price { font-size: 40px; color: #4ade80; font-weight: bold; margin-bottom: 30px; opacity: 0; animation: show 1s ease-in-out 0.4s 1 forwards; }
                    .content button { padding: 15px 35px; border: none; opacity: 0; animation: show 1s ease-in-out 0.6s 1 forwards; cursor: pointer; background: white; border-radius: 30px; font-weight: bold; transition: 0.3s; }
                    .content button:hover { background: #4ade80; color: #000; }
                    @keyframes show { from { opacity: 0; transform: translate(0, 50px); filter: blur(20px); } to { opacity: 1; transform: translate(0, 0); filter: blur(0); } }

                    /* --- ETIQUETA MINI --- */
                    .mini-label {
                        position: absolute; bottom: -40px; left: 0; width: 100%; text-align: center;
                        color: rgba(255,255,255,0.8); font-size: 14px; font-weight: 600; text-transform: uppercase;
                        letter-spacing: 1px; opacity: 0; transition: 0.5s; text-shadow: 0 2px 4px black;
                    }
                    .slide .item:nth-child(3) .mini-label,
                    .slide .item:nth-child(4) .mini-label,
                    .slide .item:nth-child(5) .mini-label { opacity: 1; bottom: -30px; }

                    /* --- BARRA SUPERIOR --- */
                    .buttons { position: absolute; bottom: 30px; z-index: 100; text-align: center; width: 100%; }
                    .buttons button { width: 60px; height: 60px; border-radius: 50%; border: 1px solid rgba(255,255,255,0.5); transition: 0.3s; cursor: pointer; background: rgba(0,0,0,0.4); color: white; margin: 0 10px; }
                    .buttons button:hover { background: #fff; color: #000; }

                    .top-bar { position: absolute; top: 30px; right: 50px; z-index: 1000; display: flex; gap: 15px; }
                    
                    .search-box { display: flex; align-items: center; background: rgba(255, 255, 255, 0.1); backdrop-filter: blur(10px); padding: 10px 20px; border-radius: 30px; border: 1px solid rgba(255,255,255,0.2); }
                    .search-box input { background: transparent; border: none; outline: none; color: white; width: 200px; margin-left: 10px; }
                    .search-box i { color: white; }
                    
                    .quick-btn { 
                        width: 45px; height: 45px; border-radius: 50%; border: 1px solid rgba(255,255,255,0.2);
                        background: rgba(255,255,255,0.1); color: white; cursor: pointer; display: flex; align-items: center; justify-content: center;
                        backdrop-filter: blur(10px); transition: 0.3s;
                    }
                    .quick-btn:hover { background: #4ade80; color: #000; border-color: #4ade80; }

                    /* --- MODAL ACCESO RÁPIDO --- */
                    .modal-overlay {
                        position: fixed; top: 0; left: 0; width: 100%; height: 100%;
                        background: rgba(0,0,0,0.9); z-index: 5000; display: none;
                        backdrop-filter: blur(10px); opacity: 0; transition: 0.3s;
                        justify-content: center; align-items: center;
                    }
                    .modal-content {
                        width: 80%; max-width: 1000px; max-height: 80vh; overflow-y: auto;
                        display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 20px;
                        padding: 40px;
                    }
                    .qa-item {
                        background: rgba(255,255,255,0.1); border-radius: 15px; padding: 15px;
                        text-align: center; cursor: pointer; transition: 0.3s; border: 1px solid transparent;
                    }
                    .qa-item:hover { background: rgba(255,255,255,0.2); border-color: #4ade80; transform: translateY(-5px); }
                    .qa-item img { width: 100%; height: 100px; object-fit: contain; margin-bottom: 10px; }
                    .qa-item span { color: white; font-size: 14px; display: block; }
                    .close-modal { position: absolute; top: 30px; right: 30px; color: white; font-size: 30px; cursor: pointer; }

                    .search-results { position: absolute; top: 60px; right: 60px; width: 250px; background: #111; border-radius: 10px; display: none; overflow: hidden; border: 1px solid #333; }
                    .search-result-item { padding: 10px 15px; color: #ccc; cursor: pointer; border-bottom: 1px solid #222; font-size: 14px; display: flex; justify-content: space-between; }
                    .search-result-item:hover { background: #4ade80; color: #000; }
                </style>
            </head>
            <body>
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
                            <input type="text" id="searchInput" placeholder="Buscar...">
                        </div>
                        <button class="quick-btn" onclick="toggleModal()"><i class="fa-solid fa-grip"></i></button>
                    </div>

                    <div id="results" class="search-results"></div>
                </div>

                <div id="quickAccessModal" class="modal-overlay">
                    <i class="fa-solid fa-xmark close-modal" onclick="toggleModal()"></i>
                    <div class="modal-content">
                        """ + quickAccessHtml.toString() + """
                    </div>
                </div>

                <script>
                    const slider = document.getElementById('slide');
                    const modal = document.getElementById('quickAccessModal');

                    document.getElementById('next').onclick = () => slider.appendChild(document.querySelectorAll('.item')[0]);
                    document.getElementById('prev').onclick = () => { let l = document.querySelectorAll('.item'); slider.prepend(l[l.length - 1]); };

                    function toggleModal() {
                        if (modal.style.display === 'flex') {
                            modal.style.opacity = '0';
                            setTimeout(() => modal.style.display = 'none', 300);
                        } else {
                            modal.style.display = 'flex';
                            setTimeout(() => modal.style.opacity = '1', 10);
                        }
                    }

                    // Buscador (ARREGLADO)
                    const products = [""" + searchData.toString() + """
                    ];
                    
                    const searchInput = document.getElementById('searchInput');
                    const resultsDiv = document.getElementById('results');

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
                                div.onclick = () => { goToProduct(p.id); };
                                resultsDiv.appendChild(div);
                            });
                        } else { resultsDiv.style.display = 'none'; }
                    });

                    function goToProduct(targetId) {
                        toggleModal();
                        const allItems = Array.from(document.querySelectorAll('.item'));
                        const targetIndex = allItems.findIndex(item => item.id === targetId);
                        if (targetIndex === -1 || targetIndex === 1) return;
                        if (targetIndex === 0) slider.appendChild(allItems[0]);
                        else {
                            let moves = targetIndex - 1;
                            for(let i = 0; i < moves; i++) slider.appendChild(document.querySelectorAll('.item')[0]);
                        }
                        searchInput.value = '';
                        resultsDiv.style.display = 'none';
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