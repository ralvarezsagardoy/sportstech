package com.ricardo;

public class WebTemplate {

    public static String getPage(String brand, String model, double price, String reviewHtml, String imageUrl) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>
                """ + brand + " " + model + """ 
                | Colección Exclusiva</title>
                
                <link rel="preconnect" href="https://fonts.googleapis.com">
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@400;700&family=Playfair+Display:ital,wght@0,400;0,700;1,400&display=swap" rel="stylesheet">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
                <style>
                    :root { --bg: #050505; --gold: #d4af37; --gold-dim: #8a7020; --text: #e0e0e0; }
                    body { margin: 0; padding: 0; background-color: var(--bg); color: var(--text); font-family: 'Playfair Display', serif; overflow-x: hidden; }
                    
                    /* 🔥 CORTINA DE TRANSICIÓN */
                    #page-transition {
                        position: fixed; top: 0; left: 0; width: 100%; height: 100%;
                        background: #000; z-index: 9999; pointer-events: none;
                        opacity: 1; transition: opacity 0.8s ease;
                    }

                    .container { display: flex; min-height: 100vh; align-items: center; justify-content: center; }
                    .product-card { display: flex; width: 90%; max-width: 1400px; height: 85vh; border: 1px solid rgba(212, 175, 55, 0.3); box-shadow: 0 0 50px rgba(0,0,0,0.8); background: radial-gradient(circle at center, #0a0a0a, #000); position: relative; }
                    .image-col { flex: 1; position: relative; overflow: hidden; border-right: 1px solid rgba(212, 175, 55, 0.2); }
                    .image-col img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.8s ease; filter: brightness(0.9) contrast(1.1); }
                    .image-col:hover img { transform: scale(1.05); }
                    .info-col { flex: 1; padding: 60px; display: flex; flex-direction: column; justify-content: center; position: relative; }
                    .info-col::after { content: ''; position: absolute; top: 30px; right: 30px; width: 100px; height: 100px; border-top: 2px solid var(--gold); border-right: 2px solid var(--gold); }
                    h2 { font-family: 'Cinzel', serif; color: var(--gold); font-size: 18px; letter-spacing: 5px; margin: 0; text-transform: uppercase; opacity: 0.8; }
                    h1 { font-family: 'Cinzel', serif; font-size: 60px; margin: 10px 0 30px; line-height: 1; background: linear-gradient(to right, #fff, #999); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
                    .price { font-size: 40px; color: var(--gold); font-family: 'Cinzel', serif; margin-bottom: 40px; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 20px; display: inline-block; width: 100%; }
                    .review-box { font-size: 18px; line-height: 1.8; color: #ccc; margin-bottom: 50px; border-left: 3px solid var(--gold); padding-left: 25px; font-style: italic; }
                    .review-box strong { color: #fff; font-weight: normal; border-bottom: 1px solid var(--gold-dim); }
                    .review-box p { margin: 0; }
                    .actions { display: flex; gap: 20px; }
                    .btn-buy { padding: 18px 50px; background: var(--gold); color: #000; border: none; font-family: 'Cinzel', serif; font-weight: bold; font-size: 16px; letter-spacing: 2px; cursor: pointer; transition: 0.3s; text-transform: uppercase; }
                    .btn-buy:hover { background: #fff; box-shadow: 0 0 20px rgba(212, 175, 55, 0.5); }
                    
                    /* Botón volver estilo cursor */
                    .btn-back { cursor: pointer; text-decoration: none; padding: 18px 30px; border: 1px solid var(--gold); color: var(--gold); font-family: 'Cinzel', serif; font-size: 14px; letter-spacing: 2px; transition: 0.3s; display: flex; align-items: center; justify-content: center; }
                    .btn-back:hover { background: rgba(212, 175, 55, 0.1); color: #fff; border-color: #fff; }
                    
                    @media (max-width: 900px) { .product-card { flex-direction: column; height: auto; margin: 20px 0; } .image-col { height: 400px; border-right: none; border-bottom: 1px solid var(--gold); } .info-col { padding: 40px; } h1 { font-size: 40px; } }
                </style>
            </head>
            <body>
                <div id="page-transition"></div> <div class="container">
                    <div class="product-card">
                        <div class="image-col">
                            <img src=\"""" + imageUrl + """
                            \" alt=\"""" + model + """
                            \">
                        </div>
                        <div class="info-col">
                            <h2>""" + brand.toUpperCase() + """
                            </h2>
                            <h1>""" + model + """
                            </h1>
                            <div class="price">$""" + price + """
                            </div>
                            <div class="review-box">
                                """ + reviewHtml + """
                            </div>
                            <div class="actions">
                                <button class="btn-buy">Adquirir</button>
                                <div onclick="navigateTo('index.html')" class="btn-back"><i class="fa-solid fa-arrow-left"></i> &nbsp; Volver</div>
                            </div>
                        </div>
                    </div>
                </div>

                <script>
                    // 🔥 ENTRADA SUAVE (Fade In)
                    window.onload = () => {
                        const curtain = document.getElementById('page-transition');
                        setTimeout(() => curtain.style.opacity = '0', 100);
                    };

                    // 🔥 SALIDA SUAVE (Fade Out)
                    function navigateTo(url) {
                        const curtain = document.getElementById('page-transition');
                        curtain.style.opacity = '1';
                        setTimeout(() => {
                            window.location.href = url;
                        }, 800);
                    }
                </script>
            </body>
            </html>
            """;
    }
}