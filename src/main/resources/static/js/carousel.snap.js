// Carrusel basado en scroll-snap con bucle infinito y dots dinámicos
// Comportamiento:
// - Usa el scroll horizontal nativo con scroll-snap para centrar las tarjetas.
// - Crea K clones en ambos extremos para simular un bucle infinito.
// - Al finalizar el scroll, elige la slide más cercana al centro del viewport.
// - Crea los puntos (dots) de forma dinámica y los mantiene sincronizados.
// Controles: botones (prev/next), flechas del teclado, scroll táctil, autoplay.
// Ajustes:
// - Cambia K (clones por lado) entre 1..3 según el ancho de la tarjeta.
// - Ajusta el intervalo de autoplay (3500ms) o desactívalo comentando el setInterval.
(function(){
  document.addEventListener('DOMContentLoaded', () => {
    const root = document.getElementById('movieCarousel');
    if(!root) return;
    const viewport = root.querySelector('.carousel-viewport');
    const track = root.querySelector('.carousel-track');
    const prev = root.querySelector('.prev');
    const next = root.querySelector('.next');
    const dotsWrap = root.querySelector('.carousel-dots');
    if(!viewport || !track) return;

    let slides = Array.from(track.querySelectorAll('.slide'));
    const count = slides.length;
    if(count === 0) return;

  // Construir dots dinámicamente
    dotsWrap && (dotsWrap.innerHTML = '');
    const dots = [];
    if(dotsWrap){
      for(let i=0;i<count;i++){
        const b = document.createElement('button');
        b.className = 'dot'; b.dataset.index = String(i);
        b.setAttribute('aria-label', `Ir a Película ${i+1}`);
        dotsWrap.appendChild(b); dots.push(b);
      }
    }

  // Infinito: clonar cabeza/cola
    const K = Math.min(2, count);
    const pre = document.createDocumentFragment();
    const post = document.createDocumentFragment();
    for(let i=count-K;i<count;i++){ pre.appendChild(slides[i].cloneNode(true)); }
    for(let i=0;i<K;i++){ post.appendChild(slides[i].cloneNode(true)); }
    track.insertBefore(pre, track.firstChild);
    track.appendChild(post);

    slides = Array.from(track.querySelectorAll('.slide'));
  let current = K; // primera real

    function getGap(){ const st = getComputedStyle(track); return parseFloat(st.gap) || 16; }
    function slideWidth(){ const el = slides[current]; return el ? el.getBoundingClientRect().width + getGap() : 0; }

    function centerTo(idx, smooth=true){
      current = idx;
      const sw = slideWidth();
      const vw = viewport.getBoundingClientRect().width;
      const half = (vw - (sw - getGap()))/2;
      const x = sw*current - half;
      viewport.scrollTo({ left: x, behavior: smooth ? 'smooth' : 'auto' });
      updateClasses(); updateDots();
    }

    function norm(){ return ((current - K) % count + count) % count; }
    function updateDots(){ dots.forEach((d,i)=> d.classList.toggle('active', i===norm())); }
    function updateClasses(){
      slides.forEach(s=> s.classList.remove('active','prev','next'));
      slides[current]?.classList.add('active');
      slides[(current-1+slides.length)%slides.length]?.classList.add('prev');
      slides[(current+1)%slides.length]?.classList.add('next');
    }

  // Detectar fin del snap (usando scroll)
    let snapTimer;
    viewport.addEventListener('scroll', ()=>{
      clearTimeout(snapTimer);
      snapTimer = setTimeout(()=>{
  // elegir la slide más cercana al centro del viewport
        const vw = viewport.getBoundingClientRect().width;
        const center = viewport.scrollLeft + vw/2;
        let best = 0; let bestDist = Infinity; let acc = 0;
        for(let i=0;i<slides.length;i++){
          const w = slides[i].getBoundingClientRect().width + getGap();
          const mid = acc + w/2;
          const dist = Math.abs(mid - center);
          if(dist < bestDist){ bestDist = dist; best = i; }
          acc += w;
        }
        current = best; updateClasses(); updateDots();
  // correcciones para el bucle continuo sin saltos visibles
        if(current < K){ current += count; centerTo(current, false); }
        else if(current >= K+count){ current -= count; centerTo(current, false); }
      }, 90);
    }, { passive: true });

  // Controles
    prev.addEventListener('click', ()=> centerTo(current-1));
    next.addEventListener('click', ()=> centerTo(current+1));
    dots.forEach((d,i)=> d.addEventListener('click', ()=> centerTo(K+i)));

  // Teclado
    root.addEventListener('keydown', e=>{
      if(e.key==='ArrowLeft') prev.click();
      if(e.key==='ArrowRight') next.click();
    });

  // Touch: el scroll ya es nativo; solo pausamos el autoplay
    let isTouch=false; let autoplay;
    viewport.addEventListener('touchstart', ()=>{ isTouch=true; clearInterval(autoplay); }, {passive:true});
    viewport.addEventListener('touchend', ()=>{ isTouch=false; autoplay = setInterval(()=> next.click(), 3500); });

  // Reproducción automática (autoplay)
    autoplay = setInterval(()=>{ if(!isTouch) next.click(); }, 3500);
    root.addEventListener('mouseover', ()=> clearInterval(autoplay));
    root.addEventListener('mouseleave', ()=> { autoplay = setInterval(()=> next.click(), 3500); });

  // Inicialización
    centerTo(current, false);
    window.addEventListener('resize', ()=> setTimeout(()=> centerTo(current, false), 120));
  });
})();
