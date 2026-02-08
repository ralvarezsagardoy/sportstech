package com.ricardo;

public class WebTemplate {

    // 🔴 CONTADOR GLOBAL
    private static int turno = 0;

    // --- EL JEFE: Recibe la URL de la imagen y la pasa al diseño ---
    public static String getNextDesign(String brand, String model, double price, String review, String imageUrl) {
        int diseño = turno % 3; 
        turno++; 

        if (diseño == 0) {
            System.out.println("🎨 Asignando turno 1: CYBERPUNK");
            return getDarkDesign(brand, model, price, review, imageUrl);
        } else if (diseño == 1) {
            System.out.println("🎨 Asignando turno 2: MINIMALISTA");
            return getLightDesign(brand, model, price, review, imageUrl);
        } else {
            System.out.println("🎨 Asignando turno 3: LUJO");
            return getLuxuryDesign(brand, model, price, review, imageUrl);
        }
    }

    // --- DISEÑO 1: CYBERPUNK (Foto grande y brillante) ---
    private static String getDarkDesign(String brand, String model, double price, String review, String imageUrl) {
        return "<!DOCTYPE html><html lang='es'>" +
               "<head><meta charset='UTF-8'><script src='https://cdn.tailwindcss.com'></script></head>" +
               "<body class='bg-slate-900 text-slate-200 font-sans min-h-screen flex flex-col items-center justify-center p-6'>" +
               "  <div class='max-w-2xl w-full text-center space-y-8'>" +
               "    <a href='index.html' class='text-emerald-400 font-bold hover:underline'>← Volver al Catálogo</a>" +
               "    <div class='mt-4'>" +
               "      <span class='bg-slate-800 text-emerald-400 px-3 py-1 rounded-full text-sm font-semibold tracking-wide uppercase'>" + brand + "</span>" +
               "      <h1 class='text-5xl font-extrabold text-white mt-4 tracking-tight'>" + model + "</h1>" +
               "    </div>" +
               
               // 📸 FOTO CYBERPUNK
               "    <div class='relative group'>" +
               "      <div class='absolute -inset-1 bg-gradient-to-r from-emerald-600 to-blue-600 rounded-2xl blur opacity-25 group-hover:opacity-75 transition duration-1000 group-hover:duration-200'></div>" +
               "      <img src='" + imageUrl + "' alt='" + model + "' class='relative rounded-2xl shadow-2xl w-full object-cover h-80 border-2 border-slate-700 transform transition group-hover:scale-[1.01]'>" +
               "    </div>" +

               "    <div class='bg-slate-800 p-8 rounded-2xl border border-slate-700 shadow-2xl'>" +
               "      <div class='flex items-baseline justify-center gap-2'>" +
               "        <span class='text-5xl font-bold text-white'>$" + price + "</span>" +
               "      </div>" +
               "      <button class='mt-6 w-full bg-emerald-500 hover:bg-emerald-400 text-slate-900 font-bold py-4 rounded-xl transition-all shadow-lg shadow-emerald-500/20'>Comprar Ahora</button>" +
               "    </div>" +
               "    <div class='text-left bg-slate-800/50 p-6 rounded-xl border border-slate-700'>" +
               "      <h3 class='text-xl font-bold text-white mb-3 border-l-4 border-emerald-500 pl-3'>Opinión del Experto</h3>" +
               "      <div class='text-slate-300 leading-relaxed text-lg'>" + review + "</div>" +
               "    </div>" +
               "  </div>" +
               "</body></html>";
    }

    // --- DISEÑO 2: MINIMALISTA (Foto a la izquierda) ---
    private static String getLightDesign(String brand, String model, double price, String review, String imageUrl) {
        return "<!DOCTYPE html><html lang='es'>" +
               "<head><meta charset='UTF-8'><script src='https://cdn.tailwindcss.com'></script></head>" +
               "<body class='bg-white text-gray-900 font-sans min-h-screen flex flex-col items-center justify-center p-6'>" +
               "  <div class='max-w-4xl w-full space-y-10'>" +
               "    <nav class='flex justify-between items-center border-b pb-4'>" +
               "       <a href='index.html' class='text-gray-500 hover:text-black transition'>← Catálogo</a>" +
               "       <span class='font-semibold text-gray-400 tracking-wider'>" + brand.toUpperCase() + "</span>" +
               "    </nav>" +
               "    <div class='grid md:grid-cols-2 gap-12 items-center'>" +
               
               // 📸 FOTO MINIMALISTA
               "      <div class='bg-gray-100 rounded-3xl p-6 flex items-center justify-center'>" +
               "         <img src='" + imageUrl + "' alt='" + model + "' class='rounded-xl shadow-lg w-full object-cover hover:scale-105 transition duration-500'>" +
               "      </div>" +

               "      <div class='space-y-6'>" +
               "        <h1 class='text-5xl font-bold tracking-tighter text-black'>" + model + "</h1>" +
               "        <p class='text-3xl text-blue-600 font-medium'>$" + price + "</p>" +
               "        <div class='text-gray-600 leading-relaxed text-lg'>" + review + "</div>" +
               "        <button class='bg-blue-600 hover:bg-blue-700 text-white px-8 py-4 rounded-full font-medium transition shadow-lg shadow-blue-200 w-full'>Añadir a la cesta</button>" +
               "      </div>" +
               "    </div>" +
               "  </div>" +
               "</body></html>";
    }

    // --- DISEÑO 3: LUJO (Foto artística) ---
    private static String getLuxuryDesign(String brand, String model, double price, String review, String imageUrl) {
        return "<!DOCTYPE html><html lang='es'>" +
               "<head><meta charset='UTF-8'><script src='https://cdn.tailwindcss.com'></script>" +
               "<link href='https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;0,700;1,400&display=swap' rel='stylesheet'></head>" +
               "<body class='bg-stone-100 text-stone-800 min-h-screen flex flex-col items-center justify-center p-8'>" +
               "  <div class='max-w-xl w-full text-center border border-stone-300 bg-white shadow-2xl overflow-hidden'>" +
               
               // 📸 FOTO LUJO
               "    <div class='w-full h-80 overflow-hidden'>" +
               "       <img src='" + imageUrl + "' alt='" + model + "' class='w-full h-full object-cover opacity-90 hover:opacity-100 transition duration-1000 hover:scale-110'>" +
               "    </div>" +

               "    <div class='p-12'>" +
               "      <p class='text-xs font-bold tracking-[0.3em] text-stone-500 uppercase mb-4'>" + brand + " Collection</p>" +
               "      <h1 class='text-4xl mb-6 font-serif italic text-stone-900' style='font-family: \"Playfair Display\", serif;'>" + model + "</h1>" +
               "      <div class='w-16 h-1 bg-amber-600 mx-auto mb-6'></div>" +
               "      <div class='text-lg leading-loose text-stone-600 mb-8 font-serif' style='font-family: \"Playfair Display\", serif;'>" + review + "</div>" +
               "      <div class='flex justify-center items-center gap-6 mt-8 border-t border-stone-200 pt-8'>" +
               "         <span class='text-3xl font-serif text-stone-900'>$" + price + "</span>" +
               "         <button class='bg-stone-900 text-white px-8 py-3 uppercase text-xs tracking-widest hover:bg-amber-700 transition duration-500'>Adquirir</button>" +
               "      </div>" +
               "      <div class='mt-6'>" +
               "        <a href='index.html' class='text-stone-400 text-xs hover:text-stone-900 transition'>VOLVER</a>" +
               "      </div>" +
               "    </div>" +
               "  </div>" +
               "</body></html>";
    }
}