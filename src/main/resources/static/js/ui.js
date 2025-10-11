// Utilidades de UI: estado del header al hacer scroll y parallax suave en el hero
(function(){
  const header = document.querySelector('.site-header');
  const hero = document.querySelector('.hero');
  let lastY = window.scrollY;

  // Estado del header cuando hay desplazamiento (agrega/quita clase)
  const onScroll = () => {
    const y = window.scrollY;
    if (header) {
      if (y > 8) header.classList.add('scrolled');
      else header.classList.remove('scrolled');
    }
    // Parallax: ajustar levemente la posición del fondo
    if (hero) {
      // Limitar el efecto a anchos no móviles
      if (window.matchMedia('(min-width: 769px)').matches) {
        hero.style.backgroundPosition = `center ${Math.round(y * -0.2)}px`;
      } else {
        hero.style.backgroundPosition = 'center 0px';
      }
    }
    lastY = y;
  };

  window.addEventListener('scroll', onScroll, { passive: true });
  onScroll();
})();
