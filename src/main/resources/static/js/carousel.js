document.addEventListener('DOMContentLoaded', () => {
  const carousel = document.getElementById('movieCarousel');
  if (!carousel) return;

  const viewport = carousel.querySelector('.carousel-viewport');
  const track = carousel.querySelector('.carousel-track');
  const prev = carousel.querySelector('.prev');
  const next = carousel.querySelector('.next');
  const dotsWrap = carousel.querySelector('.carousel-dots');

  let isTouch = false;
  let startX = 0;
  let deltaX = 0;

  // Original slides
  let origSlides = Array.from(track.querySelectorAll('.slide'));
  const origCount = origSlides.length;
  if (origCount === 0) return;

  // Build dots dynamically
  let dots = [];
  if (dotsWrap) {
    dotsWrap.innerHTML = '';
    for (let i = 0; i < origCount; i++) {
      const b = document.createElement('button');
      b.className = 'dot';
      b.dataset.index = String(i);
      b.setAttribute('aria-label', `Ir a Película ${i + 1}`);
      dotsWrap.appendChild(b);
      dots.push(b);
    }
  }

  // Clone edges for infinite loop (giratorio)
  const K = Math.min(2, origCount); // clones on each side
  const fragPre = document.createDocumentFragment();
  const fragPost = document.createDocumentFragment();
  for (let i = origCount - K; i < origCount; i++) {
    const clone = origSlides[i].cloneNode(true);
    clone.classList.add('clone');
    fragPre.appendChild(clone);
  }
  for (let i = 0; i < K; i++) {
    const clone = origSlides[i].cloneNode(true);
    clone.classList.add('clone');
    fragPost.appendChild(clone);
  }
  track.insertBefore(fragPre, track.firstChild);
  track.appendChild(fragPost);

  let allSlides = Array.from(track.querySelectorAll('.slide'));
  let current = K; // start at first real slide

  function getGap() {
    const style = getComputedStyle(track);
    return parseFloat(style.gap) || 16;
  }

  function slideWidth() {
    const base = allSlides[current] || allSlides[K] || origSlides[0];
    if (!base) return 0;
    return base.getBoundingClientRect().width + getGap();
  }

  function computeTransform() {
    const sw = slideWidth();
    const vw = viewport.getBoundingClientRect().width;
    const half = (vw - (sw - getGap())) / 2;
    const offset = sw * current - half;
    return -offset;
  }

  function setTransform(x, withTransition = true) {
    if (!withTransition) track.style.transition = 'none';
    else track.style.transition = '';
    track.style.transform = `translateX(${x}px)`;
    if (!withTransition) {
      // force reflow then restore transition
      void track.offsetHeight;
      track.style.transition = '';
    }
  }

  function normalizedIndex() {
    return ((current - K) % origCount + origCount) % origCount;
  }

  function update(withTransition = true) {
    const tx = computeTransform();
    setTransform(tx, withTransition);
    // active classes
    allSlides.forEach(s => s.classList.remove('active','prev','next'));
    if (allSlides[current]) {
      allSlides[current].classList.add('active');
      const prevIdx = (current - 1 + allSlides.length) % allSlides.length;
      const nextIdx = (current + 1) % allSlides.length;
      allSlides[prevIdx]?.classList.add('prev');
      allSlides[nextIdx]?.classList.add('next');
    }
    // dots
    dots.forEach((d, i) => d.classList.toggle('active', i === normalizedIndex()));
  }

  // Seamless jump when we land in clones
  track.addEventListener('transitionend', () => {
    if (current < K) {
      current += origCount;
      update(false); // re-apply transform and active state without animation
    } else if (current >= K + origCount) {
      current -= origCount;
      update(false);
    } else {
      // Regular transition end: ensure active classes are correct
      update(false);
    }
  });

  function goTo(idx) {
    current = idx;
    update();
  }

  // Controls
  prev.addEventListener('click', () => goTo(current - 1));
  next.addEventListener('click', () => goTo(current + 1));
  dots.forEach((d, i) => d.addEventListener('click', () => goTo(K + i)));

  // Keyboard navigation
  carousel.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowLeft') prev.click();
    if (e.key === 'ArrowRight') next.click();
  });

  // Touch / swipe
  track.addEventListener('touchstart', (e) => {
    isTouch = true;
    startX = e.touches[0].clientX;
    clearInterval(autoplay);
  }, { passive: true });
  track.addEventListener('touchmove', (e) => { deltaX = e.touches[0].clientX - startX; });
  track.addEventListener('touchend', () => {
    if (Math.abs(deltaX) > 40) { if (deltaX < 0) next.click(); else prev.click(); }
    deltaX = 0; isTouch = false; autoplay = setInterval(() => next.click(), 3500);
  });

  // Autoplay (pause on hover)
  let autoplay = setInterval(() => { if (!isTouch) next.click(); }, 3500);
  carousel.addEventListener('mouseover', () => clearInterval(autoplay));
  carousel.addEventListener('mouseleave', () => { autoplay = setInterval(() => next.click(), 3500); });

  // Init and responsive
  update(false);
  window.addEventListener('resize', () => setTimeout(() => update(), 120));
});
