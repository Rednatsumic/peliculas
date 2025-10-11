// UI helpers: header scroll state and light parallax for hero
(function(){
  const header = document.querySelector('.site-header');
  const hero = document.querySelector('.hero');
  let lastY = window.scrollY;

  // Header scrolled state
  const onScroll = () => {
    const y = window.scrollY;
    if (header) {
      if (y > 8) header.classList.add('scrolled');
      else header.classList.remove('scrolled');
    }
    // Parallax: adjust background position slightly
    if (hero) {
      // Limit effect to non-mobile widths
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
