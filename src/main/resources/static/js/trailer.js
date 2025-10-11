// Control del modal de tráiler
// - Busca disparadores con [data-video] y abre el modal con el ID de YouTube
// - Inyecta el embed con autoplay y lo limpia al cerrar (detiene la reproducción)
// - Cierre por overlay, botón de cierre o tecla ESC
document.addEventListener('DOMContentLoaded', () => {
  const modal = document.getElementById('trailerModal');
  if(!modal) return;
  const frame = document.getElementById('trailerFrame');
  const openers = document.querySelectorAll('[data-video]');

  function open(videoId){
    const src = `https://www.youtube.com/embed/${videoId}?autoplay=1&rel=0&modestbranding=1`;
    frame.src = src;
    modal.classList.remove('hidden');
  }

  function close(){
    modal.classList.add('hidden');
    // Detener el video
    frame.src = '';
  }

  openers.forEach(el => el.addEventListener('click', (e)=>{
    e.preventDefault();
    const id = el.getAttribute('data-video');
    if(id) open(id);
  }));

  modal.addEventListener('click', (e)=>{
    if(e.target.hasAttribute('data-close')) close();
  });
  document.addEventListener('keydown', (e)=>{
    if(!modal.classList.contains('hidden') && e.key === 'Escape') close();
  });
});
